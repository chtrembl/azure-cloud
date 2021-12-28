Pet Store Pet Service is the Back End Java Spring Boot Micro Service used in the Azure Pet Store Reference Guide. It's purpose is to deliver Pet Product data to consumers.

The project/API Scaffolding was generated using Swagger Codegen https://github.com/swagger-api/swagger-codegen and the following Swagger schema https://raw.githubusercontent.com/chtrembl/azure-cloud/main/petstore/petstoreproductservice/petstoreproductservice.json For the purposes of this demo, the Pet data was implemented as an in memory DB populated from the application.yml.

```
  java -jar swagger-codegen-cli-2.4.9.jar generate \
  -i petstoreproductservice.json \
  --api-package com.chtrembl.petstore.product.api \
  --model-package com.chtrembl.petstore.product.model \
  --group-id com.chtrembl.petstore.product \
  --artifact-id petstoreproductservice \
  --artifact-version 0.0.1-SNAPSHOT \
  -l spring \
  -o petstoreproductservice
```
You are also welcome and encouraged to build/deploy it outside of the Azure Pet Store Reference Guide, perhaps on your local developement machine just using Docker in Linux containers mode.

Build the Spring Boot Jar and Docker Image

```docker build -t petstoreproductservice .```

Run the Docker Image. Note this example instructs Spring Boot to run on any port of your choice, in this case, 8082 and hence forwards the port to the container accordingly. You are welcome to change this as needed. 8081 or whochever port chosen is ultimatley what the petstoreapp will point to, if/when you choose to run petstoreapp locally as well.

```docker run --rm --name petstoreproductservice -p 8082:8082 -e PETSTOREPETSERVICE_SERVER_PORT=8082 -d petstorepetservice:latest```

Other optional aruments that you can specify to do certain things that get covered in the Azure Pet Store Reference Guide(s) like:

Enable Application Inisghts (empty and disabled by default, you'll want to uncomment the property in azure-cloud\petstore\petstoreproductservice\src\main\resources\application.yml

```-e PETSTORESERVICES_AI_INSTRUMENTATION_KEY=<your key here>```

Test endpoints:

GET Request for service info (No headers needed)

```http://localhost:8082/petstoreprpductservice/v2/product/info```

GET request for Pets (Accept: application/json and Content-Type: application/json headers needed)

```http://localhost:8082/petstoreproductservice/v2/product/findByStatus?status=available```
