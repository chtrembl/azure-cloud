// ─────────────────────────────────────────────────────────────
// Role Assignments
// Grants the Foundry project managed identity the RBAC roles
// it needs to pull images from ACR and read Log Analytics data.
//
// Extensible: add more role assignments below for published-agent
// identities or operator principals as needed.
// ─────────────────────────────────────────────────────────────
param projectPrincipalId string
param acrId string
param logAnalyticsId string

// Built-in role definition IDs
// AcrPull role – grants image-pull access
var acrPullRoleDefinitionId = subscriptionResourceId('Microsoft.Authorization/roleDefinitions', '7f951dda-4ed3-4680-a7ca-43fe172d538d')

// Log Analytics Reader
var logAnalyticsReaderRoleId = subscriptionResourceId('Microsoft.Authorization/roleDefinitions', '73c42c96-874c-492b-b04d-ab87d138a893')

// ── ACR Pull for Foundry project MI ────────────────────────
resource acrPullAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(acrId, projectPrincipalId, acrPullRoleDefinitionId)
  scope: acrResource
  properties: {
    principalId: projectPrincipalId
    roleDefinitionId: acrPullRoleDefinitionId
    principalType: 'ServicePrincipal'
  }
}

resource acrResource 'Microsoft.ContainerRegistry/registries@2023-11-01-preview' existing = {
  name: last(split(acrId, '/'))
}

// ── Log Analytics Reader for Foundry project MI ────────────
resource logAnalyticsReaderAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(logAnalyticsId, projectPrincipalId, logAnalyticsReaderRoleId)
  scope: logAnalyticsResource
  properties: {
    principalId: projectPrincipalId
    roleDefinitionId: logAnalyticsReaderRoleId
    principalType: 'ServicePrincipal'
  }
}

resource logAnalyticsResource 'Microsoft.OperationalInsights/workspaces@2023-09-01' existing = {
  name: last(split(logAnalyticsId, '/'))
}
