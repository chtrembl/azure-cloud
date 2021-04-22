package org.openapitools.api;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.openapitools.model.ContainerEnvironment;
import org.openapitools.model.DataPreload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-12T22:51:57.491293-04:00[America/New_York]")

@Controller
@RequestMapping("${openapi.swaggerPetstore.base-path:/v2}")
public class PetApiController implements PetApi {
	public static Logger logger = LoggerFactory.getLogger(PetApiController.class);

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
	public PetApiController(NativeWebRequest request) {
		this.request = request;
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		try {
			this.containerEnvironment.setContainerHostName(
					InetAddress.getLocalHost().getHostAddress() + "/" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MDC.put("containerHostName", this.containerEnvironment.getContainerHostName());
		MDC.put("session_Id", request.getHeader("session-id"));
		return Optional.ofNullable(request);
	}

	@RequestMapping(value = "info", produces = { "application/json" }, method = RequestMethod.GET)
	public ResponseEntity<String> info() {
		// cred scan demo
		String password = "foobar";
		logger.info("incoming GET request to /v2/info");
		ApiUtil.setExampleResponse(request, "application/json",
				"{ \\\"message\\\" : \\\"welcome to the petstore api version 1:), thanks for stopping by...\\\", \\\"status\\\" : \\\"up\\\", \\\"author\\\" : \\\"Chris Tremblay\\\" }");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
