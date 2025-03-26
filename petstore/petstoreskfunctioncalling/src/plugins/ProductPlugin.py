from typing import Annotated
import random
from semantic_kernel.functions import kernel_function

class ProductPlugin:   
    @kernel_function(
        name="Price",
        description="Get price for a specific product by product name. For example, 'Get price for the dog bone'."
    )
    def price(
        self,
        product_name: Annotated[str, "The product name"]
    ) -> float:
        print(f"Fetching price for {product_name} via the Azure Function...")
        ## ideally this price would be referenced from (Cosmos etc... for this demo just randomizing)
        return random.uniform(10.0, 100.0)