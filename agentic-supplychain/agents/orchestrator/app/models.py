"""
Agentic Supply Chain Orchestrator – Data Models
Pydantic models for request/response payloads and internal data structures.
"""
from __future__ import annotations
from typing import Optional, Literal
from pydantic import BaseModel, Field


class QueryRequest(BaseModel):
    """Incoming user question."""
    question: str = Field(..., description="The business question to answer")
    user_context: dict = Field(default_factory=dict, description="Optional user context (e.g., region, role)")


class QueryRoute(BaseModel):
    """Classification of a query into routing categories."""
    route: Literal["structured", "knowledge", "mixed"] = Field(
        ..., description="Whether the query needs Fabric data, knowledge docs, or both"
    )
    reasoning: str = Field(default="", description="Brief explanation of routing decision")


class FabricResult(BaseModel):
    """Response from the Fabric data agent tool."""
    success: bool = True
    data: dict = Field(default_factory=dict)
    summary: str = ""
    error: Optional[str] = None


class KnowledgeResult(BaseModel):
    """Response from the Foundry IQ knowledge tool."""
    success: bool = True
    documents: list[dict] = Field(default_factory=list)
    summary: str = ""
    error: Optional[str] = None


class SynthesizedResponse(BaseModel):
    """Final orchestrated response combining structured and knowledge results."""
    question: str
    route: str
    findings: str = Field(default="", description="Key data findings from Fabric")
    policy_implications: str = Field(default="", description="Relevant policies, contracts, SLAs")
    recommended_actions: list[str] = Field(default_factory=list, description="Actionable next steps")
    supporting_evidence: list[str] = Field(default_factory=list, description="Source references and notes")
    raw_fabric: Optional[dict] = None
    raw_knowledge: Optional[list[dict]] = None


class HealthResponse(BaseModel):
    """Health check response."""
    status: str = "ok"
    agent_name: str = ""
    version: str = "0.1.0"
