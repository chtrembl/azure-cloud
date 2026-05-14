// ─────────────────────────────────────────────────────────────
// Foundry Account (Microsoft.CognitiveServices/accounts)
// Provides the Azure AI / Foundry account that hosts projects.
// ─────────────────────────────────────────────────────────────
param name string
param location string

resource foundryAccount 'Microsoft.CognitiveServices/accounts@2025-06-01' = {
  name: name
  location: location
  kind: 'AIServices'
  identity: {
    type: 'SystemAssigned'
  }
  sku: {
    name: 'S0'
  }
  properties: {
    customSubDomainName: name
    publicNetworkAccess: 'Enabled'
    allowProjectManagement: true
    apiProperties: {}
  }
}

output accountName string = foundryAccount.name
output accountId string = foundryAccount.id
output accountEndpoint string = foundryAccount.properties.endpoint
