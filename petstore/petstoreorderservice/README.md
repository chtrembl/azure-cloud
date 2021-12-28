Pet Store Pet Service is the Back End Java Spring Boot Micro Service used in the Azure Pet Store Reference Guide. It's purpose is to manage Pet Store Orders.

The project/API Scaffolding was generated using Swagger Codegen https://github.com/swagger-api/swagger-codegen and the following Swagger schema https://raw.githubusercontent.com/chtrembl/azure-cloud/main/petstore/petstoreorderservice/petstoreorderservice.json For the purposes of this demo, the Pet data was implemented as an in memory DB populated from the application.yml.

```
  java -jar swagger-codegen-cli-2.4.9.jar generate \
  -i petstoreorderservice.json \
  --api-package com.chtrembl.petstore.order.api \
  --model-package com.chtrembl.petstore.order.model \
  --group-id com.chtrembl.petstore.order \
  --artifact-id petstoreorderservice \
  --artifact-version 0.0.1-SNAPSHOT \
  -l spring \
  -o petstoreorderservice
```
You are also welcome and encouraged to build/deploy it outside of the Azure Pet Store Reference Guide, perhaps on your local developement machine just using Docker in Linux containers mode.

Build the Spring Boot Jar and Docker Image

```docker build -t petstoreorderservice .```

Run the Docker Image. Note this example instructs Spring Boot to run on any port of your choice, in this case, 8081 and hence forwards the port to the container accordingly. You are welcome to change this as needed. 8083 or whochever port chosen is ultimatley what the petstoreapp will point to, if/when you choose to run petstoreapp locally as well.

```docker run --rm --name petstoreorderservice -p 8083:8083 -e PETSTOREORDERSERVICE_SERVER_PORT=8083 -d petstorepetservice:latest```

Other optional aruments that you can specify to do certain things that get covered in the Azure Pet Store Reference Guide(s) like:

Enable Application Inisghts (empty and disabled by default, you'll want to uncomment the property in azure-cloud\petstore\petstoreorderservice\src\main\resources\application.yml

```-e PETSTORESERVICES_AI_INSTRUMENTATION_KEY=<your key here>```

Test endpoint:

GET Request for service info (No headers needed)

```http://localhost:8083/petstoreprderservice/v2/order/info```
