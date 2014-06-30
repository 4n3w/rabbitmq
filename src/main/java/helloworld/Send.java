package helloworld;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {
	
	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws java.io.IOException {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	    String message = "Hello World!";
        AMQP.BasicProperties messageProperties = new AMQP.BasicProperties().builder().deliveryMode(2).build();

        channel.basicPublish("", QUEUE_NAME, messageProperties, message.getBytes());
	    System.out.println(" [x] Sent '" + message + "'");
	    
	    channel.close();
	    connection.close();
	}
}
