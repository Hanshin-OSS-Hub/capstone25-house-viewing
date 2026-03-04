"""
부동산 권리 분석 PDF 생성 마이크로서비스
FastAPI 진입점

실행:
    uvicorn main:app --reload --port 8000

엔드포인트:
    POST /engine/analyze       - 등기부 분석 → AnalysisResult JSON
    POST /engine/generate-pdf  - 분석 JSON → PDF 바이너리
    GET  /health               - 헬스체크
    GET  /docs                 - Swagger UI

응답 헤더 (타이밍):
    X-Process-Time      : 요청 전체 처리 시간 (초)
    X-Gemini-Time       : Gemini API 호출 시간 (초, generate-pdf만)
    X-PDF-Convert-Time  : wkhtmltopdf 변환 시간 (초, generate-pdf만)
"""
import time
from fastapi import FastAPI, Request
from fastapi.responses import Response
from routers.engine import router as engine_router

app = FastAPI(
    title="부동산 권리 분석 서비스",
    description=(
        "Java Spring Boot 메인 서버와 통신하는 FastAPI 엔진 서버.\n\n"
        "- `POST /engine/analyze` : 파싱된 등기부 → risk / recovery / diff JSON\n"
        "- `POST /engine/generate-pdf` : 분석 JSON → PDF 보고서 바이너리"
    ),
    version="1.0.0",
)


@app.middleware("http")
async def add_process_time_header(request: Request, call_next) -> Response:
    """모든 응답에 X-Process-Time 헤더 추가 (단위: 초)."""
    start = time.perf_counter()
    response = await call_next(request)
    elapsed = time.perf_counter() - start
    response.headers["X-Process-Time"] = f"{elapsed:.3f}"
    return response


# /engine 하위 엔드포인트 등록
app.include_router(engine_router)


@app.get("/health", summary="헬스체크", tags=["Infra"])
async def health_check() -> dict:
    return {"status": "ok", "service": "pdf-generator", "port": 8000}
