"""DTO 정의

외부 수신: GeneratePdfRequest, GenerateDiffPdfRequest  (Java 서버 → FastAPI)
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
    senior_liens: List[int] = Field(default_factory=list, description="선순위 근저당 채권최고액 목록 (원)")


class AnalysisResult(BaseModel):
    """분석 엔진 결과 (FastAPI → Java 서버)"""
    ltv_percent: float
    risk_score: Literal["High", "Medium", "Low"]
    expected_recovery_amount: int
    diff_summary: Optional[str] = None


class RiskAnalysisRequest(BaseModel):
    """PDF 1 내부 렌더링 DTO (html_generator 전용)"""
    user_name: str
    address: str
    risk_score: Literal["High", "Medium", "Low"]
    risk_score_num: float = Field(default=0, ge=0, le=100)
    max_claim_amount: int = Field(default=0)
    ltv_percent: float = Field(default=0, ge=0, le=200)
    analysis_summary: str
    checklist: List[str]
    signals: List[dict] = Field(default_factory=list)
    playbook: List[dict] = Field(default_factory=list)
    recovery_priority: str = ""

    @field_validator("risk_score")
    @classmethod
    def validate_risk_score(cls, v: str) -> str:
        if v not in {"High", "Medium", "Low"}:
            raise ValueError("risk_score는 High/Medium/Low 중 하나여야 합니다.")
        return v


class GeneratePdfRequest(BaseModel):
    """통합 PDF 생성 DTO (Java 서버 → FastAPI)

    계약전: snapshotName + rawData 만 전달
    계약후: 계약 정보 필드 추가 전달 (deposit 포함 시 계약후로 판단)
    """
    snapshotName: str = Field(..., description="등기부 제목/파일명")
    rawData: str = Field(..., description="분석 원본 데이터 JSON 문자열")
    # 계약후 추가 필드 (없으면 계약전 PDF 생성)
    contractType: Optional[str] = Field(None, description="계약 유형 (JEONSE / MONTHLY)")
    deposit: Optional[int] = Field(None, description="보증금 (원)")
    monthlyAmount: Optional[int] = Field(None, description="월세 (원)")
    maintenanceFee: Optional[int] = Field(None, description="관리비 (원)")
    moveDate: Optional[str] = Field(None, description="전입일 (YYYY-MM-DD)")
    confirmDate: Optional[str] = Field(None, description="확정일자 (YYYY-MM-DD)")


class GenerateDiffPdfRequest(BaseModel):
    """등기부 변동 비교 PDF 요청 DTO (Java 서버 → FastAPI)"""
    snapshotName: str = Field(..., description="등기부 제목")
    originData: str = Field(..., description="직전 분석 JSON 문자열")
    newData: str = Field(..., description="변동 후 분석 JSON 문자열")
    contractType: str = Field(..., description="계약 유형 (JEONSE / MONTHLY)")
    deposit: int = Field(..., description="보증금 (원)")
    monthlyAmount: int = Field(..., description="월세 (원)")
    maintenanceFee: int = Field(..., description="관리비 (원)")
    moveDate: str = Field(..., description="전입일 (YYYY-MM-DD)")
    confirmDate: str = Field(..., description="확정일자 (YYYY-MM-DD)")



class RecoveryRenderData(BaseModel):
    """PDF 2 내부 렌더링 DTO (recovery_html_generator 전용)"""
    user_name: str
    address: str
    contract_type: str = ""
    deposit_amount: int
    monthly_amount: int = 0
    maintenance_fee: int = 0
    move_in_date: str
    confirmed_date: str
    has_residency: bool
    has_priority_right: bool
    max_claim_amount: int
    property_value: int = 0
    expected_recovery: int = 0
    recovery_rate: float = 0.0
    ltv_percent: float
    risk_score: Literal["High", "Medium", "Low"]
    signals: List[dict] = Field(default_factory=list)
