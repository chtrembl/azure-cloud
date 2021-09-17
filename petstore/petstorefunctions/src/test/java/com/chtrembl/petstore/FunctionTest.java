package com.chtrembl.petstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

/**
 * Unit test for Function class.
 */
public class FunctionTest {
	/**
	 * Unit test for HttpTriggerJava method.
	 */
	@Test
	public void testHttpTriggerJava() throws Exception {

		// Setup
		@SuppressWarnings("unchecked")
		final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

		final Map<String, String> queryParams = new HashMap<>();
		queryParams.put("name", "Azure");
		doReturn(queryParams).when(req).getQueryParameters();

		final Optional<String> queryBody = Optional.empty();
		doReturn(queryBody).when(req).getBody();

		doAnswer(new Answer<HttpResponseMessage.Builder>() {
			@Override
			public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
				HttpStatus status = (HttpStatus) invocation.getArguments()[0];
				return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
			}
		}).when(req).createResponseBuilder(any(HttpStatus.class));

		final ExecutionContext context = mock(ExecutionContext.class);
		doReturn(Logger.getGlobal()).when(context).getLogger();

		// Invoke
		final HttpResponseMessage ret = new Function().run(req, context);

		// Verify
		assertEquals(ret.getStatus(), HttpStatus.OK);
	}

	@Test
	public void testGetApplicationInsightsTelemetry() throws Exception {
		Function function = new Function();
		function.getApplicationInsightsTelemetry("30m");
	}
}
