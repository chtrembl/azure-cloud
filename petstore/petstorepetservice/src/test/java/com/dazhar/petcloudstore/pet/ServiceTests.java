package com.dazhar.petcloudstore.pet;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.dazhar.petcloudstore.pet.api.PetApiController;

import io.swagger.Swagger2SpringBoot;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PetApiController.class)
@ContextConfiguration(classes = Swagger2SpringBoot.class)
@AutoConfigureMockMvc
public class ServiceTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldReturnVersion() throws Exception {
		this.mockMvc.perform(get("/petstorepetservice/v2/pet/info")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("version")));
	}
}