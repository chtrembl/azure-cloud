

# Use Azure Log Queries & Azure Monitor to analyze and monitor your Spring Boot Applications

[Azure Monitor](https://docs.microsoft.com/en-us/azure/azure-monitor/overview) maximizes the availability and performance of your applications and services by delivering a comprehensive solution for collecting, analyzing, and acting on telemetry from your cloud and on-premises environments. It helps you understand how your applications are performing and proactively identifies issues affecting them and the resources they depend on.

Azure Log queries help you to fully leverage the value of the data collected in [Azure Monitor Logs](https://docs.microsoft.com/en-us/azure/azure-monitor/platform/data-platform-logs). A powerful query language allows you to join data from multiple tables, aggregate large sets of data, and perform complex operations with minimal code. Virtually any question can be answered and analysis performed as long as the supporting data has been collected, and you understand how to construct the right query.

Some features in Azure Monitor such as [insights](https://docs.microsoft.com/en-us/azure/azure-monitor/insights/insights-overview) and [solutions](https://docs.microsoft.com/en-us/azure/azure-monitor/monitor-reference) process log data without exposing you to the underlying queries. To fully leverage other features of Azure Monitor, you should understand how queries are constructed and how you can use them to interactively analyze data in Azure Monitor Logs.

Just a few examples of what you can do with Azure Monitor include:

-   Detect and diagnose issues across applications and dependencies with  [Application Insights](https://docs.microsoft.com/en-us/azure/azure-monitor/app/app-insights-overview).
-   Correlate infrastructure issues with  [Azure Monitor for VMs](https://docs.microsoft.com/en-us/azure/azure-monitor/insights/vminsights-overview)  and  [Azure Monitor for Containers](https://docs.microsoft.com/en-us/azure/azure-monitor/insights/container-insights-overview).
-   Drill into your monitoring data with  [Log Analytics](https://docs.microsoft.com/en-us/azure/azure-monitor/log-query/log-query-overview)  for troubleshooting and deep diagnostics.
-   Support operations at scale with  [smart alerts](https://docs.microsoft.com/en-us/azure/azure-monitor/platform/alerts-smartgroups-overview)  and  [automated actions](https://docs.microsoft.com/en-us/azure/azure-monitor/platform/alerts-action-rules).
-   Create visualizations with Azure  [dashboards](https://docs.microsoft.com/en-us/azure/azure-monitor/learn/tutorial-logs-dashboards)  and  [workbooks](https://docs.microsoft.com/en-us/azure/azure-monitor/platform/workbooks-overview).

The image below depicts typical a [Web Application Insights Telemetry Model](https://docs.microsoft.com/en-us/azure/azure-monitor/app/data-model). Consumer's of this Web Application Telemetry (transactions in Azure Monitor) might be interested but not limited to incoming/outgoing requests/responses (and the meta data associated), the various events, metrics and traces that can and will occur during those requests/responses, faults/exceptions that may occur and downstream correlated/dependencies on other applications and services. Where logging provides an overview to a discrete, event-triggered log, tracing encompasses a much wider, continuous view of an application. The goal of tracing is to following a program’s flow and data progression. When a problem does occur, tracing allows you to see how you got there: which function, the function’s duration, parameters passed, and how deep into the function the user could get. In section 4 below (**Write Custom Log Queries (Kusto (KQL) for common use cases**) we will look at what it takes to capture the flows depicted here.

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai0.png?raw=true)

A Web Application/Microservice of this type can/will offer the following types of transactions withing Azure Monitor

-   **Trace**  -  [Diagnostic logs](https://docs.microsoft.com/en-us/azure/azure-monitor/app/asp-net-trace-logs)  including TrackTrace, log4Net, NLog, and System.Diagnostic.Trace calls.
-   **Request**  - HTTP requests received by your server application, including pages, scripts, images, style files, and data. These events are used to create the request and response overview charts.
-   **Page View**  -  [Telemetry sent by the web client](https://docs.microsoft.com/en-us/azure/azure-monitor/app/javascript), used to create page view reports.
-   **Custom Event**  - If you inserted calls to TrackEvent() in order to  [monitor usage](https://docs.microsoft.com/en-us/azure/azure-monitor/app/api-custom-events-metrics), you can search them here.
-   **Exception**  - Uncaught  [exceptions in the server](https://docs.microsoft.com/en-us/azure/azure-monitor/app/asp-net-exceptions), and those that you log by using TrackException().
-   **Dependency**  -  [Calls from your server application](https://docs.microsoft.com/en-us/azure/azure-monitor/app/asp-net-dependencies)  to other services such as REST APIs or databases, and AJAX calls from your  [client code](https://docs.microsoft.com/en-us/azure/azure-monitor/app/javascript).
-   **Availability**  - Results of  [availability tests](https://docs.microsoft.com/en-us/azure/azure-monitor/app/monitor-web-app-availability).

*Note the above was taken from [Azure Monitor](https://docs.microsoft.com/en-us/azure/azure-monitor/overview)*

## Tutorial
In this tutorial we will look at what it takes to get your Spring Boot Application configured to use these Azure Monitor features.We will cover the following:

- **1. Enable/Configure Azure Application Insights**
- **2. Enable/Configure Spring Boot Application Code**
- **3. View Application Insights > Transaction Search**
- **4. Write Custom Log Queries (Kusto (KQL) for common use cases**

*Before we get started, there are two ways to enable application monitoring for Azure App Services hosted applications (In this tutorial we will be manually instrumenting the application through code):*

-   **Agent-based application monitoring**  (ApplicationInsightsAgent).
    
    -   This method is the easiest to enable, and no advanced configuration is required. It is often referred to as "runtime" monitoring. For Azure App Services we recommend at a minimum enabling this level of monitoring, and then based on your specific scenario you can evaluate whether more advanced monitoring through manual instrumentation is needed.
-   **Manually instrumenting the application through code**  by installing the Application Insights SDK.
    
    -   This approach is much more customizable, but it requires  [adding a dependency on the Application Insights SDK NuGet packages](https://docs.microsoft.com/en-us/azure/azure-monitor/app/asp-net). This method, also means you have to manage the updates to the latest version of the packages yourself.
        
    -   If you need to make custom API calls to track events/dependencies not captured by default with agent-based monitoring, you would need to use this method. Check out the  [API for custom events and metrics article](https://docs.microsoft.com/en-us/azure/azure-monitor/app/api-custom-events-metrics)  to learn more. This is also currently the only supported option for Linux based workloads.

*Note the above was taken from [Enable Application Insights](https://docs.microsoft.com/en-us/azure/azure-monitor/app/azure-web-apps?tabs=net)*

 - **Enable/Configure Azure Application Insights**
 
	 This gives us an Azure Application Insights Service (Feature of Azure Monitor) to push our data into and access our Telemetry. 
	 From the Azure Portal search for Application Insights and create new. This will configure a new instance for us to push data too.	
	 ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai1.png?raw=true)
	
	Wait for this to complete ...
		![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai2.png?raw=true)

	Make note of the Instrumentation Key, it is needed below.
		![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai3.png?raw=true)			 

 - **Enable/Configure Spring Boot Application Code**
		Update your application.yml or application.properties with the instrumentation key, this will be used by the Azure Spring Boot starter to push data into Azure Monitor.
	```yml
	azure:
		application-insights:
			instrumentation-key: <enter from above>
	```
	Add a logback-spring.xml with the following contents which configures an ApplicationInsightsAppender which is responsible for pushing all of your log data into Azure Monitor, automagically for you. These will appear as Trace's in Azure Monitor.
	```xml
	<?xml version="1.0" encoding="UTF-8"?>
	<configuration>
	<include  resource="org/springframework/boot/logging/logback/base.xml"/>
	<appender  name="aiAppender"
	class="com.microsoft.applicationinsights.logback.ApplicationInsightsAppender">
	</appender>
	<conversionRule  conversionWord="clr"  converterClass="org.springframework.boot.logging.logback.ColorConverter"  />
	<conversionRule  conversionWord="wex"  converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"  />
	<conversionRule  conversionWord="wEx"  converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"  />
	<appender  name="consoleAppender"  class="ch.qos.logback.core.ConsoleAppender">
	<layout  class="ch.qos.logback.classic.PatternLayout">
	<Pattern>
            %clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} session_Id=%X{session_Id} | %m%n%wEx
    </Pattern>
	</layout>
	</appender>
	<root  level="info">
	<appender-ref  ref="aiAppender"  />
	</root>
	</configuration>
	```
	Update your maven pom.xml to pull in the Azure dependencies needed to push log data and API for Custom Application Instrumentation.
	```xml
	<dependency>
		<groupId>com.microsoft.azure</groupId>
		<artifactId>applicationinsights-spring-boot-starter</artifactId>
		<version>2.6.1</version>
	</dependency>
	<dependency>
		<groupId>com.microsoft.azure</groupId>
		<artifactId>applicationinsights-logging-logback</artifactId>
		<version>2.6.1</version>
	</dependency>
	```
	The ApplicationInsightsAppender alone is enough to push some default Spring Boot data such as Page Request Transaction Data and Trace Transaction Data into Azure Monitor, however there is often times a need to capture Custom Events and Exceptions as well. Perhaps you'll want to tie all of these transactions together with a Session ID or Correlation ID (for multiple services/resources leveraging the Application Insights instance) to get a full user flow for a specific user etc... (As mentioned early on in this document)
 You can do this with 
	
	```java
	import com.microsoft.applicationinsights.TelemetryClient;
	...
	@Autowired
	private TelemetryClient telemetryClient;
	```
	Throughout your application code you can use TelementryClient to push transaction data (Custom Events, Page Views, Exceptions etc..) into Application Insights. Suppose you have a Session Scoped Spring Bean, called User, that is wired throughout your application, You could wire in an instance of  TelemetryClient to push data to Application Insights, specific to a session. Using [MDC](http://logback.qos.ch/manual/mdc.html) We can log all of these Telemetry Tranactions with a sessionId which will be very helpful in constructing our Kusto Log Queries.

	```java
	package com.chtrembl.petstoreapp.model;

	import java.io.Serializable;

	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.context.annotation.Scope;
	import org.springframework.context.annotation.ScopedProxyMode;
	import org.springframework.stereotype.Component;

	import com.microsoft.applicationinsights.TelemetryClient;

	@Component
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@SuppressWarnings("serial")
	public class User implements Serializable {
		private String name = null;
		private String sessionId = null;

		@Autowired
		private TelemetryClient telemetryClient;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public TelemetryClient getTelemetryClient() {
			return this.telemetryClient;
		}
	}

	```
	Then when you authenticate a new user
	```java
	@Autowired
	private User sessionUser;
	...
	this.sessionUser.getTelemetryClient().getContext().getSession().setIsNewSession(true);
	this.sessionUser.getTelemetryClient().getContext().getSession().setId(this.sessionUser.getSessionId());	
	// Track an Event
	this.sessionUser.getTelemetryClient().trackEvent(String.format("user %s session %s login", this.sessionUser.getName(), this.sessionUser.getSessionId()));
	MDC.put("session_Id", this.sessionUser.getSessionId());
	```
	And this same model can be used throughout your application's MVC flow...

	```java
	import com.microsoft.applicationinsights.telemetry.PageViewTelemetry;

	...
	// Track a Page View
	PageViewTelemetry pageViewTelemetry = new PageViewTelemetry();
	pageViewTelemetry.setUrl(new URI(request.getRequestURL().toString()));
	pageViewTelemetry.setName("Account Landing Page");
	this.sessionUser.getTelemetryClient().trackPageView(pageViewTelemetry);

	...
	// Track an Exception
	this.sessionUser.getTelemetryClient().trackException(new NullPointerException("sample null pointer"));
	```
 - **View Application Insights > Transaction Search**
First you will want to hit your application as a couple of different users (different sessions) to generate Transaction Data in Azure Monitor (you can use the PetStoreApp seen [here](https://github.com/chtrembl/Azure/tree/master/petstoreapp)  then head to Azure Portal > Application Insights.

	Under Transaction Search in the left navigation you can filter transactions within the last 30 minutes and you will see all of the transactions for the application that you just hit. (You will notice different sessionId's as well, a nice way, among many, to distinguish between flows) 
	![ai4.png](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai4.png?raw=true)

	If you select one, you can view all the transactions associated (end to end) for that request (Remember the illustration from above that depicts the user flow). For example, Login: A request comes in (Request Transaction), it gets tracked (Custom Event Transaction), the request handler mapping determines the view (Page View Transaction) and the routing occurs (Logging Transaction from the Application Insights Appender) 
	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai5.png?raw=true)

 - **Write Custom Log Queries (Kusto (KQL) for common use case**
	First you will want to hit your application as a couple of different users (different sessions) to generate Transaction Data in Azure Monitor (you can use the PetStoreApp seen [here](https://github.com/chtrembl/Azure/tree/master/petstoreapp)  then head to Azure Portal > Application Insights.

	By Selecting Log's you will be presented with a query view containing various queries on the different tables of data. You can also build your own. 

	Often times it is beneficial to get a transactional flow based on user activity. You can do this by individual transaction types (Requests, Page Views, Traces etc...) or perhaps you want to combine results across various sources. Perhaps you have a correlated id/session id that spans multiple application sources (web application and N micro services) Since our Spring boot application is logging the JSESSION ID for Traces and Events, we can easily capture this via Kusto Queries.

	First grab a session id from a user of concern or an exception etc... or your own JSESSION ID from your browser cookie viewer, we will use this to find our transactions. (Each server side session will drop this JSESSION ID cookie on the client)

	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai6.png?raw=true)

	Head to Application Inisghts > Logs and construct a new query:

	```sql
	search in (traces, customEvents) session_Id:"490439728D226DD5AC32D10CCC76E0A4"  or customDimensions.session_Id:"490439728D226DD5AC32D10CCC76E0A4"
	| extend detail = strcat(name, message)
	| order  by timestamp asc
	```
	This will query all Trace Data and Custom Event Data where session_Id matches (Put your session_Id in and remember we used MDC attribute to log all of our Transactions with session_Id to ensure Azure Montior would index accordingly). We are searching the Trace table (all of our Spring Boot Application Logs, as well as Custom Events, which remember is the TelemetryClient that we are programatically working with). When we log session_Id as Trace from our Application Logs, it appears as customDimension Data (Azure doesn't know about it) So we need to explicitly extract it. We are also creating a new result field called "detail" which will either map to the Application Log message or the TelemetryClient message. This makes readability much cleaner having it in one column. Now Execute the query and you will see an Ascending display of data detailing what the user for this JSESSION ID did during their time on the Application. (What paths they took, exceptions that occurred etc...) You can also use an ID that spans N Application/Microservices. This is just scratching the surface what you can do with Kusto.

	![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/applicationinsights/ai8.png?raw=true)
