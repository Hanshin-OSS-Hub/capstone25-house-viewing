from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from pydantic import BaseModel
from typing import Any, Dict, Optional
import os
import shutil

from Capstone import run

app = FastAPI(title="Analysis API")

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)


# =========================
# Response DTO
# =========================
class AnalyzeResponseDTO(BaseModel):
    snapshot: Optional[Dict[str, Any]] = None
    valuation: Optional[Dict[str, Any]] = None
    ltv: Dict[str, Any]
    diff: Dict[str, Any]
    risk: Dict[str, Any]
    recovery: Dict[str, Any]


# =========================
# 2) 파일 받기
# 3) 엔진 돌려서 JSON 생성
# 4) JSON 응답 반환
# =========================
@app.post("/analyze", response_model=AnalyzeResponseDTO)
async def analyze_registry(
    file: UploadFile = File(...),
    deposit: int = Form(...),
    move_in_date: str = Form(...),
    fixed_date: str = Form(...)
):
    if not file.filename.lower().endswith(".pdf"):
        raise HTTPException(status_code=400, detail="PDF 파일만 업로드 가능")

    save_path = os.path.join(UPLOAD_DIR, file.filename)

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

        # 4. 분석 결과 JSON 응답
        return {
            "snapshot": result.get("snapshot"),
            "valuation": result.get("valuation"),
            "ltv": result.get("ltv"),
            "diff": result.get("diff"),
            "risk": result.get("risk"),
            "recovery": result.get("recovery"),
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"분석 실패: {str(e)}")