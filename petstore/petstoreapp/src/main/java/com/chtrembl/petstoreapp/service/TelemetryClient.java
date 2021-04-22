package com.chtrembl.petstoreapp.service;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.MetricTelemetry;
import com.microsoft.applicationinsights.telemetry.PageViewTelemetry;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.SessionState;
import com.microsoft.applicationinsights.telemetry.SeverityLevel;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import com.microsoft.applicationinsights.telemetry.TraceTelemetry;

public class TelemetryClient extends com.microsoft.applicationinsights.TelemetryClient {
	private static String message = "azure.application-insights.instrumentation-key not found, considering configuring Application Inisghts if you would like to track Telemtry.";
	private static Logger logger = LoggerFactory.getLogger(TelemetryClient.class);

	@Override
	public void track(Telemetry arg0) {
		logger.warn(message);
	}

	@Override
	public void trackDependency(RemoteDependencyTelemetry telemetry) {
		logger.warn(message);
	}

	@Override
	public void trackDependency(String dependencyName, String commandName, Duration duration, boolean success) {
		logger.warn(message);
	}

	@Override
	public void trackEvent(EventTelemetry telemetry) {
		logger.warn(message);
	}

	@Override
	public void trackEvent(String name, Map<String, String> properties, Map<String, Double> metrics) {
		logger.warn(message);
	}

	@Override
	public void trackEvent(String name) {
		logger.warn(message);
	}

	@Override
	public void trackException(Exception exception, Map<String, String> properties, Map<String, Double> metrics) {
		logger.warn(message);
	}

	@Override
	public void trackException(Exception exception) {
		logger.warn(message);
	}

	@Override
	public void trackException(ExceptionTelemetry telemetry) {
		logger.warn(message);
	}

	@Override
	public void trackHttpRequest(String name, Date timestamp, long duration, String responseCode, boolean success) {
		logger.warn(message);
	}

	@Override
	public void trackMetric(MetricTelemetry telemetry) {
		logger.warn(message);
	}

	@Override
	public void trackMetric(String name, double value, int sampleCount, double min, double max,
			Map<String, String> properties) {
		logger.warn(message);
	}

	@Override
	public void trackMetric(String name, double value, Integer sampleCount, Double min, Double max, Double stdDev,
			Map<String, String> properties) {
		logger.warn(message);
	}

	@Override
	public void trackMetric(String name, double value) {
		logger.warn(message);
	}

	@Override
	public void trackPageView(PageViewTelemetry telemetry) {
		logger.warn(message);
	}

	@Override
	public void trackPageView(String name) {
		logger.warn(message);
	}

	@Override
	public void trackRequest(RequestTelemetry request) {
		logger.warn(message);
	}

	@Override
	public void trackSessionState(SessionState sessionState) {
		logger.warn(message);
	}

	@Override
	public void trackTrace(String message, SeverityLevel severityLevel, Map<String, String> properties) {
		logger.warn(message);
	}

	@Override
	public void trackTrace(String message, SeverityLevel severityLevel) {
		logger.warn(message);
	}

	@Override
	public void trackTrace(String message) {
		logger.warn(message);
	}

	@Override
	public void trackTrace(TraceTelemetry telemetry) {
		logger.warn(message);
	}

}
