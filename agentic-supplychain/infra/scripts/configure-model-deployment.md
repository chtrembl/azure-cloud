# Configure Model Deployment

## ⚠️ MANUAL CHECKPOINT

Model deployment in Azure AI Foundry may require portal-driven steps depending on your region and API version maturity.

## Option A: Azure Portal (Recommended for first setup)

1. Go to [Azure AI Foundry](https://ai.azure.com)
2. Select your project: **agentic-supplychain-project**
3. Navigate to **Deployments** → **+ Create deployment**
4. Select model: **gpt-4o** (or your preferred model)
5. Choose deployment type: **Standard** or **Global Standard**
6. Set deployment name (e.g., `gpt-4o`)
7. Set tokens-per-minute rate limit (10K+ recommended for demo)
8. Click **Deploy**

## Option B: Azure CLI (if supported in your region)

```bash
# Set variables
FOUNDRY_ACCOUNT_NAME="<from .env.generated>"
FOUNDRY_PROJECT_NAME="agentic-supplychain-project"
MODEL_NAME="gpt-4o"
DEPLOYMENT_NAME="gpt-4o"

az cognitiveservices account deployment create \
  --name "$FOUNDRY_ACCOUNT_NAME" \
  --resource-group agentic-supplychain \
  --deployment-name "$DEPLOYMENT_NAME" \
  --model-name "$MODEL_NAME" \
  --model-version "2024-08-06" \
  --model-format OpenAI \
  --sku-capacity 10 \
  --sku-name Standard
```

## After deployment

Update your `.env` file:

```bash
MODEL_DEPLOYMENT_NAME=gpt-4o
```

## Verification

```bash
# Check deployment exists
az cognitiveservices account deployment list \
  --name "$FOUNDRY_ACCOUNT_NAME" \
  --resource-group agentic-supplychain \
  --output table
```

## Variables produced

| Variable | Value | Where to set |
|---|---|---|
| `MODEL_DEPLOYMENT_NAME` | Your chosen deployment name (e.g., `gpt-4o`) | `.env`, GitHub Actions vars |
