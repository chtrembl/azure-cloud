
# How to develop an Azure Function App with Java and Build/Deploy it to Azure Container Registry/Function App Service

 - Using [azure-functions-archetype](https://github.com/microsoft/azure-maven-archetypes/tree/develop/azure-functions-archetype) we can instruct Maven to generate a fully functional Java project that contains all of the scaffolding needed to get started with developing Azure Functions. This document assumes you already have [Azure Functions Core Tools](https://github.com/Azure/azure-functions-core-tools) installed, which is needed for local testing. We will also generate a Dockerfile here as well.
***Note, at the time of this document, 7/2020, only Java 8 is supported***
	 ```xml
	  mvn archetype:generate -B \
	  -DarchetypeGroupId="com.microsoft.azure" \
	  -DarchetypeArtifactId="azure-functions-archetype" \
	  -Dversion="1.0-SNAPSHOT" \
	  -DgroupId="com.chtrembl" \
	  -DartifactId="petstorefunctions"
	  -Ddocker
	 ```
	 Import this project into your IDE of choice. If you inspect the pom.xml you will notice the Azure plugins that allow us to test our function code locally, executing as we would normally within Azure.
	```xml
    <plugin>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>azure-functions-maven-plugin</artifactId>
        <version>${azure.functions.maven.plugin.version}</version>
        ...

	```
	If you inspect Function.java you will see a scaffolded Java class with an auto generated function for you to get started with.  This interface com.microsoft.azure.functions.annotation.FunctionName, allows us to start writing our code that will ultimately be managed as an Azure Function App. This example function is using an HttpTrigger, which tells Azure Function App to execute it on any incoming, anonymous HTTP GET or POST request.
	
	 ```java
	@FunctionName("HttpExample")
	public HttpResponseMessage run(
	         @HttpTrigger(
	             name = "req",
	             methods = {HttpMethod.GET, HttpMethod.POST},
	             authLevel = AuthorizationLevel.ANONYMOUS)
	             HttpRequestMessage<Optional<String>> request,
	         final ExecutionContext context) {
	     context.getLogger().info("Java HTTP trigger processed a request.");

	     // Parse query parameter
	     final String query = request.getQueryParameters().get("name");
	     final String name = request.getBody().orElse(query);

	     if (name == null) {
	         return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
	     } else {
	         return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
	     }
	 }
	```
 - We can also use the the maven plugin to run our function(s) locally.
	```
	mvn clean package
	mvn azure-functions:run
	```
	You should see the following:
	
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstorefunction/cli1.png?raw=true)
	 ```
	 curl http://localhost:7071/api/HttpExample?name=Hello%20PetStore%20Shopper
	 Hello, Hello PetStore Shopper
	 ```
 - Build the Docker Image.
 	 ```
	docker build -t petstorefunctions .
	 ```
 - Run/Test the Docker Image.
 	 ```
	docker run -p 8080:80 -it petstorefunctions:latest
	curl http://localhost:8080/api/HttpExample?name=Hello%20PetStore%20Shopper
	Hello, Hello PetStore Shopper
	 ```
 - Push the Docker Image to your Azure Container Registry.
	```
	az acr build --file Dockerfile --registry petstorecr --image petstorefunctions .
	``` 
	If you head to the Azure Portal Container Registry you will see your image.
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstorefunction/cr1.png?raw=true)
 
 - Create a Resource Group for your Functions.
	```
	az group create --name PetStoreFunctionsRG  --location eastus
	``` 
 - Create a Plan for your Functions, you can ready about Function [pricing here](az%20functionapp%20plan%20create%20--resource-group%20PetStoreFunctionsRG%20--name%20PetStoreFunctionsPlan%20--location%20eastus%20--number-of-workers%201%20--sku%20EP1%20--is-linux), I am using Elastic Premium 1 on a Linux Container. You may choose to run with the Servlerless option if cold starts are acceptable for your scenario.
	```
	az functionapp plan create --resource-group PetStoreFunctionsRG --name PetStoreFunctionsPlan --location eastus --number-of-workers 1 --sku EP1 --is-linux
	``` 
 - Create a Storage Account.
	
	***Note, On any plan, a function app requires a general Azure Storage account, which supports Azure Blob, Queue, Files, and Table storage. This is because Azure Functions relies on Azure Storage for operations such as managing triggers and logging function executions...***

	```
	az storage account create --name petstorestorage --location eastus --resource-group PetStoreFunctionsRG --sku Standard_LRS
	``` 
 -  Create and Configure your Function App.
	 ```
	 az functionapp create --name PetStoreFunctions --storage-account petstorestorage --resource-group PetStoreFunctionsRG --plan PetStoreFunctionsPlan --deployment-container-image-name petstorefunctions:latest
	 ```
 - You can then head to the Azure Portal to review your Function App. You can also configure Continuous Delivery here as well.

	 ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstorefunction/portal1.png?raw=true)
 - Head to Overview and grab the URL for your Function App.
 
	  ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstorefunction/portal2.png?raw=true)
 - Test your Function App.
	 ```
	curl https://petstorefunctions.azurewebsites.net/api/HttpExample?name=Hello%20PetStore%20Shopper
	Hello, Hello PetStore Shopper
	```