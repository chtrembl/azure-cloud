package org.openapitools.api;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-14T23:19:48.387678-04:00[America/New_York]")

@RestController
@RequestMapping("${openapi.swaggerPetstore.base-path:/v1}")
public class PetsApiController implements PetsApi {
	private static Logger logger = LoggerFactory.getLogger(PetsApiController.class);

	private final NativeWebRequest request;

	@org.springframework.beans.factory.annotation.Autowired
	public PetsApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@RequestMapping(value = "info", produces = { "application/json" }, method = RequestMethod.GET)
	public String info() {
		logger.info("info method invoked...");
		logger.debug("debug method invoked...");
		return "{ \"message\" : \"welcome to the petstore api version 5:), thanks for stopping by...\", \"status\" : \"up\", \"author\" : \"Chris Tremblay\" }";
	}

}
