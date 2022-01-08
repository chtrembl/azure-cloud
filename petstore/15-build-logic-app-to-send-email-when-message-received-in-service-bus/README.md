# 15 - Build a Logic App to send an email when a message is received in a Service Bus topic

**This guide is part of the [Azure Pet Store App Dev Reference Guide](../README.md)**

In this section we'll look at how to develop a Logic App that emails consumers when a message is received in a Service Bus topic. We will be using Service Bus topics to pub/sub messages from the PetStoreOrderService. Once an order is complete, the PetStoreOrderService will publish the order to an "order" topic. The Logic App subscribed to this topic will compose an email with the details and notify the consumer.

> 📝 **Please Note, As with the other guides, the code for this is already complete, it just needs to be enabled via application configuration. The objective of this guide is to walk you through the steps needed to enable & configure the Azure services and Pet Store Application code to make this all of this work.**

First let's set things up in Azure

Head over to https://ms.portal.azure.com/ and sign in.

Lets setup and configure the Service Bus

Search for Service Bus and select "Create" or "Create service bus namespace"

You should see the following:

![](images/1.png)

Give your namespace a name and fill in the other meta data requested. The namespace will be used to locate your Service Bus instance via FQDN. Select "Review and create"

You should see the following:

![](images/2.png)

Select "Create"

You should see the following:

![](images/3.png)

Create a topic for your Service Bus. This is the way in which our PetStorOrderService will communicate with other system(s) (1 to many communication). We are using a topic instead of message queue (1 to 1 communication). Just imagine an order is placed, your PetStoreOrderService may want to use a topic as a way of informing many systems (more than 1) of an order being placed, for example, the logic app needs to send an email, perhaps a fulfillment center system needs to know as well so it can pull the products and so on... Give it a name "orders" and the meta data for which is should need such as message sizing and TTL. For this guide the defaults are fine. Select "Create"

You should see the following:

![](images/4.png)

Create a subscription. This is used for the subscribers, and in our case this is the subscription our Logic App will be using. We will call this "email". You can also specify a max delivery count. In this guide we will choose 1. One message will be sent and that is it. If the Logic App receives it, perfect, it can process it. Otherwise no other attempt will be made. For this guide this is sufficient because we know the logic app will send an email to the consumer every time it has received a message. In a production example you may want to use a higher number, perhaps you have subscribers that are not responding to messages in a timely manner and/or are experiencing technical issues and cannot respond (perhaps the logic app is down etc...), perhaps you'll want to try sending multiple times, at which point when the max attempts has occurred these messages can be moved and inspected/troubleshooted etc... The other defaults are fine.  Select "Create"

You should see the following:

![](images/5.png)

Let's now add a Shared Access Policy (SAS). We need this to establish our connections for the publisher (PetStoreOrderService) and consumer (Logic App).

You should see the following:

![](images/6.png)

Give the SAS a name. I am using "azurepetstore". You can use whichever name you would like. This is where we can control permissions (Send, Receive, Listen). Select "Create"

You should see the following:

![](images/7.png)

Copy the Keys and Connection Strings off somewhere, you will need them when you configure your PetStoreOrderService.

You should see the following:

![](images/8.png)

Head back to the Overview page and make note of your Subscription ID, , you will need them when you configure your PetStoreOrderService.

You should see the following:

![](images/9.png)

Next we will be creating the Logic App. Before we do so, it's important to understand what data we are working with and why we are building the following.

If you inspect the StoreAPIController.java class from the PetStoreOrderService https://github.com/chtrembl/azure-cloud/blob/main/petstore/petstoreorderservice/src/main/java/com/chtrembl/petstore/order/api/StoreApiController.java You will notice the following code:

```java
    if (order.isComplete() && jmsTemplate != null && order.getEmail() != null
            && order.getEmail().trim().toLowerCase().endsWith("microsoft.com")) {
        jmsTemplate.convertAndSend("orders", orderJSON);
    }
```

If the PetStoreOrderService receives a complete order, JMS (Java Messaging Service) has been enabled and the email is one from the Microsoft domain, the PetStoreOrderService will the send the following JSON (schema and sample payload below) as a message to the "order" topic we set up earlier.

```json
{
    "properties": {
        "email": {
            "type": "string"
        },
        "id": {
            "type": "string"
        },
        "products": {
            "items": {
                "properties": {
                    "id": {
                        "type": "integer"
                    },
                    "name": {
                        "type": "string"
                    },
                    "photoURL": {
                        "type": "string"
                    },
                    "quantity": {
                        "type": "integer"
                    }
                },
                "required": [
                    "id",
                    "quantity",
                    "name",
                    "photoURL"
                ],
                "type": "object"
            },
            "type": "array"
        }
    },
    "type": "object"
}
```

```json
{
   "id":"8B43F106224AFA53FE27B9BC567007EB",
   "email":"chris.tremblay@microsoft.com",
   "products":[
        {
            "id": 4,
            "quantity": 3
        }
   ],
   "complete" : true
}
```

We will be building out the following steps, below is our end result so you can get an idea of what we are solving for.

![](images/16.png)

Now lets create the Logic App to receive and compose an email when these messages are sent.

From the Azure Portal, search for Logic Apps and select "Create"

You should see the following:

![](images/10.png)

We will start with a blank Logic App.

You should see the following:

![](images/11.png)

First Step in our new Logic App is to receive the message, Search for "Service Bus" for your first step.

You should see the following:

![](images/12.png)

Select "When a message is received in a topic subscription (auto-complete)" to inform the entry point step of the Logic App, which is when a new message arrives to the specified topic.

You should see the following:

![](images/13.png)

Specify the meta data requested. "orders" is the topic we created earlier and the topic PetStoreOrderService is sending to. Also paste in your connection string, you can use the primary one from the SAS policy.

You should see the following:

![](images/14.png)

Specify the subscription "email" to use and how often you want to check the topic for new messages.

You should see the following:

![](images/15.png)

Things you can now do now with this guide

☑️ Build a Canvas Power App with Custom Connectors