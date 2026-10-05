package uk.co.whitbread.payments.model.eckoh;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EckohWebPanelRequest {

    @JsonProperty(value = "cId")
    String clientId;
    @JsonProperty(value = "property")
    String hotelName;
    @JsonProperty(value = "reservationId")
    String reservationId;
    @JsonProperty(value = "aId")
    String agentId;
    @JsonProperty(value = "merchantId")
    String merchantId;
    @JsonProperty(value = "timeofpay")
    String timeOfPay;
    @JsonProperty(value = "paymethod")
    String paymentMethod;
    @JsonProperty(value = "cardpresence")
    CardAvailability cardAvailability;
    @JsonProperty(value = "hotelCountry")
    String hotelCountry;
    @JsonProperty(value = "lang")
    String language;
    @JsonProperty(value = "env")
    String targetEckohEnvironment;
}
