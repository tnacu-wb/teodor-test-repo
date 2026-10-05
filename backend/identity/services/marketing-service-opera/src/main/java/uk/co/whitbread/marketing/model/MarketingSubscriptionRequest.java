package uk.co.whitbread.marketing.model;

import jakarta.validation.Valid;
import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotBlank;

@Data
public class MarketingSubscriptionRequest {

    private String sessionId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String emailAddress;

    private String customerId;

    private String countryCode;

    private Boolean businessClient;

    private Boolean restaurantNewsletter;

    private List<String> regions;

    @Valid
    private List<RegionSubscription> regionSubscriptions;

}
