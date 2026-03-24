from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from pydantic import BaseModel
import os
import shutil
import json
import uuid

from OCR import run

app = FastAPI(title="Analysis API")

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)


# =========================
# Response DTO
# =========================
class AnalyzeResponseDTO(BaseModel):
    riskLevel: str
    rawData: str
    mainReason: str
    ltvScore: int


# =========================
# risk_level 변환
# HIGH -> DANGER
# MEDIUM -> WARNING
# 나머지 -> SAFE
# =========================
def convert_risk_level(result: dict) -> str:
    level = str(result.get("risk", {}).get("risk_level", "")).upper()

    if level == "HIGH":
        return "DANGER"
    elif level == "MEDIUM":
        return "WARNING"
    else:
        return "SAFE"


# =========================
# mainReason 추출
# 우선순위:
# 1) recovery.warning_message + recovery.action
# 2) risk.signals[0].explain
# 3) 기본 문구
# =========================
def extract_main_reason(result: dict) -> str:
    recovery = result.get("recovery", {})

    warning_message = recovery.get("warning_message")
    action = recovery.get("action")

    if warning_message and action:
        return f"{warning_message} / {action}"
    if warning_message:
        return str(warning_message)
    if action:
        return str(action)

    signals = result.get("risk", {}).get("signals", [])
    if signals and len(signals) > 0:
        return str(signals[0].get("explain", "특이 위험 없음"))

    return "특이 위험 없음"


# =========================
# ltvScore 추출
# result["ltv"]["ltv"] 값을 정수로 변환
# =========================
def extract_ltv_score(result: dict) -> int:
    try:
        return int(round(float(result.get("ltv", {}).get("ltv", 0))))
    except Exception:
        return 0


# =========================
# 2) 파일 받기
# 3) 엔진 돌려서 JSON 생성
# 4) DTO 형태로 응답 반환
# =========================
@app.post("/engine/analyze", response_model=AnalyzeResponseDTO)
async def analyze_registry(
    file: UploadFile = File(...),
    deposit: int = Form(...),
    move_in_date: str = Form(...),
    fixed_date: str = Form(...)
):
    if not file.filename.lower().endswith(".pdf"):
        raise HTTPException(status_code=400, detail="PDF 파일만 업로드 가능")

    filename = f"{uuid.uuid4()}.pdf"
    save_path = os.path.join(UPLOAD_DIR, filename)

    try:
        # 2. 받은 파일을 서버에 저장
        with open(save_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # 사용자 입력값을 엔진에 전달할 형태로 구성
        tenant_info = {
            "deposit": deposit,
            "move_in_date": move_in_date,
            "fixed_date": fixed_date
        }

        # 3. 저장된 파일 + 사용자 입력값을 분석 엔진에 전달
        result = run(save_path, tenant_info=tenant_info)

        # 4. 전체 JSON은 문자열로, 핵심 값은 따로 추출해서 반환
        return {
            "riskLevel": convert_risk_level(result),
            "rawData": json.dumps(result, ensure_ascii=False),
            "mainReason": extract_main_reason(result),
            "ltvScore": extract_ltv_score(result)
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"분석 실패: {str(e)}")