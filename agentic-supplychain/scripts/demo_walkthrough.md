# Demo Walkthrough

This walkthrough validates the end-to-end demo scenarios for the Agentic Supply Chain orchestrator.

---

## Prerequisites

Before running the demo:

1. Azure infrastructure deployed (Bicep)
2. Model deployment configured
3. Fabric data loaded and data agent published
4. Knowledge base created and Foundry IQ connected
5. Orchestrator deployed (locally or as hosted agent)

---

## Scenario 1: Structured Data Query

**Purpose:** Prove the orchestrator can query Fabric for operational metrics.

**Prompt:**
> Which suppliers are causing the most delivery delays in the Northeast region?

**Expected Response:**
- Identifies **Apex Manufacturing (SUP-003)** as the worst performer
- Shows 68% late shipment rate, 12.4-day average delay
- Mentions Delta Components (SUP-007) as secondary
- Route: `structured`

**Validation:**
```bash
curl -X POST http://localhost:8088/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Which suppliers are causing the most delivery delays in the Northeast region?"}'
```

---

## Scenario 2: Knowledge Query

**Purpose:** Prove the orchestrator can retrieve policies and contracts from Foundry IQ.

**Prompt:**
> What are the contract penalty terms for Apex Manufacturing's late deliveries?

**Expected Response:**
- References Section 4.2 of the Apex Master Supply Agreement
- Mentions 2% per day penalty after 10 calendar days
- Mentions 20% cap on penalty
- Mentions chronic non-performance escalation at 50% threshold
- Route: `knowledge`

---

## Scenario 3: Mixed Query (Full MVP Demo)

**Purpose:** Prove the orchestrator combines both sources and synthesizes actionable recommendations.

**Prompt:**
> Which suppliers are causing the most delay in the Northeast, what inventory is at risk, and what policy-backed actions should operations take?

**Expected Response Structure:**
- **Findings:** Apex Manufacturing delays, PRD-110 CRITICAL stockout at WH-NE-01
- **Policy Implications:** Penalty clauses (Section 4.2), escalation thresholds (Tier 3)
- **Recommended Actions:**
  - Expedite replenishment for PRD-110 (Director approval per SCM-POL-004)
  - Escalate Apex Manufacturing per supplier escalation runbook
  - Evaluate Delta Components as alternate supplier
- **Supporting Evidence:** Contract references, policy document citations
- Route: `mixed`

---

## Scenario 4: Alternate Supplier Evaluation

**Prompt:**
> Can we use Delta Components as an alternate supplier for the valve assemblies Apex is failing to deliver?

**Expected Response:**
- Confirms Delta Components (SUP-007) is pre-qualified on the AVL
- Notes 48-hour emergency qualification process
- Requires Director approval and QE sign-off
- First 3 shipments require 100% incoming inspection

---

## Scenario 5: Shortage Response

**Prompt:**
> What is the current supply chain risk level for our Northeast operations and what should we do about it?

**Expected Response:**
- CRITICAL level for PRD-110 (< 3 days supply)
- References Shortage Response Playbook (SCM-PLAY-003)
- Immediate Director escalation required
- Authorize expedited/air freight
- Activate alternate supplier

---

## Running All Scenarios

```bash
# Start local agent
cd agents/orchestrator
pip install -r requirements.txt
python -m app.main &

# Run each scenario
for q in \
  "Which suppliers are causing the most delivery delays in the Northeast region?" \
  "What are the contract penalty terms for late deliveries?" \
  "Which suppliers are causing the most delay, what inventory is at risk, and what actions should we take?" \
  "Can we use Delta Components as an alternate supplier?" \
  "What is the current supply chain risk level for Northeast operations?"; do
  echo "---"
  echo "Q: $q"
  curl -s -X POST http://localhost:8088/query \
    -H "Content-Type: application/json" \
    -d "{\"question\": \"$q\"}" | python3 -m json.tool
  echo ""
done
```
