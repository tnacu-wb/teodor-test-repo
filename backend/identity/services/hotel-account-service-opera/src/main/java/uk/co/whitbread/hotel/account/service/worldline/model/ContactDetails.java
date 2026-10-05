package uk.co.whitbread.hotel.account.service.worldline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ContactDetails {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("ForeName")
    private String foreName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("LandlineNumber")
    String landlineNumber;

    @JsonProperty("Mobile")
    private String mobile;

    @JsonProperty("Email")
    private String email;
}
