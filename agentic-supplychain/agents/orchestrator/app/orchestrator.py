"""
Agentic Supply Chain Orchestrator – Routing and Synthesis
Core logic: classify queries, dispatch to tools, synthesize grounded answers.
"""
from __future__ import annotations
import re
from typing import Optional

from .models import (
    QueryRoute,
    FabricResult,
    KnowledgeResult,
    SynthesizedResponse,
)
from .tools.fabric_tool import query_fabric_agent
from .tools.knowledge_tool import query_foundry_iq
from .tools.synthesis import synthesize_response


# Keywords that suggest structured data queries (Fabric)
_STRUCTURED_KEYWORDS = [
    "supplier", "shipment", "delay", "inventory", "stockout", "warehouse",
    "purchase order", "lead time", "on-time", "delivery", "quantity",
    "product", "region", "northeast", "ranking", "top", "worst",
]

# Keywords that suggest knowledge/policy queries (Foundry IQ)
_KNOWLEDGE_KEYWORDS = [
    "policy", "contract", "sla", "sop", "procedure", "escalat",
    "penalty", "approval", "expedit", "alternate supplier", "playbook",
    "runbook", "agreement", "terms", "receiving", "shortage",
]


def classify_query(question: str) -> QueryRoute:
    """Classify a user question as structured, knowledge, or mixed."""
    q_lower = question.lower()

    has_structured = any(kw in q_lower for kw in _STRUCTURED_KEYWORDS)
    has_knowledge = any(kw in q_lower for kw in _KNOWLEDGE_KEYWORDS)

    if has_structured and has_knowledge:
        return QueryRoute(
            route="mixed",
            reasoning="Question references both data metrics and policies/contracts.",
        )
    elif has_knowledge:
        return QueryRoute(
            route="knowledge",
            reasoning="Question focuses on policies, contracts, or procedures.",
        )
    elif has_structured:
        return QueryRoute(
            route="structured",
            reasoning="Question focuses on operational data and metrics.",
        )
    else:
        # Default to mixed for comprehensive coverage
        return QueryRoute(
            route="mixed",
            reasoning="Ambiguous question — querying both sources for completeness.",
        )


async def handle_query(user_query: str, user_context: dict | None = None) -> SynthesizedResponse:
    """
    Main orchestration entry point.
    1. Classify the question
    2. Dispatch to Fabric and/or Foundry IQ
    3. Synthesize a grounded response
    """
    user_context = user_context or {}
    route = classify_query(user_query)

    fabric_result: Optional[FabricResult] = None
    kb_result: Optional[KnowledgeResult] = None

    if route.route in ("structured", "mixed"):
        fabric_result = await query_fabric_agent(user_query, user_context)

    if route.route in ("knowledge", "mixed"):
        kb_result = await query_foundry_iq(user_query, user_context)

    return synthesize_response(
        question=user_query,
        route=route.route,
        fabric_result=fabric_result,
        knowledge_result=kb_result,
    )
