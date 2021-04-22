# Azure Pet Store Reference Guides

Often times it is handy to have an N-Tiered Architecture ready to go and since it is usually hard to find one that hits all of the many services of Azure App Dev, I've decided to build one. Perhaps you're learning new technologies, giving demos, looking for a prototype or even just curious, these guides will get you on your way to running a full Hypothetical Azure Pet Store Application using the technologies illustrated below. 

![enter image description here](https://raw.githubusercontent.com/chtrembl/azure-cloud/petstore/main/petstore_architecture.png)

The objective is to showcase the many uses of App Dev Services within Azure. Below are some of the useful scenarios that are currently available.

 - CI/CD into Azure App Service & Azure Kubernetes Service using Azure DevOps Pipelines and GitHub Actions
 - N-Tiered Architecture using Pet Store Application deployed to App Services and Pet Store Service deployed to Azure Kubernetes Service
 - N-Tiered Correlated Telemetry between the layers, allowing for Full End to End Reporting using KQL within Azure Application Insights
 - Authentication of Pet Store Application users via Azure B2C
 - API Management in front of Pet Store Service, ensuring Pet Store Application is subscribed to and requests are made within rate/quota limits
 - Azure Key Vault to store sensitive keys
 - Blue/Green Deployments to Pet Store Application via Deployment Slots

*Please Note, I borrowed the format of these guides from the awesome folks who run Azure Spring Cloud over at  https://github.com/microsoft/azure-spring-cloud-training (Be sure to check them out because Azure Spring Cloud rocks and so does that training)*

## What you should expect

This is not the official documentation but an opinionated approach/guidance.

This is hands-on and will be hands on keyboard. However, once complete, this will be running in you're subscription and always readily available. The Pet Store Application and Pet Store Service are built with Java and Spring Boot. You're welcome to import these into your IDE of choice, write code and/or contribute back, however that is not mandatory for these guides. You can simply build the Docker Images and that should suffice. Writing code is not actually needed to benefit from the learning/deploying of these technologies, Docker images are all you need.

## [00 - Setup your environment](00-setup-your-environment/README.md)

Prerequisites and environment setup.

## [01 - Build the Docker Images](01-build-the-docker-images/README.md)

Build the Docker Images



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
