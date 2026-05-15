// ─────────────────────────────────────────────────────────────
// Agentic Supply Chain – Main Bicep Deployment
// Scope: resourceGroup (create the RG first via az group create)
// ─────────────────────────────────────────────────────────────
targetScope = 'resourceGroup'

// ── Parameters ─────────────────────────────────────────────
@description('Azure region for all resources')
param location string = 'swedencentral'

@description('Base name prefix used for resource naming')
param baseName string = 'agentsc'

@description('Foundry account display name')
param foundryAccountName string = '${baseName}-foundry'

@description('Foundry project name')
param foundryProjectName string = 'agentic-supplychain-project'

@description('Azure AI Search service name (must be globally unique)')
param searchServiceName string = '${baseName}-search-${uniqueString(resourceGroup().id)}'

@description('Azure Container Registry name (must be globally unique, alphanumeric)')
param acrName string = '${baseName}acr${uniqueString(resourceGroup().id)}'

@description('Log Analytics workspace name')
param logAnalyticsName string = '${baseName}-logs'

@description('Application Insights name')
param appInsightsName string = '${baseName}-appinsights'

@description('SKU for Azure AI Search')
param searchSku string = 'basic'

@description('SKU for Azure Container Registry')
param acrSku string = 'Basic'

// ── Modules ────────────────────────────────────────────────

module logAnalytics 'modules/log-analytics.bicep' = {
  name: 'logAnalytics'
  params: {
    name: logAnalyticsName
    location: location
  }
}

module appInsights 'modules/app-insights.bicep' = {
  name: 'appInsights'
  params: {
    name: appInsightsName
    location: location
    logAnalyticsWorkspaceId: logAnalytics.outputs.workspaceId
  }
}

module foundryAccount 'modules/foundry-account.bicep' = {
  name: 'foundryAccount'
  params: {
    name: foundryAccountName
    location: location
  }
}

module foundryProject 'modules/foundry-project.bicep' = {
  name: 'foundryProject'
  params: {
    name: foundryProjectName
    location: location
    foundryAccountName: foundryAccount.outputs.accountName
  }
}

module search 'modules/search.bicep' = {
  name: 'search'
  params: {
    name: searchServiceName
    location: location
    sku: searchSku
  }
}

module acr 'modules/acr.bicep' = {
  name: 'acr'
  params: {
    name: acrName
    location: location
    sku: acrSku
  }
}

module roleAssignments 'modules/role-assignments.bicep' = {
  name: 'roleAssignments'
  params: {
    projectPrincipalId: foundryProject.outputs.projectPrincipalId
    acrId: acr.outputs.acrId
    logAnalyticsId: logAnalytics.outputs.workspaceId
  }
}

// ── Outputs ────────────────────────────────────────────────
// These values feed .env, scripts, and GitHub Actions variables.

@description('Foundry project endpoint URL')
output projectEndpoint string = foundryProject.outputs.projectEndpoint

@description('Foundry project name')
output projectName string = foundryProject.outputs.projectName

@description('Foundry project system-assigned managed identity principal ID')
output projectPrincipalId string = foundryProject.outputs.projectPrincipalId

@description('ACR login server (e.g., myacr.azurecr.io)')
output acrLoginServer string = acr.outputs.acrLoginServer

@description('ACR resource ID')
output acrId string = acr.outputs.acrId

@description('ACR name')
output acrName string = acr.outputs.acrName

@description('Azure AI Search endpoint')
output searchEndpoint string = search.outputs.searchEndpoint

@description('Azure AI Search resource ID')
output searchId string = search.outputs.searchId

@description('Application Insights connection string')
output appInsightsConnectionString string = appInsights.outputs.connectionString

@description('Log Analytics workspace ID')
output logAnalyticsWorkspaceId string = logAnalytics.outputs.workspaceId

@description('Foundry account name')
output foundryAccountName string = foundryAccount.outputs.accountName
