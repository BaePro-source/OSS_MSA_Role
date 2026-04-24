from fastapi import APIRouter
from pydantic import BaseModel
from services.review_service import complete_review

router = APIRouter()


class ReviewDraftRequest(BaseModel):
    keywords: list[str]         # 사용자가 선택한 키워드
    product_title: str
    rating: int                  # 1~5


@router.post("/complete")
async def complete(req: ReviewDraftRequest):
    result = await complete_review(req.keywords, req.product_title, req.rating)
    return result
