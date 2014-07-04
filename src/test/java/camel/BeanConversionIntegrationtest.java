package camel;

import camel.routes.JettyToRabbit;
import model.Offer;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

import java.io.IOException;

public class BeanConversionIntegrationtest extends CamelTestSupport {

    @EndpointInject(uri = "{{uri.valid}}")
    protected MockEndpoint resultEndpoint;

    @EndpointInject(uri = "{{uri.invalid}}")
    protected MockEndpoint invalidEndpoint;

    @EndpointInject(uri = "{{uri.errors}}")
    protected MockEndpoint errorEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        PropertiesComponent prop = context.getComponent("properties",
                PropertiesComponent.class);
        prop.setLocation("classpath:routing.TEST.properties");
        return context;
    }

    @Test
    public void testOffer() throws InterruptedException, IOException {
        String json = "{\"offer_id\":null,\"card_type_id\":\"Andrew\",\"member_id\":null,\"redeem_date\":null}";
        resultEndpoint.expectedMessageCount(1);
        template.sendBody(json);
        resultEndpoint.assertIsSatisfied();
        Exchange e = resultEndpoint.getReceivedExchanges().iterator().next();
        Offer end = e.getIn().getBody(Offer.class);
        assertThat(end.getCard_type_id(), equalTo("Andrew"));
    }

    @Test
    public void testJunkMessage() throws InterruptedException {
        resultEndpoint.expectedMessageCount(0);
        invalidEndpoint.expectedMessageCount(1);
        errorEndpoint.expectedMessageCount(0);
        template.sendBody("#!$^#^!@%^");
        resultEndpoint.assertIsSatisfied();
        invalidEndpoint.assertIsSatisfied();
        errorEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new JettyToRabbit();
    }

}