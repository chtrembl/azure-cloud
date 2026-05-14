#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
# bootstrap-env.sh
# Loads .env, validates required Azure tooling, logs into Azure,
# creates the resource group, and deploys Bicep.
# Run from the repo root: bash infra/scripts/bootstrap-env.sh
# ─────────────────────────────────────────────────────────────
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# ── Load .env ───────────────────────────────────────────────
if [ -f "$REPO_ROOT/.env" ]; then
  set -a; source "$REPO_ROOT/.env"; set +a
  echo "✅ Loaded .env"
else
  echo "⚠️  No .env found. Copy .env.example → .env and fill in values."
  exit 1
fi

# ── Check prerequisites ────────────────────────────────────
for cmd in az docker python3; do
  if ! command -v "$cmd" &>/dev/null; then
    echo "❌ Required tool not found: $cmd"
    exit 1
  fi
done
echo "✅ Prerequisites: az, docker, python3 found"

# ── Azure login check ──────────────────────────────────────
if ! az account show &>/dev/null; then
  echo "🔐 Not logged in. Running az login..."
  az login
fi
az account set --subscription "$AZURE_SUBSCRIPTION_ID"
echo "✅ Azure subscription set to $AZURE_SUBSCRIPTION_ID"

# ── Create resource group ──────────────────────────────────
LOCATION="${AZURE_LOCATION:-eastus}"
RG="${RESOURCE_GROUP_NAME:-agentic-supplychain}"
echo "📦 Creating resource group: $RG in $LOCATION"
az group create --name "$RG" --location "$LOCATION" --output none
echo "✅ Resource group $RG ready"

# ── Deploy Bicep ────────────────────────────────────────────
echo "🚀 Deploying Bicep infrastructure..."
az deployment group create \
  --resource-group "$RG" \
  --template-file "$REPO_ROOT/infra/main.bicep" \
  --parameters @"$REPO_ROOT/infra/main.parameters.json" \
  --output json > "$REPO_ROOT/.deployment-outputs.json"

echo "✅ Bicep deployment complete. Outputs saved to .deployment-outputs.json"
echo ""
echo "Next steps:"
echo "  1. Run: bash infra/scripts/export-deployment-outputs.sh"
echo "  2. Configure model deployment (see infra/scripts/configure-model-deployment.md)"
echo "  3. Run: bash infra/scripts/postprovision-capability-host.sh"
