import config
import requests
from typing import Annotated
from semantic_kernel.functions import kernel_function

class CartPlugin:   
    @kernel_function(
        name="UpdateCart",
        description="Update a shopping cart with a product. For example, 'Please add the ball to my shopping cart'."
    )
    def updateCart(
        self,
        product_name: Annotated[str, "The product name"]
    ) -> str:
        print(f"Adding product {product_name} to your shopping cart via the Azure Function...")
        ## ideally the product_name would be cross referenced to the producr_id (Cosmos etc... for this demo just hardcoding)
        productId = 4
        if "ball" in product_name:
            productId = 1 
        elif "launcher" in product_name:
            productId = 2
        elif "lamb" in product_name:
            productId = 3
        
        # Azure Pet Store Endpoint that allows you to externally administer shopping cart
        url = "https://azurepetstore.com/api/updatecart"+"?csrf=" + config.AZURE_PETSTORE_CSRF + "&productId=" + str(productId)
        headers = {"Cookie": "JSESSIONID="+config.AZURE_PETSTORE_SESSIONID, "Content-Type": "text/html"}
        
        # update shopping cart with http call...
        try:
            response = requests.get(url, headers=headers)
            response.raise_for_status()  # Raise HTTPError for bad responses (4xx or 5xx)
            return f"Successfully added {product_name} to cart.  Response: {response.text}"
        except requests.exceptions.RequestException as e:
            return f"Error adding {product_name} to cart: {e}"