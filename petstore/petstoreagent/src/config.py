"""Configuration management for the PetStore Retail Agent."""

import os

from dotenv import load_dotenv
from pydantic import BaseModel, Field

load_dotenv()


class AgentConfig(BaseModel):
    """Configuration for the PetStore Retail Agent."""

    foundry_project_endpoint: str = Field(default_factory=lambda: os.environ["FOUNDRY_PROJECT_ENDPOINT"])
    model_deployment_name: str = Field(default_factory=lambda: os.environ.get("MODEL_DEPLOYMENT_NAME", "gpt-4o"))
    fabric_workspace_id: str = Field(default_factory=lambda: os.environ["FABRIC_WORKSPACE_ID"])
    fabric_artifact_id: str = Field(default_factory=lambda: os.environ["FABRIC_ARTIFACT_ID"])


def load_config() -> AgentConfig:
    """Load and validate agent configuration from environment variables."""
    return AgentConfig()
