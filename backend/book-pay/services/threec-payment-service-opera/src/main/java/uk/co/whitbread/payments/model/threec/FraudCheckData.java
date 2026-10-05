package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FraudCheckData {

    @JsonProperty(value = "FraudMode")
    private String fraudMode;

    @JsonProperty(value = "FraudProfileName")
    private String fraudProfileName;

    @JsonProperty(value = "FraudMDD1")
    private String channel;

    @JsonProperty(value = "FraudMDD2")
    private String subChannel;

    @JsonProperty(value = "FraudMDD3")
    private String website;

    @JsonProperty(value = "FraudMDD4")
    private Integer hoursUntilCheckIn;

    @JsonProperty(value = "FraudMDD5")
    private String checkedInOnline;

    @JsonProperty(value = "FraudMDD6")
    private LocalDate arrivalDate;

    @JsonProperty(value = "FraudMDD7")
    private int noOfNights;

    @JsonProperty(value = "FraudMDD8")
    private String roomType;

    @JsonProperty(value = "FraudMDD9")
    private Integer noOfRooms;

    @JsonProperty(value = "FraudMDD10")
    private String guestName;

    @JsonProperty(value = "FraudMDD11")
    private String bookerName;

    @JsonProperty(value = "FraudMDD12")
    private String hotelName;

    @JsonProperty(value = "FraudMDD13")
    private String hotelCode;

    @JsonProperty(value = "FraudMDD14")
    private String hotelCity;

    @JsonProperty(value = "FraudMDD15")
    private String memberRegistered;

    @JsonProperty(value = "FraudMDD16")
    private Integer noOfGuests;

    @JsonProperty(value = "FraudMDD17")
    private String roomRateType;

    @JsonProperty(value = "FraudMDD18")
    private String additionalServices;

    @JsonProperty(value = "FraudMDD19")
    private Integer memberRegisteredSinceDays;

    @JsonProperty(value = "FraudMDD20")
    private Integer previousBookingsCount;

    @JsonProperty(value = "FraudMDD21")
    private String altPayMethod;

    @JsonProperty(value = "FraudMDD22")
    private String paypalEmailId;

    @JsonProperty(value = "FraudMDD23")
    private String paypalPayerId;

    @JsonProperty(value = "FraudMDD24")
    private String paypalPayerStatus;

    @JsonProperty(value = "FraudMDD25")
    private String paypalAddressStatus;

    @JsonProperty(value = "FraudMDD26")
    private String paypalSellerProtection;

    @JsonProperty(value = "FraudMDD27")
    private String paypalFirstName;

    @JsonProperty(value = "FraudMDD28")
    private String paypalLastName;

    @JsonProperty(value = "FraudMDD29")
    private Long paypalBillingPhoneNumber;
}
