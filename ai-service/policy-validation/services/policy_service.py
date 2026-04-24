import os
import json
from openai import AsyncOpenAI

client = AsyncOpenAI(api_key=os.getenv("OPENAI_API_KEY"))

BANNED_KEYWORDS = ["마약", "총기", "불법", "도박", "음란"]


async def validate_content(content: str, content_type: str) -> dict:
    # 1차: 키워드 필터 (빠른 차단)
    for keyword in BANNED_KEYWORDS:
        if keyword in content:
            return {
                "valid": False,
                "reason": f"금지된 키워드 '{keyword}'가 포함되어 있습니다.",
                "content_type": content_type
            }

    # 2차: GPT 검증
    try:
        response = await client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {
                    "role": "system",
                    "content": (
                        "You are a content moderation assistant for a Korean second-hand marketplace. "
                        "Determine if the content is appropriate. "
                        "Return JSON: {\"valid\": true/false, \"reason\": \"Korean explanation if invalid, empty if valid\"}"
                    )
                },
                {
                    "role": "user",
                    "content": f"Content type: {content_type}\nContent: {content}"
                }
            ],
            temperature=0.1,
        )
        raw = response.choices[0].message.content.strip()
        result = json.loads(raw)
        result["content_type"] = content_type
        return result
    except Exception:
        # GPT 실패 시 통과 처리 (서비스 중단 방지)
        return {"valid": True, "reason": "", "content_type": content_type}
