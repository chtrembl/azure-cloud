

# Import and Publish your API with Azure API Management using Products and Rate Limits

[**Azure API Management**](https://azure.microsoft.com/en-us/services/api-management/) is a fully managed service that enables you to publish, secure, transform, maintain, and monitor your API's. For the purpose of this demo I will use [openapi-petstore](https://github.com/OpenAPITools/openapi-petstore) API deployed to Azure as the API chosen to be published with Azure API Management. We will cover Products and Rate Limits and what the Developer experience is like when discovering/subscribing to API's.

***Note, more on monetizing, oAuth, Deployments and Versioning to be covered in the future***

## 1. Deploy an Open API and ensure its accessible.
I've chosen to deploy the  [openapi-petstore](https://github.com/OpenAPITools/openapi-petstore) Spring Boot/Swagger API image to my Azure Container Registry/Azure App Service. For the sake of this tutorial I've modified the [Docker](https://github.com/OpenAPITools/openapi-petstore/blob/master/Dockerfile) file to disable API Key and OAuth (We can use APIM) to do this. I've also chosen to expose port 80 and configured Spring Boot to use 80. You could certainly deploy from DockerHub directly and set these and Environment Variables to your container etc... 
```
server.port=80 
```
```
FROM openjdk:8-jre-alpine

WORKDIR /petstore

ENV OPENAPI_BASE_PATH=/v3
ENV DISABLE_API_KEY=1
ENV DISABLE_OAUTH=1

COPY target/openapi-petstore-3.0.0.jar /petstore/openapi-petstore.jar

EXPOSE 80

CMD ["java", "-Dopenapi.openAPIPetstore.base-path=${OPENAPI_BASE_PATH}", "-jar", "/petstore/openapi-petstore.jar"]

```
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/appservice3.png?raw=true)

The API is up and running and can be seen here [https://openapipetstore.azurewebsites.net/](https://openapipetstore.azurewebsites.net/)

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/swagger.png?raw=true)

## 2. Create and Configure Azure API Management Service

 **- Head to Azure Portal and Search for "API Management Services"**		
 ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal1.png?raw=true)

 **-**Add a new Service****
 ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal2a.png?raw=true)
 
**- Below shows the newly created API Management Service Dashboard"**	
	
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal3.png?raw=true)

**- Select API's, +Add API and click OpenAPI"**
*Note, you can create several different API types, here I will leverage Open API/Swagger to auto generate this API and meta data, automagically :)*		

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal4.png?raw=true)

**- Paste in your Open API specification from the App Service URL above (or local file) [https://openapipetstore.azurewebsites.net/openapi.json](https://openapipetstore.azurewebsites.net/openapi.json)**
The other fields will prepopulate. I've added a v3 version to cross reference the openapipetstore version I built. Click "Create"

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal5.png?raw=true)	
	
**- Your API will automagically get generated!"**
As seen below, all of your API operations are available. Notice Backend HTTP(s) endpoints is blank?

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal6.png?raw=true)
		
**- Click on the edit button next to HTTP(s) Backend and update that to [https://openapipetstore.azurewebsites.net/v3](https://openapipetstore.azurewebsites.net/v3)** 

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal7.png?raw=true)	

**- Select and Operation and Test it out, enter some request meta data and click "Send"**
*Note, I've chosen the GET Operation for Find Pets by status "available"*		

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal8.png?raw=true)

**- The Test feature display HTTP Request/Response data here"**		

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal9.png?raw=true)

## 3. Create and Configure a Product

Products are special containers that combine one or more API's, group visibility settings and rate limits or quotas. We will create a Pet Store Reseller Product, with Rate Limits that we impose for the consumers of this product. For example, just imagine we had Reseller Pet Stores that consume this API for Pet Store Information. We may want to limit those resellers and/or monetize it based on those limits etc.. We can accomplish this with Products.

**- Select Products and Add a Product, fill in the meta data and click "Select API"**
*This product will require a subscription (we cover that below) and will be tied to the OpenAPI Petstore API.*	
	
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal10.png?raw=true)

**- Select the newly created Product, Access Control and Add Group"**
*Let's add developers and guests, that way new consumers (pet shop developers in the word) can discover the API's/Products and subscribe*		

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal10b.png?raw=true)

**- Under Policies edit the XML to impose a rate limit of 2 calls per minute""**
*Obviously this is drastic, but the idea it to keep it simple to test, after two calls to the API we will get a 429 error, showing us how rate limits work. Policies are a compelling feature of API Management. Here we can do all sorts of manipulation to the request/response more on this here [https://docs.microsoft.com/en-us/azure/api-management/api-management-policies](https://docs.microsoft.com/en-us/azure/api-management/api-management-policies)*		
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal18.png?raw=true)

**- Under Portal Overview, you can view the Publisher Portal (here you can also customize the look and feel of the externally facing Developer Portal that developers will see when discovering your API's/Products) once complete click "Publish""**
*The Publish will make all changes in Products available as well, which we will want to do to ensure public users can view this new Reseller Product and subscribe to use it.*		

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal11.png?raw=true)

**- Below shows Developer Portal View (after being customized)"**		

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal12.png?raw=true)


## 4. Discover Products and Subscribe to an API 

**- Head to the Developer Portal in a new browser [https://petstoreapis.developer.azure-api.net/](https://petstoreapis.developer.azure-api.net/)"**		
*This is where public users/developers will go to view/subscribe to API's, note in the Publisher Portal this can be skinned to look and feel however you desire.*

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal13.png?raw=true)

**- You can search for API's and view information on them"**
*Note we haven't yet signed up or subscribed to anything yet, we are in "Discovery Mode"*		
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal14.png?raw=true)

**- At this point, go ahead and Sign up, you will get an email confirmation once complete"**	

**- Select the Pet Store Reseller, and subscribe, in this example "Rhode Petstore" will be the developer/company that will be subscribing to this Product/API"**	
*Note, Products aren't required, we are just showing how they can be used to impose rate limits and quotas. There could be many products 1 API, all with different policies etc...*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal15.png?raw=true)

**- Once subscribed you will see your Developer Profile and Subscription Keys for calling the API's"**	
*Note, at this point, we will be using subscription keys are our only form of authentication to the service. Depending on where these calls to the API are being made, subscription key could be compromised. OAuth is recommended for more protection, we will cover that later.*
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal16.png?raw=true)

**- Test out the API as a consuming/subscribed developer!"**
*[https://petstoreapis.azure-api.net/v3/pet/findByStatus?status=available&subscription-key=...](https://petstoreapis.azure-api.net/v3/pet/findByStatus?status=available&subscription-key=...)*	
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal17.png?raw=true)

**- Below shows the 3rd request to the API within 1 minute"**	
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/apim/portal19.png?raw=true)
