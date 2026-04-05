FROM python:3.11-slim

# 시스템 패키지 설치
# wkhtmltopdf: PDF 변환
# fonts-noto-cjk: 한글 폰트
# libgl1, libglib2.0-0: PyMuPDF 의존성
RUN apt-get update && apt-get install -y --no-install-recommends \
    wkhtmltopdf \
    fonts-noto-cjk \
    libgl1 \
    libglib2.0-0 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 의존성 먼저 설치 (레이어 캐시 활용)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스코드 복사
COPY . .

# 업로드 디렉토리 생성
RUN mkdir -p uploads pdf_pages

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
