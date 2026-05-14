"""
Synthesis Tool
Combines Fabric structured results and Foundry IQ knowledge results
into a single grounded response with findings, policy implications,
recommended actions, and supporting evidence.
"""
from __future__ import annotations
from typing import Optional

from ..models import FabricResult, KnowledgeResult, SynthesizedResponse


def synthesize_response(
    question: str,
    route: str,
    fabric_result: Optional[FabricResult] = None,
    knowledge_result: Optional[KnowledgeResult] = None,
) -> SynthesizedResponse:
    """
    Merge structured and knowledge results into a cohesive answer.
    """
    findings = _extract_findings(fabric_result)
    policy_implications = _extract_policy_implications(knowledge_result)
    actions = _derive_actions(fabric_result, knowledge_result)
    evidence = _collect_evidence(fabric_result, knowledge_result)

    return SynthesizedResponse(
        question=question,
        route=route,
        findings=findings,
        policy_implications=policy_implications,
        recommended_actions=actions,
        supporting_evidence=evidence,
        raw_fabric=fabric_result.data if fabric_result and fabric_result.success else None,
        raw_knowledge=[d for d in knowledge_result.documents] if knowledge_result and knowledge_result.success else None,
    )


def _extract_findings(result: Optional[FabricResult]) -> str:
    """Extract key findings from Fabric data."""
    if not result or not result.success:
        return "No structured data available." if not result else f"Data retrieval error: {result.error}"
    return result.summary or "Structured data retrieved; see raw data for details."


def _extract_policy_implications(result: Optional[KnowledgeResult]) -> str:
    """Extract policy implications from knowledge results."""
    if not result or not result.success:
        return "No policy data available." if not result else f"Knowledge retrieval error: {result.error}"
    return result.summary or "Policy documents retrieved; see supporting evidence."


def _derive_actions(
    fabric_result: Optional[FabricResult],
    knowledge_result: Optional[KnowledgeResult],
) -> list[str]:
    """Derive recommended actions from combined results."""
    actions = []

    if fabric_result and fabric_result.success:
        data = fabric_result.data

        # Check for critical inventory
        at_risk = data.get("at_risk_inventory", [])
        critical_items = [i for i in at_risk if i.get("status") == "CRITICAL"]
        if critical_items:
            for item in critical_items:
                actions.append(
                    f"URGENT: Initiate expedited replenishment for {item.get('product_name', 'unknown product')} "
                    f"at {item.get('warehouse', 'unknown warehouse')} — "
                    f"only {item.get('days_of_supply', '?')} days of supply remaining."
                )

        # Check for chronic delay suppliers
        delayed = data.get("suppliers_with_delays", [])
        chronic = [s for s in delayed if s.get("late_shipment_pct", 0) > 0.5]
        if chronic:
            for supplier in chronic:
                actions.append(
                    f"Escalate supplier {supplier.get('name', 'unknown')} ({supplier.get('supplier_id', '')}) — "
                    f"{supplier.get('late_shipment_pct', 0):.0%} late delivery rate "
                    f"exceeds escalation threshold."
                )

    if knowledge_result and knowledge_result.success:
        docs = knowledge_result.documents
        has_penalty = any("penalty" in d.get("excerpt", "").lower() for d in docs)
        has_expedite = any("expedit" in d.get("excerpt", "").lower() for d in docs)
        has_alternate = any("alternate" in d.get("excerpt", "").lower() for d in docs)

        if has_penalty:
            actions.append("Review applicable delivery penalty clauses and assess financial exposure.")
        if has_expedite:
            actions.append("Obtain Director approval for expedited shipping on critical stockout items.")
        if has_alternate:
            actions.append("Evaluate pre-qualified alternate suppliers from the Approved Vendor List.")

    if not actions:
        actions.append("No immediate actions identified — continue monitoring supply chain metrics.")

    return actions


def _collect_evidence(
    fabric_result: Optional[FabricResult],
    knowledge_result: Optional[KnowledgeResult],
) -> list[str]:
    """Collect supporting evidence references."""
    evidence = []

    if fabric_result and fabric_result.success:
        evidence.append(f"Fabric data agent: {fabric_result.summary[:200]}")

    if knowledge_result and knowledge_result.success:
        for doc in knowledge_result.documents:
            source = doc.get("source", "unknown")
            title = doc.get("title", "Untitled")
            evidence.append(f"[{source}] {title}")

    return evidence
