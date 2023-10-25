package com.chtrembl.petstoreassistant.model;

import java.io.Serializable;
import java.util.HashMap;

// HACK UNTIL I HAVE TIME TO GET COSMOS DB INTEGRATION WORKING
public class ProductsCache implements Serializable {
    HashMap<String, Product> products = null;

    public ProductsCache() {
        products = new HashMap<String, Product>();
        
        products.put("1", new Product("1", "Dog Toy", "Ball",
                "Great for power chewers, plush and squeaky balls for dogs with a high prey drive, treat-dispensing balls for food-motivated pups, and even balls that promote good dental health. A varied ball collection is great for keeping your dog interested and engaged.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/ball.jpg?raw=true"));
        products.put("2", new Product("2", "Dog Toy", "Ball Launcher",
                "Help keep your pup stimulated while saving your shoulder, and some can even enable your dog to play fetch solo. They come in a range of styles, from indoor-friendly rollers to powerful outdoor launchers.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/ball-launcher.jpg?raw=true"));
        products.put("3",new Product("3", "Dog Toy", "Plush Lamb",
                "Soft plush toys are wonderful for young puppies because they provide them with a sense of comfort, especially after separation from their mother and littermates. Many older puppies and adult dogs continue to enjoy playing with plush toys and often carry them around, cuddle up to them and sleep with them",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/plush-lamb.jpg?raw=true\""));
        products.put("4",new Product("4", "Dog Toy", "Plush Moose",
                "Soft plush toys are wonderful for young puppies because they provide them with a sense of comfort, especially after separation from their mother and littermates. Many older puppies and adult dogs continue to enjoy playing with plush toys and often carry them around, cuddle up to them and sleep with them.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/plush-moose.jpg?raw=true"));
        products.put("5",new Product("5", "Dog Food", "Large Breed Dry Food",
                "Generally formulated to promote strong bones and good joint health to support their heavier frames.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-food/large-dog.jpg?raw=true"));
        products.put("6",new Product("6", "Dog Food", "Small Breed Dry Food",
                "Small breed kibble is generally better for small dogs because it's easier for them to eat and digest. Small kibble also allows small breed dogs to get all the nutrients they need without eating as much food.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-food/small-dog.jpg?raw=true"));
        products.put("7",new Product("7", "Cat Toy", "Mouse",
                "Cute Catnip Mice are made of colorful corduroy or cotton cat print bodies with ultra suede ears and tails. All of our Catnip Mice are stuffed with fresh ultra premium catnip. These treats could easily become kitty's favorite mice! * As with any product. please supervise your pet's use of this toy.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/cat-toys/mouse.jpg?raw=true"));
        products.put("8",new Product("8", "Cat Toy", "Scratcher",
                "Scratching helps remove the outer layer from a cat's nails: it's good grooming behavior. Cats scratch to stretch their claws, feet, and bodies. This releases feel-good hormones that help keep your cat healthy.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/cat-toys/scratcher.jpg?raw=true"));
        products.put("9",new Product("9", "Cat Food", "All Sizes Cat Dry Food",
                "The main benefit of dry food is its ease, convenience and cost. Millions of cats over the world are fed dry food (either exclusively or in combination) and can live long healthy lives. Dry food allows for free- feeding and the food can be left out for prolonged periods of time",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/cat-food/cat.jpg?raw=true"));
        products.put("10",new Product("10", "Fish Toy", "Mangrove Ornament",
                "With a well decorated tank containing ornaments, fish are more likely to display their natural behavior, show improved coloration, be more active, and spend more time out of hiding!",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/fish-toys/mangrove.jpg?raw=true"));
        products.put("11",new Product("11", "Fish Food", "All Sizes Fish Food",
                "A variety of feeds for all your aquarium's needs, including sun-dried sea vegetables, freeze-dried krill, mysis shrimp, bloodworms and other natural prey items.",
                "https://raw.githubusercontent.com/chtrembl/staticcontent/master/fish-food/fish.jpg?raw=true"));

    }

    //getters and setters
    public HashMap<String, Product> getProducts() {
        return products;
    }
}