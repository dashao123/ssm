package com.taotao.test.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestSpringActiveMq {
	//使用JmsTemplate发送消息
	@Test 
	public void TestSpringAC() throws Exception{
		//初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		//从容器中获得Template模板
		JmsTemplate template = applicationContext.getBean(JmsTemplate.class);
		//从容器中获得Destination对象
		Destination destination = (Destination) applicationContext.getBean("test-queue");
		
		template.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage("你好");
				return message;
			}
			
		});
	}
}
