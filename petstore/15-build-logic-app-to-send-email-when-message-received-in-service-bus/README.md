# 15 - Build a Logic App to send an email when a message is received in a Service Bus topic

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

In this section we'll look at how to develop a Logic App that emails consumers when a message is received in a Service Bus topic. We will be using Service Bus topics to pub/sub messages from the PetStoreOrderService. Once an order is complete, the PetStoreOrderService will publish the order to an "order" topic. The Logic App subscribed to this topic will compose an email with the details and notify the consumer.

> 📝 **Please Note, As with the other guides, the code for this guide is already complete, it just needs to be enabled via application configuration. The objective of this guide is to walk you through the steps needed to enable & configure the Azure services and Pet Store Application code to make this all of this work.

First let's set things up in Azure

Head over to https://ms.portal.azure.com/ and sign in.

Lets setup and configure the Service Bus

Search for Service Bus and select "Create" or "Create service bus namespace"

You should see the following:

![](images/1.png)



Things you can now do now with this guide

☑️ Build a Canvas Power App with Custom Connectors
