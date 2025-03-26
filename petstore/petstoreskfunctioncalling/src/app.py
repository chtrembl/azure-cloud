import asyncio
from client import process_prompt
from utils.helpers import format_response

async def main():
    while True:
        prompt = input("Ask about animals or pet product pricing (or type 'exit' to quit): ")
        if prompt.lower() == "exit":
            break
        if not prompt:
            print("Prompt cannot be empty. Please enter a valid query.")
            continue
        try:
            response = await process_prompt(prompt)
            print(response)
        except Exception as e:
            print(f"An error occurred: {e}")

if __name__ == "__main__":
    asyncio.run(main())