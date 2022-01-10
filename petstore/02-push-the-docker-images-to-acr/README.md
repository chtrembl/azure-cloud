# 02 - Push the Docker Images to Azure Container Registry

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

> 📝 Please note, this guide is optional and is useful for local building and deploying outside of Azure (At this point there are no dependencies on Azure, throughout the guides we will leverage various Azure services to facilitate DevOps, elements of container orchestration, telemetry etc...), you can certainly skip ahead and leverage Azure DevOps Pipelines and GitHub Actions to do the build & deploys for you and you can safely jump head to [03-configure-app-service-for-cd](../03-configure-app-service-for-cd/README.md) otherwise please continue to the next guide.

In this section, we'll build the Docker Images and run them locally

## Prerequisites (Local development only if you wish to deploy the Docker images from your local machine into Azure Container Reguistry)

Thus guide assumes you have Docker installed on your machine and the Azure CLI. Until we setup your Azure Pipeline & GitHub Action, we can use Docker and Azure CLI, locally, to build and push both the PetStoreApp and PetStoreService Docker Images into Azure Container Registry. We will also do other things like Administer our Azure Kubernetes Cluster.

- Install Docker Desktop from the following https://docs.docker.com/get-docker and ensure you can run Docker from your command line.

- Install Azure CLI from the following https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-windows?tabs=azure-cli and ensure you can run AZ from your command line.

> 📝 Please Note, Since the Docker Images were built in the previous guide, we can run from any path on the terminal. But if you prefer, or if you plan to build more images, cd to azure-cloud/petstore/petstoreapp, azure-cloud/petstore/petstorespetervice, azure-cloud/petstore/petstoreproductservice and azure-cloud/petstore/petstoresorderervice accordingly

> 📝 Please Note, We will assume you have forked the azure-cloud repository, it is the easiest way to get going (for instructions on this view the "**Forking the azure-cloud**" section in [00-setup-your-environment](../00-setup-your-environment/README.md). Also, both PetStoreApp and PetStoreService use a Spring Boot Application properties file named application.yml to drive the functionality/configuration of these applications which is located in src/main/resources/application.yml of both projects. By default, this file has all of the properties that are needed throughout the guides, and by default are commented out. This means that the applications will start automatically without having to configure anything. As you progress through the guides, each guide will inform you of what properties to uncomment and configure within your environment. If you have not already done so, login to your GitHub account, head to https://github.com/chtrembl/azure-cloud, and fork.

---

**1. Push the Pet Store App Docker Image to Azure Container Registry**

run the following commands:

`az login`

`az account list --output table`

> 📝 Please Note, use your subscription for "your subscription" and your container registry value for "youraliaspetstorecr" from the first guide 00-setup-your-environment

`az account set --subscription <your subscription>`

`az acr login --name <youraliaspetstorecr>`

> 📝 Please Note, the following is what enables us to get Web Hook control for our App Service Continuous Integration

`az acr update -n <youraliaspetstorecr> -g <yourresourcegroup> --admin-enabled true`

> 📝 Please Note, you are now tagging your local Docker image built in the previous guide so that we can push it to Azure Container Registry then push it

`docker image tag petstoreapp:latest <youraliaspetstorecr>.azurecr.io/petstoreapp:latest`

`docker push <youraliaspetstorecr>.azurecr.io/petstoreapp:latest`

You should see something similar to the below image:

![](images/02_1.png)

If you head to Azure Portal and view your Container Registry Resource "youraliaspetstorecr" you should see something similar to the below image:

![](images/02_2.png)

**2. Push the Pet Store Pet Service Docker Image to Azure Container Registry**

> 📝 Please Note, Since the Docker Images were built in the previous guide, we can run from any path on the terminal. But if you prefer, or if you plan to build more images, cd to azure-cloud/petstore/petstorepetservice

You are still logged in from before...

run the following commands:

> 📝 Please Note, you are now tagging your local Docker image built in the previous guide so that we can push it to Azure Container Registry then push it

`docker image tag <petstorepetservice>:latest <youraliaspetstorecr>.azurecr.io/petstorepetservice:latest`

`docker push <youraliaspetstorecr>.azurecr.io/petstorepetservice:latest`

**3. Push the Pet Store Product Service Docker Image to Azure Container Registry**

> 📝 Please Note, Since the Docker Images were built in the previous guide, we can run from any path on the terminal. But if you prefer, or if you plan to build more images, cd to azure-cloud/petstore/petstoreproductservice

You are still logged in from before...

run the following commands:

> 📝 Please Note, you are now tagging your local Docker image built in the previous guide so that we can push it to Azure Container Registry then push it

`docker image tag <petstoreproductservice>:latest <youraliaspetstorecr>.azurecr.io/petstoreproductservice:latest`

`docker push <youraliaspetstorecr>.azurecr.io/petstoreproductservice:latest`

**4. Push the Pet Store Order Service Docker Image to Azure Container Registry**

> 📝 Please Note, Since the Docker Images were built in the previous guide, we can run from any path on the terminal. But if you prefer, or if you plan to build more images, cd to azure-cloud/petstore/petstoreorderservice

You are still logged in from before...

run the following commands:

> 📝 Please Note, you are now tagging your local Docker image built in the previous guide so that we can push it to Azure Container Registry then push it

`docker image tag <petstoreorderervice>:latest <youraliaspetstorecr>.azurecr.io/petstoreorderservice:latest`

`docker push <youraliaspetstorecr>.azurecr.io/petstoreorderservice:latest`

If you head back to Azure Portal and view your Container Registry Resource "youraliaspetstorecr" you should now see all four repositories and images.

Things you can now do now with this guide

☑️ Administration of Azure Container Registry, pushing of Docker Images

---

➡️ Next guide: [03 - Configure App Service for continuous deployment](../03-configure-app-service-for-cd/README.md)
