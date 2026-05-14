#!/usr/bin/env python3
"""
create-foundry-connections.py
Creates or validates Foundry project connections for:
  1. Azure Container Registry
  2. Application Insights
  3. Fabric data agent (RemoteTool)
  4. Foundry IQ MCP (RemoteTool)

⚠️  PARTIALLY AUTOMATED: Connection creation APIs are preview.
    If SDK calls fail, the script prints exact portal instructions.

Requires environment variables (from .env + .env.generated):
  - AZURE_AI_PROJECT_ENDPOINT   (from Bicep output)
  - ACR_NAME                    (from Bicep output)
  - ACR_LOGIN_SERVER             (from Bicep output)
  - APPINSIGHTS_CONNECTION_STRING (from Bicep output)
  - FABRIC_DATA_AGENT_ENDPOINT   (from Fabric portal — manual)
  - FOUNDRY_IQ_MCP_URL           (from Foundry IQ portal — manual)
"""
import os
import sys
from pathlib import Path

# Load .env files
try:
    from dotenv import load_dotenv
    repo_root = Path(__file__).resolve().parents[2]
    load_dotenv(repo_root / ".env")
    load_dotenv(repo_root / ".env.generated", override=True)
except ImportError:
    pass

from azure.identity import DefaultAzureCredential
from azure.ai.projects import AIProjectClient


def get_env(name: str, required: bool = True) -> str:
    val = os.environ.get(name, "")
    if required and not val:
        print(f"⚠️  Missing env var: {name}")
    return val


def create_connections():
    endpoint = get_env("AZURE_AI_PROJECT_ENDPOINT")
    if not endpoint:
        print("❌ AZURE_AI_PROJECT_ENDPOINT is required.")
        sys.exit(1)

    credential = DefaultAzureCredential()
    client = AIProjectClient(endpoint=endpoint, credential=credential)

    connections = []

    # 1. ACR Connection
    acr_server = get_env("ACR_LOGIN_SERVER", required=False)
    if acr_server:
        connections.append({
            "name": "acr-connection",
            "type": "ContainerRegistry",
            "target": acr_server,
            "description": "Azure Container Registry for hosted agent images",
        })

    # 2. App Insights Connection
    appinsights_conn = get_env("APPINSIGHTS_CONNECTION_STRING", required=False)
    if appinsights_conn:
        connections.append({
            "name": "appinsights-connection",
            "type": "ApplicationInsights",
            "target": appinsights_conn,
            "description": "Application Insights for agent telemetry",
        })

    # 3. Fabric Data Agent Connection
    fabric_endpoint = get_env("FABRIC_DATA_AGENT_ENDPOINT", required=False)
    if fabric_endpoint:
        connections.append({
            "name": "fabric-data-agent",
            "type": "RemoteTool",
            "target": fabric_endpoint,
            "description": "Microsoft Fabric data agent for structured analytics",
        })
    else:
        print("ℹ️  FABRIC_DATA_AGENT_ENDPOINT not set — skipping Fabric connection.")
        print("   Set this after creating the Fabric data agent (see scripts/load_fabric_seed_files.md).")

    # 4. Foundry IQ MCP Connection
    iq_url = get_env("FOUNDRY_IQ_MCP_URL", required=False)
    if iq_url:
        connections.append({
            "name": "foundry-iq-mcp",
            "type": "RemoteTool",
            "target": iq_url,
            "description": "Foundry IQ knowledge base via MCP for policy/contract retrieval",
        })
    else:
        print("ℹ️  FOUNDRY_IQ_MCP_URL not set — skipping Foundry IQ connection.")
        print("   Set this after creating the knowledge base (see scripts/create_knowledge_base.py).")

    if not connections:
        print("⚠️  No connections to create. Set at least one endpoint.")
        return

    print(f"\n📡 Creating {len(connections)} connection(s)...\n")

    for conn in connections:
        try:
            # Attempt SDK-based connection creation
            # The exact SDK method depends on azure-ai-projects version
            print(f"  Creating: {conn['name']} ({conn['type']})")
            print(f"    Target: {conn['target']}")

            # Try using the connections API
            result = client.connections.create_or_update(
                name=conn["name"],
                connection={
                    "properties": {
                        "category": conn["type"],
                        "target": conn["target"],
                        "metadata": {
                            "description": conn["description"],
                        },
                    }
                },
            )
            print(f"    ✅ Created: {conn['name']}")
        except AttributeError:
            print(f"    ⚠️  SDK method not available for connection creation.")
            _print_manual_instructions(conn)
        except Exception as e:
            print(f"    ⚠️  Failed: {e}")
            _print_manual_instructions(conn)


def _print_manual_instructions(conn: dict):
    """Print portal instructions when SDK creation fails."""
    print(f"    ═══ MANUAL CHECKPOINT ═══")
    print(f"    Create connection '{conn['name']}' in Azure Portal:")
    print(f"    1. Go to Azure AI Foundry → your project")
    print(f"    2. Settings → Connections → + New Connection")
    print(f"    3. Type: {conn['type']}")
    print(f"    4. Target: {conn['target']}")
    print(f"    5. Name: {conn['name']}")
    print(f"    ═══════════════════════════\n")


if __name__ == "__main__":
    create_connections()
