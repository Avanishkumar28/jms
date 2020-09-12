package com.emeter.jms.configuration;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import com.emeter.jms.consumer.QueueConsumer;
 
@Configuration
public class AppConfig {
	
	/**********************************For WebLogic JMS*******************************************/
	/*
	 * private static final String CONTEXT_FACTORY =
	 * "weblogic.jndi.WLInitialContextFactory"; private static final String
	 * DEFAULT_BROKER_URL = "t3://localhost:7001";
	 */
	/**********************************For ActiveMQ*******************************************/
	private static final String CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	
	private static final String JNDI_CONNECTION_FACTORY = "ConnectionFactory";
	private static final String ORDER_QUEUE = "order-queue";

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	
    @Bean
    public JndiTemplate jndiTemplate() {
    	JndiTemplate jndiTemplate = new JndiTemplate();
    	Properties environment = new Properties();
    	environment.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
    	environment.put(Context.PROVIDER_URL, DEFAULT_BROKER_URL);
    	if (USERNAME != null && !USERNAME.trim().isEmpty() 
    			&& PASSWORD != null && !PASSWORD.trim().isEmpty()) {
    		environment.put(Context.SECURITY_PRINCIPAL, USERNAME);
    		environment.put(Context.SECURITY_CREDENTIALS, PASSWORD);
		}
    	jndiTemplate.setEnvironment(environment);
        return jndiTemplate;
    }
     
    @Bean
    public JndiObjectFactoryBean queueConnectionFactory() {
        JndiObjectFactoryBean queueConnectionFactory = new JndiObjectFactoryBean();
        queueConnectionFactory.setJndiTemplate(jndiTemplate());
        queueConnectionFactory.setJndiName(JNDI_CONNECTION_FACTORY);
        return queueConnectionFactory;
    }
     
    @Bean
    public JndiDestinationResolver jmsDestinationResolver() {
        JndiDestinationResolver destResolver = new JndiDestinationResolver();
        destResolver.setJndiTemplate(jndiTemplate());
        destResolver.setCache(true);
         
        return destResolver;
    }
     
    @Bean
    public JmsTemplate queueSenderTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory((ConnectionFactory) queueConnectionFactory().getObject());
        template.setDestinationResolver(jmsDestinationResolver());
        return template;
    }
     
    @Bean
    public JndiObjectFactoryBean jmsQueue() {
        JndiObjectFactoryBean jmsQueue = new JndiObjectFactoryBean();
        jmsQueue.setJndiTemplate(jndiTemplate());
        jmsQueue.setJndiName(ORDER_QUEUE);
         
        return jmsQueue;
    }
     
    @Bean
    public QueueConsumer queueListener() {
        return new QueueConsumer();
    }
     
    @Bean
    public DefaultMessageListenerContainer messageListener() {
        DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
        listener.setConcurrentConsumers(5);
        listener.setConnectionFactory((ConnectionFactory) queueConnectionFactory().getObject());
        listener.setDestination((Destination) jmsQueue().getObject());
        listener.setMessageListener(queueListener());
         
        return listener;
    }
}