#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
# export-deployment-outputs.sh
# Reads Bicep deployment outputs and writes them to .env.generated
# so downstream scripts and agent runtime can consume them.
# Run from repo root: bash infra/scripts/export-deployment-outputs.sh
# ─────────────────────────────────────────────────────────────
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Load base .env for RG name
if [ -f "$REPO_ROOT/.env" ]; then
  set -a; source "$REPO_ROOT/.env"; set +a
fi

RG="${RESOURCE_GROUP_NAME:-agentic-supplychain}"

echo "📤 Extracting deployment outputs from resource group: $RG"

OUTPUTS=$(az deployment group show \
  --resource-group "$RG" \
  --name main \
  --query properties.outputs \
  --output json 2>/dev/null || echo "{}")

if [ "$OUTPUTS" = "{}" ] || [ -z "$OUTPUTS" ]; then
  echo "⚠️  No deployment outputs found. Ensure Bicep deployment succeeded."
  exit 1
fi

# Parse outputs into env vars
PROJECT_ENDPOINT=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('projectEndpoint',{}).get('value',''))")
PROJECT_NAME=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('projectName',{}).get('value',''))")
PROJECT_PRINCIPAL_ID=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('projectPrincipalId',{}).get('value',''))")
ACR_LOGIN_SERVER=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('acrLoginServer',{}).get('value',''))")
ACR_NAME=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('acrName',{}).get('value',''))")
SEARCH_ENDPOINT=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('searchEndpoint',{}).get('value',''))")
APPINSIGHTS_CONN=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('appInsightsConnectionString',{}).get('value',''))")
LOG_ANALYTICS_ID=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('logAnalyticsWorkspaceId',{}).get('value',''))")
FOUNDRY_ACCOUNT_NAME=$(echo "$OUTPUTS" | python3 -c "import sys,json; print(json.load(sys.stdin).get('foundryAccountName',{}).get('value',''))")

# Write .env.generated
ENV_FILE="$REPO_ROOT/.env.generated"
cat > "$ENV_FILE" <<EOF
# ─────────────────────────────────────────────────────────────
# Auto-generated from Bicep deployment outputs
# Source: az deployment group show --resource-group $RG --name main
# Re-run: bash infra/scripts/export-deployment-outputs.sh
# ─────────────────────────────────────────────────────────────
AZURE_AI_PROJECT_ENDPOINT=$PROJECT_ENDPOINT
FOUNDRY_PROJECT_NAME=$PROJECT_NAME
FOUNDRY_PROJECT_PRINCIPAL_ID=$PROJECT_PRINCIPAL_ID
ACR_LOGIN_SERVER=$ACR_LOGIN_SERVER
ACR_NAME=$ACR_NAME
SEARCH_ENDPOINT=$SEARCH_ENDPOINT
APPINSIGHTS_CONNECTION_STRING=$APPINSIGHTS_CONN
LOG_ANALYTICS_WORKSPACE_ID=$LOG_ANALYTICS_ID
FOUNDRY_ACCOUNT_NAME=$FOUNDRY_ACCOUNT_NAME
EOF

echo "✅ Deployment outputs written to .env.generated"
echo ""
cat "$ENV_FILE"
