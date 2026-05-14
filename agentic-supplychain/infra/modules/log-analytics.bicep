// ─────────────────────────────────────────────────────────────
// Log Analytics Workspace
// Backing store for Application Insights and hosted-agent logs.
// ─────────────────────────────────────────────────────────────
param name string
param location string

resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2023-09-01' = {
  name: name
  location: location
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 30
  }
}

output workspaceId string = logAnalytics.id
output workspaceName string = logAnalytics.name
output customerId string = logAnalytics.properties.customerId
