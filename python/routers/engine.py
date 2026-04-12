"""/engine 라우터 — Java 서버(Spring Boot)가 호출하는 분석/PDF 생성 엔드포인트"""
from __future__ import annotations

import json as json_lib
import os
import time
from urllib.parse import quote

import pdfkit
from fastapi import APIRouter, HTTPException
from fastapi.responses import Response

from schemas.dto import (
    GeneratePdfRequest, GenerateDiffPdfRequest,
    RiskAnalysisRequest, RecoveryRenderData,
)
from generators.risk_html_generator import generate_html_report
from generators.recovery_html_generator import generate_recovery_html_report
from generators.diff_html_generator import generate_diff_html_report
from generators.verification_html_generator import build_snapshot_page

router = APIRouter(prefix="/engine", tags=["Engine"])

# wkhtmltopdf 경로: 환경변수 우선, 없으면 OS별 기본 경로로 fallback
_WKHTMLTOPDF_PATH = os.getenv(
    "WKHTMLTOPDF_PATH",
    r"C:\Program Files\wkhtmltopdf\bin\wkhtmltopdf.exe",  # Windows 기본값
)
_PDFKIT_CONFIG = (
    pdfkit.configuration(wkhtmltopdf=_WKHTMLTOPDF_PATH)
    if os.path.exists(_WKHTMLTOPDF_PATH)
    else None  # Linux(Docker)에서는 PATH에 있으면 None으로 동작
)

_PDFKIT_OPTIONS: dict = {
    "encoding": "UTF-8",
    "page-size": "A4",
    "margin-top": "12mm",
    "margin-right": "12mm",
    "margin-bottom": "14mm",
    "margin-left": "12mm",
    "enable-local-file-access": "",
    "no-outline": "",
    "quiet": "",
    "javascript-delay": "1000",
}

_LEVEL_MAP = {"HIGH": "High", "MEDIUM": "Medium", "LOW": "Low"}


def _extract_raw_info(raw: dict, snapshot_name: str) -> dict:
    """은섭 분석 JSON에서 공통 필드 추출"""
    risk_info = raw.get("risk", {})
    snapshot  = raw.get("snapshot", {})
    gabu      = snapshot.get("gabu", [])
    owners    = gabu[0].get("owners", []) if gabu else []

    return {
        "risk_level": _LEVEL_MAP.get(risk_info.get("risk_level", "MEDIUM"), "Medium"),
        "risk_score": float(risk_info.get("risk_score", 50)),
        "signals":    risk_info.get("signals", []),
        "max_claim":  int(risk_info.get("checks", {}).get("max_claim_amount_total", 0)),
        "owner_name": ", ".join(o["name"] for o in owners) if owners else "미확인",
        "address":    snapshot.get("address", {}).get("address", snapshot_name),
    }



def _build_render_data(snapshot_name: str, raw: dict) -> RiskAnalysisRequest:
    """은섭 분석 JSON → PDF 1 렌더링 데이터 변환"""
    info      = _extract_raw_info(raw, snapshot_name)
    signals   = info["signals"]
    checklist = [s["explain"] for s in signals] if signals else ["위험 시그널 없음"]
    playbook  = raw.get("recovery", {}).get("playbook", [])
    summary   = " / ".join(step["title"] for step in playbook[:2]) if playbook else "분석 결과 없음"

    return RiskAnalysisRequest(
        user_name=info["owner_name"],
        address=info["address"],
        risk_score=info["risk_level"],
        risk_score_num=info["risk_score"],
        max_claim_amount=info["max_claim"],
        analysis_summary=summary,
        checklist=checklist,
        signals=signals,
        playbook=playbook,
        recovery_priority=raw.get("recovery", {}).get("priority", ""),
    )


def _build_recovery_render_data(request: GeneratePdfRequest) -> RecoveryRenderData:
    """은섭 분석 JSON → PDF 2 렌더링 데이터 변환"""
    raw  = json_lib.loads(request.rawData)
    info = _extract_raw_info(raw, request.snapshotName)

    # property_value: ltv 정보 → valuation 순으로 fallback
    ltv_info = raw.get("ltv") or {}
    prop_val = int(ltv_info.get("house_price_won") or 0)
    if prop_val == 0:
        prop_val = int((raw.get("valuation") or {}).get("median_price_won") or 0)

    # 예상 회수액: recovery 엔진 pre-computed 값 우선 사용
    recovery_calc     = (raw.get("recovery") or {}).get("calculation") or {}
    expected_recovery = int(recovery_calc.get("recoverable_amount") or 0)
    deposit           = request.deposit or 0
    recovery_rate     = round(expected_recovery / deposit * 100, 1) if deposit > 0 else 0.0

    has_residency   = bool(request.moveDate)
    has_priority    = has_residency and bool(request.confirmDate)

    return RecoveryRenderData(
        user_name=info["owner_name"],
        address=info["address"],
        contract_type=request.contractType or "",
        deposit_amount=deposit,
        monthly_amount=request.monthlyAmount or 0,
        maintenance_fee=request.maintenanceFee or 0,
        move_in_date=request.moveDate or "",
        confirmed_date=request.confirmDate or "",
        has_residency=has_residency,
        has_priority_right=has_priority,
        max_claim_amount=info["max_claim"],
        property_value=prop_val,
        expected_recovery=expected_recovery,
        recovery_rate=recovery_rate,
        ltv_percent=info["risk_score"],
        risk_score=info["risk_level"],
        signals=info["signals"],
    )


def _pdf_bytes(html: str) -> bytes:
    """HTML → PDF 바이너리 변환"""
    try:
        return pdfkit.from_string(html, False, options=_PDFKIT_OPTIONS, configuration=_PDFKIT_CONFIG)
    except OSError as e:
        raise HTTPException(status_code=500, detail=f"PDF 변환 실패: wkhtmltopdf를 확인하세요. {e}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"PDF 변환 중 오류 발생: {e}")


@router.post(
    "/generate-pdf",
    summary="권리 분석 보고서 PDF 생성 (계약전/계약후 통합)",
    responses={
        200: {"content": {"application/pdf": {}}, "description": "PDF 파일 반환"},
        422: {"description": "요청 데이터 유효성 오류"},
        500: {"description": "PDF 변환 실패"},
    },
)
async def generate_pdf(request: GeneratePdfRequest) -> Response:
    try:
        raw: dict = json_lib.loads(request.rawData)
    except ValueError:
        raise HTTPException(status_code=422, detail="rawData가 유효한 JSON 문자열이 아닙니다.")

    t0 = time.perf_counter()

    if request.deposit is not None:
        # 계약후: 보증금 회수 분석 PDF
        render_data = _build_recovery_render_data(request)
        try:
            html_content = generate_recovery_html_report(render_data)
        except Exception as e:
            raise HTTPException(status_code=503, detail=f"보고서 HTML 생성 실패: {e}")
        filename = f"recovery_{render_data.user_name}.pdf"
    else:
        # 계약전: 위험 분석 PDF
        render_data = _build_render_data(request.snapshotName, raw)
        try:
            html_content = generate_html_report(render_data)
        except Exception as e:
            err = str(e)
            if "429" in err or "RESOURCE_EXHAUSTED" in err:
                raise HTTPException(status_code=429, detail="AI API 호출 한도 초과. 잠시 후 다시 시도하세요.")
            raise HTTPException(status_code=503, detail=f"보고서 HTML 생성 실패: {err}")
        # 마지막 페이지에 OCR 파싱 데이터 요약 추가
        snapshot = raw.get("snapshot") or {}
        if snapshot:
            snap_page = build_snapshot_page(snapshot)
            html_content = html_content.replace("</body>", f"{snap_page}</body>")
        filename = f"report_{render_data.user_name}.pdf"

    ai_time   = time.perf_counter() - t0
    t1        = time.perf_counter()
    pdf_bytes = _pdf_bytes(html_content)
    pdf_time  = time.perf_counter() - t1

    encoded_name = quote(filename, safe="")
    return Response(
        content=pdf_bytes,
        media_type="application/pdf",
        headers={
            "Content-Disposition": f"attachment; filename=\"report.pdf\"; filename*=UTF-8''{encoded_name}",
            "Content-Length": str(len(pdf_bytes)),
            "X-AI-Time": f"{ai_time:.3f}",
            "X-PDF-Convert-Time": f"{pdf_time:.3f}",
        },
    )


@router.post(
    "/generate-pdf/diff",
    summary="등기부 변동 비교 PDF 생성",
    responses={
        200: {"content": {"application/pdf": {}}, "description": "PDF 파일 반환"},
        422: {"description": "요청 데이터 유효성 오류"},
        500: {"description": "PDF 변환 실패"},
    },
)
async def generate_diff_pdf(request: GenerateDiffPdfRequest) -> Response:
    try:
        origin_raw: dict = json_lib.loads(request.originData)
        new_raw: dict    = json_lib.loads(request.newData)
    except ValueError:
        raise HTTPException(status_code=422, detail="originData 또는 newData가 유효한 JSON이 아닙니다.")

    try:
        html_content = generate_diff_html_report(
            snapshot_name=request.snapshotName,
            origin_raw=origin_raw,
            new_raw=new_raw,
            contract_type=request.contractType,
            deposit=request.deposit,
            monthly_amount=request.monthlyAmount,
            maintenance_fee=request.maintenanceFee,
            move_date=request.moveDate,
            confirm_date=request.confirmDate,
        )
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"변동 보고서 생성 실패: {e}")

    pdf_bytes    = _pdf_bytes(html_content)
    encoded_name = quote(f"diff_{request.snapshotName}.pdf", safe="")
    return Response(
        content=pdf_bytes,
        media_type="application/pdf",
        headers={
            "Content-Disposition": f"attachment; filename=\"diff_report.pdf\"; filename*=UTF-8''{encoded_name}",
            "Content-Length": str(len(pdf_bytes)),
        },
    )
