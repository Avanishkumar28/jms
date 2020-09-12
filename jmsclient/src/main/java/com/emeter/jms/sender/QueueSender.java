package com.emeter.jms.sender;

import com.emeter.jms.configuration.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
 
@Component
public class QueueSender {
    @Autowired
    private JmsTemplate jmsTemplate;
     
    public void sendToQueue(String message) {
        // instead of lambda you can just use new MessageCreator() (as an anonymous class)
        MessageCreator messageCreator = (session) -> session.createTextMessage(message); 
        try {
            jmsTemplate.send(AppConfig.ORDER_QUEUE, messageCreator);
        } catch (JmsException ex) {
            ex.printStackTrace();
        }
    }
}