import os
import json
from openai import AsyncOpenAI

client = AsyncOpenAI(api_key=os.getenv("OPENAI_API_KEY"))


async def evaluate_trust(user_id: int, reviews: list[dict], avg_rating: float) -> dict:
    reviews_str = json.dumps(reviews, ensure_ascii=False)

    response = await client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "system",
                "content": (
                    "You are a trust evaluation assistant for a second-hand marketplace. "
                    "Analyze user reviews and ratings to determine trustworthiness. "
                    "Return a JSON object with: "
                    "trust_score (0-100), trust_level (HIGH/MEDIUM/LOW/DANGER), "
                    "summary (Korean, 1-2 sentences), warning (Korean, empty string if no warning). "
                    "Respond ONLY with the JSON object."
                )
            },
            {
                "role": "user",
                "content": (
                    f"User ID: {user_id}\n"
                    f"Average rating: {avg_rating}/5\n"
                    f"Reviews: {reviews_str}"
                )
            }
        ],
        temperature=0.2,
    )

    raw = response.choices[0].message.content.strip()
    try:
        return json.loads(raw)
    except Exception:
        return {
            "trust_score": int(avg_rating * 20),
            "trust_level": "MEDIUM",
            "summary": f"평균 평점 {avg_rating:.1f}점의 사용자입니다.",
            "warning": ""
        }
