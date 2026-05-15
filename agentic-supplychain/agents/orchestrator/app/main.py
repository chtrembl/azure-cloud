"""
Agentic Supply Chain Orchestrator – FastAPI Entrypoint
Serves the orchestrator as an HTTP service for local testing
and as the hosted agent container entrypoint.

Implements the Foundry Hosted Agent "invocations" protocol:
  - GET  /readiness        → 200 when ready
  - POST /invocations      → execute agent logic
  - GET  /invocations/docs/openapi.json → OpenAPI spec
"""
import uuid
import uvicorn
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

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


@app.get("/readiness")
async def readiness():
    """Readiness probe for Foundry hosted agent platform."""
    return {"status": "ready"}


@app.get("/health", response_model=HealthResponse)
async def health():
    """Health check endpoint for container probes."""
    return HealthResponse(
        status="ok",
        agent_name=settings.agent_name,
        version="0.1.0",
    )


@app.post("/invocations")
async def invocations(request: Request):
    """
    Foundry Invocations protocol endpoint.
    Accepts {"input": "user question"} or {"question": "..."} payloads.
    """
    body = await request.json()

    # Extract the user question from various payload formats
    question = (
        body.get("input")
        or body.get("question")
        or body.get("message")
        or body.get("content", "")
    )
    user_context = body.get("user_context", body.get("context", {}))

    invocation_id = request.headers.get(
        "x-agent-invocation-id", str(uuid.uuid4())
    )

    result = await handle_query(user_query=question, user_context=user_context)

    return JSONResponse(
        content={
            "invocation_id": invocation_id,
            "status": "completed",
            "output": result.model_dump(),
        },
        headers={
            "x-agent-invocation-id": invocation_id,
        },
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
