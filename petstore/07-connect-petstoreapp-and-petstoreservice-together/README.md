# 07 - Connect PetStoreApp and PetStoreService Together

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

In this section, we'll connect PetStoreApp and PetStoreService Together

> 📝 Please Note, We will assume you have forked the azure-cloud repository, it is the easiest way to get going (for instructions on this view the "**Forking the azure-cloud**" section in [00-setup-your-environment](../00-setup-your-environment/README.md). Also, both PetStoreApp and PetStoreService use a Spring Boot Application properties file named application.yml to drive the functionality/configuration of these applications which is located in src/main/resources/application.yml of both projects. By default, this file has all of the properties that are needed throughout the guides, and by default are commented out. This means that the applications will start automatically without having to configure anything. As you progress through the guides, each guide will inform you of what properties to uncomment and configure within your environment. If you have not already done so, login to your GitHub account, head to https://github.com/chtrembl/azure-cloud, and fork.

Remember that Kubernetes External IP Address from the previous guide? Here you will update your App Service Configuration Settings to reflect it, and save it (Container Restart will happen automagically)

Currently PetStoreApp was deployed with an application properties file src/main/resources/application.yml that has the following

```
petstore:
  service:
    pet:
     url: ${PETSTOREPETSERVICE_URL:http://localhost:8081}
    product:
      url: ${PETSTOREPRODUCTSERVICE_URL:http://localhost:8082}
    order:
      url: ${PETSTOREORDERSERVICE_URL:http://localhost:8083}
```

If PETSTOREPETSERVICE_URL, PETSTOREPRODUCTSERVICE_URL & PETSTOREORDERSERVICE_URL are not injected at runtime, then the defaults will be used. (localhost is only useful for local development). Let's inject the External IP Address of your AKS Cluster that you got from [05 - Create an Azure Kubernertes Cluster](../05-create-an-azure-k8s-cluster/README.md)

Head to the Azure Portal and locate your App Service and select Configuration.

Select "New application setting"

Do this 3 times, one for PETSTOREPETSERVICE_URL, PETSTOREPRODUCTSERVICE_URL & PETSTOREORDERSERVICE_URL all pointing to the same ip address of your AKS External IP.

You should see something similar to the below image:

![](images/07_1.png)

Save those, you will get a new container runtime and then you should be able to navigate the application selecting products and adding them to your cart etc...

🎉Congratulations, you now have Pet Store App pulling data from Pet Store Service

Things you can now do now with this guide

☑️ Integrate multiple applications

---

➡️ Next guide: [08 - Configure Apps to use Application Insights](../08-configure-apps-to-use-application-insights/README.md)
