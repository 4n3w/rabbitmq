package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class FromJettyToRabbitmq {
    public static void main(String args[]) throws Exception {
        // create exchange and queue
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("EX1", "direct", true);

        channel.queueDeclare("task_queue", true, false, false, null);
        channel.queueBind("task_queue", "EX1", "#");

        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("jetty:http://localhost:9898")
                        // setting the DELIVERY_MODE to 2 means that the amqp messages will survive a broker restart/crash
                        .setHeader("rabbitmq.DELIVERY_MODE").constant(2)
                        .removeHeaders("CamelHttp*")
                        .to("rabbitmq://localhost/EX1?queue=task_queue&durable=true&autoDelete=false&username=guest&password=guest");
            }
        });

        // start the route and let it do its work
        context.start();
    }
}
