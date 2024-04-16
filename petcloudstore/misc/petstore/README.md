

# How to build a Java Spring Boot Service API with Open API / Swagger, deploy it to Azure Kubernetes Service (AKS) and implement CI/CD pipelines with Azure DevOps (ADO)

In my experience, service development approaches have often been debated (healthy debates) and can boil down to two schools of thought, **contract first** or **code first**. Whether your doing both client/service development all internal within one development team or perhaps service development by one team to be consumed by external clients/teams, both approaches have merit. However, we won't be debating them here in this tutorial. Instead, we will be using **contract first** via the handy [Swagger PetStore ](https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml) example yaml contract. Throughout this tutorial will will use Open API / Swagger to generate a a Spring Boot API Service from a pre existing contract yaml file. We will then leverage Azure Kubernetes Service (AKS) for orchestrating our deployments. We will also implement CI/CD pipelines using Azure DevOps for maximum developer productivity, allowing development/build/deploy to be initiated all within VSC.

>  *In a subsequent tutorial, we will use this PetStore Open API/Swagger generated tutorial and build upon it using Azure API Management (APIM), which is where the Swagger contract will come in handy* 

## Objectives

**1. Open API Tooling to generate Java Spring Boot boilerplate/scaffolding service API from contract yaml**

**2. Containerize the newly generated service API**

**3. Create Azure Kubernetes Service (AKS) Cluster / Deploy Java Spring Boot API Docker Image using Kubectl**

**4. Setup CI/CD pipelines with Azure DevOps**  

## 1. Open API Tooling to generate Java Spring Boot boilerplate service API from contract YAML  

There are several code generation tools available, here we will use [openapi-generator-cli](https://www.npmjs.com/package/@openapitools/openapi-generator-cli) to quickly generate our project/service API code. There are many languages/frameworks available to choose from, we will be using Spring Boot. This approach will quickly generate our API based on Open API specification to the following [Swagger PetStore ](https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml) example YAML, ensuring our implementation adheres to the contract set forth via the yaml (**contract first**)

From your terminal run the following to generate the petstore scaffolding built using Maven
```
npm install @openapitools/openapi-generator-cli -g
openapi-generator generate -i https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml -g spring -o petstore
```
You can use a CLI or IDE of choice for the next steps.

Below is CLI, using maven we can start up the petstore application and test it via curl, you should see the following json response.
```
cd petstore
mvn package spring-boot:run
curl http://localhost:8080/v1/pets

{ "name" : "name", "id" : 0, "tag" : "tag" }
```
Below is [Visual Studio Code (VSC)](https://code.visualstudio.com/) (with Java, Maven, Spring Extensions installed) as my IDE, however, you can also use [Spring Tool Suits (STS)](https://spring.io/tools) as your IDE. (For STS use, you will find a .project file in the repository here)

If you import this petstore project into VSC you will see the following. Notice the PetsApiController.java that was auto generated via openapi-generator, pretty cool stuff. At this point we have a functional API adhering to our contract yaml.  

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/vsc1.png?raw=true)

 We can use the VSC Spring Boot Extension to start up our application and test it out the API by visiting [http://localhost:8080/v1/pets](http://localhost:8080/v1/pets)

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/browser1.png?raw=true)

 This JSON response is coming from the auto generated PetsAPI.java default GET Request implementation for the /pets operation. For the remainder of this tutorial, the auto generated operations will suffice. However, in a production implementation you will probably want to implement your real business logic and/or externalize the auto generated scaffolding, separate from your real business logic, allowing for continuous integration with your API yaml (out of scope for this tutorial)

>  *Although we wont be focusing on Swagger in this tutorial, since this Pet Store API is built using Swagger Fox, you will notice the Swagger UI dashboard located at [http://localhost:8080/swagger-ui.html#/pets](http://localhost:8080/swagger-ui.html#/pets) which provides the OpenAPI Portal into the Pet Store API.*

## 2. Containerize the newly generated service API 

1. Using VI or VSC etc... create a Dockerfile in your petstore/ root with the following contents and save... This is a very basic Docker file with fundamental instructions. We are instructing Docker to use openjdk 8 as the base image. We are also instructing Docker to use our compiled Spring Boot jar as the entry point to our application. Spring Boot does the rest for us once the jar executed by the JVM.
	
	`vi Dockerfile`
	```
	FROM openjdk:8-jdk-alpine
	ARG JAR_FILE=target/*.jar
	COPY ${JAR_FILE} petstoreapi.jar
	ENTRYPOINT ["java","-jar","/petstoreapi.jar"]
	```
2. Using VI or VSC etc... update your pom.xml to configure the [jib-maven-plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin) to build the PetStoreAPI Docker image, this will be used in later steps. Add the following properties and plugin to ensure you use your container registry as the docker.image.prefix

	```xml
	<properties>
	<docker.image.prefix>petstoreapi.azurecr.io</docker.image.prefix>
	<jib-maven-plugin.version>2.2.0</jib-maven-plugin.version>
	<java.version>1.8</java.version>
	</properties>
	...
	<plugin>
	<artifactId>jib-maven-plugin</artifactId>
	<groupId>com.google.cloud.tools</groupId>
	<version>${jib-maven-plugin.version}</version>
	<configuration>
	<from>
	<image>mcr.microsoft.com/java/jdk:8-zulu-alpine</image>
	</from>
	<to>
	<image>${docker.image.prefix}/${project.artifactId}</image>
	</to>
	</configuration>
	</plugin>
	```  
3. Build a PetStoreAPI Docker image, this image will ultimately get pushed to the Azure Container Registry (ACR) that we will also create. This will build an image for local development using Docker manually, which is useful for local dev/testing.

	`docker build -t petstoreapi .`

	You can also view your locally built images (useful for running various versions, however the latest is most common)

	`docker image ls`

4. Start the PetStoreAPI container locally

	`docker run -p 8080:8080 petstoreapi:latest` 

6. Test is out via curl, you should see the following json response.

	```
	curl http://localhost:8080/v1/pets

	{ "name" : "name", "id" : 0, "tag" : "tag" }
	```

## 3. Create Azure Kubernetes Service (AKS) Cluster / Deploy Java Spring Boot API Docker Image using Kubectl

 The following assumes you have an [Azure](https://azure.microsoft.com/en-us/) subscription to work with. If not, you you can sign up [here](https://azure.microsoft.com/en-us/) to get started. There are several ways to create Azure resources, such as Azure Web Portal or the [Azure CLI](https://docs.microsoft.com/en-US/cli/azure/install-azure-cli?view=azure-cli-latest), among others. We will be using the [Azure CLI](https://docs.microsoft.com/en-US/cli/azure/install-azure-cli?view=azure-cli-latest) here to complete all of the necessary steps. The CLI is handy and often used for scripting tasks.

1. Login to your Azure account

	`az login`

2. Choose your Azure subscription (enter your subscription id here, subscriptions are listed after logging in above)

	`az account set -s <YourSubscriptionID>`

3. Create a resource group to group all of our Azure resources (AKS resources, APIM resources, Docker image etc..)
	
	`az group create --name=petstorerg --location=eastus`

4. Create an Azure Container Registry (ACR) to hold our Docker images
	  
		`az acr create --resource-group petstorerg --location eastus

		--name petstorecr --sku Basic`

5. Set the Azure Container Registry to use

	`az configure --defaults acr=petstorecr`
  
6. Login to the Azure Container Registry

	`az acr login -n petstorecr`
 
7. Build a PetStoreAPI Docker image and push to the Azure Container Registry that was created in step 4

	`az acr login && mvn compile jib:build`

>  *If your getting 401 Unauthorized issues witj JIB you can visit https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#what-should-i-do-when-the-registry-responds-with-unauthorized*
  
> An alternate approach is to push your image manually via Docker.
>
> Tag the previously built image `docker tag petstoreapi
> petstorecr.azurecr.io/petstoreapi:v1`
>
> Push the image to your Azure Container Registry `docker push
> petstorecr.azurecr.io/petstoreapi:v1`
  
8. Once your repository/image exist in your ACR, you can create a Kubernetes cluster in an Azure Kubernetes Service (AKS)

	  ` az aks create --resource-group=petstorerg --name=petstore-akscluster --attach-acr petstorecr --dns-name-prefix=petstoreapiaks --generate-ssh-keys`

	 This will take some time to complete, 5-10 minutes or so...

9. Install `kubectl` using the Azure CLI. Linux users may have to prefix this command with `sudo` since it deploys the Kubernetes CLI to `/usr/local/bin`
 
	`az aks install-cli`
  
10. Download the cluster configuration information so you can manage your cluster from the Kubernetes web interface and `kubectl`
  
	`az aks get-credentials --resource-group=petstorerg --name=petstore-akscluster`
 
11. Using VI or VSC etc... create a deployment.yaml in your petstore/ root with the following contents and save... This deployment yaml is pretty simplified. There is a Deployment to configure Kubernetes with 1 replica of the petstoreapi Docker Image that we built above (Ultimately our running Docker image is a Kubernetes Container) We have configured container resources as well, and we know our Spring Boot petstoreapi will listen to HTTP requests on 8080. We will want to access these containers externally, so we also configure a Kubernetes Service (LoadBalancer) to expose our container. Instead of exposing it on port 8080, we will target port 80.

	`vi deployment.yaml`

	```yaml

	apiVersion: apps/v1

	kind: Deployment

	metadata:

	name: petstoreapi

	spec:

	replicas: 1

	selector:

	matchLabels:

	app: petstoreapi

	template:

	metadata:

	labels:

	app: petstoreapi

	spec:

	nodeSelector:

	"beta.kubernetes.io/os": linux

	containers:

	- name: petstoreapi

	image: petstorecr.azurecr.io/petstoreapi:v1

	resources:

	requests:

	cpu: 100m

	memory: 128Mi

	limits:

	cpu: 250m

	memory: 256Mi

	ports:

	- containerPort: 8080

	---

	apiVersion: v1

	kind: Service

	metadata:

	name: petstoreapi

	spec:

	type: LoadBalancer

	ports:

	- port: 8080

	targetPort: 80

	selector:

	app: petstoreapi

	```

12. Deploy your Kubernetes configuration to AKS (You can run this command any time you want to deploy updates to be re orchestrated by K8S/AKS)

	`kubectl apply -f deployment.yaml`

	If successful you will be able to access the AKS cluster via the Service Load Balancer configured above. Since this is dynamic, run the following

	`kubectl get services -o jsonpath={.items[*].status.loadBalancer.ingress[0].ip} --namespace=default`
 
	This will output the Service Load Balancer IP address, 52.191.95.150 for example...

	Then access petstore api via the ipaddress (52.191.95.150 in my case)
  
	```
	curl http://52.191.95.150:8080/v1/pets

	{ "name" : "name", "id" : 0, "tag" : "tag" }
	```

>  *If your not able to access your Service Load Balancer, something may have went wrong with the deployment. You can run the following command to get some insight, if you see something in STATUS other than RUNNING, you will need to investigate by getting the pod details and troubleshooting, seen below...*

>
 ```
~/dev/git/petstore$ kubectl get all
NAME READY STATUS RESTARTS AGE
pod/petstoreapi-77c556d945-btb65 0/1 ImagePullBackOff 0 14s

~/dev/git/petstore$ kubectl describe pod/petstoreapi-77c556d945-btb65
  

Warning Failed 11s (x3 over 53s) kubelet, aks-nodepool1-56647556-vmss000001 Failed to pull image "petstoreapi:v1": rpc error: code = Unknown desc = Error response from daemon: pull access denied for petstoreapi, repository does not exist or may require 'docker login': denied: requested access to the resource is denied

```

>  *In the above, ImagePullBackOff indicated that the image could not be pulled from the Azure Container registry, checking permissions and container image definition in deployment.yaml usually resolves...*

  

13. You can view the application logs from the Spring Boot running container via the Azure Portal. If you head to the Azure Portal > Kubernetes Services and select the petstore-akscluster, there will be an Insights link. Select the Containers tab followed by the running container "petstoreapi" from the table below (we only have 1). You can the select "View container logs" which will show standard out from the Spring Boot petstoreapi container.

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/insights1.png?raw=true)

  ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/insights2.png?raw=true)

*Note, you can also use kubectl to tail your pod application logs*
```
kubectl get all
kubectl logs --follow <pod names here>
```

## 4. Setup CI/CD pipelines with Azure DevOps
Using Kubectl to orchestrate your containers is fine and well for local development, however for maximum productivity and efficiency in a team setting, Azure DevOps (ADO) offers several features to help accelerate this process (source control git/repos, work item boards and CI/CD pipelines, to name a few). We will take a look at configuring an ADO Project with CI/CD to pull this petstore project from GitHub and build/release to our AKS cluster.
 
We will work in [Azure DevOps](https://dev.azure.com/) (ADO) for the pipeline creation. ADO simplifies this with pipeline yaml creation for you.
 
 1. If you do not have an ADO account you will need to create one along with an organization and project. Seen below is the PetStore Project within the chtrembl organization. Select Pipelines to create a new one.

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops1.png?raw=true)

3. Specify the location of your code, in our case GitHub

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops2.png?raw=true)

  

4. Select a repository to pull from, in our case petstore

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops3.png?raw=true)

5. Select a pipeline for ADO to generate, we will be selecting the Kubernetes Service. This will generate the scaffolding to build/deploy our petstore project to AKS

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops4.png?raw=true)

6. Fill in the AKS meta data required

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops5.png?raw=true)

7. Here you can review the generated scaffolding and make any changes... Notice line 19, I replaced the generated pull secret value with $(crImagePullSecret) in favor of interpolation. Using the variables feature in Pipelines, we can externalize environment specific and or sensitive data. I've chosen to do that here, by adding crImagePullSecret as a variable key, with the value that was originally populated here by ADO Pipelines. Each time a new pipeline is run, crImagePullSecret will be injected. You can learn more about ADO variabes [here](https://docs.microsoft.com/en-us/azure/devops/pipelines/build/variables?view=azure-devops&tabs=yaml)

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops6.png?raw=true)

  

8. Once you review and save, the first pipeline run will initiate

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops7.png?raw=true)

8. At this point your first pipeline is probably successful, but your not able to access the ingress Load Balancer specified above http://52.191.95.150/v1/pets because ADO made some changes without accounting for our ports. ADO will create a [manifests](https://github.com/chtrembl/petstore/tree/master/manifests) folder with deployment.yaml and service.yaml and commit changes to your Git Branch. You will need to modify these deployment yamls to ensure the Spring Boot port 8080 configuration is still present (ADO doesn't know about out these changes we made to support Spring Boot). Also, verify the newly created [azure-pipelines.yml](https://github.com/chtrembl/petstore/blob/master/azure-pipelines.yml  "azure-pipelines.yml") Also, since we built our pipeline with an AKS task, we will be missing the compilation task, in our case Maven. There are several way to get this added. You can ensure the following Maven task is pasted first in [azure-pipelines.yml](https://github.com/chtrembl/petstore/blob/master/azure-pipelines.yml  "azure-pipelines.yml")

	 ```yml
	- task: Maven@3
	   displayName: Build Spring Boot Jar
	   inputs:
			mavenPomFile: 'pom.xml'
			mavenOptions: '-Xmx3072m'
			javaHomeOption: 'JDKVersion'
			jdkVersionOption: '1.8'
			jdkArchitectureOption: 'x64'
			publishJUnitResults: true
			testResultsFiles: '**/surefire-reports/TEST-*.xml'
			goals: 'package'
	```

Once you commit your changes to Git, a new pipeline will execute as seen below which will now build the Spring Boot Jar and forward port 80 to 8080 accordingly. You are now developing, building, deploying from within VSC continuously.

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstore/devops8.png?raw=true)
