package com.chtrembl.petstore.pet.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.chtrembl.petstore.pet.model.ContainerEnvironment;
import com.chtrembl.petstore.pet.model.DataPreload;
import com.chtrembl.petstore.pet.model.ModelApiResponse;
import com.chtrembl.petstore.pet.model.Pet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-12-20T15:31:39.272-05:00")

@Controller
@RequestMapping("${openapi.swaggerPetstore.base-path:/petstorepetservice/v2}")
public class PetApiController implements PetApi {
	static final Logger log = LoggerFactory.getLogger(PetApiController.class);

	private final ObjectMapper objectMapper;

	private final NativeWebRequest request;

	@Autowired
	private ContainerEnvironment containerEnvironment;

	@Autowired
	private DataPreload dataPreload;

	@Override
	public DataPreload getBeanToBeAutowired() {
		return dataPreload;
	}

	@org.springframework.beans.factory.annotation.Autowired
	public PetApiController(ObjectMapper objectMapper, NativeWebRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	// should really be in an interceptor
	public void conigureThreadForLogging() {
		try {
			this.containerEnvironment.setContainerHostName(
					InetAddress.getLocalHost().getHostAddress() + "/" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			log.info("PetStoreOrderService getRequest() error: " + e.getMessage());
		}
		MDC.put("containerHostName", this.containerEnvironment.getContainerHostName());
		MDC.put("session_Id", request.getHeader("session-id"));
	}

	@RequestMapping(value = "pet/info", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<String> info() {
		conigureThreadForLogging();

		log.info("PetStorePetService incoming GET request to petstorepetservice/v2/info");
		ApiUtil.setResponse(request, "application/json",
				"{ \"service\" : \"pet service\", \"version\" : \"" + containerEnvironment.getAppVersion()
						+ "\", \"container\" : \"" + containerEnvironment.getContainerHostName() + "\" }");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Pet>> findPetsByStatus(
			@NotNull @ApiParam(value = "Status values that need to be considered for filter", required = true, allowableValues = "available, pending, sold") @Valid @RequestParam(value = "status", required = true) List<String> status) {
		conigureThreadForLogging();

    	PetApiController.log.info(String.format(
					"PetStorePetService incoming GET request to petstorepetservice/v2/pet/findPetsByStatus?status=%s",
					status));
		try {
			String petsJSON = new ObjectMapper().writeValueAsString(this.getPreloadedPets());
			ApiUtil.setResponse(request, "application/json", petsJSON);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (JsonProcessingException e) {
			PetApiController.log.error("PetStorePetService with findPetsByStatus() " + e.getMessage());
			ApiUtil.setResponse(request, "application/json", e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Void> addPet(
			@ApiParam(value = "Pet object that needs to be added to the store", required = true) @Valid @RequestBody Pet body) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> deletePet(
			@ApiParam(value = "Pet id to delete", required = true) @PathVariable("petId") Long petId,
			@ApiParam(value = "") @RequestHeader(value = "api_key", required = false) String apiKey) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<List<Pet>> findPetsByTags(
			@NotNull @ApiParam(value = "Tags to filter by", required = true) @Valid @RequestParam(value = "tags", required = true) List<String> tags) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<List<Pet>>(objectMapper.readValue(
						"[ {  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"doggie\",  \"id\" : 0,  \"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : \"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : \"available\"}, {  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"doggie\",  \"id\" : 0,  \"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : \"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : \"available\"} ]",
						List.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("PetStorePetService Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<List<Pet>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		if (accept != null && accept.contains("application/xml")) {
			try {
				return new ResponseEntity<List<Pet>>(objectMapper.readValue(
						"<Pet>  <id>123456789</id>  <name>doggie</name>  <photoUrls>    <photoUrls>aeiou</photoUrls>  </photoUrls>  <tags>  </tags>  <status>aeiou</status></Pet>",
						List.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("PetStorePetService Couldn't serialize response for content type application/xml", e);
				return new ResponseEntity<List<Pet>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<List<Pet>>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Pet> getPetById(
			@ApiParam(value = "ID of pet to return", required = true) @PathVariable("petId") Long petId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Pet>(objectMapper.readValue(
						"{  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"doggie\",  \"id\" : 0,  \"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : \"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : \"available\"}",
						Pet.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("PetStorePetService Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<Pet>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		if (accept != null && accept.contains("application/xml")) {
			try {
				return new ResponseEntity<Pet>(objectMapper.readValue(
						"<Pet>  <id>123456789</id>  <name>doggie</name>  <photoUrls>    <photoUrls>aeiou</photoUrls>  </photoUrls>  <tags>  </tags>  <status>aeiou</status></Pet>",
						Pet.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("PetStorePetService Couldn't serialize response for content type application/xml", e);
				return new ResponseEntity<Pet>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<Pet>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> updatePet(
			@ApiParam(value = "Pet object that needs to be added to the store", required = true) @Valid @RequestBody Pet body) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> updatePetWithForm(
			@ApiParam(value = "ID of pet that needs to be updated", required = true) @PathVariable("petId") Long petId,
			@ApiParam(value = "Updated name of the pet") @RequestParam(value = "name", required = false) String name,
			@ApiParam(value = "Updated status of the pet") @RequestParam(value = "status", required = false) String status) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<ModelApiResponse> uploadFile(
			@ApiParam(value = "ID of pet to update", required = true) @PathVariable("petId") Long petId,
			@ApiParam(value = "Additional data to pass to server") @RequestParam(value = "additionalMetadata", required = false) String additionalMetadata,
			@ApiParam(value = "file detail") @Valid @RequestPart("file") MultipartFile file) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<ModelApiResponse>(
						objectMapper.readValue("{  \"code\" : 0,  \"type\" : \"type\",  \"message\" : \"message\"}",
								ModelApiResponse.class),
						HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("PetStorePetService Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<ModelApiResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<ModelApiResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

}
