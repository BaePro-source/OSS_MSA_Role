from fastapi import FastAPI
from routers import trust

app = FastAPI(title="AI Trust Evaluation Service")
app.include_router(trust.router, prefix="/api/ai/trust")


@app.get("/health")
def health():
    return {"status": "UP", "service": "ai-trust"}
