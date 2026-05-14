"""
Risk Analyst Agent (Optional)
A specialist agent for deeper risk analysis and recommendation composition.
Can be deployed alongside the orchestrator for multi-agent workflows.

This is a minimal implementation that can be expanded as needed.
"""
import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel, Field

app = FastAPI(
    title="Supply Chain Risk Analyst",
    description="Optional specialist agent for risk scoring and mitigation recommendations.",
    version="0.1.0",
)


class RiskRequest(BaseModel):
    supplier_data: dict = Field(default_factory=dict)
    inventory_data: dict = Field(default_factory=dict)
    policy_context: list[dict] = Field(default_factory=list)


class RiskAssessment(BaseModel):
    risk_score: float = Field(0.0, description="Overall risk score 0-100")
    risk_level: str = Field("low", description="low/medium/high/critical")
    factors: list[str] = Field(default_factory=list)
    mitigations: list[str] = Field(default_factory=list)


@app.get("/health")
async def health():
    return {"status": "ok", "agent": "risk-analyst", "version": "0.1.0"}


@app.post("/assess", response_model=RiskAssessment)
async def assess_risk(request: RiskRequest):
    """Assess supply chain risk based on combined data and policy context."""
    factors = []
    mitigations = []
    score = 0.0

    # Evaluate supplier delays
    suppliers = request.supplier_data.get("suppliers_with_delays", [])
    for s in suppliers:
        if s.get("late_shipment_pct", 0) > 0.5:
            score += 30
            factors.append(f"Chronic delays from {s.get('name', 'unknown')} ({s.get('late_shipment_pct', 0):.0%} late)")
            mitigations.append(f"Escalate {s.get('name', 'unknown')} per supplier escalation runbook")

    # Evaluate inventory risk
    inventory = request.inventory_data.get("at_risk_inventory", [])
    for item in inventory:
        if item.get("status") == "CRITICAL":
            score += 40
            factors.append(f"Critical stockout: {item.get('product_name', 'unknown')} at {item.get('warehouse', 'unknown')}")
            mitigations.append(f"Expedite replenishment for {item.get('product_name', 'unknown')}")
        elif item.get("status") == "LOW":
            score += 15
            factors.append(f"Low inventory: {item.get('product_name', 'unknown')}")

    score = min(score, 100)
    risk_level = "low" if score < 25 else "medium" if score < 50 else "high" if score < 75 else "critical"

    return RiskAssessment(
        risk_score=score,
        risk_level=risk_level,
        factors=factors,
        mitigations=mitigations,
    )


if __name__ == "__main__":
    uvicorn.run("app.main:app", host="0.0.0.0", port=8089, reload=True)
