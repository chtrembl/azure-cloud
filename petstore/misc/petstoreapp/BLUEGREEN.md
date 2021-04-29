

# Blue-Green (A/B) Deployments with Azure App Service are simplified with Deployment Slots

The objective with Blue-Green deployments is eliminate downtime within your applications by deploying new changes to some parallel environment (Green) where these new changes can be smoke/regression tested while the live environment (Blue) still handles the live traffic until Green has been vetted out. Once the new changes are confirmed in Green, the environments (slots) can be be easily swapped. At this point Blue will have the latest changes while Green becomes the previous/rollback environment.

*Below outlines the Azure Portal steps to configure your App Service to handle Blue-Green deployments.
This can be automated with the Azure CLI, however below focuses on Azure Portal UI.*

**Head to Azure Portal and select App Service.**

*This step assumes you already have an application deployed to Azure App Service.*

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot1.png?raw=true)

**Select Container Settings and disable Continuous Deployment.**

*This will be the product (Blue) slot, we will use our Green Slot that we create next, to handle our CD.*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot2.png?raw=true)

**Select Deployment Slots.**
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot3.png?raw=true)

**Add a Slot, you can name this however you prefer. Staging-Green is my preference. You can also clone settings from your existing container. Select Add.**

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot4.png?raw=true)

**Your new Deployment Slot appears, note Production (Blue) is still taking 100% traffic.**

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot5.png?raw=true)

**Select the new Staging-Green slot, and enable Continuous Deployment.**

*Continuous Deployment gives us the ability to push new Docker Images to our Azure Container Registry allowing this Slot to create/destroy new containers as needed (without any dowtime to our Production/Blue container/slot).*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot6.png?raw=true)

**Push a new Docker Image to your Azure Container Registry.**

*Ideally you have an Azure DevOps Pipeline/GitHub Action to do this for you. Here I am building a Docker Image and pushing it into Azure Container Registry.*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot7.png?raw=true)

**Back in the Log Stream for Staging-Green, you will see a new container that gets created from your recently pushed Docker Image.**

*These slots/containers are containers running within your App Service Plan.*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot8.png?raw=true)

**Head to Overview of your Staging-Green to grab the test URL that allows you to smoke/regression test.**

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot9.png?raw=true)

**Once this Green slot has been vetted out, you can select Swap.**

*This will create a new container in Production (Blue) with the same image used from Staging-Green and destroy the old once it is live. Production (Blue) is now taking 100% traffic on the newly constructed container with zero downtime.*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot10.png?raw=true)

**Confirm your production Log Stream to see the new container logs.**

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/slot11.png?raw=true)

