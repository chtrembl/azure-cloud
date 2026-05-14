"""
Common models shared across agents.
Import these for consistent data contracts between orchestrator and specialists.
"""
from __future__ import annotations
from typing import Optional
from pydantic import BaseModel, Field


class SupplierMetrics(BaseModel):
    supplier_id: str
    name: str
    region: str = ""
    avg_delay_days: float = 0.0
    late_shipment_pct: float = 0.0
    total_orders: int = 0


class InventoryPosition(BaseModel):
    warehouse: str
    product: str
    product_name: str = ""
    current_qty: int = 0
    reorder_point: int = 0
    days_of_supply: float = 0.0
    status: str = "OK"  # OK, LOW, CRITICAL


class PolicyDocument(BaseModel):
    source: str
    title: str
    excerpt: str = ""
    relevance_score: float = 0.0


class AgentMessage(BaseModel):
    role: str = "assistant"
    content: str = ""
    agent_name: str = ""
    metadata: dict = Field(default_factory=dict)
