from fastapi import FastAPI
from routers import review

app = FastAPI(title="AI Review Writer Service")
app.include_router(review.router, prefix="/api/ai/review-writer")


@app.get("/health")
def health():
    return {"status": "UP", "service": "ai-review-writer"}
