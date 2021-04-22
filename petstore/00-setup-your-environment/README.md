

# 00 - Setup your environment

__This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)__

In this section, we'll begin to set up your environment

---

## Creating Azure Resources

To save time, we have an ARM template to get a few Azure resources created:

 - App Service (Pet Store Application)
 - App Service Plan (Resources needed to host the Pet Store Application)
 - Container Registry (Used to store Docker Images for Pet Store Application and Pet Store Service)
 
> 💡 Note, Azure B2C, Azure Kubernetes Service, Azure Application Insights, API Management & Azure Key Vault will be setup in the subsequent guides.

Go ahead and Deploy to Azure

 [![Deploy to Azure](https://aka.ms/deploytoazurebutton)](https%3A%2F%2Fraw.githubusercontent.com%2Fchtrembl%2Fazure-cloud%2Fmain%2Fpetstore%2F00-setup-your-environment%2Fazuredeploy.json)

> 💡 Create a new resource when prompted, such as: **azurepetstorerg** and substitute your network alias for **youralias**, or something unique that will appear as a FQDM for accessing your Azure Pet Store Application.

>⏱ The resource provisioning will take some time. **Do not wait!** Continue with the guides.

## Prerequisites

These guides assume you have Docker Desktop installed on your machine. Until we setup your Azure Pipeline & GitHub Action, we will use Docker, locally, to build and push both the PetStoreApp and PetStoreService Docker Images into Azure Container Registry. We will also use Azure CLI to push Docker images into our Azure Container Registry and do other things like Administer our Azure Kubernetes Cluster.

 - Install Docker Desktop from the following https://www.docker.com/products/docker-desktop and ensure you can run Docker from your command line.
 
 - Install Azure CLI from the following https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-windows?tabs=azure-cli and ensure you can run AZ from your command line.



Optionally, you can also run the code locally and/or contribute to code changes, and in this case can then use Visual Studio Code or an IDE of your choice.

---

➡️ Next guide: [01 - Build the Docker Images](../01-build-the-docker-images/README.md)
