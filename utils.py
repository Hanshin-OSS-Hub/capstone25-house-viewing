"""공용 유틸리티 — 여러 모듈에서 공유하는 함수"""
from __future__ import annotations

import json
import re


def fmt_krw(amount: int) -> str:
    return f"{amount:,} 원"


def extract_json(raw: str) -> dict:
    """AI 응답 텍스트에서 JSON 추출 (마크다운 코드블록 포함 대응)."""
    raw = raw.strip()
    match = re.search(r"```(?:json)?\s*([\s\S]+?)```", raw, re.IGNORECASE)
    if match:
        raw = match.group(1).strip()
    return json.loads(raw)


def build_signals_html(signals: list) -> str:
    """위험 시그널 리스트 → badge 스타일 HTML. 빈 리스트면 빈 문자열 반환."""
    html = ""
    for sig in signals:
        severity = sig.get("severity", "MEDIUM")
        explain  = sig.get("explain", "")
        if severity == "HIGH":
            badge_bg, badge_text, item_bg, item_border = "#c0392b", "고위험", "#fff5f5", "#fecaca"
        else:
            badge_bg, badge_text, item_bg, item_border = "#d35400", "주의",   "#fff8f0", "#fed7aa"
        html += (
            f'<li style="background:{item_bg};border:1px solid {item_border};'
            f'padding:0;overflow:hidden;display:table;width:100%;margin-bottom:5px;border-radius:3px;">'
            f'<span style="display:table-cell;width:54px;background:{badge_bg};color:#ffffff;'
            f'font-size:7.5pt;font-weight:700;text-align:center;padding:8px 4px;vertical-align:middle;">'
            f'{badge_text}</span>'
            f'<span style="display:table-cell;font-size:9pt;color:#374151;padding:7px 10px;'
            f'vertical-align:middle;line-height:1.6;">{explain}</span>'
            f'</li>'
        )
    return html
