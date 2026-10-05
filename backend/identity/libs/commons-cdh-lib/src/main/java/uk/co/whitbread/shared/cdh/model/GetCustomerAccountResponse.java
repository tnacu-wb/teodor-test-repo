package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCustomerAccountResponse {

    @JsonProperty("AdditionalGuests")
    private List<AdditionalGuest> additionalGuests;

    @JsonProperty("BookingPreference")
    private BookingPreference bookingPreference;

    @JsonProperty("ContactDetail")
    private ContactDetail contactDetail;

    @JsonProperty("BartGuestHistoryCreation")
    private String bartGuestHistoryCreation;

    @JsonProperty("BartGuestHistoryNumber")
    private String bartGuestHistoryNumber;

    @JsonProperty("PaymentPreference")
    private PaymentPreference paymentPreference;

    @JsonProperty("CustomerAccountId")
    private String customerAccountId;
}
