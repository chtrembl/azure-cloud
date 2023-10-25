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
		// add tests back
	}	
}
