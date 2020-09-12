package com.emeter.jms;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.emeter.jms.configuration.AppConfig;
import com.emeter.jms.sender.QueueSender;

public class Application {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		// Launch the application
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		//JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		QueueSender sender = context.getBean(QueueSender.class);
		
		DefaultMessageListenerContainer listener = context.getBean(DefaultMessageListenerContainer.class);
		
		Application app = new Application();
		app.sendMessages(sender);
		listener.start();

		// Send a message with a POJO - the template reuse the message converter
		//jmsTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello"));
	}

	private void sendMessages(QueueSender sender) {
		Runnable senderThread = ()->{
			System.out.println("Sending message......");
			for(int i = 1; i<=10; i++) {
				sender.sendToQueue("This is test message with MessageID: "+i);
				sleep(1000);
			}
		};
		new Thread(senderThread).start();
	}
	
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}