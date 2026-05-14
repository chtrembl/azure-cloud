#!/usr/bin/env python3
"""
collect_outputs.py
Collects all deployment outputs, environment variables, and configuration
into a single summary for operator review.

Run: python scripts/collect_outputs.py
"""
import os
import json
from pathlib import Path

try:
    from dotenv import load_dotenv
    repo_root = Path(__file__).resolve().parent.parent
    load_dotenv(repo_root / ".env")
    load_dotenv(repo_root / ".env.generated", override=True)
except ImportError:
    repo_root = Path(__file__).resolve().parent.parent


VARIABLES = [
    ("AZURE_SUBSCRIPTION_ID", "Azure subscription", "Known before deploy", ".env"),
    ("AZURE_TENANT_ID", "Entra ID tenant", "Known before deploy", ".env"),
    ("AZURE_CLIENT_ID", "Service principal / OIDC client", "Known before deploy", ".env"),
    ("AZURE_LOCATION", "Azure region", "Known before deploy", ".env"),
    ("RESOURCE_GROUP_NAME", "Resource group name", "Known before deploy", ".env"),
    ("FOUNDRY_ACCOUNT_NAME", "Foundry account name", "Bicep output", ".env.generated"),
    ("FOUNDRY_PROJECT_NAME", "Foundry project name", "Bicep output", ".env.generated"),
    ("AZURE_AI_PROJECT_ENDPOINT", "Foundry project endpoint", "Bicep output", ".env.generated"),
    ("MODEL_DEPLOYMENT_NAME", "LLM deployment name", "Manual Step 3", ".env"),
    ("SEARCH_ENDPOINT", "AI Search endpoint", "Bicep output", ".env.generated"),
    ("ACR_NAME", "Container Registry name", "Bicep output", ".env.generated"),
    ("ACR_LOGIN_SERVER", "ACR login server URL", "Bicep output", ".env.generated"),
    ("APPINSIGHTS_CONNECTION_STRING", "App Insights connection", "Bicep output", ".env.generated"),
    ("FABRIC_WORKSPACE_NAME", "Fabric workspace", "Manual / Fabric portal", ".env"),
    ("FABRIC_DATA_AGENT_ENDPOINT", "Fabric data agent URL", "Manual / Fabric portal", ".env"),
    ("FOUNDRY_IQ_MCP_URL", "Foundry IQ MCP endpoint", "Manual / Foundry portal", ".env"),
    ("AGENT_NAME", "Hosted agent name", "Default or custom", ".env"),
]


def main():
    print("═══════════════════════════════════════════════════════════")
    print("  Agentic Supply Chain – Configuration Summary")
    print("═══════════════════════════════════════════════════════════")
    print()
    print(f"{'Variable':<35} {'Status':<8} {'Source':<25} {'File'}")
    print("─" * 100)

    for var_name, description, source, file_loc in VARIABLES:
        value = os.environ.get(var_name, "")
        status = "✅ SET" if value else "❌ EMPTY"
        print(f"{var_name:<35} {status:<8} {source:<25} {file_loc}")

    print()

    # Check for .env.generated
    gen_file = repo_root / ".env.generated"
    if gen_file.exists():
        print(f"📄 .env.generated exists (last modified: {gen_file.stat().st_mtime})")
    else:
        print("⚠️  .env.generated not found — run: bash infra/scripts/export-deployment-outputs.sh")

    # Check for .deployment-outputs.json
    outputs_file = repo_root / ".deployment-outputs.json"
    if outputs_file.exists():
        print(f"📄 .deployment-outputs.json exists")
    else:
        print("ℹ️  .deployment-outputs.json not found (created by bootstrap-env.sh)")

    print()


if __name__ == "__main__":
    main()
