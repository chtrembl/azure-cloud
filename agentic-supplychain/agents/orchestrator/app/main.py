"""
Agentic Supply Chain Orchestrator – FastAPI Entrypoint
Serves the orchestrator as an HTTP service for local testing
and as the hosted agent container entrypoint.
"""
import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .config import settings
from .models import QueryRequest, SynthesizedResponse, HealthResponse
from .orchestrator import handle_query

app = FastAPI(
    title="Agentic Supply Chain Orchestrator",
    description="Hosted agent orchestrator that combines Fabric structured data with Foundry IQ knowledge.",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health", response_model=HealthResponse)
async def health():
    """Health check endpoint for container probes."""
    return HealthResponse(
        status="ok",
        agent_name=settings.agent_name,
        version="0.1.0",
    )


@app.post("/query", response_model=SynthesizedResponse)
async def query(request: QueryRequest):
    """
    Accept a business question, route to appropriate tools,
    and return a synthesized grounded answer.
    """
    return await handle_query(
        user_query=request.question,
        user_context=request.user_context,
    )


if __name__ == "__main__":
    uvicorn.run(
        "app.main:app",
        host=settings.host,
        port=settings.port,
        reload=True,
    )
