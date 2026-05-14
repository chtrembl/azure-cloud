// ─────────────────────────────────────────────────────────────
// Foundry Project (Microsoft.CognitiveServices/accounts/projects)
// Creates a project within the Foundry account with system-assigned
// managed identity for hosted agent runtime.
// ─────────────────────────────────────────────────────────────
param name string
param location string
param foundryAccountName string

resource foundryAccount 'Microsoft.CognitiveServices/accounts@2025-06-01' existing = {
  name: foundryAccountName
}

resource foundryProject 'Microsoft.CognitiveServices/accounts/projects@2025-06-01' = {
  parent: foundryAccount
  name: name
  location: location
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    description: 'Agentic supply-chain orchestrator project'
    displayName: 'Agentic Supply Chain'
  }
}

output projectName string = foundryProject.name
output projectId string = foundryProject.id
output projectEndpoint string = foundryProject.properties.endpoints['AI Foundry API']
output projectPrincipalId string = foundryProject.identity.principalId
