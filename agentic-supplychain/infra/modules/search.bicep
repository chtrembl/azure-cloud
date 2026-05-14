// ─────────────────────────────────────────────────────────────
// Azure AI Search
// Provides the search index backing Foundry IQ knowledge bases.
// ─────────────────────────────────────────────────────────────
param name string
param location string

@allowed(['free', 'basic', 'standard', 'standard2', 'standard3'])
param sku string = 'basic'

resource searchService 'Microsoft.Search/searchServices@2024-06-01-preview' = {
  name: name
  location: location
  sku: {
    name: sku
  }
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    hostingMode: 'default'
    publicNetworkAccess: 'enabled'
    replicaCount: 1
    partitionCount: 1
  }
}

output searchEndpoint string = 'https://${searchService.name}.search.windows.net'
output searchId string = searchService.id
output searchName string = searchService.name
