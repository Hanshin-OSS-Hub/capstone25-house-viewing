"""OCR 파싱 검증 보고서 HTML 생성기

원본 등기부등본 이미지와 OCR 파싱 결과를 나란히 표시하여
사용자가 파싱 정확도를 직접 확인할 수 있는 PDF를 생성한다.
"""
from __future__ import annotations

import base64
import os


# ─────────────────────────────────────────────
# 내부 헬퍼
# ─────────────────────────────────────────────

def _img_b64(path: str) -> str:
    """이미지 파일을 base64 data URI로 변환"""
    abs_path = os.path.abspath(path)
    with open(abs_path, "rb") as f:
        data = base64.b64encode(f.read()).decode("utf-8")
    return f"data:image/jpeg;base64,{data}"


def _fmt_krw(amount) -> str:
    try:
        v = int(amount)
        return f"{v:,}원"
    except Exception:
        return "정보 없음"


def _safe(val, default: str = "—") -> str:
    if val is None or val == "":
        return default
    return str(val)


# ─────────────────────────────────────────────
# 스냅샷 파싱 데이터 HTML
# ─────────────────────────────────────────────

def _render_address_section(snapshot: dict) -> str:
    addr = snapshot.get("address") or {}
    address_str = _safe(addr.get("address"), "주소 파싱 실패")
    viewed_at   = _safe(snapshot.get("viewed_at"), "—")
    return f"""
<div class="data-section">
  <h3 class="section-title">📍 부동산 정보</h3>
  <table class="data-table">
    <tr><th>주소</th><td>{address_str}</td></tr>
    <tr><th>열람 일시</th><td>{viewed_at}</td></tr>
  </table>
</div>
"""


def _render_gabu_section(snapshot: dict) -> str:
    gabu = snapshot.get("gabu") or []
    if not gabu:
        return """
<div class="data-section">
  <h3 class="section-title">갑구 (소유권에 관한 사항)</h3>
  <p class="empty-msg">파싱된 갑구 데이터 없음</p>
</div>
"""
    rows = ""
    for entry in gabu:
        rank    = _safe(entry.get("rank"))
        purpose = _safe(entry.get("purpose"))
        receipt = entry.get("receipt") or {}
        date    = _safe(receipt.get("date"))
        owners  = entry.get("owners") or []
        owner_str = ", ".join(
            f"{o.get('name', '?')}({o.get('share', '')})" if o.get("share") else o.get("name", "?")
            for o in owners
        ) or "—"
        rows += f"""
<tr>
  <td class="rank">{rank}</td>
  <td>{purpose}</td>
  <td>{owner_str}</td>
  <td>{date}</td>
</tr>"""

    return f"""
<div class="data-section">
  <h3 class="section-title">갑구 (소유권에 관한 사항)</h3>
  <table class="data-table striped">
    <thead><tr><th>순위</th><th>등기목적</th><th>권리자</th><th>접수일</th></tr></thead>
    <tbody>{rows}</tbody>
  </table>
</div>
"""


def _render_eulgu_section(snapshot: dict) -> str:
    eulgu = snapshot.get("eulgu") or []
    if not eulgu:
        return """
<div class="data-section">
  <h3 class="section-title">을구 (소유권 이외의 권리)</h3>
  <p class="empty-msg">파싱된 을구 데이터 없음</p>
</div>
"""
    rows = ""
    for entry in eulgu:
        rank       = _safe(entry.get("rank"))
        purpose    = _safe(entry.get("purpose"))
        receipt    = entry.get("receipt") or {}
        date       = _safe(receipt.get("date"))
        owners     = entry.get("owners") or []
        creditor   = ", ".join(o.get("name", "?") for o in owners) or "—"
        max_claim  = entry.get("max_claim_amount")
        amount_str = _fmt_krw(max_claim) if max_claim is not None else "—"
        rows += f"""
<tr>
  <td class="rank">{rank}</td>
  <td>{purpose}</td>
  <td>{creditor}</td>
  <td>{amount_str}</td>
  <td>{date}</td>
</tr>"""

    return f"""
<div class="data-section">
  <h3 class="section-title">을구 (소유권 이외의 권리)</h3>
  <table class="data-table striped">
    <thead><tr><th>순위</th><th>등기목적</th><th>채권자/권리자</th><th>채권최고액</th><th>접수일</th></tr></thead>
    <tbody>{rows}</tbody>
  </table>
</div>
"""


# ─────────────────────────────────────────────
# 이미지 섹션 HTML
# ─────────────────────────────────────────────

def _render_images_section(image_files: list[str]) -> str:
    if not image_files:
        return '<p class="empty-msg">원본 이미지 없음</p>'

    items = ""
    for i, path in enumerate(image_files, start=1):
        try:
            src = _img_b64(path)
            items += f"""
<div class="img-wrapper">
  <p class="img-label">페이지 {i}</p>
  <img src="{src}" alt="등기부등본 {i}페이지" />
</div>"""
        except Exception:
            items += f'<p class="empty-msg">페이지 {i} 이미지 로드 실패: {path}</p>'

    return f'<div class="images-column">{items}</div>'


# ─────────────────────────────────────────────
# 메인 생성 함수
# ─────────────────────────────────────────────

_CSS = """
* { box-sizing: border-box; margin: 0; padding: 0; }
body {
  font-family: 'Noto Sans KR', 'Malgun Gothic', sans-serif;
  font-size: 11px;
  color: #1a1a2e;
  background: #f8f9fa;
}
.page-wrap {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px 16px;
}

/* ── 헤더 ── */
.report-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 60%, #0f3460 100%);
  color: #ffffff;
  border-radius: 12px;
  padding: 24px 28px;
  margin-bottom: 24px;
}
.report-header h1 { font-size: 18px; font-weight: 700; margin-bottom: 6px; }
.report-header .subtitle { font-size: 11px; color: #a8b2c1; }
.badge {
  display: inline-block;
  background: rgba(255,255,255,0.15);
  border-radius: 20px;
  padding: 3px 10px;
  font-size: 10px;
  margin-top: 8px;
}

/* ── 2단 레이아웃 ── */
.two-col {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.col-images {
  flex: 0 0 44%;
  min-width: 0;
}
.col-data {
  flex: 1;
  min-width: 0;
}

/* ── 컬럼 헤더 ── */
.col-header {
  background: #0f3460;
  color: #fff;
  border-radius: 8px 8px 0 0;
  padding: 10px 14px;
  font-size: 12px;
  font-weight: 700;
}

/* ── 이미지 ── */
.images-column {
  background: #fff;
  border: 1px solid #dde3ed;
  border-radius: 0 0 8px 8px;
  padding: 12px;
}
.img-wrapper { margin-bottom: 14px; }
.img-wrapper:last-child { margin-bottom: 0; }
.img-label {
  font-size: 10px;
  color: #6b7280;
  margin-bottom: 4px;
  font-weight: 600;
}
.images-column img {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  display: block;
}

/* ── 파싱 데이터 ── */
.data-panel {
  background: #fff;
  border: 1px solid #dde3ed;
  border-radius: 0 0 8px 8px;
  padding: 16px;
}
.data-section { margin-bottom: 20px; }
.data-section:last-child { margin-bottom: 0; }
.section-title {
  font-size: 12px;
  font-weight: 700;
  color: #0f3460;
  border-left: 3px solid #e74c3c;
  padding-left: 8px;
  margin-bottom: 10px;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 10px;
}
.data-table th, .data-table td {
  padding: 6px 8px;
  border: 1px solid #e5e7eb;
  vertical-align: top;
  word-break: break-word;
}
.data-table th {
  background: #f1f5f9;
  color: #374151;
  font-weight: 600;
  white-space: nowrap;
  width: 80px;
}
.data-table.striped tbody tr:nth-child(even) td { background: #f9fafb; }
.data-table thead tr th {
  background: #e8edf5;
  color: #1e3a5f;
  font-size: 10px;
  width: auto;
}
.rank { text-align: center; width: 32px; color: #6b7280; }
.empty-msg { font-size: 10px; color: #9ca3af; padding: 8px 0; }

/* ── 주의 배너 ── */
.notice-bar {
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 8px;
  padding: 10px 14px;
  margin-bottom: 20px;
  font-size: 10px;
  color: #856404;
}
"""

_GOOGLE_FONTS = (
    '<link rel="preconnect" href="https://fonts.googleapis.com">'
    '<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>'
    '<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;600;700&display=swap" rel="stylesheet">'
)


def generate_verification_html_report(snapshot: dict, image_files: list[str] | None = None) -> str:
    """OCR 검증 HTML 보고서 생성

    Args:
        snapshot: ocr_core.run() 반환값의 'snapshot' 키
        image_files: 절대경로 이미지 파일 목록 (None 또는 빈 리스트면 데이터만 표시)
    Returns:
        HTML 문자열
    """
    addr_html  = _render_address_section(snapshot)
    gabu_html  = _render_gabu_section(snapshot)
    eulgu_html = _render_eulgu_section(snapshot)

    gabu_count  = len(snapshot.get("gabu") or [])
    eulgu_count = len(snapshot.get("eulgu") or [])

    show_images = bool(image_files)
    total_pages = len(image_files) if image_files else 0

    if show_images:
        images_html = _render_images_section(image_files)
        layout = f"""
  <!-- 2단 레이아웃 -->
  <div class="two-col">
    <!-- 좌: 원본 이미지 -->
    <div class="col-images">
      <div class="col-header">📄 원본 등기부등본</div>
      {images_html}
    </div>
    <!-- 우: 파싱 데이터 -->
    <div class="col-data">
      <div class="col-header">📋 OCR 파싱 결과</div>
      <div class="data-panel">
        {addr_html}
        {gabu_html}
        {eulgu_html}
      </div>
    </div>
  </div>"""
        notice = "⚠️ 아래 내용은 OCR 자동 파싱 결과입니다. 좌측 원본 이미지와 우측 파싱 데이터를 직접 비교하여 오류 여부를 확인하시기 바랍니다."
        badge_pages = f'<span class="badge">총 {total_pages}페이지</span>'
    else:
        layout = f"""
  <!-- 단일 컬럼 레이아웃 (이미지 없음) -->
  <div class="col-header">📋 OCR 파싱 결과</div>
  <div class="data-panel">
    {addr_html}
    {gabu_html}
    {eulgu_html}
  </div>"""
        notice = "⚠️ 아래 내용은 OCR 자동 파싱 결과입니다. 원본 등기부등본과 비교하여 오류 여부를 확인하시기 바랍니다."
        badge_pages = ""

    return f"""<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>OCR 파싱 검증 보고서</title>
  {_GOOGLE_FONTS}
  <style>{_CSS}</style>
</head>
<body>
<div class="page-wrap">

  <!-- 헤더 -->
  <div class="report-header">
    <h1>🔍 OCR 파싱 검증 보고서</h1>
    <div class="subtitle">원본 이미지와 파싱 결과를 비교하여 정확도를 확인하세요.</div>
    {badge_pages}
    <span class="badge">갑구 {gabu_count}건</span>
    <span class="badge">을구 {eulgu_count}건</span>
  </div>

  <!-- 주의 안내 -->
  <div class="notice-bar">{notice}</div>

  {layout}

</div>
</body>
</html>"""
