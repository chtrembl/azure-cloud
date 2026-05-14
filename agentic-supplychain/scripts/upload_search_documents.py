#!/usr/bin/env python3
"""
upload_search_documents.py
Uploads knowledge-base markdown documents to Azure AI Search for indexing.
These documents back the Foundry IQ knowledge base.

Requires:
  SEARCH_ENDPOINT     – Azure AI Search endpoint (from Bicep output)
  SEARCH_ADMIN_KEY    – Azure AI Search admin key (from Azure Portal → Search → Keys)
                        OR use DefaultAzureCredential with Search Index Data Contributor role

⚠️  PARTIALLY AUTOMATED: The search admin key must be obtained from the portal
    or via `az search admin-key show`. RBAC-based access is preferred.
"""
import os
import sys
import json
import hashlib
from pathlib import Path

try:
    from dotenv import load_dotenv
    repo_root = Path(__file__).resolve().parent.parent
    load_dotenv(repo_root / ".env")
    load_dotenv(repo_root / ".env.generated", override=True)
except ImportError:
    pass

KNOWLEDGE_DIR = Path(__file__).resolve().parent.parent / "data" / "knowledge"
INDEX_NAME = "supplychain-knowledge"


def collect_documents() -> list[dict]:
    """Collect all markdown files from the knowledge directory."""
    docs = []
    for md_file in sorted(KNOWLEDGE_DIR.rglob("*.md")):
        rel_path = md_file.relative_to(KNOWLEDGE_DIR)
        content = md_file.read_text(encoding="utf-8")

        # Extract title from first heading
        title = str(rel_path)
        for line in content.split("\n"):
            if line.startswith("# "):
                title = line[2:].strip()
                break

        # Determine category from directory
        category = rel_path.parts[0] if len(rel_path.parts) > 1 else "general"

        doc_id = hashlib.md5(str(rel_path).encode()).hexdigest()

        docs.append({
            "id": doc_id,
            "title": title,
            "content": content,
            "category": category,
            "source_path": str(rel_path),
            "file_name": md_file.name,
        })

    return docs


def create_index_definition() -> dict:
    """Return the Azure AI Search index definition."""
    return {
        "name": INDEX_NAME,
        "fields": [
            {"name": "id", "type": "Edm.String", "key": True, "filterable": True},
            {"name": "title", "type": "Edm.String", "searchable": True, "filterable": True},
            {"name": "content", "type": "Edm.String", "searchable": True},
            {"name": "category", "type": "Edm.String", "filterable": True, "facetable": True},
            {"name": "source_path", "type": "Edm.String", "filterable": True},
            {"name": "file_name", "type": "Edm.String", "filterable": True},
        ],
    }


def main():
    endpoint = os.environ.get("SEARCH_ENDPOINT", "")
    admin_key = os.environ.get("SEARCH_ADMIN_KEY", "")

    documents = collect_documents()
    print(f"📄 Found {len(documents)} knowledge documents:")
    for doc in documents:
        print(f"   [{doc['category']}] {doc['title']} ({doc['source_path']})")

    if not endpoint:
        print("\n⚠️  SEARCH_ENDPOINT not set. Dry run only — no upload performed.")
        print("   Set SEARCH_ENDPOINT from Bicep output or .env.generated")
        print(f"\n   Index definition: {INDEX_NAME}")
        print(f"   Documents ready: {len(documents)}")
        return

    print(f"\n🔍 Search endpoint: {endpoint}")
    print(f"   Index name: {INDEX_NAME}")

    try:
        import httpx

        headers = {"Content-Type": "application/json"}
        if admin_key:
            headers["api-key"] = admin_key
        else:
            print("   Using DefaultAzureCredential (ensure Search Index Data Contributor role)")
            from azure.identity import DefaultAzureCredential
            credential = DefaultAzureCredential()
            token = credential.get_token("https://search.azure.com/.default")
            headers["Authorization"] = f"Bearer {token.token}"

        api_version = "2024-07-01"

        # Create or update index
        print("\n📐 Creating/updating search index...")
        index_url = f"{endpoint}/indexes/{INDEX_NAME}?api-version={api_version}"
        resp = httpx.put(index_url, json=create_index_definition(), headers=headers, timeout=30.0)
        if resp.status_code in (200, 201):
            print(f"   ✅ Index '{INDEX_NAME}' ready")
        else:
            print(f"   ⚠️  Index creation returned {resp.status_code}: {resp.text}")

        # Upload documents
        print(f"\n📤 Uploading {len(documents)} documents...")
        upload_url = f"{endpoint}/indexes/{INDEX_NAME}/docs/index?api-version={api_version}"
        batch = {"value": [{"@search.action": "mergeOrUpload", **doc} for doc in documents]}
        resp = httpx.post(upload_url, json=batch, headers=headers, timeout=30.0)
        if resp.status_code in (200, 207):
            print(f"   ✅ Documents uploaded successfully")
        else:
            print(f"   ⚠️  Upload returned {resp.status_code}: {resp.text}")

    except ImportError as e:
        print(f"\n❌ Missing dependency: {e}")
        print("   pip install httpx azure-identity")
    except Exception as e:
        print(f"\n❌ Error: {e}")
        print("\n═══ MANUAL CHECKPOINT ═══")
        print("Upload documents manually via Azure Portal → AI Search → Index → Upload")


if __name__ == "__main__":
    main()
