#!/usr/bin/env python3
"""
create_knowledge_base.py
Creates or guides creation of the Foundry IQ knowledge base connected to
Azure AI Search.

⚠️  MANUAL CHECKPOINT: Foundry IQ knowledge base creation is currently
    a portal-driven operation. This script validates prerequisites and
    provides exact portal instructions.

Requires:
  SEARCH_ENDPOINT               – from Bicep output
  AZURE_AI_PROJECT_ENDPOINT     – from Bicep output
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


def main():
    search_endpoint = os.environ.get("SEARCH_ENDPOINT", "")
    project_endpoint = os.environ.get("AZURE_AI_PROJECT_ENDPOINT", "")

    print("═══════════════════════════════════════════════════════════")
    print("  Foundry IQ Knowledge Base Setup")
    print("═══════════════════════════════════════════════════════════")
    print()

    # Prerequisites check
    print("📋 Prerequisites:")
    checks = []

    if search_endpoint:
        print(f"  ✅ SEARCH_ENDPOINT: {search_endpoint}")
        checks.append(True)
    else:
        print("  ❌ SEARCH_ENDPOINT not set (run export-deployment-outputs.sh)")
        checks.append(False)

    if project_endpoint:
        print(f"  ✅ AZURE_AI_PROJECT_ENDPOINT: {project_endpoint}")
        checks.append(True)
    else:
        print("  ❌ AZURE_AI_PROJECT_ENDPOINT not set")
        checks.append(False)

    # Check if documents have been uploaded
    knowledge_dir = Path(__file__).resolve().parent.parent / "data" / "knowledge"
    doc_count = len(list(knowledge_dir.rglob("*.md")))
    if doc_count > 0:
        print(f"  ✅ Knowledge documents: {doc_count} markdown files found")
        checks.append(True)
    else:
        print(f"  ❌ No knowledge documents found in data/knowledge/")
        checks.append(False)

    print()

    if not all(checks):
        print("❌ Prerequisites not met. Fix the above items first.")
        print()
        print("Steps to complete prerequisites:")
        print("  1. Deploy Bicep: bash infra/scripts/bootstrap-env.sh")
        print("  2. Export outputs: bash infra/scripts/export-deployment-outputs.sh")
        print("  3. Upload docs: python scripts/upload_search_documents.py")
        sys.exit(1)

    print("═══ MANUAL CHECKPOINT: Create Knowledge Base ═══")
    print()
    print("Foundry IQ knowledge base must be created via the portal.")
    print()
    print("Step-by-step instructions:")
    print()
    print("1. UPLOAD DOCUMENTS TO SEARCH (if not done):")
    print("   python scripts/upload_search_documents.py")
    print()
    print("2. CREATE FOUNDRY IQ KNOWLEDGE BASE:")
    print("   a. Go to https://ai.azure.com")
    print("   b. Navigate to your project")
    print("   c. Go to Knowledge Bases → + New Knowledge Base")
    print("   d. Name: 'supplychain-knowledge'")
    print(f"   e. Select Azure AI Search connection: {search_endpoint}")
    print("   f. Select index: 'supplychain-knowledge'")
    print("   g. Configure fields:")
    print("      - Content field: content")
    print("      - Title field: title")
    print("      - Category field: category")
    print("   h. Save and test with a sample query")
    print()
    print("3. CREATE MCP REMOTE TOOL CONNECTION:")
    print("   a. In project Settings → Connections")
    print("   b. + New Connection → Type: RemoteTool")
    print("   c. Name: foundry-iq-mcp")
    print("   d. Set the MCP endpoint URL")
    print("   e. Save the endpoint URL to .env as FOUNDRY_IQ_MCP_URL")
    print()
    print("4. VERIFY:")
    print("   Test a query like: 'What are the penalty terms for late deliveries?'")
    print("   Expected: Results from apex_supplier_master_agreement.md")
    print()
    print("After completion, update your .env:")
    print("   FOUNDRY_IQ_MCP_URL=<endpoint from step 3e>")


if __name__ == "__main__":
    main()
