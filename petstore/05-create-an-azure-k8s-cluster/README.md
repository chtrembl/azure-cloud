# 05 - Create an Azure Kubernetes Cluster

__This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)__

Lets get an AKS Cluster provisioned in the same Resource Group as your other services (that you provisioned in 00-setup-your-envrionment)

> 📝 Please Note, You can use AZ CLI or Azure Portal to do this. If you didn't complete the first two guides, 01-build-the-docker-images and 02-push-the-docker-images-to-acr and/or do not have a local environment for building Java applications and pushing Docker Images, you will want to provision an AKS Cluster via Azure Portal and let Azure DevOps Piplelines handle the CI/CD for you (next guide). Complete the first step within Azure Portal below and then move on to the next guide. Otherwise, skip the Azure Portal AKS provisioning and follow along to build/deploy/provision locally.

##Provision AKS via Azure Portal

##Provision AKS via Azure CLI

> 📝 Please Note, We will be using the [Azure CLI](https://docs.microsoft.com/en-US/cli/azure/install-azure-cli?view=azure-cli-latest) here to complete all of the necessary steps. The CLI is handy and often used for scripting tasks.

1. Login to your Azure account

	`az login`

2. Choose your Azure subscription (enter your subscription id here, subscriptions are listed after logging in above)

	`az account set -s <YourSubscriptionIDThatYouHaveBeenUsing>`

3. You should already have a Resource Group that you have been using for all of the previous guides. If so, skip this step. If not, create a resource group to group for your AKS Cluster.
	
	`az group create --name=<yourresourcegroup> --location=eastus`

4. You should arleady have an Azure Container Registry that you have been using for all of the previous guides. If so, skip this step. If not, create an Azure Container Registry (ACR) to hold our Docker images
	  
		`az acr create --resource-group <yourresourcegroup> --location eastus

		--name azurepetstorecr --sku Basic`

5. Set the Azure Container Registry to use

	`az configure --defaults acr=<yourazurecontainerregistry>`
  
6. Login to the Azure Container Registry

	`az acr login -n <yourazurecontainerregistry>`
 
7. (Optionally) Build a Pet Store Service Docker Image and push to the Azure Container Registry. This will allow us to test our Pet Store Service running in AKS before we setup CI/CD with Azure DevOps Pipelines. If you didnt complete guides 01-build-the-docker-images and 02-push-the-docker-images-to-acr and/or you do not have a local environment for building Java applications and Pushing Docker images, no worries, you can skip this step.

  
8. Create a Kubernetes cluster in an Azure Kubernetes Service (AKS)

	  ` az aks create --resource-group=<yourresourcegroup> --name=azurepetstore-akscluster --attach-acr <yourazurecontainerregistry> --dns-name-prefix=<youralias>azurepetstoreserviceaks --generate-ssh-keys`

	 This will take some time to complete, 5-10 minutes or so...

9. Install `kubectl` using the Azure CLI. Linux users may have to prefix this command with `sudo` since it deploys the Kubernetes CLI to `/usr/local/bin`
 
	`az aks install-cli`
  
10. Download the cluster configuration information so you can manage your cluster from the Kubernetes web interface and `kubectl`
  
	`az aks get-credentials --resource-group=<yourresourcegroup> --name=azurepetstore-akscluster`
 
11. Using VI or VSC etc... create a deployment.yaml in your petstoreservice/ root with the following contents and save... This deployment yaml is pretty simplified. There is a Deployment to configure Kubernetes with 1 replica of the petstoreservice Docker Image that we built above (Ultimately our running Docker image is a Kubernetes Container) We have configured container resources as well, and we know our Spring Boot petstoreservice will listen to HTTP requests on 8080. We will want to access these containers externally, so we also configure a Kubernetes Service (LoadBalancer) to expose our container. Instead of exposing it on port 8080, we will target port 80.
```


---
➡️ Next guide: [06 - Configure Azure DevOps Pipeline for CI/CD into Azure Kubernetes Service](../06-configure-devops-pipeline-for-ci-cdREADME.md)
