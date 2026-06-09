"""Unit tests for PetStore Agent custom tools."""

import json

from tools.inventory import check_stock_level, get_low_stock_alerts, trigger_reorder
from tools.orders import calculate_shipping_estimate, lookup_order, process_return
from tools.recommendations import get_customer_favorites, get_recommendations_by_pet, get_trending_products


class TestInventoryTools:
    def test_check_stock_level_returns_valid_json(self):
        result = json.loads(check_stock_level("PROD-001", "STORE-NYC-01"))
        assert result["product_id"] == "PROD-001"
        assert result["store_location"] == "STORE-NYC-01"
        assert "quantity_on_hand" in result
        assert result["status"] == "in_stock"

    def test_trigger_reorder_returns_confirmation(self):
        result = json.loads(trigger_reorder("PROD-045", 50, "SUP-001"))
        assert result["status"] == "submitted"
        assert result["quantity"] == 50
        assert "order_reference" in result

    def test_get_low_stock_alerts_returns_alerts(self):
        result = json.loads(get_low_stock_alerts("STORE-NYC-01"))
        assert result["store_location"] == "STORE-NYC-01"
        assert len(result["alerts"]) > 0


class TestOrderTools:
    def test_lookup_order_returns_details(self):
        result = json.loads(lookup_order("ORD-2025-00123"))
        assert result["order_id"] == "ORD-2025-00123"
        assert result["status"] == "shipped"
        assert len(result["items"]) > 0

    def test_process_return_authorizes(self):
        result = json.loads(process_return("ORD-001", "PROD-001", "damaged"))
        assert result["status"] == "authorized"
        assert "return_id" in result

    def test_calculate_shipping_returns_options(self):
        result = json.loads(calculate_shipping_estimate("10001", 5.5))
        assert len(result["options"]) == 3
        assert result["options"][0]["method"] == "Standard"


class TestRecommendationTools:
    def test_get_recommendations_by_pet_known(self):
        result = json.loads(get_recommendations_by_pet("dog", "food"))
        assert result["pet_type"] == "dog"
        assert len(result["recommendations"]) == 3

    def test_get_recommendations_by_pet_unknown(self):
        result = json.loads(get_recommendations_by_pet("hamster", "toys"))
        assert "recommendations" in result

    def test_get_trending_products(self):
        result = json.loads(get_trending_products(7))
        assert result["time_period_days"] == 7
        assert result["trending"][0]["rank"] == 1

    def test_get_customer_favorites(self):
        result = json.loads(get_customer_favorites("CUST-001"))
        assert result["customer_id"] == "CUST-001"
        assert len(result["suggestions"]) > 0
