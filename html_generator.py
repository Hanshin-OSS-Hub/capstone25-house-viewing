"""
HTML 보고서 생성 모듈

구조:
  generate_html_report()  ← 공개 API (routers/engine.py에서 호출)
      ├─ generate_analysis_content()  ← Gemini가 텍스트 JSON 생성
      │       실패 시 _fallback_content() 로 대체
      └─ _render_template()           ← 고정 템플릿에 텍스트 삽입 → HTML 반환
"""
from __future__ import annotations

import logging
import os
from datetime import datetime

from dto import RiskAnalysisRequest

logger = logging.getLogger(__name__)

_FALLBACK_ENABLED = os.getenv("GEMINI_FALLBACK_ENABLED", "false").lower() != "false"

# ── 위험 등급 메타데이터 ───────────────────────────────────────
_RISK_META: dict[str, dict] = {
    "High": {
        "label": "HIGH · 위험",
        "color": "#c0392b",
        "light": "#fff5f5",
        "border": "#e74c3c",
        "icon": "&#9888;",   # ⚠
        "bar_color": "#c0392b",
    },
    "Medium": {
        "label": "MEDIUM · 주의",
        "color": "#d35400",
        "light": "#fff8f0",
        "border": "#e67e22",
        "icon": "&#9685;",   # ◕
        "bar_color": "#e67e22",
    },
    "Low": {
        "label": "LOW · 안전",
        "color": "#1e8449",
        "light": "#f0fff4",
        "border": "#27ae60",
        "icon": "&#10003;",  # ✓
        "bar_color": "#27ae60",
    },
}


def _fallback_content(data: RiskAnalysisRequest) -> dict:
    """Gemini 호출 실패 시 기본 텍스트 콘텐츠."""
    ltv = data.ltv_percent
    if ltv > 80:
        headline = "선순위 채권 과다 — 보증금 손실 위험"
        main = (
            f"현재 LTV(주택담보대출비율)가 {ltv:.1f}%로 안전 기준(80%)을 초과합니다. "
            "선순위 근저당 채권액이 임차인의 보증금 회수 가능 금액을 크게 압박하고 있습니다. "
            "경매 진행 시 낙찰가에서 선순위 채권이 먼저 변제되므로, "
            "임차인 보증금의 전액 회수가 어려울 수 있습니다."
        )
    elif ltv > 60:
        headline = "부분 손실 가능 — 면밀한 검토 필요"
        main = (
            f"LTV가 {ltv:.1f}%로 주의 구간(60~80%)에 해당합니다. "
            "경매 진행 시 임차인 보증금의 일부가 회수되지 않을 수 있습니다. "
            "선순위 채권 내용과 최우선 변제 요건을 반드시 확인하세요."
        )
    else:
        headline = "보증금 회수 가능성 높음"
        main = (
            f"LTV가 {ltv:.1f}%로 안전 구간(60% 이하)에 해당합니다. "
            "현재 시세 대비 채권 비율이 낮아 보증금 회수 가능성이 상대적으로 높습니다. "
            "단, 부동산 시장 변동에 따라 상황이 달라질 수 있으니 정기적으로 확인하세요."
        )
    return {
        "risk_headline": headline,
        "main_analysis": main,
        "recovery_comment": (
            f"경매 배당 분석 결과, 임차인이 회수할 수 있는 예상 금액은 "
            f"{data.expected_recovery_amount:,}원입니다. "
            "이 금액은 현재 시세와 선순위 채권 현황을 기반으로 산출된 추정치로, "
            "실제 낙찰가에 따라 달라질 수 있습니다."
        ),
        "action_items": data.checklist,
    }


def _fmt_krw(amount: int) -> str:
    return f"{amount:,} 원"


def _render_template(data: RiskAnalysisRequest, content: dict) -> str:
    """고정 HTML 템플릿에 데이터와 Gemini 텍스트를 삽입하여 반환."""
    meta = _RISK_META[data.risk_score]
    now = datetime.now().strftime("%Y년 %m월 %d일  %H:%M")

    ltv = data.ltv_percent
    ltv_bar = min(ltv, 100)
    ltv_color = meta["bar_color"]

    # LTV 기준선 위치 (60%, 80%)
    action_items_html = "".join(
        f'<li><span class="bullet">&#10148;</span>{item}</li>'
        for item in content.get("action_items", data.checklist)
    )

    checklist_html = "".join(
        f'<li><span class="chk-icon">&#10007;</span>{item}</li>'
        for item in data.checklist
    )

    return f"""<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>부동산 권리 분석 보고서</title>
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;600;700&display=swap" rel="stylesheet"/>
  <style>
    /* ── Reset ── */
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}

    body {{
      font-family: "Noto Sans KR", "Malgun Gothic", "맑은 고딕", "Apple SD Gothic Neo", Arial, sans-serif;
      font-size: 10pt;
      color: #1c2333;
      background: #ffffff;
      -webkit-print-color-adjust: exact;
    }}

    /* ── 페이지 ── */
    .page {{
      width: 100%;
      max-width: 680px;
      margin: 0 auto;
      padding: 28px 32px 44px;
    }}

    /* ── 최상단 워터마크 바 ── */
    .top-bar {{
      background: #1c2333;
      color: #ffffff;
      padding: 6px 14px;
      font-size: 8pt;
      letter-spacing: 1.5px;
      margin-bottom: 24px;
      display: table;
      width: 100%;
    }}
    .top-bar-left {{ display: table-cell; }}
    .top-bar-right {{ display: table-cell; text-align: right; color: #8d97aa; }}

    /* ── 헤더 ── */
    .header {{
      display: table;
      width: 100%;
      border-bottom: 3px solid #1c2333;
      padding-bottom: 14px;
      margin-bottom: 20px;
    }}
    .header-title {{ display: table-cell; vertical-align: middle; }}
    .header-title h1 {{
      font-size: 19pt;
      font-weight: 700;
      color: #1c2333;
      letter-spacing: -0.5px;
    }}
    .header-title .subtitle {{
      font-size: 9pt;
      color: #6b7280;
      margin-top: 3px;
    }}
    .header-badge {{ display: table-cell; text-align: right; vertical-align: middle; }}
    .badge {{
      display: inline-block;
      background: {meta["color"]};
      color: #ffffff;
      font-size: 11pt;
      font-weight: 700;
      padding: 8px 20px;
      border-radius: 4px;
      letter-spacing: 0.3px;
    }}

    /* ── 섹션 공통 ── */
    .section {{ margin-bottom: 18px; }}
    .section-title {{
      font-size: 9.5pt;
      font-weight: 700;
      color: #1c2333;
      text-transform: uppercase;
      letter-spacing: 0.8px;
      border-bottom: 1.5px solid #e5e7eb;
      padding-bottom: 5px;
      margin-bottom: 10px;
    }}

    /* ── 기본 정보 ── */
    .info-table {{ width: 100%; border-collapse: collapse; }}
    .info-table td {{
      padding: 7px 10px;
      font-size: 9.5pt;
      border-bottom: 1px solid #f3f4f6;
    }}
    .info-table td.label {{
      width: 24%;
      font-weight: 600;
      color: #374151;
      background: #f9fafb;
      border-right: 1px solid #e5e7eb;
    }}
    .info-table td.value {{ color: #1c2333; }}

    /* ── 위험 요약 카드 ── */
    .risk-card {{
      background: {meta["light"]};
      border: 1.5px solid {meta["border"]};
      border-left: 5px solid {meta["color"]};
      border-radius: 4px;
      padding: 14px 18px;
    }}
    .risk-headline {{
      font-size: 11.5pt;
      font-weight: 700;
      color: {meta["color"]};
      margin-bottom: 8px;
    }}
    .risk-body {{
      font-size: 9.5pt;
      line-height: 1.8;
      color: #374151;
    }}

    /* ── LTV 게이지 ── */
    .ltv-row {{
      display: table;
      width: 100%;
      margin-bottom: 6px;
    }}
    .ltv-label {{ display: table-cell; font-size: 9pt; color: #6b7280; }}
    .ltv-value {{
      display: table-cell;
      text-align: right;
      font-size: 13pt;
      font-weight: 700;
      color: {ltv_color};
    }}
    .ltv-track {{
      background: #e5e7eb;
      border-radius: 3px;
      height: 10px;
      position: relative;
      margin-bottom: 4px;
    }}
    .ltv-fill {{
      background: {ltv_color};
      width: {ltv_bar:.1f}%;
      height: 100%;
      border-radius: 3px;
    }}
    .ltv-markers {{
      position: relative;
      height: 14px;
      font-size: 7.5pt;
      color: #9ca3af;
    }}
    .marker-60 {{ position: absolute; left: 60%; border-left: 1px dashed #9ca3af; height: 6px; top: -16px; }}
    .marker-80 {{ position: absolute; left: 80%; border-left: 1px dashed #9ca3af; height: 6px; top: -16px; }}
    .marker-60-label {{ position: absolute; left: 58%; top: 0; }}
    .marker-80-label {{ position: absolute; left: 78%; top: 0; }}
    .ltv-legend {{
      font-size: 7.5pt;
      color: #9ca3af;
      text-align: right;
      margin-top: 2px;
    }}

    /* ── 두 컬럼 레이아웃 ── */
    .two-col {{ display: table; width: 100%; border-spacing: 10px; }}
    .col-left {{ display: table-cell; width: 55%; vertical-align: top; padding-right: 10px; }}
    .col-right {{ display: table-cell; width: 45%; vertical-align: top; }}

    /* ── 배당금 카드 ── */
    .recovery-card {{
      background: #1c2333;
      color: #ffffff;
      border-radius: 6px;
      padding: 16px 18px;
    }}
    .recovery-card .card-label {{
      font-size: 8.5pt;
      color: #8d97aa;
      margin-bottom: 6px;
    }}
    .recovery-card .amount {{
      font-size: 16pt;
      font-weight: 700;
      color: #ffffff;
      margin-bottom: 10px;
      letter-spacing: -0.5px;
    }}
    .recovery-card .comment {{
      font-size: 8.5pt;
      line-height: 1.7;
      color: #c5cad5;
      border-top: 1px solid #3a4255;
      padding-top: 8px;
    }}

    /* ── 조치사항 ── */
    .action-list {{ list-style: none; padding: 0; }}
    .action-list li {{
      display: table;
      width: 100%;
      padding: 7px 10px;
      margin-bottom: 5px;
      background: #fff8f0;
      border: 1px solid #fed7aa;
      border-radius: 3px;
      font-size: 9.5pt;
    }}
    .bullet {{
      display: table-cell;
      width: 16px;
      color: #d35400;
      font-size: 9pt;
      padding-right: 6px;
      vertical-align: top;
    }}

    /* ── 체크리스트 ── */
    .check-list {{ list-style: none; padding: 0; }}
    .check-list li {{
      display: table;
      width: 100%;
      padding: 6px 10px;
      margin-bottom: 4px;
      background: #fff5f5;
      border: 1px solid #fecaca;
      border-radius: 3px;
      font-size: 9pt;
    }}
    .chk-icon {{
      display: table-cell;
      width: 14px;
      color: #c0392b;
      font-weight: 700;
      padding-right: 6px;
    }}

    /* ── 면책 조항 ── */
    .disclaimer {{
      margin-top: 24px;
      padding: 10px 14px;
      background: #f9fafb;
      border: 1px solid #e5e7eb;
      border-radius: 3px;
      font-size: 7.5pt;
      color: #9ca3af;
      line-height: 1.7;
    }}

    /* ── 푸터 ── */
    .footer {{
      display: table;
      width: 100%;
      margin-top: 14px;
      font-size: 7.5pt;
      color: #d1d5db;
      border-top: 1px solid #e5e7eb;
      padding-top: 8px;
    }}
    .footer-left {{ display: table-cell; }}
    .footer-right {{ display: table-cell; text-align: right; }}
  </style>
</head>
<body>
<div class="page">

  <!-- 상단 바 -->
  <div class="top-bar">
    <div class="top-bar-left">부동산 권리 분석 보고서 &nbsp;·&nbsp; REAL ESTATE RIGHTS ANALYSIS REPORT</div>
    <div class="top-bar-right">CONFIDENTIAL</div>
  </div>

  <!-- 헤더 -->
  <div class="header">
    <div class="header-title">
      <h1>권리 분석 보고서</h1>
      <div class="subtitle">임차인 보증금 위험도 평가 &nbsp;|&nbsp; {now} 생성</div>
    </div>
    <div class="header-badge">
      <span class="badge">{meta["icon"]} &nbsp;{meta["label"]}</span>
    </div>
  </div>

  <!-- 1. 기본 정보 -->
  <div class="section">
    <div class="section-title">기본 정보</div>
    <table class="info-table">
      <tr>
        <td class="label">임차인 성명</td>
        <td class="value">{data.user_name}</td>
        <td class="label" style="border-left:1px solid #e5e7eb;">위험 등급</td>
        <td class="value" style="color:{meta["color"]};font-weight:700;">{data.risk_score} — {meta["label"].split("·")[1].strip()}</td>
      </tr>
      <tr>
        <td class="label">소재지</td>
        <td class="value" colspan="3">{data.address}</td>
      </tr>
    </table>
  </div>

  <!-- 2. 핵심 위험 분석 -->
  <div class="section">
    <div class="section-title">핵심 위험 분석</div>
    <div class="risk-card">
      <div class="risk-headline">{meta["icon"]} &nbsp;{content.get("risk_headline", data.analysis_summary[:30])}</div>
      <div class="risk-body">{content.get("main_analysis", data.analysis_summary)}</div>
    </div>
  </div>

  <!-- 3. LTV 위험도 + 배당금 (2컬럼) -->
  <div class="section">
    <div class="two-col">
      <div class="col-left">
        <div class="section-title">LTV 위험도</div>
        <div class="ltv-row">
          <div class="ltv-label">Loan-to-Value 비율</div>
          <div class="ltv-value">{ltv:.1f}%</div>
        </div>
        <div class="ltv-track">
          <div class="ltv-fill"></div>
          <div class="marker-60"></div>
          <div class="marker-80"></div>
        </div>
        <div class="ltv-markers">
          <span class="marker-60-label">60%</span>
          <span class="marker-80-label">80%</span>
        </div>
        <div class="ltv-legend">&#9632; 안전 &nbsp; &#9632; 주의 &nbsp; &#9632; 위험</div>
      </div>
      <div class="col-right">
        <div class="section-title">예상 배당금</div>
        <div class="recovery-card">
          <div class="card-label">경매 배당 시 임차인 추정 회수액</div>
          <div class="amount">{_fmt_krw(data.expected_recovery_amount)}</div>
          <div class="comment">{content.get("recovery_comment", "경매 낙찰 후 선순위 채권 변제 후 임차인에게 배당될 예상 금액입니다.")}</div>
        </div>
      </div>
    </div>
  </div>

  <!-- 4. 즉시 조치사항 -->
  <div class="section">
    <div class="section-title">즉시 조치사항</div>
    <ul class="action-list">{action_items_html}</ul>
  </div>

  <!-- 5. 주의사항 체크리스트 -->
  <div class="section">
    <div class="section-title">주의사항 체크리스트</div>
    <ul class="check-list">{checklist_html}</ul>
  </div>

  <!-- 면책 조항 -->
  <div class="disclaimer">
    ※ 본 보고서는 AI 기반 자동 분석 시스템에 의해 생성된 참고 자료이며 법적 효력이 없습니다.
    실제 계약 전 반드시 법무사 또는 공인중개사의 전문 자문을 받으시기 바랍니다.
    분석 결과에 따른 모든 법적·재산적 책임은 이용자 본인에게 있습니다.
  </div>

  <!-- 푸터 -->
  <div class="footer">
    <div class="footer-left">부동산 권리 분석 서비스 &copy; 2025</div>
    <div class="footer-right">생성: {now} &nbsp;|&nbsp; AI-Powered Analysis</div>
  </div>

</div>
</body>
</html>"""


def generate_html_report(data: RiskAnalysisRequest) -> str:
    """
    공개 API: Gemini 텍스트 + 고정 템플릿으로 HTML 보고서 생성.
    Gemini 실패 시 기본 텍스트(_fallback_content)로 자동 대체.
    """
    try:
        from gemini_client import generate_analysis_content
        content = generate_analysis_content(data)
        logger.info("Gemini 콘텐츠 생성 성공")
    except Exception as exc:
        if _FALLBACK_ENABLED:
            logger.warning(f"Gemini 실패 → 기본 텍스트로 대체: {exc}")
            content = _fallback_content(data)
        else:
            raise

    return _render_template(data, content)
