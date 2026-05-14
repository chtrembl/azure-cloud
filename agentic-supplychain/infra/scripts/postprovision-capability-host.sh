#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
# postprovision-capability-host.sh
# Creates the account-level capability host with public hosting
# enabled — required before deploying hosted agents.
#
# ⚠️  MANUAL CHECKPOINT: This uses az rest with a preview API.
# If it fails, create the capability host via Azure Portal →
# Foundry account → Settings → Capability Host.
#
# Requires: FOUNDRY_ACCOUNT_NAME, AZURE_SUBSCRIPTION_ID,
#           RESOURCE_GROUP_NAME from .env / .env.generated
# ─────────────────────────────────────────────────────────────
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Load env files
for f in "$REPO_ROOT/.env" "$REPO_ROOT/.env.generated"; do
  [ -f "$f" ] && { set -a; source "$f"; set +a; }
done

SUBSCRIPTION="${AZURE_SUBSCRIPTION_ID:?Set AZURE_SUBSCRIPTION_ID in .env}"
RG="${RESOURCE_GROUP_NAME:-agentic-supplychain}"
ACCOUNT="${FOUNDRY_ACCOUNT_NAME:?Set FOUNDRY_ACCOUNT_NAME in .env or run export-deployment-outputs.sh}"

API_VERSION="2025-04-01-preview"
RESOURCE_URL="https://management.azure.com/subscriptions/${SUBSCRIPTION}/resourceGroups/${RG}/providers/Microsoft.CognitiveServices/accounts/${ACCOUNT}/capabilityHosts/default?api-version=${API_VERSION}"

echo "🔧 Creating capability host for account: $ACCOUNT"
echo "   API version: $API_VERSION"
echo ""

BODY=$(cat <<'ENDJSON'
{
  "properties": {
    "capabilityHostKind": "Agents",
    "publicHostingEnabled": true
  }
}
ENDJSON
)

RESPONSE=$(az rest \
  --method PUT \
  --url "$RESOURCE_URL" \
  --body "$BODY" \
  --output json 2>&1) || {
  echo "❌ az rest failed. Response:"
  echo "$RESPONSE"
  echo ""
  echo "═══════════════════════════════════════════════════════"
  echo "  MANUAL CHECKPOINT: Create capability host via portal"
  echo "═══════════════════════════════════════════════════════"
  echo "1. Go to Azure Portal → AI Foundry account '$ACCOUNT'"
  echo "2. Settings → Capability Host"
  echo "3. Enable 'Agents' capability with public hosting"
  echo "4. Save and wait for provisioning"
  echo ""
  exit 1
}

echo "✅ Capability host created/updated:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
