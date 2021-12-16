PetStoreService is the Back End Java Spring Boot Web Service used in the Azure Pet Store Reference Guide.

You are also welcome and encouraged to build/deploy it outside of the Azure Pet Store Reference Guide, perhaps on your local developement machine just using Docker in Linux containers mode.

Build the Spring Boot Jar and Docker Image

```docker build -t petstoreservice .```

Run the Docker Image. Note this example instructs Spring Boot to run on any port of your choice, in this case, 8081 and hence forwards the port to the container accordingly. You are welcome to change this as needed. 8081 or whochever port chosen is ultimatley what the petstoreapp will point to, if/when you choose to run petstoreapp locally as well. 

```docker run --rm --name petstoreservice -p 8081:8081 -e PETSTORESERVICE_SERVER_PORT=8081 -d petstoreservice:latest```

Other optional aruments that you can specify to do certain things that get covered in the Azure Pet Store Reference Guide(s) like:

- enable Application Inisghts (empty and disabled by default, you'll want to uncomment the property in azure-cloud\petstore\petstoreservice\src\main\resources\application.yml

```-e PETSTORESERVICE_AI_INSTRUMENTATION_KEY=<your key here>```
