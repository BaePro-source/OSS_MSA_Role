import os
import json
from openai import AsyncOpenAI

client = AsyncOpenAI(api_key=os.getenv("OPENAI_API_KEY"))


async def recommend_products(message: str, products: list[dict]) -> list[dict]:
    product_list = json.dumps(products, ensure_ascii=False)

    response = await client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "system",
                "content": (
                    "You are a second-hand marketplace recommendation assistant. "
                    "Given a user's message and a list of products, "
                    "recommend the most relevant products. "
                    "Prefer items with lower prices or better condition. "
                    "Return a JSON array of product IDs (max 5). "
                    "Respond ONLY with the JSON array, no other text."
                )
            },
            {
                "role": "user",
                "content": f"User message: {message}\n\nProducts:\n{product_list}"
            }
        ],
        temperature=0.3,
    )

    raw = response.choices[0].message.content.strip()
    try:
        recommended_ids = json.loads(raw)
        return [p for p in products if p.get("id") in recommended_ids]
    except Exception:
        return products[:5]


async def recommend_by_history(history: list[dict], products: list[dict]) -> list[dict]:
    history_str = json.dumps(history, ensure_ascii=False)
    products_str = json.dumps(products, ensure_ascii=False)

    response = await client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {
                "role": "system",
                "content": (
                    "Based on the user's purchase history, "
                    "recommend products they might be interested in. "
                    "Return a JSON array of product IDs (max 5). "
                    "Respond ONLY with the JSON array."
                )
            },
            {
                "role": "user",
                "content": f"History:\n{history_str}\n\nAvailable products:\n{products_str}"
            }
        ],
        temperature=0.3,
    )

    raw = response.choices[0].message.content.strip()
    try:
        recommended_ids = json.loads(raw)
        return [p for p in products if p.get("id") in recommended_ids]
    except Exception:
        return products[:5]
