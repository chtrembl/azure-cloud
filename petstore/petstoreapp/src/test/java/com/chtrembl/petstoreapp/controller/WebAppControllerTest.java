package com.chtrembl.petstoreapp.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.chtrembl.petstoreapp.model.Pet;
import com.chtrembl.petstoreapp.model.Category;
import com.chtrembl.petstoreapp.service.PetStoreService;

import junit.framework.TestCase;

@WebMvcTest(WebAppController.class)
public class WebAppControllerTest extends TestCase{

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PetStoreService service;

	@Test
	public void testControllerGetRequest() throws Exception {
        List<Pet> pets = new ArrayList<Pet>();
        Category category = new Category();

        pets.add(new Pet("Bulldog",category));
        pets.add(new Pet("Labrador",category));
        pets.add(new Pet("Poodle",category));
      
        when(service.getPets("Dog")).thenReturn(pets);

		this.mockMvc.perform(get("/dogbreeds").param("category", "Dog")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Bulldog")))
                .andExpect(content().string(containsString("Labrador")))
                .andExpect(content().string(containsString("Poodle")));    
    }    
}