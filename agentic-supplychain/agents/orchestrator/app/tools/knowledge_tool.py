"""
Foundry IQ / Knowledge Tool
Queries the Foundry IQ knowledge base (backed by Azure AI Search) for
policy documents, contracts, SLAs, SOPs, and procedures.

The Foundry IQ knowledge base is configured as a RemoteTool (MCP)
connection in the Foundry project. This module wraps the call with
error handling and provides a mock fallback for local development.

Requires: FOUNDRY_IQ_MCP_URL in environment
"""
from __future__ import annotations
import logging
import httpx

from ..config import settings
from ..models import KnowledgeResult

logger = logging.getLogger(__name__)

# Mock knowledge results for local development
_MOCK_KNOWLEDGE_RESPONSE = [
    {
        "source": "contracts/apex_supplier_master_agreement.md",
        "title": "Apex Manufacturing – Master Supply Agreement",
        "excerpt": (
            "Section 4.2 – Delivery Penalties: For shipments exceeding 10 calendar days past the "
            "confirmed delivery date, a penalty of 2% of the purchase order value per day applies, "
            "capped at 20%. Chronic non-performance (>50% late over any rolling 90-day window) "
            "triggers mandatory escalation to VP Supply Chain within 5 business days."
        ),
    },
    {
        "source": "policies/expedited_shipping_policy.md",
        "title": "Expedited Shipping Authorization Policy",
        "excerpt": (
            "Section 2.1 – Authorization Levels: Expedited shipping for orders exceeding $5,000 "
            "requires Director-level approval. For critical stockout situations (days of supply < 3), "
            "the Operations Director may authorize air freight at up to 3x standard shipping cost."
        ),
    },
    {
        "source": "policies/supplier_escalation_runbook.md",
        "title": "Supplier Performance Escalation Runbook",
        "excerpt": (
            "Step 3 – Escalation Threshold: If a supplier's on-time delivery rate falls below 50% "
            "in any 90-day period, initiate Tier 2 escalation: formal written notice, corrective "
            "action plan request within 10 business days, and alternate supplier qualification."
        ),
    },
    {
        "source": "policies/alternate_supplier_approval_policy.md",
        "title": "Alternate Supplier Qualification and Approval",
        "excerpt": (
            "Section 3.1 – Emergency Qualification: In stockout emergencies, pre-qualified alternate "
            "suppliers from the Approved Vendor List may be activated with Director approval and "
            "Quality Engineering sign-off within 48 hours."
        ),
    },
]


async def query_foundry_iq(question: str, user_context: dict) -> KnowledgeResult:
    """
    Query Foundry IQ for relevant knowledge documents.
    Falls back to mock data if the endpoint is not configured.
    """
    endpoint = settings.foundry_iq_mcp_url

    if not endpoint:
        logger.warning("FOUNDRY_IQ_MCP_URL not set — returning mock knowledge data")
        summary_parts = [f"• {doc['title']}: {doc['excerpt'][:100]}..." for doc in _MOCK_KNOWLEDGE_RESPONSE]
        return KnowledgeResult(
            success=True,
            documents=_MOCK_KNOWLEDGE_RESPONSE,
            summary="Relevant policies and contracts found:\n" + "\n".join(summary_parts),
        )

    try:
        async with httpx.AsyncClient(timeout=30.0) as client:
            response = await client.post(
                endpoint,
                json={
                    "query": question,
                    "top_k": 5,
                    "context": user_context,
                },
                headers={"Content-Type": "application/json"},
            )
            response.raise_for_status()
            data = response.json()

            documents = data.get("documents", data.get("results", []))
            summary = data.get("summary", "")
            if not summary and documents:
                summary_parts = [
                    f"• {doc.get('title', 'Document')}: {doc.get('excerpt', '')[:100]}..."
                    for doc in documents[:5]
                ]
                summary = "Retrieved documents:\n" + "\n".join(summary_parts)

            return KnowledgeResult(
                success=True,
                documents=documents,
                summary=summary,
            )
    except httpx.HTTPStatusError as e:
        logger.error(f"Foundry IQ HTTP error: {e.response.status_code}")
        return KnowledgeResult(success=False, error=f"HTTP {e.response.status_code}: {e.response.text}")
    except Exception as e:
        logger.error(f"Foundry IQ error: {e}")
        return KnowledgeResult(success=False, error=str(e))
