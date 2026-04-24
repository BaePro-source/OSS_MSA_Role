from fastapi import FastAPI
from routers import recommendation

app = FastAPI(title="AI Recommendation Service")
app.include_router(recommendation.router, prefix="/api/ai/recommend")


@app.get("/health")
def health():
    return {"status": "UP", "service": "ai-recommendation"}
