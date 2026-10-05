package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
public class PaypalForwardAPITransactionRequest {

    @JsonProperty(value = "merchant_id")
    private String merchantId;
    @JsonProperty(value = "payment_method_token")
    private String paymentMethodToken;
    @JsonProperty(value = "name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonProperty(value = "device_data")
    private String deviceData;
    @JsonProperty(value = "tsp")
    private Tsp tsp;
    @JsonProperty(value = "url")
    private String url;
    @JsonProperty(value = "method")
    private String method;
    @JsonProperty(value = "sensitive_data")
    private SensitiveData sensitiveData;
    @JsonProperty(value = "data")
    private ForwardAPIData data;
    @JsonProperty(value = "config")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Config config;
    @Data
    @Builder
    public static class Tsp {
        @JsonProperty(value = "currency_code")
        private String currencyCode;
        @JsonProperty(value = "expire_at")
        private String expireAt;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SensitiveData {
        @JsonProperty(value = "Type")
        private String type;
        @JsonProperty(value = "Version")
        String version;
        @JsonProperty(value = "ValidationID")
        private String validationID;
        @JsonProperty(value = "ValidationCode")
        private String validationCode;
        @JsonProperty(value = "ValidationCodeHash")
        private String validationCodeHash;
        @JsonProperty(value = "OptionFlags")
        private String optionFlags;
        @JsonProperty(value = "COFIndicator")
        private String cofIndicator;
        @JsonProperty(value = "UserData1")
        private String settlementReference;
        @JsonProperty(value = "UserData2")
        private String bookingReference;
        @JsonProperty(value = "UserData3")
        private String bookingChannel;
        @JsonProperty(value = "UserData4")
        private String paypalString;
        @JsonProperty(value = "SCATransRef")
        private String scaTransRef;
        @JsonProperty(value = "MITType")
        private String mitType;
        @JsonProperty(value = "TransInitiator")
        private String transInitiator;
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
        private String arrivalDate;
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
        @JsonProperty(value = "FraudMode")
        private String fraudMode;
        @JsonProperty(value = "FraudProfileName")
        private String fraudProfileName;
    }
    @Data
    @Builder
    public static class ForwardAPIData {
        @JsonProperty(value = "CardholderStreetAddress1")
        private String cardholderStreetAddress1;
        @JsonProperty(value = "CardholderStreetAddress2")
        private String cardholderStreetAddress2;
        @JsonProperty(value = "CardholderStreetAddress3")
        private String cardholderStreetAddress3;
        @JsonProperty(value = "CardholderStreetAddress4")
        private String cardholderStreetAddress4;
        @JsonProperty(value = "CardholderCity")
        private String cardholderCity;
        @JsonProperty(value = "CardholderState")
        private String cardholderState;
        @JsonProperty(value = "CardholderCountry")
        private String cardholderCountry;
        @JsonProperty(value = "CardholderZipCode")
        private String cardholderZipCode;
        @JsonProperty(value = "CardholderNameFirst")
        private String cardholderNameFirst;
        @JsonProperty(value = "CardholderNameLast")
        private String cardholderNameLast;
        @JsonProperty(value = "CardholderEmail")
        private String cardholderEmail;
        @JsonProperty(value = "CardholderTelephone")
        private String cardholderTelephone;
        @JsonProperty(value = "Amount")
        private String amount;
        @JsonProperty(value = "Currency")
        private String currency;
        @JsonProperty(value = "RequesterTransRefNum")
        private String paymentId;
    }

    @Data
    @Builder
    public static class Config {
        @JsonProperty(value = "name")
        private String name;
        @JsonProperty(value = "methods")
        private List<String> methods;
        @JsonProperty(value = "url")
        private String url;
        @JsonProperty(value = "request_format")
        private RequestFormat requestFormat;
        @JsonProperty(value = "types")
        private List<String> types;
        @JsonProperty(value = "transformations")
        private List<Transformation> transformations;
    }

    @Data
    @Builder
    public static class RequestFormat {
        @JsonProperty(value = "/body")
        private String body;
    }

    @Data
    @Builder
    public static class Transformation {
        @JsonProperty(value = "path")
        private String path;
        @JsonProperty(value = "value")
        private Object value;
    }

}
