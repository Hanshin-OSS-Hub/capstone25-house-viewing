"""/engine 라우터 — Java 서버(Spring Boot)가 호출하는 분석/PDF 생성 엔드포인트"""
from __future__ import annotations

import json as json_lib
import os
import time
from urllib.parse import quote

import pdfkit
from fastapi import APIRouter, HTTPException
from fastapi.responses import Response

from dto import (
    AnalysisResult, GeneratePdfRequest, RegistryDocument,
    RiskAnalysisRequest, RecoveryPdfRequest, RecoveryRenderData,
)
from html_generator import generate_html_report
from recovery_html_generator import generate_recovery_html_report

router = APIRouter(prefix="/engine", tags=["Engine"])

_WKHTMLTOPDF_PATH = r"C:\Program Files\wkhtmltopdf\bin\wkhtmltopdf.exe"
_PDFKIT_CONFIG = (
    pdfkit.configuration(wkhtmltopdf=_WKHTMLTOPDF_PATH)
    if os.path.exists(_WKHTMLTOPDF_PATH)
    else None  # PATH에 있으면 None (자동 탐지)
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


def _extract_raw_info(raw: dict, snapshot_id: int) -> dict:
    """은섭 분석 JSON에서 공통 필드 추출 (두 PDF 엔드포인트에서 공유)."""
    risk_info  = raw.get("risk", {})
    snapshot   = raw.get("snapshot", {})
    gabu       = snapshot.get("gabu", [])
    owners     = gabu[0].get("owners", []) if gabu else []

    return {
        "risk_level":  _LEVEL_MAP.get(risk_info.get("risk_level", "MEDIUM"), "Medium"),
        "risk_score":  float(risk_info.get("risk_score", 50)),
        "signals":     risk_info.get("signals", []),
        "max_claim":   int(risk_info.get("checks", {}).get("max_claim_amount_total", 0)),
        "owner_name":  ", ".join(o["name"] for o in owners) if owners else "미확인",
        "address":     snapshot.get("address", {}).get("address", f"스냅샷 ID: {snapshot_id}"),
    }


@router.post(
    "/analyze",
    response_model=AnalysisResult,
    summary="등기부 분석 → risk / recovery / diff JSON 반환",
    responses={
        200: {"description": "분석 결과 JSON"},
        422: {"description": "요청 데이터 유효성 오류"},
    },
)
async def analyze(doc: RegistryDocument) -> AnalysisResult:
    """TODO (은섭): 실제 분석 엔진 로직으로 교체. 현재는 LTV 계산 스텁."""
    total_senior_liens = sum(doc.senior_liens)
    ltv = ((total_senior_liens + doc.deposit_amount) / doc.property_value * 100
           if doc.property_value > 0 else 0.0)

    if ltv > 80:
        risk = "High"
    elif ltv > 60:
        risk = "Medium"
    else:
        risk = "Low"

    remaining = doc.property_value - total_senior_liens
    recovery  = max(0, min(doc.deposit_amount, remaining))

    return AnalysisResult(
        ltv_percent=round(ltv, 2),
        risk_score=risk,
        expected_recovery_amount=recovery,
        diff_summary=None,
    )


def _build_render_data(snapshot_id: int, raw: dict) -> RiskAnalysisRequest:
    """은섭 분석 JSON → PDF 1 렌더링 데이터 변환"""
    info     = _extract_raw_info(raw, snapshot_id)
    signals  = info["signals"]
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


@router.post(
    "/generate-pdf",
    summary="권리 분석 보고서 PDF 생성",
    response_description="생성된 PDF 바이너리 파일",
    responses={
        200: {"content": {"application/pdf": {}}, "description": "PDF 파일 반환"},
        422: {"description": "요청 데이터 유효성 오류"},
        500: {"description": "PDF 변환 실패 (wkhtmltopdf 확인 필요)"},
    },
)
async def generate_pdf(request: GeneratePdfRequest) -> Response:
    try:
        raw: dict = json_lib.loads(request.rawData)
    except ValueError:
        raise HTTPException(status_code=422, detail="rawData가 유효한 JSON 문자열이 아닙니다.")

    render_data = _build_render_data(request.snapshotId, raw)

    t0 = time.perf_counter()
    try:
        html_content: str = generate_html_report(render_data)
    except Exception as e:
        err = str(e)
        if "429" in err or "RESOURCE_EXHAUSTED" in err:
            raise HTTPException(status_code=429, detail="AI API 호출 한도 초과. 잠시 후 다시 시도하세요.")
        raise HTTPException(status_code=503, detail=f"보고서 HTML 생성 실패: {err}")
    ai_time = time.perf_counter() - t0

    t1 = time.perf_counter()
    try:
        pdf_bytes: bytes = pdfkit.from_string(
            html_content, False,
            options=_PDFKIT_OPTIONS,
            configuration=_PDFKIT_CONFIG,
        )
    except OSError as e:
        raise HTTPException(status_code=500, detail=f"PDF 변환 실패: wkhtmltopdf를 확인하세요. 상세 오류: {e}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"PDF 변환 중 예상치 못한 오류 발생: {e}")
    pdf_time = time.perf_counter() - t1

    encoded_name = quote(f"report_{render_data.user_name}.pdf", safe="")
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


def _build_recovery_render_data(request: RecoveryPdfRequest) -> RecoveryRenderData:
    """은섭 분석 JSON → PDF 2 렌더링 데이터 변환 (우선변제권 = 전입신고 AND 확정일자)"""
    raw      = json_lib.loads(request.rawData)
    info     = _extract_raw_info(raw, request.snapshotId)
    max_claim = info["max_claim"]

    has_priority = request.hasResidency and bool(request.confirmedDate)

    prop_val = request.propertyValue
    if prop_val > 0:
        remaining         = max(0, prop_val - max_claim)
        expected_recovery = min(request.depositAmount, remaining)
        recovery_rate     = (expected_recovery / request.depositAmount * 100) if request.depositAmount > 0 else 0.0
    else:
        expected_recovery = 0
        recovery_rate     = 0.0

    return RecoveryRenderData(
        user_name=info["owner_name"],
        address=f"스냅샷 ID: {request.snapshotId}",
        deposit_amount=request.depositAmount,
        move_in_date=request.moveInDate,
        confirmed_date=request.confirmedDate,
        has_residency=request.hasResidency,
        has_priority_right=has_priority,
        max_claim_amount=max_claim,
        property_value=prop_val,
        expected_recovery=expected_recovery,
        recovery_rate=round(recovery_rate, 1),
        ltv_percent=info["risk_score"],  # 은섭 JSON에 ltv 없음 → risk_score 대체
        risk_score=info["risk_level"],
        signals=info["signals"],
    )


@router.post(
    "/generate-recovery-pdf",
    summary="보증금 회수 분석 PDF 생성",
    response_description="생성된 PDF 바이너리 파일",
    responses={
        200: {"content": {"application/pdf": {}}, "description": "PDF 파일 반환"},
        422: {"description": "요청 데이터 유효성 오류"},
        500: {"description": "PDF 변환 실패"},
    },
)
async def generate_recovery_pdf(request: RecoveryPdfRequest) -> Response:
    try:
        json_lib.loads(request.rawData)
    except ValueError:
        raise HTTPException(status_code=422, detail="rawData가 유효한 JSON 문자열이 아닙니다.")

    render_data = _build_recovery_render_data(request)

    try:
        html_content: str = generate_recovery_html_report(render_data)
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"보고서 HTML 생성 실패: {e}")

    try:
        pdf_bytes: bytes = pdfkit.from_string(
            html_content, False,
            options=_PDFKIT_OPTIONS,
            configuration=_PDFKIT_CONFIG,
        )
    except OSError as e:
        raise HTTPException(status_code=500, detail=f"PDF 변환 실패: {e}")

    encoded_name = quote(f"recovery_{render_data.user_name}.pdf", safe="")
    return Response(
        content=pdf_bytes,
        media_type="application/pdf",
        headers={
            "Content-Disposition": f"attachment; filename=\"recovery.pdf\"; filename*=UTF-8''{encoded_name}",
            "Content-Length": str(len(pdf_bytes)),
        },
    )
