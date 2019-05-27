package com.taotao.test.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestActiveMq {
	
	/**
	 * queue
	 * Producer  生产者
	 * @throws Exception
	 */
	@Test
	public void testQueueProducer() throws Exception {
		//1.先创建链接工厂对象COnnectionFactory,需要指定服务的ip和端口
		ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//2.使用工厂来创建连接对象Connection
		Connection connection = factory.createConnection();
		//3.开启链接,调用Connection对象的start方法
		connection.start();
		//4.使用Connection对象创建session对象
		//第一个参数是是否开启事务,一般不使用事务
		//第一个参数为true,第二个参数自动忽略,如实若是False,第二个参数为消息的应答模式
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//5.使用session对象创建一个Destination对象,两种形式queue和topic
		//参数是消息队列的名称
		Queue queue = session.createQueue("test-queue");
		//6.使用session对象创建一个Producer对象
		MessageProducer producer = session.createProducer(queue);
		//7.创建一个TestMessage对像
		TextMessage message = session.createTextMessage("你好啊,activeMQ");
		//8.发送消息
		producer.send(message);
		//9.关闭资源
		producer.close();
		session.close();
		connection.close();
		
	}
	@Test
	public void TestQueueTextMessage() throws Exception {
		//创建一个连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
		//使用连接工厂对象创建一个连接
		Connection connection = connectionFactory.createConnection();
		//开启连接
		connection.start();
		//使用连接对象创建一个Session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//使用Session创建一个Destination，Destination应该和消息的发送端一致。
		Queue queue = session.createQueue("test-queue");
		//使用Session创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		//向Consumer对象中设置一个MessageListener对象，用来接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				//取消息的内容
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					try {
						String text = textMessage.getText();
						//打印消息内容
						System.out.println(text);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		//系统等待接收消息
		/*while(true) {
			Thread.sleep(100);
		}*/
		System.in.read();
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	    
		    
	}
	
	//topic
	//Producer跟queue差不多
}
