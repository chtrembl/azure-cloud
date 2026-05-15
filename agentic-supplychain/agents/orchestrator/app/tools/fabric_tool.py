"""
Fabric Data Agent Tool
Queries the Microsoft Fabric data agent for structured supply chain analytics.

The Fabric data agent is configured as a RemoteTool connection in the
Foundry project. This module wraps the call with error handling and
provides a mock fallback for local development.

Requires: FABRIC_DATA_AGENT_ENDPOINT in environment
"""
from __future__ import annotations
import os
import logging
import httpx

from ..config import settings
from ..models import FabricResult

logger = logging.getLogger(__name__)

# Mock data for local development when Fabric is not connected
_MOCK_FABRIC_RESPONSE = {
    "suppliers_with_delays": [
        {"supplier_id": "SUP-003", "name": "Apex Manufacturing", "region": "Northeast",
         "avg_delay_days": 12.4, "late_shipment_pct": 0.68, "total_orders": 47},
        {"supplier_id": "SUP-007", "name": "Delta Components", "region": "Northeast",
         "avg_delay_days": 4.2, "late_shipment_pct": 0.22, "total_orders": 31},
    ],
    "at_risk_inventory": [
        {"warehouse": "WH-NE-01", "product": "PRD-110", "product_name": "Industrial Valve Assembly",
         "current_qty": 12, "reorder_point": 50, "days_of_supply": 2.1, "status": "CRITICAL"},
        {"warehouse": "WH-NE-01", "product": "PRD-205", "product_name": "Precision Bearing Kit",
         "current_qty": 28, "reorder_point": 40, "days_of_supply": 5.8, "status": "LOW"},
    ],
    "summary": (
        "Apex Manufacturing (SUP-003) has the highest delay rate in the Northeast at 68% late shipments "
        "with an average delay of 12.4 days across 47 orders. Warehouse WH-NE-01 has 2 products at risk: "
        "Industrial Valve Assembly is CRITICAL with only 2.1 days of supply remaining."
    ),
}


async def query_fabric_agent(question: str, user_context: dict) -> FabricResult:
    """
    Send a natural-language question to the Fabric data agent.
    Falls back to mock data if the endpoint is not configured.
    """
    endpoint = settings.fabric_data_agent_endpoint

    if not endpoint:
        logger.warning("FABRIC_DATA_AGENT_ENDPOINT not set — returning mock data")
        return FabricResult(
            success=True,
            data=_MOCK_FABRIC_RESPONSE,
            summary=_MOCK_FABRIC_RESPONSE["summary"],
        )

    try:
        # Acquire token for Fabric API
        from azure.identity import DefaultAzureCredential
        credential = DefaultAzureCredential()
        token = credential.get_token("https://api.fabric.microsoft.com/.default")

        async with httpx.AsyncClient(timeout=30.0) as client:
            # Fabric data agent uses OpenAI-compatible chat completions format
            response = await client.post(
                endpoint,
                json={
                    "messages": [
                        {"role": "user", "content": question}
                    ],
                },
                headers={
                    "Content-Type": "application/json",
                    "Authorization": f"Bearer {token.token}",
                },
            )
            response.raise_for_status()
            data = response.json()

            # Extract answer from OpenAI-style response
            summary = ""
            if "choices" in data and data["choices"]:
                summary = data["choices"][0].get("message", {}).get("content", "")
            
            return FabricResult(
                success=True,
                data=data,
                summary=summary or str(data),
            )
    except httpx.HTTPStatusError as e:
        logger.error(f"Fabric agent HTTP error: {e.response.status_code}")
        return FabricResult(success=False, error=f"HTTP {e.response.status_code}: {e.response.text}")
    except Exception as e:
        logger.error(f"Fabric agent error: {e}")
        return FabricResult(success=False, error=str(e))
