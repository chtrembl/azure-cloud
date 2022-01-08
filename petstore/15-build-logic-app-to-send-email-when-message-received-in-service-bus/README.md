# 15 - Build a Logic App to send an email when a message is received in a Service Bus topic

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

In this section we'll look at how to develop a Logic App that emails consumers when a message is received in a Service Bus topic. We will be using Service Bus topics to pub/sub messages from the PetStoreOrderService. Once an order is complete, the PetStoreOrderService will publish the order to an "order" topic. The Logic App subscribed to this topic will compose an email with the details and notify the consumer.

Head over to https://powerapps.microsoft.com/ and sign in and select the Home icon from the left navigation.

You should see the following:

![](images/pa1.png)



Things you can now do now with this guide

☑️ Build a Canvas Power App with Custom Connectors
