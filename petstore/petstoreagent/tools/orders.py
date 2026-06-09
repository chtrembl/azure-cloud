"""Order processing tools for the PetStore Retail Agent."""

import json
from datetime import datetime, timedelta


def lookup_order(order_id: str) -> str:
    """Look up the status and details of a customer order.

    :param order_id: The order identifier (e.g., "ORD-2025-00123").
    :return: JSON string with order details.
    """
    return json.dumps({
        "order_id": order_id,
        "status": "shipped",
        "items": [
            {"product_id": "PROD-001", "name": "Organic Puppy Chow", "quantity": 2, "unit_price": 34.99},
            {"product_id": "PROD-088", "name": "Squeaky Bone Toy", "quantity": 1, "unit_price": 12.99},
        ],
        "total": 82.97,
        "shipping_carrier": "FedEx",
        "tracking_number": "FX-789456123",
        "estimated_delivery": (datetime.now() + timedelta(days=2)).isoformat(),
    })


def process_return(order_id: str, product_id: str, reason: str) -> str:
    """Initiate a return for a product from an order.

    :param order_id: The original order identifier.
    :param product_id: The product to return.
    :param reason: The reason for the return.
    :return: JSON string with return authorization details.
    """
    return json.dumps({
        "return_id": f"RET-{order_id}-{product_id}",
        "order_id": order_id,
        "product_id": product_id,
        "reason": reason,
        "status": "authorized",
        "refund_amount": 34.99,
        "return_label_url": "https://petstore.example.com/returns/label/RET-001",
    })


def calculate_shipping_estimate(zip_code: str, weight_lbs: float) -> str:
    """Calculate shipping cost and delivery estimate for a given destination.

    :param zip_code: The destination ZIP code.
    :param weight_lbs: The total weight in pounds.
    :return: JSON string with shipping options.
    """
    return json.dumps({
        "zip_code": zip_code,
        "weight_lbs": weight_lbs,
        "options": [
            {"method": "Standard", "cost": 5.99, "days": 5},
            {"method": "Express", "cost": 12.99, "days": 2},
            {"method": "Next Day", "cost": 24.99, "days": 1},
        ],
    })


order_functions = [lookup_order, process_return, calculate_shipping_estimate]
