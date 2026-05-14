"""
Agentic Supply Chain Orchestrator – Configuration
Loads environment variables from .env / .env.generated and provides
typed configuration for the orchestrator runtime.
"""
import os
from pathlib import Path
from pydantic import Field
from pydantic_settings import BaseSettings

_repo_root = Path(__file__).resolve().parents[3]

# Load .env files if python-dotenv is available
try:
    from dotenv import load_dotenv
    load_dotenv(_repo_root / ".env")
    load_dotenv(_repo_root / ".env.generated", override=True)
except ImportError:
    pass


class Settings(BaseSettings):
    # Foundry project endpoint – from Bicep output
    azure_ai_project_endpoint: str = Field(default="", description="Foundry project endpoint URL")

    # Model deployment name – set after model deployment (Step 3)
    model_deployment_name: str = Field(default="gpt-4o", description="LLM deployment name in Foundry")

    # Fabric data agent endpoint – from Fabric portal (manual)
    fabric_data_agent_endpoint: str = Field(default="", description="Fabric data agent endpoint URL")

    # Foundry IQ MCP URL – from Foundry IQ setup (manual)
    foundry_iq_mcp_url: str = Field(default="", description="Foundry IQ MCP endpoint for knowledge retrieval")

    # Application Insights connection string – from Bicep output
    appinsights_connection_string: str = Field(default="", description="App Insights connection string")

    # Agent identity
    agent_name: str = Field(default="orchestrator-agent", description="Display name for the hosted agent")

    # Server settings
    host: str = Field(default="0.0.0.0", description="Server bind host")
    port: int = Field(default=8088, description="Server bind port")

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        extra = "ignore"


settings = Settings()
