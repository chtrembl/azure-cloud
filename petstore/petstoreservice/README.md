PetStoreService is the Back End Java Spring Boot Web Service used in the Azure Pet Store Reference Guide.

You are also welcome to build/deploy it outside if the Azure Pet Store Reference Guide.

Build Spring Boot Jar and Docker Image

```docker build -t petstorservice .```

Run Docker Image

```docker run -p 8080:8080 -e PETSTORESERVICE_SERVER_PORT=8080 petstoreservice:latest```

Other optional aruments that you can specify to do certain things that get covered int the Azure Pet Store Reference Guide(s) like:

- enable Application Inisghts (empty and disabled by default)

```-e PETSTORESERVICE_AI_INSTRUMENTATION_KEY=<your key here>```
