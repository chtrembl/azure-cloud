## <img style="width:6%;opacity:80%;" src="azure.svg"> Portfolio

---

### My Azure Demos & Learning Guides

[Learn Azure with the Pet Store Learning Guides](/azure-cloud/petstore)
<a href="https://github.com/chtrembl/azure-cloud/raw/main/petstore/petstore_architecture.png?raw=true" target="_blank"><img src="https://github.com/chtrembl/azure-cloud/raw/main/petstore/petstore_architecture.png?raw=true"/></a>

<p/>
  
Often, it is handy to have an N-Tiered Azure architected deployment ready to go, complete with functional code and CI/CD into services for you to experiment Azure with. Since it is usually hard to find one that hits many of the App Dev Services of Azure, I’ve decided to build one. Perhaps you’re learning new Azure technologies, giving Azure demos, looking for a prototype with Azure or simply just curious about Azure, these learning guides will get you on your way to running a full hypothetical Azure Pet Store application and all of its dependencies, using the Azure technologies, as illustrated below. Already built and cloud agnostic are a Pet Store web app and 3 Pet Store microservices that you can deploy into Azure App Service (petstoreapp) and Azure Kubernetes Service (petstorepetservice, petstoreproductservice & petstoreorderservice). You will first build and run these locally (optional) and slowly add in the other services depicted (Azure B2C, Application Insights, API Management, Function App, Logic App, Service Bus, Power App, Regression Suite etc…). These applications were built with Java/Spring Boot and while the guides are not necessarily focused on coding (not required) you are encouraged to build/deploy them locally (All you need is Docker locally) to get the full developer experience. You can however use Azure DevOps Pipelines and GitHub Actions to do the build & deploys for you and make changes directly in your cloud repository instead of locally. View to learn more!

<p/>

You can view the live deployment here <a href="https://azurepetstore.com" target="_blank">https://azurepetstore.com</a>
