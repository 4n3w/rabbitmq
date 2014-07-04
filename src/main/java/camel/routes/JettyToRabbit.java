package camel.routes;

import com.fasterxml.jackson.core.JsonParseException;
import model.Offer;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class JettyToRabbit extends RouteBuilder {

    @Override
    @SuppressWarnings("unchecked")
    public void configure() throws Exception {

        from("{{uri.input}}")
                .doTry()
                    .unmarshal().json(JsonLibrary.Jackson, Offer.class)
                    .setHeader("rabbitmq.DELIVERY_MODE").constant(2)
                    .removeHeaders("CamelHttp*")
                    .to("{{uri.valid}}")
                .doCatch(ValidationException.class, JsonParseException.class)
                    .to("{{uri.invalid}}")
                .doCatch(Exception.class)
                    .to("{{uri.errors}}")
                .end();
    }
}
