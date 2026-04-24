from fastapi import APIRouter, Header
from pydantic import BaseModel
from services.recommendation_service import recommend_products, recommend_by_history

router = APIRouter()


class ChatRequest(BaseModel):
    message: str
    products: list[dict]   # product-service 에서 넘어온 물품 목록


class HistoryRequest(BaseModel):
    history: list[dict]    # trade-history-service 에서 넘어온 거래 이력
    products: list[dict]


@router.post("/chat")
async def recommend_from_chat(req: ChatRequest):
    result = await recommend_products(req.message, req.products)
    return {"recommendations": result}


@router.post("/history")
async def recommend_from_history(req: HistoryRequest):
    result = await recommend_by_history(req.history, req.products)
    return {"recommendations": result}
