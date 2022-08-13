# Azure Pet Store Learning Guides

Often, it is handy to have an N-Tiered Azure architected deployment ready to go, complete with functional code, CI/CD and various services for you to experiment Azure with. Since it is usually hard to find one that hits many of the App Dev Services of Azure, I've decided to build one. Perhaps you're learning new Azure technologies, giving Azure demos, looking for a prototype with Azure or simply just curious about Azure, these guides will get you on your way to running a full Hypothetical Azure Pet Store Application and its dependencies, using the Azure technologies illustrated below. Already built and cloud agnostic are a Pet Store web app and 3 Pet Store micro services that you can deploy into Azure App Service (petstoreapp) and Azure Kubernetes Service (petstorepetservice, petstoreproductservice & petstoreorderservice). You will first build and run these locally (optional) and slowly add in the other services depicted (Azure B2C, Application Insights, API Management, Function App, Power App, Regression Suite etc...). These applications were built with Java/Spring Boot and while the guides are not necessarily focused on coding (not required) you are encouraged to build/deploy them locally (All you need is Docker locally) to get the full developer experience. You can however use Azure DevOps Pipelines and GitHub Actions to do the build & deploys for you and make changes directly in your cloud repository instead of locally.

> You can view the live deployment here [https://azurepetstore.com](https://azurepetstore.com) 

![](https://github.com/chtrembl/azure-cloud/blob/main/petstore/petstore_architecture.png?raw=true)
  
As mentioned above, the objective is for you to be able to learn and showcase the many uses of App Dev Services within Azure. Below are some of the useful scenarios that are currently available in these guides. 

- CI/CD into Azure App Service & Azure Kubernetes Service using Azure DevOps Pipelines and GitHub Actions

- N-Tiered Architecture using Pet Store Application deployed to App Services and Pet Store Services deployed to Azure Kubernetes Service

- N-Tiered Correlated Telemetry between the layers, allowing for Full End to End Reporting using KQL within Azure Application Insights

- Authentication of Pet Store Application users via Azure B2C

- API Management in front of Pet Store Services, ensuring Pet Store Application is subscribed to and requests are made within rate/quota limits

- Blue/Green Deployments to Pet Store Application via Deployment Slots
  
- Functional Spring Boot code showcasing the Azure SDK's for integrating with these services

- Azure Serverless (Logic Apps and Function Apps)

- Azure Power Apps

- Azure Service Bus

## This is not official Microsoft documentation but rather an opinionated approach/guidance

This is hands-on and will be hands on keyboard. However, once complete, this will be running in your subscription and always readily available. The Pet Store Application and Pet Store Services are built with Java and Spring Boot. You're welcome to import these into your IDE of choice, write code and/or contribute back, however that is not mandatory for these guides. As mentioned above, you can use Azure DevOps Pipelines and GitHub Actions to handle the build & deploys for you.

> 📝 Please Note, you **will need** an Azure Subscription and GitHub account for these guides. If not, you you can sign up [here](https://azure.microsoft.com/en-us/) to get started. 

## [00 - Setup your environment](00-setup-your-environment/README.md)

Prerequisites and environment setup (Optionaly you can build/deploy locally)

## [01 - Build the Docker Images](01-build-the-docker-images/README.md)

  Build the Docker Images

## [02 - Push the Docker Images to Azure Container Registry](02-push-the-docker-images-to-acr/README.md)

  Push the Docker Images to Azure Container Registy

## [03 - Configure App Service for continuous deployment](03-configure-app-service-for-cd/README.md)

  Configure App Service for continuous deployments of Azure Container Registry Docker image updates

## [04 - Configure Git Hub Action for CI/CD into App Service](04-configure-git-hub-action-for-ci-cd-into-app-service/README.md)

   Configure Git Hub Action for CI/CD into App Service

## [05 - Create an Azure Kubernetes Cluster and configure NGINX Ingress controller](05-create-an-azure-k8s-cluster/README.md)

   Create an Azure Kubernetes Cluster

## [06 - Configure Azure DevOps Pipeline for CI/CD into Azure Kubernetes Service](06-configure-ado-pipeline-for-ci-cd-into-aks/README.md)

   Configure Azure DevOps Pipeline for CI/CD into Azure Kubernetes Service

## [07 - Connect PetStoreApp and PetStoreService Together](07-connect-petstoreapp-and-petstoreservice-together/README.md)

   Connect PetStoreApp and PetStoreService Together

## [08 - Configure Apps to use Application Insights](08-configure-apps-to-use-application-insights/README.md)

   Configure Apps to use Application Insights

## [09 - Configure API Management in front of PetStoreService](09-configure-apim-in-front-of-petstoreservice/README.md)

   Configure API Management in front of PetStoreService

## [10 - Configure B2C for PetStoreApp Identity Management](10-configure-b2c-for-petstoreapp-identity-management/README.md)

   Configure B2C for PetStoreApp Identity Management

## [11 - DataOps: Configure Azure DevOps Pipeline for CI/CD into Azure Databricks](11-configure-ado-pipeline-for-ci-cd-into-databricks/README.md)

   Configure Azure DevOps Pipeline for CI/CD into Azure Databricks

## [12 - Configure Azure DevOps pipelines to execute automated regression tests](12-configure-petstore-automation-testing)

   Configure Azure DevOps pipelines to execute automated regression tests
   
## [13 - Build and Deploy Azure Functions](13-build-deploy-azure-functions)

   Build and Deploy Azure Functions

## [14 - Build a Canvas Power App that uses a Custom Connector to Pull Data](14-build-power-apps)

   Build a Canvas Power App that uses a Custom Connector to Pull Data

## [15 - Build a Logic App to send an email when a message is received in a Service Bus topic](15-build-logic-app-to-send-email-when-message-received-in-service-bus)

   Build a Logic App to send an email when a message is received in a Service Bus topic

## [16 - Build a Bicep IaC Template and Configure a DevOps Pipeline for Self Service Deployment of Azure Infrastructure](16-build-bicep-iac-template-configure-devops-pipeline-self-service-deployment-azure-infrastructure/README.md)

## [17 - Configure SignalR to send Real-Time Analytics to Client Browser](17-configure-signalr-to-send-real-time-analytics-to-client-browser/README.md)

---

## Legal Notices

Microsoft and any contributors grant you a license to the Microsoft documentation and other content
in this repository under the [Creative Commons Attribution 4.0 International Public License](https://creativecommons.org/licenses/by/4.0/legalcode),
see the [LICENSE](LICENSE) file, and grant you a license to any code in the repository under the [MIT License](https://opensource.org/licenses/MIT), see the
[LICENSE-CODE](LICENSE-CODE) file.

Microsoft, Windows, Microsoft Azure and/or other Microsoft products and services referenced in the documentation
may be either trademarks or registered trademarks of Microsoft in the United States and/or other countries.
The licenses for this project do not grant you rights to use any Microsoft names, logos, or trademarks.
Microsoft's general trademark guidelines can be found at http://go.microsoft.com/fwlink/?LinkID=254653.

Privacy information can be found at https://privacy.microsoft.com/en-us/

Microsoft and any contributors reserve all other rights, whether under their respective copyrights, patents,
or trademarks, whether by implication, estoppel or otherwise.

