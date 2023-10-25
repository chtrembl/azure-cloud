// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.chtrembl.petstoreassistant;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.model.ProductsCache;
import com.chtrembl.petstoreassistant.utility.PetStoreAssistantUtilities;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
	@Test
	public void contextLoads() {
	}

	@Test
	public void testFindProducts()
	{
		ProductsCache productsCache = new ProductsCache();
		DPResponse dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion("here are the dog toys available:  1. product: ball, product id: 1[doc2] 2. product: plush lamb, product id: 3[doc3] 3. product: ball launcher, product id: 2[doc4] 4. product: plush moose, product id: 4[doc5]  please note that each product is separated by a comma as requested",productsCache);
		assertEquals("We have, Ball, Plush Lamb, Ball Launcher and Plush Moose", dpResponse.getDpResponseText());

		dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion("here are the dog toys available:  1. product id: 1, product: ball[doc2] 2. product id: 3, product: plush lamb[doc3] 3. product id: 2, product: ball launcher[doc4] 4. product id: 4, product: plush moose[doc5]",productsCache);
		assertEquals("We have, Ball, Plush Lamb, Ball Launcher and Plush Moose", dpResponse.getDpResponseText());

		dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion("here are the cat products:  1. product: cute catnip mice, product id: 7[doc1] 2. product: scratcher, product id: 8 3. product: all sizes cat dry food, product id: 9[doc4]",productsCache);
		assertEquals("We have, Mouse, Scratcher and All Sizes Cat Dry Food", dpResponse.getDpResponseText());

		dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion("here are the dog products:  1. product: ball, product id: 1 2. product: plush lamb, product id: 3[doc3] 3. product: small breed dry food, product id: 6[doc4] 4. product: ball launcher, product id: 2[doc5]",productsCache);
		assertEquals("We have, Ball, Plush Lamb, Small Breed Dry Food and Ball Launcher", dpResponse.getDpResponseText());
		
		dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion("there are the product ids for the dog toys:  - ball: productid 1[doc2] - ball launcher: productid 2[doc3] - plush lamb: productid 3[doc4] - plush moose: productid 4[doc5]",productsCache);
		assertEquals("We have, Ball, Ball Launcher, Plush Lamb and Plush Moose", dpResponse.getDpResponseText());

		dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion("yes, there is a product related to fish food. the product id for the fish food is 11[doc1]",productsCache);
		assertEquals("We have, All Sizes Fish Food", dpResponse.getDpResponseText());
	}	
}
