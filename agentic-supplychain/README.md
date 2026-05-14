# Agentic Supply Chain Demo

An end-to-end reference implementation for an **agentic supply-chain orchestrator** that combines:

- **Agent orchestration** via a hosted Python orchestrator in Azure Foundry Agent Service
- **Knowledge grounding** via Foundry IQ backed by Azure AI Search
- **Governed structured data** via Microsoft Fabric data agent
- **Pro-code extensibility** via containerized Python agents deployed to ACR

## Architecture

```
┌────────────┐     ┌─────────────────────────┐
│   User /   │────▶│  Orchestrator Agent      │
│   Client   │     │  (Foundry hosted agent)  │
└────────────┘     └───────┬─────────┬────────┘
                           │         │
                    ┌──────▼──┐  ┌───▼──────────┐
                    │ Fabric  │  │ Foundry IQ   │
                    │ Data    │  │ Knowledge    │
                    │ Agent   │  │ (AI Search)  │
                    └─────────┘  └──────────────┘
```

The orchestrator classifies incoming questions, dispatches to Fabric (structured data) and/or Foundry IQ (knowledge), and synthesizes a grounded response with findings, policy implications, recommended actions, and supporting evidence.

---

## Prerequisites

| Tool | Version | Purpose |
|---|---|---|
| Azure CLI (`az`) | 2.60+ | Infrastructure deployment |
| Docker | 24+ | Container build/push |
| Python | 3.10+ | Agent runtime, scripts |
| Git | 2.x | Repository management |

**Azure requirements:**
- Active Azure subscription with Contributor access
- Microsoft Entra ID tenant
- Microsoft Fabric capacity (F2+ or P1+) for Fabric data agent

Validate prerequisites:
```bash
bash scripts/validate_prereqs.sh
```

---

## Quick Start (Local)

```bash
# 1. Clone and configure
cd agentic-supplychain
cp .env.example .env
# Edit .env — fill in AZURE_SUBSCRIPTION_ID, AZURE_TENANT_ID, etc.

# 2. Validate prerequisites
bash scripts/validate_prereqs.sh

# 3. Deploy Azure infrastructure
bash infra/scripts/bootstrap-env.sh

# 4. Export deployment outputs
bash infra/scripts/export-deployment-outputs.sh

# 5. Configure model deployment (see manual checkpoint below)
# Then set MODEL_DEPLOYMENT_NAME in .env

# 6. Create capability host
bash infra/scripts/postprovision-capability-host.sh

# 7. Run orchestrator locally (with mock data)
cd agents/orchestrator
pip install -r requirements.txt
python -m app.main

# 8. Test
curl http://localhost:8088/health
curl -X POST http://localhost:8088/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Which suppliers have the most delays in the Northeast?"}'
```

---

## Deployment Order

| Step | Action | Type | Script/Runbook |
|---|---|---|---|
| 1 | Create resource group | **Fully automated** | `bootstrap-env.sh` |
| 2 | Deploy Bicep infrastructure | **Fully automated** | `bootstrap-env.sh` |
| 3 | Configure model deployment | **⚠️ Manual checkpoint** | `infra/scripts/configure-model-deployment.md` |
| 4 | Create capability host | **Partially automated** | `postprovision-capability-host.sh` |
| 5 | Load Fabric data & create data agent | **⚠️ Manual checkpoint** | `scripts/load_fabric_seed_files.md` |
| 6 | Upload knowledge docs & create Foundry IQ KB | **Partially automated** | `scripts/upload_search_documents.py` + `scripts/create_knowledge_base.py` |
| 7 | Build and push orchestrator image | **Fully automated** | See ACR Build/Push below |
| 8 | Deploy hosted agent to Foundry | **Partially automated** | `infra/scripts/deploy-hosted-agent.py` |
| 9 | Create/verify project connections | **Partially automated** | `infra/scripts/create-foundry-connections.py` |
| 10 | Validate and demo | **Fully automated** | `scripts/validate_deployment.py` |

---

## Detailed Steps

### Step 1–2: Infrastructure Deployment (Fully Automated)

```bash
# Login and deploy everything
bash infra/scripts/bootstrap-env.sh
bash infra/scripts/export-deployment-outputs.sh
```

Deploys: Foundry account, Foundry project, AI Search, ACR, Log Analytics, App Insights, RBAC.

### Step 3: Model Deployment (⚠️ Manual Checkpoint)

See `infra/scripts/configure-model-deployment.md` for detailed instructions.

**Portal path:** Azure AI Foundry → Project → Deployments → + Create → gpt-4o

After deploying, update `.env`:
```bash
MODEL_DEPLOYMENT_NAME=gpt-4o
```

### Step 4: Capability Host (Partially Automated)

```bash
bash infra/scripts/postprovision-capability-host.sh
```

If the `az rest` call fails, follow the portal instructions printed by the script.

### Step 5: Fabric Data Setup (⚠️ Manual Checkpoint)

Follow `scripts/load_fabric_seed_files.md`:
1. Create Fabric workspace on F2+/P1+ capacity
2. Create Lakehouse
3. Upload CSVs from `data/fabric/`
4. Create and publish Fabric data agent
5. Update `.env` with `FABRIC_DATA_AGENT_ENDPOINT`

### Step 6: Knowledge Base Setup (Partially Automated)

```bash
# Upload documents to Azure AI Search
python scripts/upload_search_documents.py

# Follow guided setup for Foundry IQ knowledge base
python scripts/create_knowledge_base.py
```

After setup, update `.env`:
```bash
FOUNDRY_IQ_MCP_URL=<your-mcp-endpoint>
```

### Step 7: ACR Build/Push (Fully Automated)

```bash
source .env && source .env.generated
az acr login --name "$ACR_NAME"
docker build -t "${ACR_LOGIN_SERVER}/orchestrator:latest" agents/orchestrator
docker push "${ACR_LOGIN_SERVER}/orchestrator:latest"
```

### Step 8: Deploy Hosted Agent (Partially Automated)

```bash
export AGENT_IMAGE="${ACR_LOGIN_SERVER}/orchestrator:latest"
python infra/scripts/deploy-hosted-agent.py
```

### Step 9: Project Connections (Partially Automated)

```bash
python infra/scripts/create-foundry-connections.py
```

### Step 10: Validate

```bash
python scripts/validate_deployment.py
python scripts/collect_outputs.py
```

---

## Configuration Matrix

| Variable | Required | Source | Example | Used In | When Available |
|---|---|---|---|---|---|
| `AZURE_SUBSCRIPTION_ID` | Yes | Azure Portal | `12345678-...` | .env, scripts, GH Actions | Before deploy |
| `AZURE_TENANT_ID` | Yes | Azure Portal | `87654321-...` | .env, scripts, GH Actions | Before deploy |
| `AZURE_CLIENT_ID` | For CI | App Registration | `abcdef12-...` | GH Actions OIDC | Before deploy |
| `AZURE_LOCATION` | Yes | Choice | `eastus` | .env, Bicep | Before deploy |
| `RESOURCE_GROUP_NAME` | Yes | Choice | `agentic-supplychain` | .env, scripts | Before deploy |
| `FOUNDRY_ACCOUNT_NAME` | Auto | Bicep output | `agentsc-foundry` | .env.generated | After Step 2 |
| `FOUNDRY_PROJECT_NAME` | Auto | Bicep output | `agentic-supplychain-project` | .env.generated | After Step 2 |
| `AZURE_AI_PROJECT_ENDPOINT` | Auto | Bicep output | `https://...` | .env.generated, agent | After Step 2 |
| `MODEL_DEPLOYMENT_NAME` | Yes | Manual Step 3 | `gpt-4o` | .env, agent | After Step 3 |
| `SEARCH_ENDPOINT` | Auto | Bicep output | `https://...search.windows.net` | .env.generated | After Step 2 |
| `ACR_NAME` | Auto | Bicep output | `agentscacr...` | .env.generated | After Step 2 |
| `ACR_LOGIN_SERVER` | Auto | Bicep output | `agentscacr...azurecr.io` | .env.generated | After Step 2 |
| `APPINSIGHTS_CONNECTION_STRING` | Auto | Bicep output | `InstrumentationKey=...` | .env.generated | After Step 2 |
| `FABRIC_WORKSPACE_NAME` | Yes | Fabric portal | `supply-chain-ws` | .env | After Step 5 |
| `FABRIC_DATA_AGENT_ENDPOINT` | Yes | Fabric portal | `https://...` | .env, agent | After Step 5 |
| `FOUNDRY_IQ_MCP_URL` | Yes | Foundry portal | `https://...` | .env, agent | After Step 6 |
| `AGENT_NAME` | Yes | Default | `orchestrator-agent` | .env, agent | Before deploy |
| `AGENT_IMAGE` | For deploy | Build step | `myacr.azurecr.io/orchestrator:latest` | deploy script | After Step 7 |

---

## Local Development

```bash
# Run orchestrator locally (uses mock data when endpoints not configured)
cd agents/orchestrator
pip install -r requirements.txt
python -m app.main
# Server starts on http://localhost:8088

# Run tests
cd agents/orchestrator
pytest
```

---

## GitHub Actions

Three workflows mirror the local deployment:

| Workflow | Trigger | Purpose |
|---|---|---|
| `infra-deploy.yml` | Push to `infra/**` or manual | Deploy Bicep infrastructure |
| `agent-build-deploy.yml` | Push to `agents/orchestrator/**` or manual | Build, push, deploy agent |
| `evals.yml` | Manual | Run evaluation dataset |

### Required GitHub Variables

Set in **Settings → Environments** or **Settings → Variables**:

- `AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_SUBSCRIPTION_ID`
- `AZURE_LOCATION`, `ACR_NAME`, `AZURE_AI_PROJECT_ENDPOINT`
- `MODEL_DEPLOYMENT_NAME`, `AGENT_NAME`, `AGENT_ID` (for evals)

---

## Troubleshooting

### Bicep deployment fails
```bash
# Check deployment status
az deployment group list --resource-group agentic-supplychain --output table
# View error details
az deployment group show --resource-group agentic-supplychain --name main --query properties.error
```

### Capability host creation fails
- Verify Foundry account name matches Bicep output
- Try portal: Azure AI Foundry → account → Settings → Capability Host
- Ensure API version `2025-04-01-preview` is available in your region

### ACR push fails
```bash
az acr login --name "$ACR_NAME"
# If token expired, re-login to Azure: az login
```

### Agent deployment fails
- Verify `AZURE_AI_PROJECT_ENDPOINT` is correct
- Verify model deployment exists: check via portal or `az cognitiveservices account deployment list`
- Check that capability host is provisioned with public hosting enabled

### Hosted agent identity changes after publish
- After publishing, Foundry may create a new identity
- Re-run RBAC assignments for the new identity's principal ID
- Check: `az role assignment list --resource-group agentic-supplychain --output table`

### Mock data appears instead of live data
- The orchestrator returns mock data when `FABRIC_DATA_AGENT_ENDPOINT` or `FOUNDRY_IQ_MCP_URL` are empty
- Set these values after completing Steps 5 and 6

---

## Demo Walkthrough

See `scripts/demo_walkthrough.md` for the full scenario walkthrough, or run:

```bash
# Start local orchestrator
cd agents/orchestrator && python -m app.main &

# Scenario 1: Structured data
curl -s -X POST http://localhost:8088/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Which suppliers have the most delays in the Northeast?"}' | python3 -m json.tool

# Scenario 2: Knowledge
curl -s -X POST http://localhost:8088/query \
  -H "Content-Type: application/json" \
  -d '{"question": "What are the contract penalty terms for late deliveries?"}' | python3 -m json.tool

# Scenario 3: Mixed (full MVP)
curl -s -X POST http://localhost:8088/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Which suppliers are causing the most delay, what inventory is at risk, and what actions should we take?"}' | python3 -m json.tool
```

---

## Platform Constraints

- **Fabric workspace/data agent creation** requires portal-driven steps
- **Foundry model deployment** automation varies by region and API version maturity
- **Hosted agent identity may change** after publish, requiring new RBAC assignments
- **Foundry IQ** requires Azure AI Search + knowledge setup (not pure Bicep)
- **Some Foundry/Fabric integrations** are partially automated rather than fully scriptable

---

## Copy/Paste Commands Block

```bash
# ── FRESH TERMINAL SESSION ──────────────────────────────────

# Clone and setup
cd agentic-supplychain
cp .env.example .env
# >>> EDIT .env: set AZURE_SUBSCRIPTION_ID, AZURE_TENANT_ID <<<

# Validate
bash scripts/validate_prereqs.sh

# Deploy infra
bash infra/scripts/bootstrap-env.sh
bash infra/scripts/export-deployment-outputs.sh

# >>> MANUAL: Configure model deployment (see infra/scripts/configure-model-deployment.md) <<<
# >>> Then set MODEL_DEPLOYMENT_NAME=gpt-4o in .env <<<

# Capability host
bash infra/scripts/postprovision-capability-host.sh

# >>> MANUAL: Setup Fabric (see scripts/load_fabric_seed_files.md) <<<
# >>> Then set FABRIC_WORKSPACE_NAME and FABRIC_DATA_AGENT_ENDPOINT in .env <<<

# Knowledge base
python scripts/upload_search_documents.py
python scripts/create_knowledge_base.py
# >>> Then set FOUNDRY_IQ_MCP_URL in .env <<<

# Build and push container
source .env && source .env.generated
az acr login --name "$ACR_NAME"
docker build -t "${ACR_LOGIN_SERVER}/orchestrator:latest" agents/orchestrator
docker push "${ACR_LOGIN_SERVER}/orchestrator:latest"

# Deploy hosted agent
export AGENT_IMAGE="${ACR_LOGIN_SERVER}/orchestrator:latest"
python infra/scripts/deploy-hosted-agent.py

# Create connections
python infra/scripts/create-foundry-connections.py

# Validate
python scripts/validate_deployment.py
python scripts/collect_outputs.py

# Local test
cd agents/orchestrator
pip install -r requirements.txt
python -m app.main &
curl http://localhost:8088/health
curl -s -X POST http://localhost:8088/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Which suppliers are causing the most delay and what should we do?"}' | python3 -m json.tool
```
