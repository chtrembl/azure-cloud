# 16 - Build a Bicep IaC Template and Configure a DevOps Pipeline for Self Service Deployment of Azure Infrastructure

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

Just imagine you're a developer for the azurepetstore.com e-commerce website. Your code will have a dependency on infrastructure. For example, you may need an Azure Kubernetes Cluster, a Service Bus or perhaps a Cosmos DB. Sure you can head over to the Azure Portal (as we did in the previous guides) and provision your infrastructure manually and/or use the Azure CLI. But there are challenges with this approach. How does this scale? What about state changes? Version Control? Automatic Deployments? 

In this section we'll look at IaC (Infrastructure as Code) and see how to build a Bicep Template that can be used to provision Azure Infrastructure, specifically an Azure Cosmos DB. We will then build an Azure DevOps Pipeline to deploy this Bicep Template via Self Service / On Demand. We will then configure the existing Logic App to push orders into this Cosmos DB.

Think of Bicep as an abstraction layer on top of ARM (Azure Resource Manager), it is a domain-specific language (DSL) that uses declarative syntax to deploy Azure resources. In a Bicep file, you define the infrastructure you want to deploy to Azure, and then use that file throughout the development lifecycle to repeatedly deploy your infrastructure. Built Bicep templates will generate ARM Json that we will use to deploy to Azure. 

More on [Bicep](https://docs.microsoft.com/en-us/azure/azure-resource-manager/bicep/overview?tabs=bicep)

More on [ARM](https://docs.microsoft.com/en-us/azure/azure-resource-manager/templates/overview)

You can create your own Bicep Templates from scratch and/or leverage pre built Azure Quickstart Templates that are available from the community to help jumpstart your IaC efforts.

More on [Quickstart Templates](https://azure.microsoft.com/en-us/resources/templates)

## Step 1 Create your Bicep Template ##

Within VS Code, install the Bicep Extension
![](images/1.png)

If you cloned/forked this repository you will notice a iac/bicep/db folder containing a parameters file, bicep file and the built main.json ARM Template. It does not matter where these files reside, you will specify location when you deploy the ARM Template. You can create your own folder in iac/bicep/<newfoldername> and/or just follow ahead. I used 'db' to indicate that this as for database infrastructure.

Within Visual Studio Code you can use Ctrl + Shift + P to help facilitate the creation of these files and/or you can create them manually.

I manually created the following two files:
```iac\bicep\db\azuredeploy.parameters.json```
```iac\bicep\db\main.bicep```

I populated these two files from the following [Cosmos DB Quickstart Template](https://github.com/Azure/azure-quickstart-templates/tree/master/quickstarts/microsoft.documentdb/cosmosdb-sql)

For my use case the defaults are fine

azuredeploy.parameters.json:

```json
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "cosmosAccountName": {
      "value": "AzurePetStore"
    },
    "cosmosPrimaryRegion": {
      "value": "East US 2"
    },
    "cosmosDatabaseName": {
      "value": "E-Commerce"
    },
    "cosmosContainerName": {
      "value": "Orders"
    },
    "cosmosThroughput": {
      "value": 400
    }
  }
}
```

main.bicep
```bicep
@description('Cosmos DB account name, max length 44 characters')
param cosmosAccountName string = 'sql-${uniqueString(resourceGroup().id)}'

@description('Location for the Cosmos DB account.')
param cosmosLocation string = resourceGroup().location

@description('The primary replica region for the Cosmos DB account.')
param cosmosPrimaryRegion string

@allowed([
  'Eventual'
  'ConsistentPrefix'
  'Session'
  'BoundedStaleness'
  'Strong'
])
@description('The default consistency level of the Cosmos DB account.')
param defaultConsistencyLevel string = 'Session'

@minValue(10)
@maxValue(2147483647)
@description('Max stale requests. Required for BoundedStaleness. Valid ranges, Single Region: 10 to 1000000. Multi Region: 100000 to 1000000.')
param maxStalenessPrefix int = 100000

@minValue(5)
@maxValue(86400)
@description('Max lag time (minutes). Required for BoundedStaleness. Valid ranges, Single Region: 5 to 84600. Multi Region: 300 to 86400.')
param maxIntervalInSeconds int = 300

@allowed([
  true
  false
])
@description('Enable automatic failover for regions')
param automaticFailover bool = true

@description('The name for the database')
param cosmosDatabaseName string = 'myDatabase'

@description('The name for the container')
param cosmosContainerName string = 'myContainer'

var consistencyPolicy = {
  Eventual: {
    defaultConsistencyLevel: 'Eventual'
  }
  ConsistentPrefix: {
    defaultConsistencyLevel: 'ConsistentPrefix'
  }
  Session: {
    defaultConsistencyLevel: 'Session'
  }
  BoundedStaleness: {
    defaultConsistencyLevel: 'BoundedStaleness'
    maxStalenessPrefix: maxStalenessPrefix
    maxIntervalInSeconds: maxIntervalInSeconds
  }
  Strong: {
    defaultConsistencyLevel: 'Strong'
  }
}
var locations = [
  {
    locationName: cosmosPrimaryRegion
    failoverPriority: 0
    isZoneRedundant: false
  }
]

resource account 'Microsoft.DocumentDB/databaseAccounts@2021-10-15' = {
  name: toLower(cosmosAccountName)
  location: cosmosLocation
  kind: 'GlobalDocumentDB'
  properties: {
    consistencyPolicy: consistencyPolicy[defaultConsistencyLevel]
    locations: locations
    databaseAccountOfferType: 'Standard'
    enableAutomaticFailover: automaticFailover
  }
}

resource database 'Microsoft.DocumentDB/databaseAccounts/sqlDatabases@2021-10-15' = {
  name: '${account.name}/${cosmosDatabaseName}'
  properties: {
    resource: {
      id: cosmosDatabaseName
    }
  }
}

resource container 'Microsoft.DocumentDB/databaseAccounts/sqlDatabases/containers@2021-10-15' = {
  name: '${database.name}/${cosmosContainerName}'
  properties: {
    resource: {
      id: cosmosContainerName
      partitionKey: {
        paths: [
          '/customer/zipcode'
        ]
        kind: 'Hash'
      }
      defaultTtl: 86400
    }
  }
}
```


> 📝 **Please Note, **

Things you can now do now with this guide

☑️ Build a Bicep Template

☑️ Deploy a Bicep Template

☑️ Configure a DevOps Pipeline to Deploy a Bicep Template as Self Service
