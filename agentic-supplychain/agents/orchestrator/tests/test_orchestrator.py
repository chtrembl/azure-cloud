"""
Tests for the Agentic Supply Chain Orchestrator
Covers query classification, synthesis formatting, and API endpoints.
"""
import pytest
from httpx import AsyncClient, ASGITransport

from app.orchestrator import classify_query, handle_query
from app.models import QueryRoute, SynthesizedResponse
from app.main import app


# ── Query Classification Tests ──────────────────────────────


class TestClassifyQuery:
    def test_structured_query(self):
        route = classify_query("Which suppliers have the most delayed shipments?")
        assert route.route == "structured"

    def test_knowledge_query(self):
        route = classify_query("What is the escalation policy for late deliveries?")
        assert route.route == "knowledge"

    def test_mixed_query(self):
        route = classify_query(
            "Which suppliers are causing the most delay in the Northeast, "
            "and what contract penalties apply?"
        )
        assert route.route == "mixed"

    def test_ambiguous_defaults_to_mixed(self):
        route = classify_query("What should we do about the current situation?")
        assert route.route == "mixed"

    def test_inventory_is_structured(self):
        route = classify_query("What inventory is at risk of stockout?")
        assert route.route == "structured"

    def test_policy_is_knowledge(self):
        route = classify_query("What is the expedited shipping approval process?")
        assert route.route == "knowledge"

    def test_contract_penalty_plus_supplier_is_mixed(self):
        route = classify_query(
            "Which suppliers have delivery penalties in their contracts "
            "and what are their current delay metrics?"
        )
        assert route.route == "mixed"


# ── Orchestrator Integration Tests ──────────────────────────


class TestHandleQuery:
    @pytest.mark.asyncio
    async def test_structured_query_returns_findings(self):
        result = await handle_query("Which suppliers have the worst delivery delays?")
        assert isinstance(result, SynthesizedResponse)
        assert result.route == "structured"
        assert result.findings  # Not empty
        assert result.recommended_actions  # Has at least one action

    @pytest.mark.asyncio
    async def test_knowledge_query_returns_policies(self):
        result = await handle_query("What is the escalation policy for contract penalties?")
        assert isinstance(result, SynthesizedResponse)
        assert result.route == "knowledge"
        assert result.policy_implications  # Not empty

    @pytest.mark.asyncio
    async def test_mixed_query_returns_both(self):
        result = await handle_query(
            "Which suppliers are causing delays and what contract penalties apply?"
        )
        assert isinstance(result, SynthesizedResponse)
        assert result.route == "mixed"
        assert result.findings
        assert result.policy_implications
        assert len(result.recommended_actions) > 0

    @pytest.mark.asyncio
    async def test_response_has_supporting_evidence(self):
        result = await handle_query("What inventory is at risk and what policies apply?")
        assert result.supporting_evidence  # Has references


# ── Synthesis Formatting Tests ──────────────────────────────


class TestSynthesisFormatting:
    @pytest.mark.asyncio
    async def test_critical_inventory_generates_urgent_action(self):
        result = await handle_query("What inventory is at critical stockout risk?")
        urgent_actions = [a for a in result.recommended_actions if "URGENT" in a]
        assert len(urgent_actions) > 0, "Critical inventory should produce URGENT actions"

    @pytest.mark.asyncio
    async def test_actions_are_strings(self):
        result = await handle_query("What should operations do about supplier delays?")
        for action in result.recommended_actions:
            assert isinstance(action, str)
            assert len(action) > 10  # Not empty/trivial


# ── API Endpoint Tests ──────────────────────────────────────


class TestAPI:
    @pytest.mark.asyncio
    async def test_health_endpoint(self):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.get("/health")
            assert response.status_code == 200
            data = response.json()
            assert data["status"] == "ok"

    @pytest.mark.asyncio
    async def test_query_endpoint(self):
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/query",
                json={"question": "Which suppliers have the most delays?"},
            )
            assert response.status_code == 200
            data = response.json()
            assert "findings" in data
            assert "recommended_actions" in data
