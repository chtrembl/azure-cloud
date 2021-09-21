package com.chtrembl.petstore;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	// app id and api key to query application insights with
	private String APP_ID = System.getenv("appId") != null ? System.getenv("appId") : System.getProperty("appId");
	private String API_KEY = System.getenv("apiKey") != null ? System.getenv("apiKey") : System.getProperty("apiKey");

	protected String getApplicationInsightsTelemetry(String minsAgo) {
		if (APP_ID == null || API_KEY == null) {
			APP_ID = "";
			API_KEY = "";
		}

		String sessionsJson = "";

		// application insights POST to query data
		HttpRequest request = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString("{\"query\":\"traces | where timestamp > ago(" + minsAgo
						+ ") | summarize Traces = count() by tostring(customDimensions.session_Id), client_Browser, client_StateOrProvince | where client_Browser != 'Other'\"}"))
				.uri(URI.create("https://api.applicationinsights.io/v1/apps/" + this.APP_ID + "/query"))
				.setHeader("x-api-key", this.API_KEY).setHeader("Content-Type", "application/json").build();

		HttpResponse<String> response = null;
		String responseBody = "";
		try {
			response = Function.HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			responseBody = response.body();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "{\"error\":\"Exception getting response body\"}";
		}

		try {
			// transform application insights query response for Power Apps consumptions
			// etc...
			JsonNode root = Function.OBJECT_MAPPER.readTree(responseBody);
			JsonNode sessions = root.path("tables").findPath("rows");

			Response transformedResponse = new Response();

			// transform the sessionBrowser so the name can be easily loaded as an image
			// within PowerApps
			sessions.forEach(jsonNode -> {
				String sessionId = ((ArrayNode) jsonNode).get(0).toString().replace("\"", "").trim();
				String sessionBrowser = ((ArrayNode) jsonNode).get(1).toString().replace("\"", "").trim().toLowerCase();
				if (sessionBrowser.contains("edg")) {
					sessionBrowser = "edge";
				} else if (sessionBrowser.contains("chrome")) {
					sessionBrowser = "chrome";
				} else if (sessionBrowser.contains("safari")) {
					sessionBrowser = "safari";
				} else if (sessionBrowser.contains("firefox")) {
					sessionBrowser = "firefox";
				} else if (sessionBrowser.contains("opera")) {
					sessionBrowser = "opera";
				} else {
					sessionBrowser = "unknown";
				}

				String sessionState = ((ArrayNode) jsonNode).get(2).toString().replace("\"", "").trim();
				Integer sessionPageHits = Integer.valueOf(((ArrayNode) jsonNode).get(3).toString());

				Session session = new Session(sessionId, sessionBrowser, sessionState, sessionPageHits);
				transformedResponse.getSessions().add(session);
			});

			transformedResponse.setSessionCount(sessions.size());

			sessionsJson = Function.OBJECT_MAPPER.writeValueAsString(transformedResponse);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{\"error\":\"Exception mapping response body\"}";
		}

		System.out.println(sessionsJson);

		return sessionsJson;
	}

	// HTTP Trigger on POST requests with valid apiKey which is this functions layer
	// of protection
	@FunctionName("petStoreCurrentSessionTelemetry")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger processed a request.");

		if (this.API_KEY != null && request.getQueryParameters() != null
				&& !this.API_KEY.equals(request.getQueryParameters().get("apiKey"))) {
			return request.createResponseBuilder(HttpStatus.OK).body("{\"error\":\"access denied\"}")
					.header("Content-Type", "application/json").build();
		}

		final String minsAgo = request.getQueryParameters().get("minsAgo");

		return request.createResponseBuilder(HttpStatus.OK).body(this.getApplicationInsightsTelemetry(minsAgo))
				.header("Content-Type", "application/json").build();
	}
}
