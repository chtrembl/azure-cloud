## Setting up Bing Search and querying the API ##

## Step1 Provision Bing Search ##

Head to Azure Portal and Search for "Bing" Select Bing Resources and create a new "Bing Search" Resource.

> 📝 Please Note, I chose Bing Search vs Bing Custom Search. While Bing Web Search API allows you to search over the entire web, Bing Custom Search allows you to select the slices of the web that you want to search over and control the ranking when searching over your targeted web space. For this tutorial Bing Search us sufficient.

Give your new Bing Search Resource a name, subscription, pricing tier, resource group and acknowledge/consent and click "Create"

You should see the following:

![](images/1.png)

![](images/2.png)

Once your Bing Search Resource is provisioned you can start using it. You will want to take note of your Access Keys. Select "Keys and Endpoints" and copy one of your keys off somewhere safe.

You should see the following:

![](images/3.png)

Back on the Overview tab you can Try Out the API and view Sample code etc...

You should see the following:

![](images/4.png)

## Step2 Use Bing Search ##

You can use POSTMan for Free, to initiate GET Request(s) to Bing Search API

Download POSTMan [here](https://www.postman.com/)

Within POSTMan you can create a new GET Request with a Search URL containing Search Query Parameter and Header Value containing your subscription key from above (The step where you copied the key off somewhere safe)

You should see the following:

![](images/5.png)

As seen in the above screenshot I created a new GET Request, gave it a URL ```https://api.bing.microsoft.com/v7.0/search?q=microsoft``` and specified 1 header with a key: ```Ocp-Apim-Subscription-Key``` and value: ```<The step where you copied the key off somewhere safe>```

