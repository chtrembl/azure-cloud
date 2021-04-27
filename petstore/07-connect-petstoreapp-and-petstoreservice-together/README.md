# 07 - Connect PetStoreApp and PetStoreService Together

__This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)__

> 📝 Please Note, We will assume you have forked the azure-cloud repository, it is the easiest way to get going. Also, your application.yml for both PetStoreApp and PetStoreService, located in src/main/resources/application.yml of both projects, should have all of the Azure properties commented out. They may be uncommented becasue I am using the full deployment against this repository. You will want to comment them for now and slowly uncomment them as we progress through the guides, each guide will inform you. If you have not already done so, login to your GitHub account, head to https://github.com/chtrembl/azure-cloud, select Fork, select your GitHub account.

Remember that Kubernetes Load Balancer IP Address from the previous guide? Update your App Service Configuration Setting for ```PETSTORESERVICE_URL``` to reflect it, and save it (Container Restart will happen automagically)

You should see something similar to the below image:

![](images/appservice1.png)

Head over to yor App Service URL and now test it out. You should see the same Pet Store App, however now able to pull Dog Breeds JSON from the Pet Store Service.

You should see something similar to the below image:

![](images/appservice2.png)

Click On Dog Breeds

You should see something similar to the below image:

![](images/appservice3.png)

🎉Congratulations, you now have Pet Store App pulling data from Pet Store Service

Things you can now do now with this guide

☑️ Integrate two applications

---
➡️ Next guide: [08 - Configure Apps to use Application Insights](../08-configure-apps-to-use-application-insights/README.md)
