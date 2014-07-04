package camel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.Offer;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class BeanValidationRoutingIntegration extends CamelTestSupport {

    private static ObjectMapper mapper;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @EndpointInject(uri = "mock:invalid")
    protected MockEndpoint invalidEndpoint;

    @EndpointInject(uri = "mock:unhandled")
    protected MockEndpoint unhandledEndpoint;

    @EndpointInject(uri = "mock:finally")
    protected MockEndpoint finallyEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @BeforeClass
    public static void beforeClass(){
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    }

    @Test
    public void testSendMatchingMessage() throws Exception {
        String json = "{\"yoffer_id\":null,\"card_type_id\":null,\"member_id\":null,\"redeem_date\":null}";

//        resultEndpoint.expectedBodiesReceived(json);
        template.sendBody(json);
//        errorEndpoint.expectedBodiesReceived(json);
        unhandledEndpoint.expectedHeaderReceived("asdaskdasd", "asdf");
//        errorEndpoint.mess
        unhandledEndpoint.expectedMessageCount(1);
        invalidEndpoint.expectedMessageCount(0);
        resultEndpoint.expectedMessageCount(0);
        unhandledEndpoint.assertIsSatisfied();
        invalidEndpoint.assertIsSatisfied();
//        resultEndpoint.assertIsSatisfied();
    }


    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
//                from("direct:start").filter(header("foo").isEqualTo("bar")).to("bean-validator://validate-offer").to("mock:result");
                from("direct:start")
                        .doTry()
                            .unmarshal().json(JsonLibrary.Jackson, Offer.class)
//                            .to("bean-validator://validate-offer")
                            .to("mock:result")
                        .doCatch(ValidationException.class)
                            .to("mock:invalid")
                        .doCatch(Exception.class)
                            .to("mock:unhandled")
//                        .doFinally()
//                            .to("mock:finally")
                .end();
            }
        };
    }
}
