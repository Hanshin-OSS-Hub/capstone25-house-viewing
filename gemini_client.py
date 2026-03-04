"""
Gemini API 클라이언트 모듈 (google-genai SDK 1.x)

역할:
  - 분석 데이터를 Gemini에게 전달
  - Gemini는 HTML 구조가 아닌 "분석 텍스트 JSON"만 반환
  - HTML 구조/디자인은 html_generator.py의 고정 템플릿이 담당
  → 이렇게 해야 디자인 일관성을 유지하면서 AI 콘텐츠를 활용할 수 있음
"""
from __future__ import annotations

import json
import logging
import os
import re

from dotenv import load_dotenv
from google import genai
from google.genai import types

from dto import RiskAnalysisRequest

load_dotenv()
logger = logging.getLogger(__name__)

_API_KEY: str | None = os.getenv("GEMINI_API_KEY")
_MODEL_NAME: str = os.getenv("GEMINI_MODEL", "gemini-2.0-flash")

# ── 시스템 프롬프트 ───────────────────────────────────────────
_SYSTEM_PROMPT = """당신은 부동산 임대차 전문 법무사이자 리스크 분석 전문가입니다.
임차인의 보증금 안전성을 분석하고, 전문적이면서도 임차인이 이해하기 쉬운 한국어로 분석 내용을 작성합니다.

[작성 원칙]
- 전문적이지만 임차인 관점에서 쉽게 이해할 수 있는 언어 사용
- 구체적인 수치와 근거를 포함한 분석
- 위험 등급에 따라 경고/안심 톤 조절
- 실질적으로 도움이 되는 조치 사항 제시"""

# ── 유저 프롬프트 ──────────────────────────────────────────────
def _build_prompt(data: RiskAnalysisRequest) -> str:
    risk_desc = {
        "High": "위험 (보증금 전액 회수 불가 가능성 높음)",
        "Medium": "주의 (부분 손실 가능성 있음)",
        "Low": "안전 (보증금 회수 가능성 높음)",
    }
    return f"""다음 부동산 임차 분석 데이터를 바탕으로 분석 텍스트를 작성하세요.

[입력 데이터]
- 임차인: {data.user_name}
- 주소: {data.address}
- 위험 등급: {data.risk_score} ({risk_desc.get(data.risk_score, '')})
- LTV 비율: {data.ltv_percent}%
- 예상 배당금: {data.expected_recovery_amount:,}원
- 기존 분석 요약: {data.analysis_summary}
- 주의 항목: {', '.join(data.checklist)}

아래 JSON 형식으로만 응답하세요 (다른 텍스트 없이):
{{
  "risk_headline": "위험 등급을 한 문장으로 요약 (20자 이내)",
  "main_analysis": "핵심 위험 요인과 그 이유를 구체적으로 설명 (3~4문장, LTV 수치 포함)",
  "recovery_comment": "예상 배당금 {data.expected_recovery_amount:,}원에 대한 해석과 의미 (2문장)",
  "action_items": ["즉시 해야 할 조치 1", "즉시 해야 할 조치 2", "즉시 해야 할 조치 3"]
}}"""


def _extract_json(raw: str) -> dict:
    """Gemini 응답에서 JSON 추출 및 파싱."""
    raw = raw.strip()
    # 마크다운 코드블록 제거
    match = re.search(r"```(?:json)?\s*([\s\S]+?)```", raw, re.IGNORECASE)
    if match:
        raw = match.group(1).strip()
    return json.loads(raw)


def generate_analysis_content(data: RiskAnalysisRequest) -> dict:
    """
    Gemini API를 호출하여 분석 텍스트 콘텐츠를 생성합니다.

    Returns:
        {
          "risk_headline": str,
          "main_analysis": str,
          "recovery_comment": str,
          "action_items": list[str]
        }

    Raises:
        ValueError: API 키 미설정
        Exception: Gemini API 호출 실패 또는 JSON 파싱 실패
    """
    if not _API_KEY:
        raise ValueError("GEMINI_API_KEY 환경 변수가 설정되지 않았습니다.")

    client = genai.Client(api_key=_API_KEY)
    prompt = _build_prompt(data)

    logger.info(f"Gemini 분석 텍스트 생성 시작 (model={_MODEL_NAME})")

    response = client.models.generate_content(
        model=_MODEL_NAME,
        contents=prompt,
        config=types.GenerateContentConfig(
            system_instruction=_SYSTEM_PROMPT,
            temperature=0.4,
            max_output_tokens=1024,
        ),
    )

    content = _extract_json(response.text)
    logger.info("Gemini 분석 텍스트 생성 완료")
    return content
