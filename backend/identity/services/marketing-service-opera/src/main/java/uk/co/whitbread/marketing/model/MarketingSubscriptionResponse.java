package uk.co.whitbread.marketing.model;


import lombok.Data;

@Data
public class MarketingSubscriptionResponse {

    private Boolean success;

    private String emailAddress;

    private String customerId;

}
