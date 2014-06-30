package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FromRabbitmqToFile {
    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("rabbitmq://localhost/EX1?queue=task_queue&durable=true&autoDelete=false&username=guest&password=guest")
                        .to("file:data/outbox");
            }
        });

        // start the route and let it do its work
        context.start();
    }
}
