PetStoreApp is the front facing Java Spring Boot Web Application used in the Azure Pet Store Reference Guide.

You are also welcome to build/deploy it outside if the Azure Pet Store Reference Guide.

```mvn clean package```

```docker build -t petstoreapp .```

```docker run -p 8080:8080 -e PETSTOREAPP_SERVER_PORT=8080 -e PETSTORESERVICE_URL=http://localhost:8080 petstoreapp:latest```

Other optional aruments that you can specify to do certain things that get covered int the Azure Pet Store Reference Guide(s) like:

- enable Application Inisghts

```-e PETSTOREAPP_AI_INSTRUMENTATION_KEY=<your key here>```


- enable APIM Management and pass along a subscription key in the REST calls

```-e PETSTORESERVICE_SUBSCRIPTION_KEY=<your key here>```

- enable AAD B2C

```-e PETSTOREAPP_B2C_TENANT=<your tenant here> -e PETSTOREAPP_B2C_CLIENT_ID=<your client id here> -e PETSTOREAPP_B2C_CLIENT_SECRET=<your client secret here> -e PETSTOREAPP_B2C_REPLY_URL=<your b2c reply uel here> -e PETSTOREAPP_B2C_LOGOUT_URL=<your b2c logout uel here>```

...
