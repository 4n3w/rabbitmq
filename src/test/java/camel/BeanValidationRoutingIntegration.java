package camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Offer;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class BeanValidationRoutingIntegration extends CamelTestSupport {

    private static ObjectMapper mapper;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @EndpointInject(uri = "mock:invalid")
    protected MockEndpoint invalidEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @BeforeClass
    public static void beforeClass(){
        mapper = new ObjectMapper();
    }

    @Test
    public void testSendMatchingMessage() throws Exception {
        Offer offer = new Offer();
        OutputStream o = new ByteArrayOutputStream();
        mapper.writeValue(o, offer);
        offer.setRedeem_date("1221");
        String expectedBody = "<matched/>";
//        resultEndpoint.expectedBodiesReceived(o.toString());
        invalidEndpoint.expectedBodiesReceived(o.toString());
//        resultEndpoint.expectedMessageCount(0);
        template.sendBodyAndHeader(o.toString(), "foo", "bar");
//        resultEndpoint.assertIsSatisfied();
        invalidEndpoint.assertIsSatisfied();
    }

//    @Test
//    public void testSendNotMatchingMessage() throws Exception {
//        resultEndpoint.expectedMessageCount(0);
//        template.sendBodyAndHeader("<notMatched/>", "foo", "notMatchedHeaderValue");
//        resultEndpoint.assertIsSatisfied();
//    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
//                from("direct:start").filter(header("foo").isEqualTo("bar")).to("bean-validator://validate-offer").to("mock:result");
                from("direct:start")
                        .doTry()
                            .to("bean-validator://validate-offer")
                        .doCatch(org.apache.camel.ValidationException.class)
                            .to("mock:invalid")
                        .to("mock:result");
            }
        };
    }
}
