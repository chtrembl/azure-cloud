# Fabric Data Setup – Seed Files Runbook

## ⚠️ MANUAL CHECKPOINT

Microsoft Fabric workspace and data agent creation require portal-driven steps.

---

## Prerequisites

- [ ] Microsoft Fabric capacity (F2+ or P1+) available
- [ ] Fabric workspace created or identified
- [ ] User has admin/contributor access to the workspace

---

## Step 1: Create or Identify Fabric Workspace

1. Go to [Microsoft Fabric](https://app.fabric.microsoft.com)
2. Create a new workspace or use an existing one
3. Ensure workspace is backed by **F2+ or P1+** capacity
4. Record the workspace name → set `FABRIC_WORKSPACE_NAME` in `.env`

## Step 2: Create a Lakehouse

1. In the workspace, click **+ New** → **Lakehouse**
2. Name: `supplychain_lakehouse`
3. Wait for provisioning to complete

## Step 3: Load CSV Files

Upload all CSV files from `data/fabric/` into the Lakehouse:

### Option A: Manual Upload
1. Open the Lakehouse
2. Click **Get data** → **Upload files**
3. Upload each CSV file:
   - `suppliers.csv`
   - `products.csv`
   - `purchase_orders.csv`
   - `shipments.csv`
   - `inventory_positions.csv`
   - `warehouses.csv`
   - `incidents.csv`
4. For each file, select **Load to new table** (auto-detect schema)

### Option B: Notebook Upload
1. Create a new Notebook in the workspace
2. Use PySpark to load CSVs from a mounted location or upload URL

## Step 4: Verify Tables

After loading, confirm these tables exist in the Lakehouse:
- `suppliers` (10 rows)
- `products` (12 rows)
- `purchase_orders` (15 rows)
- `shipments` (11 rows)
- `inventory_positions` (12 rows)
- `warehouses` (4 rows)
- `incidents` (7 rows)

## Step 5: Create Semantic Model (Optional but Recommended)

1. In the Lakehouse, click **New semantic model**
2. Select all 7 tables
3. Define relationships:
   - `suppliers.supplier_id` → `purchase_orders.supplier_id`
   - `products.product_id` → `purchase_orders.product_id`
   - `purchase_orders.po_id` → `shipments.po_id`
   - `products.product_id` → `inventory_positions.product_id`
   - `warehouses.warehouse_id` → `inventory_positions.warehouse_id`
   - `suppliers.supplier_id` → `incidents.supplier_id`
4. Save and publish

## Step 6: Create Fabric Data Agent

1. In the workspace, click **+ New** → **Data Agent** (preview)
2. Name: `supplychain-data-agent`
3. Select the Lakehouse or semantic model as the data source
4. Configure:
   - Allow natural language queries
   - Include all tables
5. **Test** with a sample query: "Which suppliers have the highest delay rates?"
6. **Publish** the data agent
7. Copy the **data agent endpoint URL**

## Step 7: Configure Access

1. Share the data agent with demo users
2. **Important:** Sharing the data agent alone is insufficient — users also need:
   - Read access to the underlying Lakehouse/tables
   - If using RLS/CLS: appropriate role membership

## Step 8: Update Environment

Add to your `.env`:
```bash
FABRIC_WORKSPACE_NAME=<your-workspace-name>
FABRIC_DATA_AGENT_ENDPOINT=<endpoint-url-from-step-6>
```

## Variables Produced

| Variable | Value | Source |
|---|---|---|
| `FABRIC_WORKSPACE_NAME` | Your workspace name | Step 1 |
| `FABRIC_DATA_AGENT_ENDPOINT` | Data agent endpoint URL | Step 6 |
