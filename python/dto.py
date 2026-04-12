"""DTO 정의

외부 수신 (Java 서버 → FastAPI):
    AnalyzeResponseDTO      /engine/analyze 응답
    GeneratePdfRequest      /engine/generate-pdf 요청 (계약전/후 통합)
    GenerateDiffPdfRequest  /engine/generate-pdf/diff 요청

내부 렌더링:
    RiskAnalysisRequest     risk_html_generator 용
    RecoveryRenderData      recovery_html_generator 용
"""
from __future__ import annotations

from typing import List, Literal, Optional

from pydantic import BaseModel, Field, field_validator


# ─────────────────────────────────────────────
# /engine/analyze 응답 DTO
# ─────────────────────────────────────────────

class AnalyzeResponseDTO(BaseModel):
    """OCR 분석 결과 (FastAPI → Java 서버)"""
    riskLevel: str = Field(..., description="DANGER / WARNING / SAFE")
    rawData:   str = Field(..., description="전체 분석 JSON 문자열 (image_files 포함)")
    mainReason: str = Field(..., description="주요 위험 사유 한 줄 요약")
    ltvScore:  int  = Field(..., description="LTV 수치 (정수, %)")


# ─────────────────────────────────────────────
# /engine/generate-pdf 요청 DTO
# ─────────────────────────────────────────────

class GeneratePdfRequest(BaseModel):
    """통합 PDF 생성 DTO (Java 서버 → FastAPI)

    계약전: snapshotName + rawData 만 전달
    계약후: deposit 포함 시 계약후로 판단
    """
    snapshotName:    str            = Field(...,  description="등기부 제목/파일명")
    rawData:         str            = Field(...,  description="분석 원본 데이터 JSON 문자열")
    contractType:    Optional[str]  = Field(None, description="계약 유형 (JEONSE / MONTHLY)")
    deposit:         Optional[int]  = Field(None, description="보증금 (원)")
    monthlyAmount:   Optional[int]  = Field(None, description="월세 (원)")
    maintenanceFee:  Optional[int]  = Field(None, description="관리비 (원)")
    moveDate:        Optional[str]  = Field(None, description="전입일 (YYYY-MM-DD)")
    confirmDate:     Optional[str]  = Field(None, description="확정일자 (YYYY-MM-DD)")


class GenerateDiffPdfRequest(BaseModel):
    """등기부 변동 비교 PDF 요청 DTO (Java 서버 → FastAPI)"""
    snapshotName:    str = Field(..., description="등기부 제목")
    originData:      str = Field(..., description="직전 분석 JSON 문자열")
    newData:         str = Field(..., description="변동 후 분석 JSON 문자열")
    contractType:    str = Field(..., description="계약 유형 (JEONSE / MONTHLY)")
    deposit:         int = Field(..., description="보증금 (원)")
    monthlyAmount:   int = Field(..., description="월세 (원)")
    maintenanceFee:  int = Field(..., description="관리비 (원)")
    moveDate:        str = Field(..., description="전입일 (YYYY-MM-DD)")
    confirmDate:     str = Field(..., description="확정일자 (YYYY-MM-DD)")


# ─────────────────────────────────────────────
# 내부 렌더링 DTO
# ─────────────────────────────────────────────

class RiskAnalysisRequest(BaseModel):
    """PDF 1 내부 렌더링 DTO (risk_html_generator 전용)"""
    user_name:         str
    address:           str
    risk_score:        Literal["High", "Medium", "Low"]
    risk_score_num:    float         = Field(default=0, ge=0, le=100)
    max_claim_amount:  int           = Field(default=0)
    ltv_percent:       float         = Field(default=0, ge=0, le=200)
    analysis_summary:  str
    checklist:         List[str]
    signals:           List[dict]    = Field(default_factory=list)
    playbook:          List[dict]    = Field(default_factory=list)
    recovery_priority: str           = ""

    @field_validator("risk_score")
    @classmethod
    def validate_risk_score(cls, v: str) -> str:
        if v not in {"High", "Medium", "Low"}:
            raise ValueError("risk_score는 High/Medium/Low 중 하나여야 합니다.")
        return v


class RecoveryRenderData(BaseModel):
    """PDF 2 내부 렌더링 DTO (recovery_html_generator 전용)"""
    user_name:          str
    address:            str
    contract_type:      str   = ""
    deposit_amount:     int
    monthly_amount:     int   = 0
    maintenance_fee:    int   = 0
    move_in_date:       str
    confirmed_date:     str
    has_residency:      bool
    has_priority_right: bool
    max_claim_amount:   int
    property_value:     int   = 0
    expected_recovery:  int   = 0
    recovery_rate:      float = 0.0
    ltv_percent:        float
    risk_score:         Literal["High", "Medium", "Low"]
    signals:            List[dict] = Field(default_factory=list)
