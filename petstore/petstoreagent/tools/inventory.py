"""Inventory management tools for the PetStore Retail Agent."""

import json


def check_stock_level(product_id: str, store_location: str) -> str:
    """Check the current stock level for a product at a specific store location.

    :param product_id: The unique product identifier (e.g., "PROD-001").
    :param store_location: The store location code (e.g., "STORE-NYC-01").
    :return: JSON string with stock level details.
    """
    # Custom business logic - this would integrate with your inventory system
    return json.dumps({
        "product_id": product_id,
        "store_location": store_location,
        "quantity_on_hand": 42,
        "reorder_point": 10,
        "status": "in_stock",
    })


def trigger_reorder(product_id: str, quantity: int, supplier_id: str) -> str:
    """Trigger a reorder for a product from a supplier.

    :param product_id: The unique product identifier.
    :param quantity: The quantity to reorder.
    :param supplier_id: The supplier to order from.
    :return: JSON string with reorder confirmation.
    """
    return json.dumps({
        "product_id": product_id,
        "quantity": quantity,
        "supplier_id": supplier_id,
        "order_reference": f"RO-{product_id}-{quantity}",
        "status": "submitted",
        "estimated_delivery_days": 3,
    })


def get_low_stock_alerts(store_location: str) -> str:
    """Get all products that are below their reorder point at a given location.

    :param store_location: The store location code.
    :return: JSON string with list of low-stock products.
    """
    return json.dumps({
        "store_location": store_location,
        "alerts": [
            {"product_id": "PROD-045", "name": "Premium Dog Food 20lb", "quantity": 3, "reorder_point": 15},
            {"product_id": "PROD-112", "name": "Cat Litter Multi-Pack", "quantity": 5, "reorder_point": 20},
        ],
    })


# Functions to register with the agent
inventory_functions = [check_stock_level, trigger_reorder, get_low_stock_alerts]
