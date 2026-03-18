"""
부동산 권리 분석 PDF 생성 마이크로서비스 — FastAPI 진입점

실행: uvicorn main:app --reload --port 8000
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


app.include_router(engine_router)


@app.get("/health", summary="헬스체크", tags=["Infra"])
async def health_check() -> dict:
    return {"status": "ok", "service": "pdf-generator", "port": 8000}
