package model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;

public class OfferTest {
    private static ObjectMapper mapper;
    private static Validator validator;

    private Set<ConstraintViolation<Offer>> getViolations(Offer offer){
        return validator.validate(offer);
    }

    @BeforeClass
    public static void setup(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    }

    @Test
    public void unmarshalValidOfferFromJSON() throws IOException {
        String jsonString = "{\"offer\":{\"offer_id\":\"123\",\"card_type_id\":\"123\",\"member_id\":\"Andrew\",\"redeem_date\":\"July 1, 1867\"}}";
        Offer offer = mapper.readValue(jsonString, Offer.class);

        assertThat(offer.getCard_type_id(), equalTo("123"));
        assertThat(offer.getMember_id(), equalTo("Andrew"));
        assertThat(offer.getOffer_id(), equalTo("123"));
        assertThat(offer.getRedeem_date(), equalTo("July 1, 1867"));
    }

    @Test
    public void unmarshalInvalidOfferFromJSON() throws IOException {
        String jsonString = "{\"offer\":{\"offer_id\":\"123\",\"member_id\":\"Andrew\",\"redeem_date\":\"July 1, 1867\"}}";
        Offer offer = mapper.readValue(jsonString, Offer.class);

        Set<ConstraintViolation<Offer>> violations = getViolations(offer);
        assertThat(violations.iterator().next().getMessage(), equalTo("may not be null"));
        assertThat(offer.getOffer_id(), equalTo("123"));
    }

    @Test
    public void marshalValidOfferToJSON() throws IOException {
        Offer offer = new Offer();
        offer.setCard_type_id("123");
        offer.setOffer_id("123");
        offer.setMember_id("Andrew");
        offer.setRedeem_date("July 1, 1867");
        OutputStream o = new ByteArrayOutputStream();
        mapper.writeValue(o, offer);
        assertThat(o.toString(), equalTo("{\"offer\":{\"offer_id\":\"123\",\"card_type_id\":\"123\",\"member_id\":\"Andrew\",\"redeem_date\":\"July 1, 1867\"}}"));
        assertThat(getViolations(offer).size(), equalTo(0));
    }

    @Test
    public void marshalOfferMissingOfferId() throws IOException {
        Offer offer = new Offer();
        offer.setCard_type_id("123");
        offer.setMember_id("Andrew");
        offer.setRedeem_date("July 1, 1867");

        OutputStream o = new ByteArrayOutputStream();
        mapper.writeValue(o, offer);
        Set<ConstraintViolation<Offer>> violations = getViolations(offer);
        assertThat(violations.size(), equalTo(1));
        assertThat(violations.iterator().next().getMessage(), equalTo("may not be null"));
        assertThat( o.toString(), equalTo("{\"offer\":{\"offer_id\":null,\"card_type_id\":\"123\",\"member_id\":\"Andrew\",\"redeem_date\":\"July 1, 1867\"}}"));
    }

}

