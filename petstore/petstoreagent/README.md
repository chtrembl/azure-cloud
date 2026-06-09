# 🐾 PetStore Retail Agent

An AI-powered retail orchestrator agent built on **Azure AI Foundry Agent Service** with **Microsoft Fabric Data Agent** integration. This agent provides real-time retail intelligence for the PetStore business.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Azure AI Foundry                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │          PetStore Retail Agent (Orchestrator)      │  │
│  │                                                   │  │
│  │  ┌─────────────┐  ┌────────────┐  ┌───────────┐  │  │
│  │  │  Inventory  │  │   Orders   │  │  Recommend │  │  │
│  │  │    Tools    │  │   Tools    │  │    Tools   │  │  │
│  │  └─────────────┘  └────────────┘  └───────────┘  │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │         Microsoft Fabric Data Agent               │  │
│  │     (Enterprise Data Warehouse Queries)           │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Features

- **Fabric Data Agent Integration** — Natural language queries against your enterprise data in Microsoft Fabric (sales, inventory, customer analytics)
- **Custom Function Tools** — Full control over business logic with Python functions for inventory, orders, and recommendations
- **Azure AI Foundry Deployment** — Production-grade hosting with built-in auth, telemetry, and scaling
- **Interactive CLI** — Run locally for development and testing

## Quick Start

### Prerequisites

- Python 3.10+
- Azure subscription with AI Foundry project
- Microsoft Fabric workspace with a published Data Agent
- Azure CLI authenticated (`az login`)

### Setup

```bash
cd petstore/petstoreagent

# Create virtual environment
python -m venv .venv
source .venv/bin/activate  # or .venv\Scripts\activate on Windows

# Install dependencies
pip install -e ".[dev]"

# Configure environment
cp .env.sample .env
# Edit .env with your Azure AI Foundry endpoint and Fabric Data Agent IDs
```

### Run

```bash
# Interactive mode
python -m src.main

# Run tests
pytest
```

### Example Interactions

```
🛒 You: What's the stock level of premium dog food at the NYC store?
🤖 Agent: Premium Dog Food 20lb (PROD-045) at STORE-NYC-01 has 3 units remaining,
   which is below the reorder point of 15. Would you like me to trigger a reorder?

🛒 You: What are our top trending products this week?
🤖 Agent: Based on Fabric analytics, here are the top trending products (7-day window):
   1. Smart Pet Feeder WiFi — +145% sales growth
   2. Squeaky Bone Toy — +89% sales growth
   3. Calming Pet Bed Large — +67% sales growth

🛒 You: Show me total revenue by category for Q2
🤖 Agent: [Queries Fabric Data Agent for real-time warehouse data]
   Q2 Revenue by Category:
   - Food & Nutrition: $234,500
   - Toys & Enrichment: $89,200
   - Health & Wellness: $67,800
```

## Project Structure

```
petstoreagent/
├── pyproject.toml          # Project config and dependencies
├── .env.sample             # Environment variable template
├── README.md
├── src/
│   ├── __init__.py
│   ├── config.py           # Configuration management
│   ├── agent.py            # Core agent orchestrator
│   └── main.py             # CLI entry point
├── tools/
│   ├── __init__.py
│   ├── inventory.py        # Stock management functions
│   ├── orders.py           # Order processing functions
│   └── recommendations.py  # Product recommendation functions
└── tests/
    ├── __init__.py
    └── test_tools.py       # Unit tests for custom tools
```

## Deploying to Azure AI Foundry

The agent is designed for deployment via the Azure AI Foundry Agent Service:

1. **Create a Foundry Project** in the Azure portal
2. **Deploy a model** (e.g., `gpt-4o`) in your project
3. **Publish your Fabric Data Agent** and note the Workspace/Artifact IDs
4. **Register the agent** using the SDK (the `create()` method handles this)
5. **Integrate with your app** via the Foundry Agent Service REST API or SDK

For CI/CD deployment, use the Azure CLI or Bicep templates to automate agent registration.

## Extending

Add new tools by:
1. Creating a new file in `tools/`
2. Defining functions with typed parameters and docstrings
3. Adding the function list to the toolset in `agent.py`

The agent automatically registers function signatures and docstrings as tool descriptions for the LLM.
