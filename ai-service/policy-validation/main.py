from fastapi import FastAPI
from routers import policy

app = FastAPI(title="AI Policy Validation Service")
app.include_router(policy.router, prefix="/api/ai/policy")


@app.get("/health")
def health():
    return {"status": "UP", "service": "ai-policy"}
