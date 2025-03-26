import config
import logging
import semantic_kernel as sk
from plugins.ProductPlugin import ProductPlugin
from plugins.CartPlugin import CartPlugin
from semantic_kernel.connectors.ai.open_ai import AzureChatCompletion
from semantic_kernel.contents import ChatHistory
from semantic_kernel.connectors.ai.open_ai import AzureChatCompletion, AzureChatPromptExecutionSettings
from semantic_kernel.connectors.ai.function_choice_behavior import FunctionChoiceBehavior
from semantic_kernel.functions import KernelArguments

#uncomment this to grab debug logs including REST Body to Azure Open AI
#logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(name)s - %(message)s')
#httpcore_logger = logging.getLogger("httpcore.http11")
#httpcore_logger.setLevel(logging.DEBUG)
#h11_logger = logging.getLogger("h11")
#h11_logger.setLevel(logging.DEBUG)

kernel = sk.Kernel()

# Azure OpenAI Chat Completion
chat_service = AzureChatCompletion(
    service_id="azure_openai",
    deployment_name=config.AZURE_OPENAI_DEPLOYMENT_NAME,
    endpoint=config.AZURE_OPENAI_ENDPOINT.rstrip("/openai"), ## not sure why/how extra openai is getting appended
    api_key=config.AZURE_OPENAI_API_KEY,
    api_version=config.AZURE_OPENAI_SERVICE_VERSION
)
kernel.add_service(chat_service)

# Register custom plugins...
product_plugin = ProductPlugin()
cart_plugin = CartPlugin()
kernel.add_plugin(product_plugin, plugin_name="ProductPlugin")
kernel.add_plugin(cart_plugin, plugin_name="CartPlugin")

# chat history can be persisted...
chat_history = ChatHistory()
chat_history.add_system_message("You are a helpful Pet Store Sales Assistant. Use Semantic Kernel Functions when needed.")

# template for LLM and logical grouping for plugin/function that will facilitate our chat experience
chat_function = kernel.add_function(
    prompt="{{$chat_history}}{{$user_input}}",
    plugin_name="AzurePetStoreAssistant",
    function_name="Chat",
)

request_settings = AzureChatPromptExecutionSettings()
request_settings.service_id = "azure_openai"
request_settings.function_choice_behavior = FunctionChoiceBehavior.Auto(filters={"excluded_plugins": ["AzurePetStoreAssistant"]})

# arguments for LLM
arguments = KernelArguments(settings=request_settings)

#invoke LLM
async def process_prompt(prompt: str) -> str:
    arguments["user_input"] = prompt
    arguments["chat_history"] = chat_history
    response = await kernel.invoke(chat_function, arguments=arguments)
    chat_history.add_user_message(prompt)
    return response