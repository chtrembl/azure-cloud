// ─────────────────────────────────────────────────────────────
// Azure Container Registry
// Stores the hosted-agent Docker images.
// ─────────────────────────────────────────────────────────────
param name string
param location string

@allowed(['Basic', 'Standard', 'Premium'])
param sku string = 'Basic'

resource acr 'Microsoft.ContainerRegistry/registries@2023-11-01-preview' = {
  name: name
  location: location
  sku: {
    name: sku
  }
  properties: {
    adminUserEnabled: false
    publicNetworkAccess: 'Enabled'
  }
}

output acrLoginServer string = acr.properties.loginServer
output acrId string = acr.id
output acrName string = acr.name
