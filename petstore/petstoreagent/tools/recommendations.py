"""Product recommendation tools for the PetStore Retail Agent."""

import json


def get_recommendations_by_pet(pet_type: str, category: str) -> str:
    """Get product recommendations based on pet type and category.

    :param pet_type: The type of pet (e.g., "dog", "cat", "bird", "fish").
    :param category: Product category (e.g., "food", "toys", "health", "accessories").
    :return: JSON string with recommended products.
    """
    recommendations = {
        ("dog", "food"): [
            {"product_id": "PROD-001", "name": "Organic Puppy Chow", "price": 34.99, "rating": 4.8},
            {"product_id": "PROD-002", "name": "Grain-Free Adult Kibble", "price": 49.99, "rating": 4.7},
            {"product_id": "PROD-003", "name": "Senior Dog Nutrition Plus", "price": 39.99, "rating": 4.6},
        ],
        ("cat", "toys"): [
            {"product_id": "PROD-050", "name": "Interactive Laser Pointer", "price": 19.99, "rating": 4.9},
            {"product_id": "PROD-051", "name": "Feather Wand Deluxe", "price": 14.99, "rating": 4.7},
            {"product_id": "PROD-052", "name": "Catnip Mouse 3-Pack", "price": 9.99, "rating": 4.5},
        ],
    }

    key = (pet_type.lower(), category.lower())
    products = recommendations.get(key, [{"message": "No specific recommendations found, querying Fabric for trends."}])

    return json.dumps({"pet_type": pet_type, "category": category, "recommendations": products})


def get_trending_products(time_period_days: int) -> str:
    """Get trending products based on recent sales velocity.

    :param time_period_days: Number of days to analyze for trends (e.g., 7, 30).
    :return: JSON string with trending products.
    """
    return json.dumps({
        "time_period_days": time_period_days,
        "trending": [
            {"rank": 1, "product_id": "PROD-200", "name": "Smart Pet Feeder WiFi", "sales_growth_pct": 145},
            {"rank": 2, "product_id": "PROD-088", "name": "Squeaky Bone Toy", "sales_growth_pct": 89},
            {"rank": 3, "product_id": "PROD-155", "name": "Calming Pet Bed Large", "sales_growth_pct": 67},
        ],
    })


def get_customer_favorites(customer_id: str) -> str:
    """Get personalized recommendations based on a customer's purchase history.

    :param customer_id: The customer identifier.
    :return: JSON string with personalized product suggestions.
    """
    return json.dumps({
        "customer_id": customer_id,
        "based_on": "purchase_history",
        "suggestions": [
            {"product_id": "PROD-003", "name": "Senior Dog Nutrition Plus", "reason": "You bought this 30 days ago"},
            {"product_id": "PROD-210", "name": "Joint Health Supplement", "reason": "Popular with similar customers"},
            {"product_id": "PROD-099", "name": "Waterproof Dog Jacket", "reason": "Seasonal recommendation"},
        ],
    })


recommendation_functions = [get_recommendations_by_pet, get_trending_products, get_customer_favorites]
