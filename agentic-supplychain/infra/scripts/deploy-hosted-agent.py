#!/usr/bin/env python3
"""
deploy-hosted-agent.py
Deploys the orchestrator as a hosted agent in Azure Foundry Agent Service.

Uses the Azure AI Projects SDK to create or update a hosted agent definition
backed by the container image in ACR.

Required environment variables:
  AZURE_AI_PROJECT_ENDPOINT  – Foundry project endpoint (from Bicep output)
  MODEL_DEPLOYMENT_NAME      – LLM model deployment name (from Step 3)
  AGENT_IMAGE                – Full ACR image ref, e.g., myacr.azurecr.io/orchestrator:latest
  AGENT_NAME                 – Display name for the agent (default: orchestrator-agent)

Optional:
  FABRIC_DATA_AGENT_ENDPOINT – Fabric data agent endpoint (from Fabric portal)
  FOUNDRY_IQ_MCP_URL         – Foundry IQ MCP URL (from Foundry IQ setup)
"""
import os
import sys
from pathlib import Path

try:
    from dotenv import load_dotenv
    repo_root = Path(__file__).resolve().parents[2]
    load_dotenv(repo_root / ".env")
    load_dotenv(repo_root / ".env.generated", override=True)
except ImportError:
    pass

from azure.identity import DefaultAzureCredential
from azure.ai.projects import AIProjectClient


def main():
    endpoint = os.environ.get("AZURE_AI_PROJECT_ENDPOINT", "")
    model = os.environ.get("MODEL_DEPLOYMENT_NAME", "")
    image = os.environ.get("AGENT_IMAGE", "")
    agent_name = os.environ.get("AGENT_NAME", "orchestrator-agent")

    missing = []
    if not endpoint:
        missing.append("AZURE_AI_PROJECT_ENDPOINT")
    if not model:
        missing.append("MODEL_DEPLOYMENT_NAME")
    if not image:
        missing.append("AGENT_IMAGE")

    if missing:
        print(f"❌ Missing required env vars: {', '.join(missing)}")
        sys.exit(1)

    print(f"🚀 Deploying hosted agent: {agent_name}")
    print(f"   Endpoint: {endpoint}")
    print(f"   Model:    {model}")
    print(f"   Image:    {image}")

    credential = DefaultAzureCredential()
    client = AIProjectClient(endpoint=endpoint, credential=credential)

    # Build tool definitions for the agent
    tools = []

    fabric_endpoint = os.environ.get("FABRIC_DATA_AGENT_ENDPOINT", "")
    if fabric_endpoint:
        tools.append({
            "type": "remote_tool",
            "remote_tool": {
                "connection_name": "fabric-data-agent",
                "allowed_tools": ["*"],
            },
        })
        print(f"   Tool: Fabric data agent ({fabric_endpoint})")

    iq_url = os.environ.get("FOUNDRY_IQ_MCP_URL", "")
    if iq_url:
        tools.append({
            "type": "remote_tool",
            "remote_tool": {
                "connection_name": "foundry-iq-mcp",
                "allowed_tools": ["*"],
            },
        })
        print(f"   Tool: Foundry IQ MCP ({iq_url})")

    # Read system prompt
    system_prompt_path = Path(__file__).resolve().parents[2] / "agents" / "orchestrator" / "app" / "prompts" / "system.txt"
    system_prompt = "You are a supply chain operations assistant."
    if system_prompt_path.exists():
        system_prompt = system_prompt_path.read_text().strip()

    try:
        agent = client.agents.create_agent(
            model=model,
            name=agent_name,
            instructions=system_prompt,
            tools=tools if tools else None,
            headers={"x-ms-enable-preview": "true"},
        )
        print(f"\n✅ Agent created successfully!")
        print(f"   Agent ID: {agent.id}")
        print(f"   Name:     {agent.name}")
        print(f"\n   Save this AGENT_ID for evals: {agent.id}")
    except Exception as e:
        print(f"\n❌ Agent creation failed: {e}")
        print("\n═══ MANUAL CHECKPOINT ═══")
        print("If the SDK call fails due to preview constraints:")
        print("1. Go to Azure AI Foundry portal → your project")
        print("2. Agents → + New Agent")
        print(f"3. Name: {agent_name}")
        print(f"4. Model: {model}")
        print("5. Add tools: Fabric data agent, Foundry IQ MCP")
        print("6. For hosted agent: configure container image in agent settings")
        print(f"7. Image: {image}")
        print("═══════════════════════════")
        sys.exit(1)


if __name__ == "__main__":
    main()
