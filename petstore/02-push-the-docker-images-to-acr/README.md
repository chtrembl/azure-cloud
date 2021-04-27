# 02 - Push the Docker Images to Azure Container Registy (Local Development Only)

__This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)__

In this section, we'll push the Docker Images to Azure Container Registy

**1. Push the Pet Store App Docker Image to Azure Container Registry**

> 📝 Please Note, Since the Docker Images were built in the previous guide, we can run from any path on the terminal. But if you prefer, or if you plan to build more images, cd to azure-cloud/petstore/petstoreapp 

> 📝 Please Note, We will assume you have forked the azure-cloud repository, it is the easiest way to get going. Also, your application.yml for both PetStoreApp and PetStoreService, located in src/main/resources/application.yml of both projects, should have all of the Azure properties commented out. They may be uncommented becasue I am using the full deployment against this repository. You will want to comment them for now and slowly uncomment them as we progress through the guides, each guide will inform you. If you have not already done so, login to your GitHub account, head to https://github.com/chtrembl/azure-cloud, select Fork, select your GitHub account.

run the following commands:

```az login``` 

```az account list --output table```

> 📝 Please Note, use your subscription for "your subscription" and your container registry value for "youraliaspetstorecr" from the first guide 00-setup-your-environment

```az account set --subscription <your subscription>```

```az acr login --name <youraliaspetstorecr>```

> 📝 Please Note, the following is what enables us to get Web Hook control for our App Service Continuous Integration

```az acr update -n <youraliaspetstorecr> -g <yourresourcegroup> --admin-enabled true```

> 📝 Please Note, tag your local Docker image built in the previous guide so that we can push it to Azure Container Registry then push it

```docker image tag petstoreapp:latest <youraliaspetstorecr>.azurecr.io/petstoreapp:latest```

```docker push <youraliaspetstorecr>.azurecr.io/petstoreapp:latest```

You should see something similar to the below image:

![](images/petstoreapp_push.png)

If you head to Azure Portal and view your Container Registry Resource "youraliaspetstorecr" you should see something similar to the below image:

![](images/petstoreapp_cr.png)

**1. Push the Pet Store Service Docker Image to Azure Container Registry**

> 📝 Please Note, Since the Docker Images were built in the previous guide, we can run from any path on the terminal. But if you prefer, or if you plan to build more images, cd to azure-cloud/petstore/petstoreservice

You are still logged in from before...

run the following commands:

> 📝 Please Note, tag your local Docker image built in the previous guide so that we can push it to Azure Container Registry then push it

```docker image tag <petstoreservice>:latest <youraliaspetstorecr>.azurecr.io/petstoreservice:latest```

```docker push <youraliaspetstorecr>.azurecr.io/petstoreservice:latest```

Things you can now do now with this guide

☑️ Administration of Azure Container Registry, pushing of Docker Images

---
➡️ Next guide: [03 - Configure App Service for continuous deployment](../03-configure-app-service-for-cd/README.md)