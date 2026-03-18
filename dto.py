"""
DTO 정의

외부 수신: GeneratePdfRequest, RecoveryPdfRequest  (Java 서버 → FastAPI)
내부 렌더링: RiskAnalysisRequest (PDF 1), RecoveryRenderData (PDF 2)
"""
from __future__ import annotations
from pydantic import BaseModel, Field, field_validator
from typing import List, Literal, Optional



class RegistryDocument(BaseModel):
    """등기부 원시 데이터 (Java 서버 → FastAPI)"""
    document_id: str = Field(..., description="등기부 고유 ID (S3 key 또는 DB PK)")
    raw_text: Optional[str] = Field(None, description="OCR/파싱된 등기부 전문 텍스트")
    deposit_amount: int = Field(..., description="임차인 보증금 (원)")
    property_value: int = Field(..., description="KB시세 등 감정가 (원)")
    senior_liens: List[int] = Field(
        default_factory=list,
        description="선순위 근저당 채권최고액 목록 (원)",
    )


class AnalysisResult(BaseModel):
    """분석 엔진 결과 (FastAPI → Java 서버)"""
    ltv_percent: float = Field(..., description="LTV 비율 (%)")
    risk_score: Literal["High", "Medium", "Low"] = Field(..., description="위험 등급")
    expected_recovery_amount: int = Field(..., description="예상 배당금 (원)")
    diff_summary: Optional[str] = Field(None, description="이전 분석 대비 변경 요약")



class RiskAnalysisRequest(BaseModel):
    """PDF 1 내부 렌더링 DTO (html_generator 전용)"""
    user_name: str = Field(..., description="임차인 이름")
    address: str = Field(..., description="임대 부동산 주소")
    risk_score: Literal["High", "Medium", "Low"] = Field(..., description="위험 등급")
    risk_score_num: float = Field(default=0, ge=0, le=100, description="위험 점수 (0~100, 은섭 엔진 산출)")
    max_claim_amount: int = Field(default=0, description="선순위 채권최고액 합계 (원)")
    ltv_percent: float = Field(default=0, ge=0, le=200, description="LTV 비율 (%, 현재 미사용 — risk_score_num으로 대체)")
    analysis_summary: str = Field(..., description="종합 분석 요약")
    checklist: List[str] = Field(..., description="주의사항 체크리스트")
    signals: List[dict] = Field(default_factory=list, description="위험 시그널 목록 (severity, code, explain)")
    playbook: List[dict] = Field(default_factory=list, description="대응 단계 플레이북")
    recovery_priority: str = Field(default="", description="대응 우선순위 (IMMEDIATE/SOON/NORMAL)")

    @field_validator("risk_score")
    @classmethod
    def validate_risk_score(cls, v: str) -> str:
        allowed = {"High", "Medium", "Low"}
        if v not in allowed:
            raise ValueError(f"risk_score는 {allowed} 중 하나여야 합니다.")
        return v


class GeneratePdfRequest(BaseModel):
    """PDF 1 외부 수신 DTO (Java 서버 → FastAPI)"""
    snapshotId: int = Field(..., description="등기부 기본키 (DB PK)")
    rawData: str    = Field(..., description="은섭 분석 엔진 결과 JSON 문자열")



class RecoveryPdfRequest(BaseModel):
    """PDF 2 외부 수신 DTO (Java 서버 → FastAPI)"""
    snapshotId: int = Field(..., description="등기부 기본키 (DB PK)")
    depositAmount: int = Field(..., description="임차인 보증금 (원)")
    propertyValue: int = Field(default=0, description="부동산 시세/감정가 (원, 0이면 계산 불가)")
    moveInDate: str = Field(..., description="전입일 (YYYY-MM-DD)")
    confirmedDate: str = Field(..., description="확정일자 (YYYY-MM-DD)")
    hasResidency: bool = Field(..., description="전입신고 여부 (대항력 조건)")
    rawData: str = Field(..., description="은섭 분석 엔진 결과 JSON 문자열 (위험 정보 재사용)")


class RecoveryRenderData(BaseModel):
    """PDF 2 내부 렌더링 DTO (recovery_html_generator 전용)"""
    user_name: str = Field(..., description="임차인 이름")
    address: str = Field(..., description="임대 부동산 주소")
    deposit_amount: int = Field(..., description="보증금 (원)")
    move_in_date: str = Field(..., description="전입일")
    confirmed_date: str = Field(..., description="확정일자")
    has_residency: bool = Field(..., description="전입신고 여부")
    has_priority_right: bool = Field(..., description="우선변제권 여부 (전입신고 + 확정일자)")
    max_claim_amount: int = Field(..., description="선순위 채권최고액 합계 (원)")
    property_value: int = Field(default=0, description="부동산 시세/감정가 (원)")
    expected_recovery: int = Field(default=0, description="예상 회수 보증금 (원)")
    recovery_rate: float = Field(default=0.0, description="보증금 회수율 (%)")
    ltv_percent: float = Field(..., description="LTV 비율 (%)")
    risk_score: Literal["High", "Medium", "Low"] = Field(..., description="위험 등급")
    signals: List[dict] = Field(default_factory=list, description="위험 시그널 목록")
