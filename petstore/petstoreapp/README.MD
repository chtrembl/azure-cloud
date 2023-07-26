PetStoreApp is the front facing Java Spring Boot Web Application used in the Azure Pet Store Reference Guide.

You are also welcome and encouraged to build/deploy it outside of the Azure Pet Store Reference Guide, perhaps on your local developement machine just using Docker in Linux containers mode.

Build the Spring Boot Jar and Docker Image

```docker build -t petstoreapp .```

Run the Docker Image. Note this example instructs Spring Boot to run on any port of your choice, in this case, 8080 and hence forwards the port to the container accordingly. You are welcome to change this as needed.

```docker run --rm --name petstoreapp -p 8080:8080 -e PETSTOREAPP_SERVER_PORT=8080 petstoreapp:latest```

Other optional aruments that you can specify to do certain things that get covered in the Azure Pet Store Reference Guide(s) like:

- enable Application Inisghts (empty and disabled by default, you'll want to uncomment the property in azure-cloud\petstore\petstoreapp\src\main\resources\application.yml

```-e PETSTORESERVICES_AI_INSTRUMENTATION_KEY=<your key here>```

- enable APIM Management and pass along a subscription key in the REST calls (empty and disabled by default, you'll want to uncomment the property in azure-cloud\petstore\petstoreapp\src\main\resources\application.yml

```-e PETSTORESERVICE_SUBSCRIPTION_KEY=<your key here>```

- enable AAD B2C (empty and disabled by default, you'll want to uncomment the property in azure-cloud\petstore\petstoreapp\src\main\resources\application.yml

```-e PETSTOREAPP_B2C_TENANT=<your tenant here> -e PETSTOREAPP_B2C_CLIENT_ID=<your client id here> -e PETSTOREAPP_B2C_CLIENT_SECRET=<your client secret here> -e PETSTOREAPP_B2C_REPLY_URL=<your b2c reply uel here> -e PETSTOREAPP_B2C_LOGOUT_URL=<your b2c logout uel here>```
