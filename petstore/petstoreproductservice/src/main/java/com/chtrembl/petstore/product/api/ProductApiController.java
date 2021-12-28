package com.chtrembl.petstore.product.api;

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

import com.chtrembl.petstore.product.model.ContainerEnvironment;
import com.chtrembl.petstore.product.model.DataPreload;
import com.chtrembl.petstore.product.model.ModelApiResponse;
import com.chtrembl.petstore.product.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-12-21T10:17:09.908-05:00")

@Controller
@RequestMapping("${openapi.swaggerPetstore.base-path:/petstoreproductservice/v2}")
public class ProductApiController implements ProductApi {

	static final Logger log = LoggerFactory.getLogger(ProductApiController.class);

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
	public ProductApiController(ObjectMapper objectMapper, NativeWebRequest request) {
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

	@RequestMapping(value = "product/info", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<String> info() {
		conigureThreadForLogging();

		// password used for cred scan demo
		String password = "foobar";
		log.info("PetStoreProductService incoming GET request to petstoreproductservice/v2/info");
		ApiUtil.setResponse(request, "application/json",
				"{ \"service\" : \"product service\", \"version\" : \"" + containerEnvironment.getAppVersion()
						+ "\", \"container\" : \"" + containerEnvironment.getContainerHostName() + "\" }");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Product>> findProductsByStatus(
			@NotNull @ApiParam(value = "Status values that need to be considered for filter", required = true, allowableValues = "available, pending, sold") @Valid @RequestParam(value = "status", required = true) List<String> status) {
		conigureThreadForLogging();

		String acceptType = request.getHeader("Content-Type");
		String contentType = request.getHeader("Content-Type");
		if (acceptType != null && contentType != null && acceptType.contains("application/json")
				&& contentType.contains("application/json")) {
			ProductApiController.log.info(String.format(
					"PetStoreProductService incoming GET request to petstoreproductservice/v2/pet/findProductsByStatus?status=%s",
					status));
			try {
				String petsJSON = new ObjectMapper().writeValueAsString(this.getPreloadedProducts());
				ApiUtil.setResponse(request, "application/json", petsJSON);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (JsonProcessingException e) {
				ProductApiController.log.error("PetStoreProductService with findProductsByStatus() " + e.getMessage());
				ApiUtil.setResponse(request, "application/json", e.getMessage());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<List<Product>>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> addProduct(
			@ApiParam(value = "Product object that needs to be added to the store", required = true) @Valid @RequestBody Product body) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> deleteProduct(
			@ApiParam(value = "Product id to delete", required = true) @PathVariable("productId") Long productId,
			@ApiParam(value = "") @RequestHeader(value = "api_key", required = false) String apiKey) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<List<Product>> findProductsByTags(
			@NotNull @ApiParam(value = "Tags to filter by", required = true) @Valid @RequestParam(value = "tags", required = true) List<String> tags) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<List<Product>>(objectMapper.readValue(
						"[ {  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"toy\",  \"id\" : 0,  \"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : \"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : \"available\"}, {  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"toy\",  \"id\" : 0,  \"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : \"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : \"available\"} ]",
						List.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<List<Product>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		if (accept != null && accept.contains("application/xml")) {
			try {
				return new ResponseEntity<List<Product>>(objectMapper.readValue(
						"<Product>  <id>123456789</id>  <name>toy</name>  <photoUrls>    <photoUrls>aeiou</photoUrls>  </photoUrls>  <tags>  </tags>  <status>aeiou</status></Product>",
						List.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/xml", e);
				return new ResponseEntity<List<Product>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<List<Product>>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Product> getProductById(
			@ApiParam(value = "ID of product to return", required = true) @PathVariable("productId") Long productId) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<Product>(objectMapper.readValue(
						"{  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"toy\",  \"id\" : 0,  \"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : \"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : \"available\"}",
						Product.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<Product>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		if (accept != null && accept.contains("application/xml")) {
			try {
				return new ResponseEntity<Product>(objectMapper.readValue(
						"<Product>  <id>123456789</id>  <name>toy</name>  <photoUrls>    <photoUrls>aeiou</photoUrls>  </photoUrls>  <tags>  </tags>  <status>aeiou</status></Product>",
						Product.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				log.error("Couldn't serialize response for content type application/xml", e);
				return new ResponseEntity<Product>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<Product>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> updateProduct(
			@ApiParam(value = "Product object that needs to be added to the store", required = true) @Valid @RequestBody Product body) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> updateProductWithForm(
			@ApiParam(value = "ID of product that needs to be updated", required = true) @PathVariable("productId") Long productId,
			@ApiParam(value = "Updated name of the product") @RequestParam(value = "name", required = false) String name,
			@ApiParam(value = "Updated status of the product") @RequestParam(value = "status", required = false) String status) {
		String accept = request.getHeader("Accept");
		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<ModelApiResponse> uploadFile(
			@ApiParam(value = "ID of product to update", required = true) @PathVariable("productId") Long productId,
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
				log.error("Couldn't serialize response for content type application/json", e);
				return new ResponseEntity<ModelApiResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<ModelApiResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

}
