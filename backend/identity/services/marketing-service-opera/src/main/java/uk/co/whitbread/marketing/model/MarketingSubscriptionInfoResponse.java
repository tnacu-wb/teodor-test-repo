package uk.co.whitbread.marketing.model;


import lombok.Data;

import java.util.List;

@Data
public class MarketingSubscriptionInfoResponse {

    private Boolean subscribedStatus;

    private String sessionId;

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String countryCode;

    private Boolean businessClient;

    private Boolean restaurantNewsletter;

    private List<String> regions;

    private List<RegionSubscription> regionSubscriptions;

}
