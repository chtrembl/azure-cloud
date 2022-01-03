package io.swagger.configuration;

import java.io.IOException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.qpid.jms.message.JmsBytesMessage;
import org.apache.qpid.jms.provider.amqp.message.AmqpJmsMessageFacade;
import org.apache.qpid.proton.amqp.Symbol;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectWriter;

@Component
//https://docs.microsoft.com/en-us/azure/developer/java/spring-framework/configure-spring-boot-starter-java-app-with-azure-service-bus
public class CustomMessageConverter extends MappingJackson2MessageConverter {

	private static final String TYPE_ID_PROPERTY = "_type";
	private static final Symbol CONTENT_TYPE = Symbol.valueOf("application/json");

	public CustomMessageConverter() {
		this.setTargetType(MessageType.BYTES);
		this.setTypeIdPropertyName(TYPE_ID_PROPERTY);
	}

	@Override
	protected BytesMessage mapToBytesMessage(Object object, Session session, ObjectWriter objectWriter)
			throws JMSException, IOException {
		final BytesMessage bytesMessage = super.mapToBytesMessage(object, session, objectWriter);
		JmsBytesMessage jmsBytesMessage = (JmsBytesMessage) bytesMessage;
		AmqpJmsMessageFacade facade = (AmqpJmsMessageFacade) jmsBytesMessage.getFacade();
		facade.setContentType(CONTENT_TYPE);
		return jmsBytesMessage;
	}
}