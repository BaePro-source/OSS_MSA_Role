import os
import json
from openai import AsyncOpenAI

client = AsyncOpenAI(api_key=os.getenv("OPENAI_API_KEY"))

DEFAULT_REVIEWS = {
    5: "거래가 매우 만족스러웠습니다. 물품 상태도 좋고 판매자분도 친절했어요. 추천합니다!",
    4: "전반적으로 만족스러운 거래였습니다. 물품 상태가 양호했어요.",
    3: "보통 수준의 거래였습니다. 물품은 설명과 비슷했습니다.",
    2: "아쉬운 점이 있었지만 거래는 완료되었습니다.",
    1: "기대와 달랐던 거래였습니다.",
}


async def complete_review(keywords: list[str], product_title: str, rating: int) -> dict:
    try:
        response = await client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {
                    "role": "system",
                    "content": (
                        "You are a review writing assistant for a Korean second-hand marketplace. "
                        "Write a natural Korean review based on the given keywords, product, and rating. "
                        "Keep it 2-3 sentences. Be honest and proportional to the rating. "
                        "Return JSON: {\"review\": \"...\", \"rating\": N}"
                    )
                },
                {
                    "role": "user",
                    "content": (
                        f"Product: {product_title}\n"
                        f"Rating: {rating}/5\n"
                        f"Keywords: {', '.join(keywords)}"
                    )
                }
            ],
            temperature=0.7,
        )
        raw = response.choices[0].message.content.strip()
        result = json.loads(raw)
        result["rating"] = max(1, min(5, int(result.get("rating", rating))))
        return result
    except Exception:
        return {"review": DEFAULT_REVIEWS.get(rating, DEFAULT_REVIEWS[3]), "rating": rating}
