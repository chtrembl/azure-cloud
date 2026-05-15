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
    client = AIProjectClient(endpoint=endpoint, credential=credential, allow_preview=True)

    # Read system prompt
    system_prompt_path = Path(__file__).resolve().parents[2] / "agents" / "orchestrator" / "app" / "prompts" / "system.txt"
    system_prompt = "You are a supply chain operations assistant."
    if system_prompt_path.exists():
        system_prompt = system_prompt_path.read_text().strip()

    try:
        from azure.ai.projects.models import HostedAgentDefinition, ProtocolVersionRecord, AgentProtocol

        # Build hosted agent definition with container image
        definition = HostedAgentDefinition(
            image=image,
            cpu="1",
            memory="2Gi",
            container_protocol_versions=[
                ProtocolVersionRecord(protocol=AgentProtocol.INVOCATIONS, version="1.0.0"),
            ],
            environment_variables={
                "AZURE_AI_PROJECT_ENDPOINT": endpoint,
                "MODEL_DEPLOYMENT_NAME": model,
            },
        )

        # Add optional env vars if set
        fabric_endpoint = os.environ.get("FABRIC_DATA_AGENT_ENDPOINT", "")
        if fabric_endpoint:
            definition.environment_variables["FABRIC_DATA_AGENT_ENDPOINT"] = fabric_endpoint
            print(f"   Tool: Fabric data agent ({fabric_endpoint})")

        iq_url = os.environ.get("FOUNDRY_IQ_MCP_URL", "")
        if iq_url:
            definition.environment_variables["FOUNDRY_IQ_MCP_URL"] = iq_url
            print(f"   Tool: Foundry IQ MCP ({iq_url})")

        agent = client.agents.create_version(
            agent_name=agent_name,
            definition=definition,
            description=system_prompt[:512],
        )
        print(f"\n✅ Agent version created successfully!")
        print(f"   Agent: {agent_name}")
        print(f"   Version: {agent.as_dict()}")
        print(f"\n   Save AGENT_NAME for evals: {agent_name}")
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
