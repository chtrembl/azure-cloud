"""Main entry point for the PetStore Retail Agent."""

import sys

from .agent import PetStoreAgent


def main():
    """Run the PetStore Retail Agent in interactive mode."""
    print("🐾 PetStore Retail Agent")
    print("=" * 50)
    print("Powered by Azure AI Foundry + Microsoft Fabric")
    print("Type 'quit' to exit.\n")

    agent = PetStoreAgent()
    agent.create()

    try:
        while True:
            user_input = input("\n🛒 You: ").strip()
            if not user_input:
                continue
            if user_input.lower() in ("quit", "exit", "q"):
                break

            try:
                response = agent.chat(user_input)
                print(f"\n🤖 Agent: {response}")
            except Exception as e:
                print(f"\n❌ Error: {e}", file=sys.stderr)
    except KeyboardInterrupt:
        print("\n\nShutting down...")
    finally:
        agent.cleanup()


if __name__ == "__main__":
    main()
