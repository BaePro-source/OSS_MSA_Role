from fastapi import APIRouter
from pydantic import BaseModel
from services.trust_service import evaluate_trust

router = APIRouter()


class TrustRequest(BaseModel):
    target_user_id: int
    reviews: list[dict]     # review-service 에서 넘어온 리뷰 목록
    average_rating: float


@router.post("/evaluate")
async def evaluate(req: TrustRequest):
    result = await evaluate_trust(req.target_user_id, req.reviews, req.average_rating)
    return result
