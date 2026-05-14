// ─────────────────────────────────────────────────────────────
// Foundry Project (Microsoft.CognitiveServices/accounts/projects)
// Creates a project within the Foundry account with system-assigned
// managed identity for hosted agent runtime.
// ─────────────────────────────────────────────────────────────
param name string
param location string
param foundryAccountName string
param appInsightsId string

resource foundryAccount 'Microsoft.CognitiveServices/accounts@2024-10-01' existing = {
  name: foundryAccountName
}

resource foundryProject 'Microsoft.CognitiveServices/accounts/projects@2024-10-01' = {
  parent: foundryAccount
  name: name
  location: location
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    description: 'Agentic supply-chain orchestrator project'
    friendlyName: 'Agentic Supply Chain'
    // Link Application Insights for hosted-agent observability
    customProperties: {
      ApplicationInsightsResourceId: appInsightsId
    }
  }
}

output projectName string = foundryProject.name
output projectId string = foundryProject.id
output projectEndpoint string = foundryProject.properties.endpoint
output projectPrincipalId string = foundryProject.identity.principalId
