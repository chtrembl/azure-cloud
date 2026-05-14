# Agentic Supply Chain Demo Spec

This specification defines a complete reference implementation for an **agentic supply-chain demo** that showcases four capabilities together:

- **Agent orchestration**
- **Knowledge grounding with Azure Foundry IQ**
- **Shared governed data with Microsoft Fabric**
- **Pro-code hosted agents in Python**

The target deployment model is a **new Azure resource group named `agentic-supplychain`** containing the Azure-side infrastructure deployed with **Bicep**, plus a GitHub repository structure that contains the Python hosted agent(s), sample data files, sample knowledge-base files, deployment automation, validation scripts, and GitHub Actions workflows. [file:1]

---

## Purpose

The desired outcome is a demo environment where a user asks a business question such as:

> Which suppliers are causing the most delay in the Northeast, what inventory is at risk, and what policy-backed actions should operations take?

The response must be grounded in both:

- **Fabric structured data**
- **Foundry IQ / Azure AI Search-backed knowledge content**

The demo must prove these four areas:

1. **Agent orchestration** via a hosted Python orchestrator agent and optional specialist agent.
2. **Knowledge retrieval** via Foundry IQ backed by Azure AI Search.
3. **Governed structured analytics** via a Fabric data agent over curated sample data.
4. **Pro-code extensibility** via hosted Python agents packaged as containers and deployed to Azure Foundry Agent Service. [file:1]

---

## Architecture

The recommended architecture is:

1. A client or lightweight app authenticates the user with **Microsoft Entra ID** and calls the primary orchestrator.
2. The **hosted orchestrator agent** runs in Foundry Agent Service as a containerized Python service.
3. The orchestrator calls:
   - a **Fabric data agent tool** for structured analytics and governed business metrics,
   - a **Foundry IQ / MCP knowledge tool** for policy, contract, SLA, and SOP retrieval.
4. Optional specialist agents may be added for risk analysis or recommendation composition, but they are not required for the first working MVP.
5. Azure-side telemetry flows to **Application Insights** and **Log Analytics** for hosted-agent observability. [file:1]

---

## Implementation contract

This section is authoritative for GitHub Copilot CLI or any code-generation tool.

### General rule

Generate **every required file** in this spec unless explicitly marked optional.

Do not:
- omit named files,
- create empty stub files,
- replace implementation with TODO-only placeholders,
- overstate automation for steps that currently require manual or portal-driven operations.

### Delivery standard

For each file generated:

- include working content, not blank placeholders,
- include comments only where they help the operator understand configuration, identity, environment variables, or post-provisioning,
- use realistic defaults where safe,
- parameterize environment-dependent values,
- ensure repo structure, scripts, and documentation are internally consistent.

### Automation classification

Every step in the README and scripts must be clearly classified as one of:

- **Fully automated** — can run end-to-end from local machine or GitHub Actions.
- **Partially automated** — scriptable, but requires operator-supplied IDs, endpoints, or approval.
- **Manual checkpoint** — requires portal action or currently unstable preview functionality.

### Definition of done

The generated solution is complete only if it provides:

1. Azure infrastructure deployment from a developer laptop using Azure CLI + Bicep.
2. A generated repo with all required folders, files, scripts, workflows, and configuration assets.
3. A working Python orchestrator service with modular tools, config, prompts, tests, Dockerfile, and deployment definition.
4. Realistic Fabric CSV sample data and knowledge-base markdown content.
5. Helper scripts and runbooks for Foundry connections, knowledge seeding, and Fabric setup.
6. GitHub Actions that mirror the local deployment path.
7. A README that allows a fresh operator to provision, configure, deploy, validate, and demo the MVP.
8. A validation path for the end-to-end demo scenarios.

### Placeholder inventory requirement

Every file that uses configurable values must clearly indicate:

- the placeholder or variable name,
- what it is for,
- where it is used,
- how it is obtained,
- whether it is known before deployment,
- whether it is output by deployment,
- whether it must be collected from Fabric/Foundry after setup.

### Local-first requirement

Optimize for a **local laptop-driven workflow first**. GitHub Actions should mirror the same steps for CI/CD, but the repository must be operable locally without requiring GitHub Actions.

---

## Azure resources required

The Azure-side bill of materials in the `agentic-supplychain` resource group must include:

| Resource | Purpose | Required | Notes |
|---|---|---|---|
| `Microsoft.Resources/resourceGroups` | Deployment container named `agentic-supplychain` | Yes | Primary resource group |
| `Microsoft.CognitiveServices/accounts` | Azure AI / Foundry account | Yes | Must support project management and model deployments |
| `Microsoft.CognitiveServices/accounts/projects` | Foundry project | Yes | Use system-assigned managed identity |
| Model deployment in Foundry | LLM for orchestrator and evals | Yes | May require manual checkpoint depending on API/region |
| `Microsoft.Search/searchServices` | Azure AI Search | Yes | Required for Foundry IQ knowledge bases |
| `Microsoft.Insights/components` | Application Insights | Yes | Monitoring and hosted-agent connection |
| `Microsoft.OperationalInsights/workspaces` | Log Analytics | Yes | Backing store for logs and App Insights |
| `Microsoft.ContainerRegistry/registries` | Azure Container Registry | Yes | Stores hosted agent image |
| `Microsoft.ManagedIdentity/userAssignedIdentities` | Optional deployment identity | Recommended | Useful for automation or expansion |
| Role assignments | RBAC wiring | Yes | Needed for Foundry project identity, ACR pull, logs, and operators |

These resources align with the infrastructure foundation and identity needs already described in the original spec. [file:1]

---

## External-but-required platform dependencies

The following dependencies are required but are **not expected to be fully provisioned in the same Azure Bicep deployment**:

| Platform element | Purpose | Provisioning approach |
|---|---|---|
| Microsoft Fabric workspace | Shared data plane | Create in Fabric portal/admin |
| Fabric capacity (F2+ or P1+) | Required for Fabric data agents | Must exist before demo setup |
| Fabric data agent | Structured data tool for Foundry | Create after sample data is loaded |
| Foundry IQ knowledge base | Enterprise retrieval layer | Create after Search exists and docs are loaded/indexed |
| Account-level capability host with public hosting enabled | Required for hosted agents | Post-provisioning step |
| Hosted agent versions/publishing | Container-based deployment | Post-provisioning CLI/SDK step |

The generated repo must treat these as **partially automated** or **manual checkpoint** items rather than pretending they are all pure Bicep deploys. [file:1]

---

## Identity and authorization model

The design must use **both user identity and agent identity**.

### Identity rules

- The **user** signs into the app or caller with Entra ID and interacts using delegated identity where applicable.
- The **Foundry project managed identity** is used by unpublished hosted agents and needs ACR pull and observability-related permissions.
- Calls to the **Fabric data agent** should preserve user context / on-behalf-of identity where supported so Fabric can enforce permissions such as RLS/CLS.
- Foundry IQ should preserve permission-aware retrieval when configured with supported permission-aware sources.
- After publishing a hosted agent, Foundry may provision a **new agent identity**, and permissions may need to be re-granted to that published identity.

### Minimum RBAC assignments

At minimum, document or create:

| Principal | Scope | Role | Purpose |
|---|---|---|---|
| Deployment operator / GitHub OIDC principal | Resource group or subscription | Contributor or scoped deployment role | Deploy infra and configure services |
| Foundry project managed identity | Azure Container Registry | Container Registry Repository Reader | Pull hosted agent image |
| Foundry project managed identity | Log Analytics workspace | Log Analytics Data Reader | Hosted-agent observability |
| Demo users | Foundry project | Azure AI User or equivalent | Interact with agent runtime |
| Demo users | Fabric data agent and underlying Fabric data | Read + underlying source access | Data agent sharing alone is insufficient |

This identity split is part of the original spec and must be preserved. [file:1]

---

## Repository structure to generate

Generate this repository structure:

```text
agentic-supplychain/
├── README.md
├── azure.yaml
├── .env.example
├── infra/
│   ├── main.bicep
│   ├── main.parameters.json
│   ├── modules/
│   │   ├── foundry-account.bicep
│   │   ├── foundry-project.bicep
│   │   ├── search.bicep
│   │   ├── acr.bicep
│   │   ├── log-analytics.bicep
│   │   ├── app-insights.bicep
│   │   └── role-assignments.bicep
│   └── scripts/
│       ├── bootstrap-env.sh
│       ├── postprovision-capability-host.sh
│       ├── create-foundry-connections.py
│       ├── deploy-hosted-agent.py
│       ├── configure-model-deployment.md
│       └── export-deployment-outputs.sh
├── agents/
│   ├── orchestrator/
│   │   ├── app/
│   │   │   ├── main.py
│   │   │   ├── orchestrator.py
│   │   │   ├── config.py
│   │   │   ├── models.py
│   │   │   ├── tools/
│   │   │   │   ├── fabric_tool.py
│   │   │   │   ├── knowledge_tool.py
│   │   │   │   └── synthesis.py
│   │   │   └── prompts/
│   │   │       ├── system.txt
│   │   │       └── routing.txt
│   │   ├── requirements.txt
│   │   ├── Dockerfile
│   │   ├── agent.yaml
│   │   ├── pytest.ini
│   │   └── tests/
│   │       └── test_orchestrator.py
│   ├── risk-analyst/
│   │   ├── app/main.py
│   │   ├── requirements.txt
│   │   ├── Dockerfile
│   │   └── agent.yaml
│   └── common/
│       └── models.py
├── data/
│   ├── fabric/
│   │   ├── suppliers.csv
│   │   ├── products.csv
│   │   ├── purchase_orders.csv
│   │   ├── shipments.csv
│   │   ├── inventory_positions.csv
│   │   ├── warehouses.csv
│   │   └── incidents.csv
│   ├── knowledge/
│   │   ├── contracts/
│   │   │   ├── apex_supplier_master_agreement.md
│   │   │   ├── northstar_logistics_sla.md
│   │   │   └── delta_components_terms.md
│   │   ├── policies/
│   │   │   ├── expedited_shipping_policy.md
│   │   │   ├── supplier_escalation_runbook.md
│   │   │   └── alternate_supplier_approval_policy.md
│   │   └── procedures/
│   │       ├── warehouse_receiving_sop.md
│   │       └── shortage_response_playbook.md
│   └── evals/
│       ├── demo-questions.jsonl
│       └── eval-config.json
├── scripts/
│   ├── generate_sample_data.py
│   ├── upload_search_documents.py
│   ├── create_knowledge_base.py
│   ├── validate_prereqs.sh
│   ├── validate_deployment.py
│   ├── collect_outputs.py
│   ├── demo_walkthrough.md
│   └── load_fabric_seed_files.md
└── .github/
    └── workflows/
        ├── infra-deploy.yml
        ├── agent-build-deploy.yml
        └── evals.yml
```

Notes:
- `risk-analyst/` is optional.
- All other files are required unless explicitly documented otherwise.
- Additional helper files may be added if they improve operability, but they must not replace required files. [file:1]

---

## Bicep specification

The Bicep deployment creates the Azure-side foundation only.

### `infra/main.bicep`

The main Bicep file must:

- target `resourceGroup` scope,
- create the Foundry account,
- create the Foundry project with `identity: { type: 'SystemAssigned' }`,
- create Azure AI Search,
- create Azure Container Registry,
- create Log Analytics,
- create Application Insights,
- create RBAC assignments for the project managed identity,
- output:
  - project endpoint,
  - project name,
  - project principal ID,
  - ACR login server,
  - ACR resource ID,
  - Search endpoint,
  - Search resource ID,
  - App Insights connection string,
  - Log Analytics workspace ID.

A representative baseline shape should follow the original spec’s structure and module pattern. [file:1]

### Required modules

- `foundry-account.bicep`
- `foundry-project.bicep`
- `search.bicep`
- `acr.bicep`
- `log-analytics.bicep`
- `app-insights.bicep`
- `role-assignments.bicep`

### Role assignment module requirements

At minimum assign:

- **Container Registry Repository Reader** to the Foundry project managed identity at ACR scope.
- **Log Analytics Data Reader** to the Foundry project managed identity at Log Analytics scope.

Make the role assignment module extensible for future published-agent identity assignments. [file:1]

---

## Post-provisioning steps to encode

Not everything should be done in pure Bicep. The repo must include explicit automation or operator guidance for the remaining setup.

### Step 1: Create resource group

```bash
az group create --name agentic-supplychain --location eastus
```

### Step 2: Deploy Bicep

```bash
az deployment group create \
  --resource-group agentic-supplychain \
  --template-file infra/main.bicep \
  --parameters @infra/main.parameters.json
```

### Step 3: Configure model deployment

Provide either:
- a scriptable path if stable in the target environment, or
- a runbook with exact portal / CLI / SDK steps if not fully stable.

Persist the chosen deployment name into `.env`, `.env.generated`, or GitHub environment variables.

### Step 4: Create account-level capability host

Include `infra/scripts/postprovision-capability-host.sh` using `az rest` to create the capability host with public hosting enabled.

### Step 5: Create Foundry project connections

Include `infra/scripts/create-foundry-connections.py` to create or help create connections for:

- Azure Container Registry
- Application Insights
- Fabric data agent
- Foundry IQ MCP RemoteTool connection

### Step 6: Build and push hosted-agent container

Support local and CI flow:

1. build Docker image,
2. push to ACR,
3. deploy hosted agent version using Python SDK or REST.

### Step 7: Deploy hosted agent definition

Include `infra/scripts/deploy-hosted-agent.py` that uses the Foundry Python SDK and accepts environment variables such as:

- `AZURE_AI_PROJECT_ENDPOINT`
- `MODEL_DEPLOYMENT_NAME`
- `FABRIC_DATA_AGENT_ENDPOINT`
- `FOUNDRY_IQ_MCP_URL`
- `AGENT_IMAGE`
- `AGENT_NAME`

These post-provisioning expectations are already indicated in the original spec and should remain explicit. [file:1]

---

## Python hosted-agent specification

The first required agent is the **orchestrator**.

### Runtime requirements

- Python 3.10+ minimum
- preferably Python 3.11 in Docker image
- FastAPI-based or equivalent adapter for local testing and hosted deployment

### Responsibilities

The orchestrator must:

- accept a business question,
- classify it as structured, knowledge, or mixed,
- call the Fabric data-agent tool when structured analytics are needed,
- call the Foundry IQ / knowledge tool when policy or document retrieval is needed,
- synthesize a grounded answer with sections such as:
  - findings,
  - policy implications,
  - recommended actions,
  - supporting evidence or notes.

These behaviors directly reflect the original orchestration and synthesis goals. [file:1]

### Minimum package requirements

`requirements.txt` must include at least:

```txt
azure-ai-projects
azure-identity
fastapi
uvicorn
httpx
pydantic
python-dotenv
```

Add test dependencies and any Agent Framework packages only if used intentionally. [file:1]

### Runtime shape

Generate:

- `main.py` for app entrypoint / server exposure,
- `orchestrator.py` for routing and synthesis logic,
- `config.py` for environment/config loading,
- `tools/fabric_tool.py`,
- `tools/knowledge_tool.py`,
- `tools/synthesis.py`,
- `tests/test_orchestrator.py`.

### Core logic shape

The implementation should roughly support a pattern like:

```python
async def handle_query(user_query: str, user_context: dict) -> dict:
    route = classify_query(user_query)

    fabric_result = None
    kb_result = None

    if route in ("structured", "mixed"):
        fabric_result = await query_fabric_agent(user_query, user_context)

    if route in ("knowledge", "mixed"):
        kb_result = await query_foundry_iq(user_query, user_context)

    return synthesize_response(
        question=user_query,
        fabric_result=fabric_result,
        knowledge_result=kb_result,
    )
```

This mirrors the control flow already outlined in the original spec. [file:1]

---

## Dockerfile specification

Each hosted agent must include a Dockerfile equivalent to:

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY app ./app
ENV PYTHONUNBUFFERED=1
EXPOSE 8088
CMD ["python", "-m", "app.main"]
```

The image must be suitable for:
- local testing,
- ACR push,
- hosted-agent deployment. [file:1]

---

## `agent.yaml` specification

Each hosted agent must include an `agent.yaml` that defines:

- service name,
- display name,
- image reference placeholder,
- protocol,
- environment variables,
- version,
- health endpoint if applicable.

Include placeholders for:

- project endpoint,
- model deployment name,
- App Insights connection string,
- Fabric agent endpoint,
- Foundry IQ MCP endpoint,
- agent display name,
- agent version. [file:1]

---

## Fabric sample data specification

The demo data should be intentionally shaped to produce obvious insights.

### Required tables

- `suppliers.csv`
- `products.csv`
- `purchase_orders.csv`
- `shipments.csv`
- `inventory_positions.csv`
- `warehouses.csv`
- `incidents.csv`

### Data-shaping requirements

The data must encode:

- one supplier with chronic late deliveries,
- one Northeast warehouse near stockout,
- one product family with substitute suppliers,
- one supplier tied to a contract with penalties and escalation thresholds,
- one scenario where policy allows expedited shipping with director approval.

These patterns are already explicitly called for in the original spec and should be preserved. [file:1]

### Example rows

Use the original spec’s example structure for `suppliers.csv` and `inventory_positions.csv` as a baseline. [file:1]

### Fabric runbook requirement

Include a markdown runbook describing how to:

1. create or identify a Fabric workspace on supported capacity,
2. create a Lakehouse or Warehouse,
3. load the CSVs,
4. create a curated semantic model,
5. create the Fabric data agent,
6. grant required access to demo users,
7. capture the data agent endpoint/connection details.

---

## Knowledge-base sample document specification

The knowledge corpus must be authored in markdown and include:

### Contracts
- `apex_supplier_master_agreement.md`
- `northstar_logistics_sla.md`
- `delta_components_terms.md`

### Policies
- `expedited_shipping_policy.md`
- `supplier_escalation_runbook.md`
- `alternate_supplier_approval_policy.md`

### Procedures
- `warehouse_receiving_sop.md`
- `shortage_response_playbook.md`

### Content rules

The knowledge docs must include facts not available in Fabric tables, such as:

- penalties beginning after 10 days late,
- escalation thresholds,
- alternate supplier approvals,
- expedited shipping approval authority.

That distinction is necessary to prove the orchestrator needs both Fabric and Foundry IQ. [file:1]

### Knowledge setup runbook

Include steps to:

1. upload or index markdown docs into Azure AI Search,
2. create the knowledge base,
3. connect it to Foundry IQ,
4. create a Foundry project `RemoteTool` connection,
5. restrict `allowed_tools` appropriately.

---

## GitHub Actions specification

Generate three workflows.

### `infra-deploy.yml`

Must:

- support `workflow_dispatch`,
- optionally support push trigger for `infra/**`,
- use GitHub OIDC and `azure/login@v2`,
- create the resource group if needed,
- deploy `infra/main.bicep`.

This aligns with the original workflow expectations. [file:1]

### `agent-build-deploy.yml`

Must:

- support local-equivalent CI path,
- build the orchestrator image,
- log in to ACR,
- push the image,
- run the hosted-agent deployment script.

Use variables such as:

- `AZURE_CLIENT_ID`
- `AZURE_TENANT_ID`
- `AZURE_SUBSCRIPTION_ID`
- `AZURE_AI_PROJECT_ENDPOINT`
- `MODEL_DEPLOYMENT_NAME`
- `ACR_NAME`
- `AGENT_NAME`

### `evals.yml`

Must:

- support `workflow_dispatch`,
- authenticate with Azure,
- execute the evaluation dataset against the deployed agent,
- use the configured project endpoint, model deployment, and agent ID.

---

## Validation assets

In addition to the original spec, generate:

- `scripts/validate_prereqs.sh`
- `scripts/validate_deployment.py`
- `scripts/collect_outputs.py`
- `scripts/demo_walkthrough.md`

These files must help verify:

- prerequisite tooling is installed,
- Azure deployment outputs are present,
- required endpoints and settings have been captured,
- hosted-agent image and deployment are aligned,
- demo scenarios can be exercised in order.

---

## Required environment variables

Include a `.env.example` with at least:

```env
AZURE_SUBSCRIPTION_ID=
AZURE_TENANT_ID=
AZURE_CLIENT_ID=
AZURE_LOCATION=eastus
RESOURCE_GROUP_NAME=agentic-supplychain
FOUNDRY_ACCOUNT_NAME=
FOUNDRY_PROJECT_NAME=agentic-supplychain-project
AZURE_AI_PROJECT_ENDPOINT=
MODEL_DEPLOYMENT_NAME=
SEARCH_SERVICE_NAME=
SEARCH_ENDPOINT=
ACR_NAME=
APPINSIGHTS_CONNECTION_STRING=
FABRIC_WORKSPACE_NAME=
FABRIC_DATA_AGENT_ENDPOINT=
FOUNDRY_IQ_MCP_URL=
AGENT_NAME=orchestrator-agent
```

Also include any additional values required by the generated scripts, agent runtime, and workflows. This minimum list comes from the original spec and should be extended only as needed. [file:1]

---

## Configuration matrix requirement

The README must include a configuration matrix like:

| Variable | Required | Source | Example | Used in | When available |
|---|---|---|---|---|---|

This table must cover:
- Bicep parameters,
- environment variables,
- workflow variables,
- deployment outputs,
- manual/post-setup values from Fabric and Foundry.

---

## README requirements

The README must include:

- architecture summary,
- prerequisites,
- local tooling requirements,
- Azure setup and login requirements,
- exact deployment order,
- local execution commands,
- post-provisioning steps,
- Fabric setup steps,
- Foundry IQ setup steps,
- ACR build/push steps,
- hosted-agent deployment steps,
- connection setup steps,
- validation steps,
- troubleshooting,
- demo walkthrough,
- assumptions and current platform limitations.

The README must be sufficient for a fresh operator to recreate the MVP in a new environment.

---

## Deployment order

The README and scripts must guide the operator through this order:

1. Create the `agentic-supplychain` resource group.
2. Deploy Azure resources via Bicep.
3. Configure the Foundry model deployment.
4. Create the Foundry capability host for hosted-agent runtime.
5. Load sample Fabric data and publish the Fabric data agent.
6. Load knowledge documents and create the Azure AI Search / Foundry IQ knowledge setup.
7. Build and push the orchestrator image to ACR.
8. Deploy the hosted agent version to Foundry.
9. Create or verify project connections to Fabric, ACR, App Insights, and Foundry IQ.
10. Run validations and execute demo prompts.

This deployment order is carried forward from the original spec and should remain the canonical path. [file:1]

---

## Practical constraints

The generated project must explicitly acknowledge these constraints:

- Fabric workspace and Fabric data-agent creation may still require portal-driven steps.
- Foundry model deployment automation may vary by region, API version, or preview maturity.
- Hosted-agent identity may change after publish, requiring new RBAC assignments.
- Foundry IQ depends on Azure AI Search and is not complete without Search + knowledge setup.
- Some Foundry/Fabric integration details may be partially automated rather than fully automated.

These constraints are already present in the original spec and should be preserved to avoid overpromising. [file:1]

---

## Prompt to hand to GitHub Copilot CLI

Use the following prompt verbatim:

```text
Use @docs/agentic-supplychain-spec.md as the authoritative implementation spec and do not omit any required artifact named in that spec.

Generate the full repository required for the MVP demo, not just a scaffold. Create every required file named in the spec with working content. If any Azure Foundry or Microsoft Fabric step cannot be fully automated, still generate the surrounding scripts and documentation, and mark the exact manual checkpoint with precise instructions, inputs, outputs, and validation steps.

Primary objective:
Produce the full repository, scripts, infrastructure-as-code, app code, deployment automation, sample data, documentation, and validation assets needed so an operator can provision Azure resources from a local machine, configure the remaining Foundry/Fabric dependencies, deploy the hosted orchestrator agent, and walk through the end-to-end demo scenarios.

Critical rules:
1. Generate every required file in the spec. Do not skip files. Do not collapse multiple required files into one.
2. Do not leave empty placeholders or TODO-only stubs. If something cannot be fully automated because of current platform constraints, implement the surrounding script/framework and clearly mark the exact manual checkpoint.
3. Prefer executable local workflows first, then mirror them in GitHub Actions.
4. Every configurable value must be parameterized via Bicep parameters, environment variables, or config files.
5. In every file that uses placeholders, add comments that explain:
   - what the variable is,
   - where it is used,
   - how it is obtained,
   - whether it is known before deployment, output by deployment, or gathered manually afterward.
6. Make the repository runnable in phases, but ensure the final result supports the full MVP demo.
7. Use eastus defaults unless the spec or parameters override them.
8. Keep the solution aligned to Azure Foundry hosted agents, Azure AI Search-backed Foundry IQ, Microsoft Fabric data agent integration, Application Insights, Log Analytics, and ACR.
9. Include practical safeguards for current platform realities where some Foundry/Fabric steps may be partially automated or require manual portal actions.

Build in these phases.

Phase 1:
- Create the full folder structure.
- Create README.md with prerequisites, architecture summary, deployment order, local execution steps, GitHub Actions usage, troubleshooting, and demo walkthrough.
- Create .env.example with all required variables from the spec plus comments describing source and purpose.
- Create azure.yaml if specified by the spec.
- Create infra/main.bicep, infra/main.parameters.json, and all Bicep modules.
- Create infra/scripts/bootstrap-env.sh and any script needed to capture deployment outputs into local env files.
- Create post-provisioning scripts for capability host setup and project/resource connection scaffolding.
- After Phase 1, summarize created files, assumptions, manual checkpoints, and missing external prerequisites.

Phase 2:
- Create the Python orchestrator agent with modular structure, config, prompts, tools, synthesis logic, tests, requirements, Dockerfile, and agent.yaml.
- Create deployment scripts for local docker build/push and hosted-agent deployment into Foundry.
- Make the orchestrator code clearly separate structured retrieval, knowledge retrieval, and final synthesis.
- Add basic unit tests for routing behavior and synthesis formatting.
- After Phase 2, summarize created files, assumptions, manual checkpoints, and how to run tests locally.

Phase 3:
- Create all sample Fabric CSV files with realistic demo patterns from the spec.
- Create all knowledge-base markdown files for contracts, policies, procedures, and playbooks.
- Create helper scripts for sample data generation, document upload/indexing, and knowledge-base creation scaffolding.
- Create Fabric and Foundry IQ runbooks with exact operator steps and required IDs/endpoints.
- Create evaluation dataset/config files.
- After Phase 3, summarize created files, assumptions, manual checkpoints, and demo data/knowledge patterns.

Phase 4:
- Create GitHub Actions workflows for infra deploy, agent build/deploy, and evals.
- Ensure workflows mirror the local deployment path and use OIDC plus repo/environment variables.
- Add validation scripts or commands that verify key deployment outputs, expected connections, and scenario readiness.
- After Phase 4, summarize created files, assumptions, manual checkpoints, required secrets/variables, and exact run order.

Completion requirements:
- Include exact commands to run locally from a developer laptop for provisioning, post-provisioning, container build/push, hosted-agent deployment, seed-data setup, and testing.
- Include a consolidated configuration matrix that lists every variable, parameter, secret, endpoint, resource name, and ID required; where it is defined; how it is obtained; and when it becomes available.
- Include a demo walkthrough showing how to validate each required scenario end-to-end.
- Clearly label which parts are:
  - fully automated,
  - partially automated,
  - manual because of platform limitations.
- Do not claim a step is fully automated if it depends on a portal-only or preview-only operation without a reliable scripted path.

When generating code:
- Favor clarity and deployability over cleverness.
- Use comments sparingly but include high-value operator guidance around configuration, identity, endpoints, and post-deploy outputs.
- Keep paths exactly aligned with the spec unless there is a strong reason to improve them; if improved, update README references consistently.
```

---

## Minimum success criteria

The generated solution is complete only if it supports this demonstrable flow:

1. The user asks which suppliers are driving delivery delays and what inventory is at risk.
2. The orchestrator queries Fabric and returns a ranked structured answer.
3. The user asks what policies, contracts, or SOPs apply to the worst case.
4. The orchestrator queries Foundry IQ and returns a grounded knowledge-backed answer.
5. The orchestrator synthesizes both into recommended next actions such as expedite, escalate, or use alternate supplier with approval.

That end-to-end story is the core MVP and is the same success path defined by your original spec. [file:1]