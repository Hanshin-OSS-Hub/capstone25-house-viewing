"""
DTO 정의 모듈

엔드포인트별 입출력 모델:
  - POST /engine/analyze      : RegistryDocument  → AnalysisResult
  - POST /engine/generate-pdf : GeneratePdfRequest → (PDF binary)

외부 수신 DTO:
  GeneratePdfRequest  ← Java 서버가 실제로 보내는 JSON (snapshotId + rawData)

내부 렌더링 모델:
  RiskAnalysisRequest ← rawData를 파싱해서 만드는 내부 객체 (html_generator 전용)
"""
from __future__ import annotations
from pydantic import BaseModel, Field, field_validator
from typing import List, Literal, Optional


# ─────────────────────────────────────────────────────────────
# /engine/analyze  (은섭 엔진 결과 수신용)
# Java 서버가 파싱된 등기부 데이터를 보내면
# 분석 엔진이 risk / recovery / diff JSON을 반환
# ─────────────────────────────────────────────────────────────

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


# ─────────────────────────────────────────────────────────────
# /engine/generate-pdf  (PDF 생성 요청용)
# Java 서버가 분석 완료된 JSON을 보내면 PDF를 반환
# ─────────────────────────────────────────────────────────────

class RiskAnalysisRequest(BaseModel):
    """PDF 생성 요청 DTO (Java 서버 → FastAPI)"""
    user_name: str = Field(..., description="임차인 이름")
    address: str = Field(..., description="임대 부동산 주소")
    risk_score: Literal["High", "Medium", "Low"] = Field(..., description="위험 등급")
    ltv_percent: float = Field(..., ge=0, le=200, description="LTV 비율 (%)")
    expected_recovery_amount: int = Field(..., ge=0, description="예상 배당금 (원)")
    analysis_summary: str = Field(..., description="종합 분석 요약")
    checklist: List[str] = Field(..., description="주의사항 체크리스트")
    signals: List[dict] = Field(default_factory=list, description="위험 시그널 목록 (severity, code, explain)")
    playbook: List[dict] = Field(default_factory=list, description="복구 단계 플레이북")
    recovery_priority: str = Field(default="", description="복구 우선순위 (SOON/LATER 등)")

    @field_validator("risk_score")
    @classmethod
    def validate_risk_score(cls, v: str) -> str:
        allowed = {"High", "Medium", "Low"}
        if v not in allowed:
            raise ValueError(f"risk_score는 {allowed} 중 하나여야 합니다.")
        return v


# ─────────────────────────────────────────────────────────────
# /engine/generate-pdf  (Java 서버 → FastAPI 외부 수신용 DTO)
# Java 서버가 딱 두 개 필드만 보내줌:
#   snapshotId : 등기부 기본키 (숫자)
#   rawData    : 은섭 분석 결과 JSON 전체 (문자열)
# ─────────────────────────────────────────────────────────────

class GeneratePdfRequest(BaseModel):
    """Java 서버 → FastAPI 수신 DTO (외부 API)"""
    snapshotId: int = Field(..., description="등기부 기본키 (DB PK)")
    rawData: str    = Field(..., description="은섭 분석 엔진 결과 JSON 문자열")
