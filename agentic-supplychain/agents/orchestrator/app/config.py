"""
Agentic Supply Chain Orchestrator – Configuration
Loads environment variables from .env / .env.generated and provides
typed configuration for the orchestrator runtime.
"""
import os
from pathlib import Path
from pydantic import Field
from pydantic_settings import BaseSettings

_repo_root = Path(__file__).resolve().parents[3] if len(Path(__file__).resolve().parents) > 3 else Path("/app")

# Load .env files if python-dotenv is available (local dev only)
try:
    from dotenv import load_dotenv
    env_path = _repo_root / ".env"
    if env_path.exists():
        load_dotenv(env_path)
    gen_path = _repo_root / ".env.generated"
    if gen_path.exists():
        load_dotenv(gen_path, override=True)
except ImportError:
    pass


class Settings(BaseSettings):
    # Foundry project endpoint – from Bicep output
    azure_ai_project_endpoint: str = Field(default="", description="Foundry project endpoint URL")

    # Model deployment name – set after model deployment (Step 3)
    model_deployment_name: str = Field(default="gpt-4o", description="LLM deployment name in Foundry")

    # Fabric data agent endpoint – from Fabric portal (manual)
    # Platform injects as FABRIC_DATA_ENDPOINT (FABRIC_DATA_AGENT_ENDPOINT locally)
    fabric_data_agent_endpoint: str = Field(default="", description="Fabric data agent endpoint URL")

    # Foundry IQ MCP URL – from Foundry IQ setup (manual)
    # Platform injects as KNOWLEDGE_MCP_URL (FOUNDRY_IQ_MCP_URL locally)
    foundry_iq_mcp_url: str = Field(default="", description="Foundry IQ MCP endpoint for knowledge retrieval")

    # Application Insights connection string – from Bicep output
    appinsights_connection_string: str = Field(default="", description="App Insights connection string")

    # Agent identity
    agent_name: str = Field(default="orchestrator-agent", description="Display name for the hosted agent")

    # Server settings
    host: str = Field(default="0.0.0.0", description="Server bind host")
    port: int = Field(default=8088, description="Server bind port")

    @classmethod
    def _resolve_env_aliases(cls):
        """Map platform env var names to local config names."""
        aliases = {
            "FABRIC_DATA_ENDPOINT": "FABRIC_DATA_AGENT_ENDPOINT",
            "KNOWLEDGE_MCP_URL": "FOUNDRY_IQ_MCP_URL",
        }
        for platform_name, local_name in aliases.items():
            if os.environ.get(platform_name) and not os.environ.get(local_name):
                os.environ[local_name] = os.environ[platform_name]

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        extra = "ignore"


Settings._resolve_env_aliases()
settings = Settings()
