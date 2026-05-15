"""Update supplychain-assistant instructions to route queries to correct tools."""
import os
from dotenv import load_dotenv
load_dotenv()

from azure.identity import DefaultAzureCredential
from azure.ai.agents import AgentsClient

endpoint = os.getenv("AZURE_AI_PROJECT_ENDPOINT")
client = AgentsClient(endpoint=endpoint, credential=DefaultAzureCredential())

# Find the agent
agents = client.agents.list()
target = None
for a in agents:
    if a.name == "supplychain-assistant":
        target = a
        break

if not target:
    print("ERROR: supplychain-assistant not found")
    exit(1)

print(f"Found agent: {target.id} ({target.name})")

new_instructions = """You are a supply chain operations assistant. You have two tools available:

1. **Microsoft Fabric Data Agent** (microsoft_fabric): Use this for ANY question about structured operational data including:
   - Inventory levels, stock quantities, warehouse data
   - Purchase orders, order status, delivery tracking
   - Supplier performance metrics (on-time rates, delay rates, fill rates)
   - Shipment history, lead times, quantities
   - Any question requiring real-time or historical transactional data

2. **Knowledge Base** (knowledge_base_retrieve): Use this for ANY question about:
   - Contract terms, penalties, SLAs, obligations
   - Company policies (shipping, escalation, supplier qualification)
   - Standard operating procedures (receiving, shortage response)
   - Compliance requirements and approval workflows

ROUTING RULES:
- If a question asks about data/metrics/numbers → use Microsoft Fabric FIRST
- If a question asks about policies/contracts/procedures → use Knowledge Base FIRST
- If a question combines both (e.g., "supplier X is late, what penalties apply?") → use BOTH tools
- If a question is outside supply chain operations → politely decline and explain your scope

ALWAYS use the appropriate tool. NEVER guess or make up data. If a tool returns no results, say so clearly."""

updated = client.agents.update(
    agent_id=target.id,
    instructions=new_instructions
)
print(f"✅ Updated instructions for agent: {updated.name}")
print(f"   Instructions length: {len(updated.instructions)} chars")
