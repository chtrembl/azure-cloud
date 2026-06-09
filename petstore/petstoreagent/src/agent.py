"""PetStore Retail Agent - Orchestrator agent connecting to Fabric Data Agents."""

from azure.ai.projects import AIProjectClient
from azure.ai.projects.models import FabricTool, FunctionTool, ToolSet
from azure.identity import DefaultAzureCredential

from .config import AgentConfig, load_config
from .tools.inventory import inventory_functions
from .tools.orders import order_functions
from .tools.recommendations import recommendation_functions

AGENT_INSTRUCTIONS = """You are the PetStore Retail Agent, an intelligent assistant for a pet retail business.
You orchestrate data from Microsoft Fabric to help store associates and customers with:

1. **Inventory Management** - Check stock levels, reorder alerts, and product availability across locations.
2. **Order Processing** - Look up order status, process returns, and track shipments.
3. **Product Recommendations** - Suggest products based on pet type, customer history, and trending items.
4. **Sales Analytics** - Provide real-time sales insights, revenue metrics, and performance dashboards.

When answering questions about inventory, sales data, or customer analytics, use the Fabric Data Agent tool
to query the enterprise data warehouse. For business logic operations (placing orders, calculating recommendations),
use the custom function tools provided.

Always be helpful, concise, and proactive in offering relevant insights."""


class PetStoreAgent:
    """Orchestrator agent that connects to Fabric Data Agents for retail intelligence."""

    def __init__(self, config: AgentConfig | None = None):
        self.config = config or load_config()
        self.credential = DefaultAzureCredential()
        self.client = AIProjectClient(
            endpoint=self.config.foundry_project_endpoint,
            credential=self.credential,
        )
        self.agent = None

    def _build_toolset(self) -> ToolSet:
        """Build the complete toolset with Fabric and custom function tools."""
        toolset = ToolSet()

        # Fabric Data Agent tool for querying enterprise data
        fabric_tool = FabricTool(
            workspace_id=self.config.fabric_workspace_id,
            artifact_id=self.config.fabric_artifact_id,
        )
        toolset.add(fabric_tool)

        # Custom function tools for business logic
        all_functions = inventory_functions + order_functions + recommendation_functions
        function_tool = FunctionTool(functions=all_functions)
        toolset.add(function_tool)

        return toolset

    def create(self) -> "PetStoreAgent":
        """Create and register the agent with Azure AI Foundry."""
        toolset = self._build_toolset()

        self.agent = self.client.agents.create_agent(
            model=self.config.model_deployment_name,
            name="petstore-retail-agent",
            instructions=AGENT_INSTRUCTIONS,
            toolset=toolset,
        )
        print(f"Agent created: {self.agent.id}")
        return self

    def chat(self, user_message: str) -> str:
        """Send a message to the agent and return the response."""
        if not self.agent:
            raise RuntimeError("Agent not created. Call create() first.")

        thread = self.client.agents.create_thread()
        self.client.agents.create_message(
            thread_id=thread.id,
            role="user",
            content=user_message,
        )

        run = self.client.agents.create_and_process_run(
            thread_id=thread.id,
            agent_id=self.agent.id,
        )

        if run.status == "failed":
            raise RuntimeError(f"Agent run failed: {run.last_error}")

        messages = self.client.agents.list_messages(thread_id=thread.id)
        return messages.get_last_text_message_by_role("assistant").text.value

    def cleanup(self):
        """Delete the agent from Foundry when done."""
        if self.agent:
            self.client.agents.delete_agent(self.agent.id)
            print(f"Agent deleted: {self.agent.id}")
