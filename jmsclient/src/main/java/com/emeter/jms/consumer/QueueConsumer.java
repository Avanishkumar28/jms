package com.emeter.jms.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class QueueConsumer implements MessageListener {

  @Override
  public void onMessage(Message message) {

    try {
      String textMessage = ((TextMessage) message).getText();
      System.out.println("Message recived: " + textMessage);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
}