from fastapi import APIRouter
from pydantic import BaseModel
from services.policy_service import validate_content

router = APIRouter()


class ValidationRequest(BaseModel):
    content: str
    content_type: str  # "product", "chat", "review"


@router.post("/validate")
async def validate(req: ValidationRequest):
    result = await validate_content(req.content, req.content_type)
    return result
