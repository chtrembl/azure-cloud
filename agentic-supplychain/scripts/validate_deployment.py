#!/usr/bin/env python3
"""
validate_deployment.py
Validates that the deployed Azure resources, connections, and agent
are properly configured and accessible.

Run: python scripts/validate_deployment.py
"""
import os
import sys
from pathlib import Path

try:
    from dotenv import load_dotenv
    repo_root = Path(__file__).resolve().parent.parent
    load_dotenv(repo_root / ".env")
    load_dotenv(repo_root / ".env.generated", override=True)
except ImportError:
    pass


def check(name: str, var: str, required: bool = True) -> bool:
    val = os.environ.get(var, "")
    if val:
        # Truncate display for long values
        display = val[:60] + "..." if len(val) > 60 else val
        print(f"  ✅ {name}: {display}")
        return True
    elif required:
        print(f"  ❌ {name}: NOT SET ({var})")
        return False
    else:
        print(f"  ⚠️  {name}: not set (optional) ({var})")
        return True


def main():
    print("═══════════════════════════════════════════════════════════")
    print("  Agentic Supply Chain – Deployment Validation")
    print("═══════════════════════════════════════════════════════════")
    print()

    all_ok = True

    # Phase 1: Azure Identity
    print("🔐 Azure Identity:")
    all_ok &= check("Subscription ID", "AZURE_SUBSCRIPTION_ID")
    all_ok &= check("Tenant ID", "AZURE_TENANT_ID")
    print()

    # Phase 2: Bicep Outputs
    print("🏗️  Infrastructure Outputs:")
    all_ok &= check("Project Endpoint", "AZURE_AI_PROJECT_ENDPOINT")
    all_ok &= check("ACR Name", "ACR_NAME")
    all_ok &= check("Search Endpoint", "SEARCH_ENDPOINT")
    all_ok &= check("App Insights", "APPINSIGHTS_CONNECTION_STRING")
    print()

    # Phase 3: Model Deployment
    print("🤖 Model Deployment:")
    all_ok &= check("Model Deployment", "MODEL_DEPLOYMENT_NAME")
    print()

    # Phase 4: External Dependencies
    print("🔗 External Dependencies:")
    fabric_ok = check("Fabric Data Agent", "FABRIC_DATA_AGENT_ENDPOINT", required=False)
    iq_ok = check("Foundry IQ MCP URL", "FOUNDRY_IQ_MCP_URL", required=False)
    print()

    # Phase 5: Agent Config
    print("📦 Agent Configuration:")
    all_ok &= check("Agent Name", "AGENT_NAME")
    print()

    # Check local files
    print("📁 Repository Files:")
    repo_root = Path(__file__).resolve().parent.parent
    required_files = [
        "infra/main.bicep",
        "agents/orchestrator/Dockerfile",
        "agents/orchestrator/app/main.py",
        "agents/orchestrator/requirements.txt",
        "data/fabric/suppliers.csv",
        "data/knowledge/contracts/apex_supplier_master_agreement.md",
    ]
    for f in required_files:
        path = repo_root / f
        if path.exists():
            print(f"  ✅ {f}")
        else:
            print(f"  ❌ {f} MISSING")
            all_ok = False
    print()

    # Try to reach the local agent if running
    print("🌐 Local Agent (optional):")
    try:
        import httpx
        resp = httpx.get("http://localhost:8088/health", timeout=3.0)
        if resp.status_code == 200:
            print(f"  ✅ Local agent healthy: {resp.json()}")
        else:
            print(f"  ⚠️  Local agent returned {resp.status_code}")
    except Exception:
        print("  ℹ️  Local agent not running (start with: cd agents/orchestrator && python -m app.main)")
    print()

    # Summary
    print("═══════════════════════════════════════════════════════════")
    if all_ok:
        print("✅ All required validations passed!")
    else:
        print("❌ Some validations failed. Review the items above.")
    print("═══════════════════════════════════════════════════════════")

    sys.exit(0 if all_ok else 1)


if __name__ == "__main__":
    main()
