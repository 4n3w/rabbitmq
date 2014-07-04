package model;

import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.NotNull;

@JsonRootName(value = "offer")
public class Offer {

    @NotNull private String offer_id;
    @NotNull private String card_type_id;
    @NotNull private String member_id;
    @NotNull private String redeem_date; //TODO: This shouldn't be a string

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getCard_type_id() {
        return card_type_id;
    }

    public void setCard_type_id(String card_type_id) {
        this.card_type_id = card_type_id;
    }

    public String getRedeem_date() {
        return redeem_date;
    }

    public void setRedeem_date(String redeem_date) {
        this.redeem_date = redeem_date;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "offer_id='" + offer_id + '\'' +
                ", card_type_id='" + card_type_id + '\'' +
                ", member_id='" + member_id + '\'' +
                ", redeem_date='" + redeem_date + '\'' +
                '}';
    }
}

