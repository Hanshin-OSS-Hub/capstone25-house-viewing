"""Gemini API 클라이언트 (google-genai SDK 1.x) — 분석 텍스트 JSON 생성"""
from __future__ import annotations

import logging
import os

from dotenv import load_dotenv
from google import genai
from google.genai import types

from ai_prompt import SYSTEM_PROMPT, build_prompt
from dto import RiskAnalysisRequest
from utils import extract_json

load_dotenv()
logger = logging.getLogger(__name__)

_API_KEY: str | None = os.getenv("GEMINI_API_KEY")
_MODEL_NAME: str = os.getenv("GEMINI_MODEL", "gemini-2.0-flash")


def generate_analysis_content(data: RiskAnalysisRequest) -> dict:
    """Gemini API 호출 → {risk_headline, main_analysis, recovery_comment, action_items}"""
    if not _API_KEY:
        raise ValueError("GEMINI_API_KEY 환경 변수가 설정되지 않았습니다.")

    client = genai.Client(api_key=_API_KEY)

    logger.info(f"Gemini 분석 텍스트 생성 시작 (model={_MODEL_NAME})")

    response = client.models.generate_content(
        model=_MODEL_NAME,
        contents=build_prompt(data),
        config=types.GenerateContentConfig(
            system_instruction=SYSTEM_PROMPT,
            temperature=0.4,
            max_output_tokens=1024,
        ),
    )

    content = extract_json(response.text)
    logger.info("Gemini 분석 텍스트 생성 완료")
    return content
