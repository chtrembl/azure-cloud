#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────
# validate_prereqs.sh
# Validates that all required local tools and Azure access
# are available before starting deployment.
# Run from repo root: bash scripts/validate_prereqs.sh
# ─────────────────────────────────────────────────────────────
set -euo pipefail

echo "═══════════════════════════════════════════════════════════"
echo "  Agentic Supply Chain – Prerequisite Validation"
echo "═══════════════════════════════════════════════════════════"
echo ""

ERRORS=0

# ── Required CLI tools ──────────────────────────────────────
echo "🔧 Checking CLI tools:"

for tool in az docker python3 pip3 git curl; do
  if command -v "$tool" &>/dev/null; then
    version=$($tool --version 2>&1 | head -1)
    echo "  ✅ $tool: $version"
  else
    echo "  ❌ $tool: NOT FOUND"
    ERRORS=$((ERRORS + 1))
  fi
done
echo ""

# ── Azure CLI extensions ────────────────────────────────────
echo "🔌 Checking Azure CLI extensions:"
if command -v az &>/dev/null; then
  for ext in ai; do
    if az extension show --name "$ext" &>/dev/null 2>&1; then
      echo "  ✅ az extension: $ext"
    else
      echo "  ⚠️  az extension '$ext' not installed (run: az extension add --name $ext)"
    fi
  done
else
  echo "  ⚠️  Azure CLI not available — skipping extension checks"
fi
echo ""

# ── Azure login ─────────────────────────────────────────────
echo "🔐 Checking Azure login:"
if command -v az &>/dev/null; then
  if az account show &>/dev/null 2>&1; then
    ACCOUNT=$(az account show --query "{name:name, id:id}" --output tsv 2>/dev/null)
    echo "  ✅ Logged in: $ACCOUNT"
  else
    echo "  ❌ Not logged in (run: az login)"
    ERRORS=$((ERRORS + 1))
  fi
fi
echo ""

# ── Python version ──────────────────────────────────────────
echo "🐍 Checking Python version:"
if command -v python3 &>/dev/null; then
  PY_VERSION=$(python3 -c "import sys; print(f'{sys.version_info.major}.{sys.version_info.minor}')")
  PY_MAJOR=$(echo "$PY_VERSION" | cut -d. -f1)
  PY_MINOR=$(echo "$PY_VERSION" | cut -d. -f2)
  if [ "$PY_MAJOR" -ge 3 ] && [ "$PY_MINOR" -ge 10 ]; then
    echo "  ✅ Python $PY_VERSION (≥ 3.10 required)"
  else
    echo "  ❌ Python $PY_VERSION (≥ 3.10 required)"
    ERRORS=$((ERRORS + 1))
  fi
fi
echo ""

# ── Docker ──────────────────────────────────────────────────
echo "🐳 Checking Docker:"
if command -v docker &>/dev/null; then
  if docker info &>/dev/null 2>&1; then
    echo "  ✅ Docker daemon running"
  else
    echo "  ❌ Docker installed but daemon not running"
    ERRORS=$((ERRORS + 1))
  fi
fi
echo ""

# ── .env file ──────────────────────────────────────────────
echo "📄 Checking .env file:"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
if [ -f "$REPO_ROOT/.env" ]; then
  echo "  ✅ .env file exists"
  # Check key variables
  source "$REPO_ROOT/.env" 2>/dev/null || true
  [ -n "${AZURE_SUBSCRIPTION_ID:-}" ] && echo "  ✅ AZURE_SUBSCRIPTION_ID set" || echo "  ⚠️  AZURE_SUBSCRIPTION_ID not set"
  [ -n "${AZURE_TENANT_ID:-}" ] && echo "  ✅ AZURE_TENANT_ID set" || echo "  ⚠️  AZURE_TENANT_ID not set"
else
  echo "  ⚠️  .env not found (copy .env.example → .env)"
fi
echo ""

# ── Summary ─────────────────────────────────────────────────
echo "═══════════════════════════════════════════════════════════"
if [ "$ERRORS" -eq 0 ]; then
  echo "✅ All prerequisite checks passed!"
else
  echo "❌ $ERRORS prerequisite check(s) failed."
fi
echo "═══════════════════════════════════════════════════════════"

exit "$ERRORS"
