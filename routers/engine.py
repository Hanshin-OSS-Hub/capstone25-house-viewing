"""
/engine 라우터

Java 서버(Spring Boot)가 ProcessBuilder 또는 REST로 호출하는 두 엔드포인트:

  POST /engine/analyze
      ┌─ Java 서버가 파싱된 등기부 데이터(RegistryDocument)를 전달
      └─ 분석 엔진이 risk / recovery / diff JSON(AnalysisResult)을 반환
         → 이 로직은 은섭 담당. 현재는 LTV 계산 스텁(stub)으로 구현.

  POST /engine/generate-pdf
      ┌─ Java 서버가 분석 완료된 JSON(RiskAnalysisRequest)을 전달
      └─ HTML 보고서 생성 후 PDF 바이너리(application/pdf)를 반환
         → 태수 담당. 구현 완료.
"""
from __future__ import annotations

import json as json_lib
import os
import time
from urllib.parse import quote
import pdfkit
from fastapi import APIRouter, HTTPException
from fastapi.responses import Response

from dto import AnalysisResult, GeneratePdfRequest, RegistryDocument, RiskAnalysisRequest
from html_generator import generate_html_report

router = APIRouter(prefix="/engine", tags=["Engine"])

# wkhtmltopdf 실행 파일 경로 (PATH에 없을 경우 대비)
_WKHTMLTOPDF_PATH = r"C:\Program Files\wkhtmltopdf\bin\wkhtmltopdf.exe"
_PDFKIT_CONFIG = (
    pdfkit.configuration(wkhtmltopdf=_WKHTMLTOPDF_PATH)
    if os.path.exists(_WKHTMLTOPDF_PATH)
    else None  # PATH에 있으면 None (자동 탐지)
)

# wkhtmltopdf 변환 옵션
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
    "javascript-delay": "1000",   # Google Fonts 로딩 대기 (ms)
}


# ─────────────────────────────────────────────────────────────
# POST /engine/analyze
# ─────────────────────────────────────────────────────────────
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
    """
    ### 처리 흐름
    1. Java 서버로부터 파싱된 등기부 데이터 수신
    2. LTV / 위험등급 / 예상 배당금 계산 (은섭 엔진 연동 예정)
    3. AnalysisResult JSON 반환 → Java 서버가 DB 저장 후 /generate-pdf 로 전달

    > **TODO (은섭):** 실제 분석 엔진 로직으로 교체
    """
    # ── 스텁(Stub): 기본 LTV 계산 ──────────────────────────────
    total_senior_liens = sum(doc.senior_liens)
    ltv = ((total_senior_liens + doc.deposit_amount) / doc.property_value * 100
           if doc.property_value > 0 else 0.0)

    if ltv > 80:
        risk = "High"
    elif ltv > 60:
        risk = "Medium"
    else:
        risk = "Low"

    # 선순위 채권 소진 후 남은 금액을 보증금 기준으로 배당
    remaining = doc.property_value - total_senior_liens
    recovery = max(0, min(doc.deposit_amount, remaining))

    return AnalysisResult(
        ltv_percent=round(ltv, 2),
        risk_score=risk,
        expected_recovery_amount=recovery,
        diff_summary=None,  # 이전 분석과의 비교는 추후 구현
    )


# ─────────────────────────────────────────────────────────────
# 내부 헬퍼: 은섭 JSON → 템플릿 렌더링 데이터 변환
# ─────────────────────────────────────────────────────────────
def _build_render_data(snapshot_id: int, raw: dict) -> RiskAnalysisRequest:
    """
    은섭 분석 JSON을 html_generator가 쓸 수 있는 RiskAnalysisRequest로 변환.

    은섭 JSON에서 꺼내는 값:
      risk.risk_level        → 위험 등급 (HIGH/MEDIUM/LOW → High/Medium/Low)
      risk.risk_score        → 위험 점수 0~100 (LTV 게이지에 표시)
      risk.signals[].explain → 위험 시그널 설명 → 체크리스트
      risk.checks.max_claim_amount_total → 채권최고액 합계
      snapshot.gabu[].owners → 소유자 이름
      recovery.playbook[].title → 복구 단계 제목 → 조치사항
    """
    level_map = {"HIGH": "High", "MEDIUM": "Medium", "LOW": "Low"}

    # ① 위험 등급 / 점수
    risk_info  = raw.get("risk", {})
    risk_level = level_map.get(risk_info.get("risk_level", "MEDIUM"), "Medium")
    risk_score_num = float(risk_info.get("risk_score", 50))   # 0~100

    # ② 체크리스트 = 위험 시그널 설명 문장들
    signals   = risk_info.get("signals", [])
    checklist = [s["explain"] for s in signals] if signals else ["위험 시그널 없음"]

    # ③ 채권최고액 합계
    checks      = risk_info.get("checks", {})
    total_claim = checks.get("max_claim_amount_total", 0)

    # ④ 소유자 이름 (갑구 첫 번째 항목)
    gabu       = raw.get("snapshot", {}).get("gabu", [])
    owners     = gabu[0].get("owners", []) if gabu else []
    owner_name = ", ".join(o["name"] for o in owners) if owners else "미확인"

    # ⑤ 복구 플레이북 제목 → 조치사항 요약
    playbook = raw.get("recovery", {}).get("playbook", [])
    summary  = " / ".join(step["title"] for step in playbook[:2]) if playbook else "분석 결과 없음"

    return RiskAnalysisRequest(
        user_name=owner_name,
        address=f"스냅샷 ID: {snapshot_id}",
        risk_score=risk_level,
        ltv_percent=risk_score_num,          # 위험 점수(0~100)를 LTV 게이지에 표시
        expected_recovery_amount=total_claim,
        analysis_summary=summary,
        checklist=checklist,
        signals=signals,
        playbook=playbook,
        recovery_priority=raw.get("recovery", {}).get("priority", ""),
    )


# ─────────────────────────────────────────────────────────────
# POST /engine/generate-pdf
# ─────────────────────────────────────────────────────────────
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
    """
    ### 처리 흐름
    1. Java 서버로부터 snapshotId + rawData(은섭 JSON 문자열) 수신
    2. rawData를 파싱해서 렌더링 데이터로 변환
    3. `generate_html_report()` → 전문 HTML 보고서 생성
    4. `pdfkit.from_string()` → HTML → PDF 바이너리 변환
    5. `application/pdf` Response 반환 → Java 서버가 DB 저장 후 앱에 전달
    """
    # ── 0단계: rawData 문자열 → 딕셔너리로 열기 ─────────────
    try:
        raw: dict = json_lib.loads(request.rawData)
    except ValueError:
        raise HTTPException(
            status_code=422,
            detail="rawData가 유효한 JSON 문자열이 아닙니다.",
        )

    # ── 0.5단계: 은섭 JSON → 렌더링 데이터 변환 ─────────────
    render_data = _build_render_data(request.snapshotId, raw)

    # ── 1단계: Gemini HTML 생성 ──────────────────────────────
    t0 = time.perf_counter()
    try:
        html_content: str = generate_html_report(render_data)
    except Exception as e:
        err = str(e)
        if "429" in err or "RESOURCE_EXHAUSTED" in err:
            raise HTTPException(
                status_code=429,
                detail="Gemini API 호출 한도 초과. 잠시 후 다시 시도하세요.",
            )
        raise HTTPException(status_code=503, detail=f"보고서 HTML 생성 실패: {err}")
    gemini_time = time.perf_counter() - t0

    # ── 2단계: wkhtmltopdf PDF 변환 ──────────────────────────
    t1 = time.perf_counter()
    try:
        pdf_bytes: bytes = pdfkit.from_string(
            html_content,
            False,  # False = 파일 저장 안 함, 바이너리로 반환
            options=_PDFKIT_OPTIONS,
            configuration=_PDFKIT_CONFIG,
        )
    except OSError as e:
        raise HTTPException(
            status_code=500,
            detail=f"PDF 변환 실패: wkhtmltopdf를 확인하세요. 상세 오류: {e}",
        )
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"PDF 변환 중 예상치 못한 오류 발생: {e}",
        )
    pdf_time = time.perf_counter() - t1

    # ── 응답 ─────────────────────────────────────────────────
    encoded_name = quote(f"report_{render_data.user_name}.pdf", safe="")
    return Response(
        content=pdf_bytes,
        media_type="application/pdf",
        headers={
            "Content-Disposition": f"attachment; filename=\"report.pdf\"; filename*=UTF-8''{encoded_name}",
            "Content-Length": str(len(pdf_bytes)),
            # 프론트 로딩 UX용 타이밍 헤더
            "X-Gemini-Time": f"{gemini_time:.3f}",
            "X-PDF-Convert-Time": f"{pdf_time:.3f}",
        },
    )
