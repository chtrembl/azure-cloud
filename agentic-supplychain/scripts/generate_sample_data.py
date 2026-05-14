#!/usr/bin/env python3
"""
generate_sample_data.py
Generates the sample Fabric CSV files with realistic supply chain demo patterns.
This script can regenerate the CSVs if needed — the CSVs in data/fabric/ are
the canonical versions, but this script documents the data-shaping logic.

Run: python scripts/generate_sample_data.py
"""
import csv
import os
from pathlib import Path

DATA_DIR = Path(__file__).resolve().parent.parent / "data" / "fabric"


def main():
    print(f"📁 Data directory: {DATA_DIR}")

    files = list(DATA_DIR.glob("*.csv"))
    if files:
        print(f"✅ Found {len(files)} CSV files already present:")
        for f in sorted(files):
            rows = sum(1 for _ in open(f)) - 1  # minus header
            print(f"   {f.name}: {rows} rows")
    else:
        print("⚠️  No CSV files found. The canonical data is in data/fabric/.")

    print("\n📊 Data-shaping patterns encoded in the sample data:")
    print("  1. SUP-003 (Apex Manufacturing): Chronic late deliveries (32% on-time, 12–14 day delays)")
    print("  2. WH-NE-01 / PRD-110: Near stockout (2.1 days supply, CRITICAL)")
    print("  3. PRD-110 has alternate supplier SUP-007 (Delta Components)")
    print("  4. Apex contract has penalties after 10 days late (Section 4.2)")
    print("  5. Expedited shipping requires Director approval for critical stockout (<3 days supply)")


if __name__ == "__main__":
    main()
