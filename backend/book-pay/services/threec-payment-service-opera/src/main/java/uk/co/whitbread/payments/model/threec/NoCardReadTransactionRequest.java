package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.payments.model.MitType;

/**
 * {
 * "Request":{
 * "Type":"payrequestnocardread",
 * "Version":"W2MXG520",
 * "Credentials":{
 * "ValidationID":"mWB-DEFAULT",
 * "ValidationCode":"LxElfdYBDqCH70ZKVP5eFXn9K"
 * },
 * "Params":{
 * "CardNumber":"4216333880397891103",
 * "CardExpiryDateMMYY":"1123",
 * "CardholderStreetAddress1":"120 Holborn",
 * "CardholderStreetAddress2":"Some Area",
 * "CardholderCity":"London",
 * "CardholderState":"Essex",
 * "CardholderCountry":"GB",
 * "CardholderZipCode":"EC1N 2TD",
 * "CardholderNameFirst":"Mr. James Bond",
 * "CardholderNameLast":null,
 * "CardholderEmail":"james.bond@whitbread.com",
 * "Amount":"4900",
 * "Currency":"GBP",
 * "RequesterTransRefNum":"edec46de-ba0d-4f4f-ac99-c8003ac34b74",
 * "OptionFlags":"G",
 * "CofIndicator":"S",
 * "UserData1":"123456789101112122",
 * "UserData2":"BR260692A",
 * "SCATransRef":null,
 * "MITType":null,
 * "TransInitiator":null,
 * "FraudCheckData":null
 * }
 * }
 * }
 */
@Data
public class NoCardReadTransactionRequest {

    @JsonProperty(value = "Request")
    private Request request;

    @Data
    public static class Request {
        @JsonProperty(value = "Type")
        String type;
        @JsonProperty(value = "Version")
        String version;
        @JsonProperty(value = "Credentials")
        private Credentials credentials;
        @JsonProperty(value = "Params")
        private Params params;
    }

    @Data
    @Builder
    public static class Credentials {
        @JsonProperty(value = "ValidationID")
        private String validationId;
        @JsonProperty(value = "ValidationCode")
        private String validationCode;
    }

    @Data
    public static class Params {
        @JsonProperty(value = "CardNumber")
        private String token;
        @JsonProperty(value = "CardExpiryDateMMYY")
        private String expiryDate;
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
        private String transactionReference;
        @JsonProperty(value = "OptionFlags")
        private String optionFlags;
        @JsonProperty(value = "CofIndicator")
        private String cardOnFileIndicator;
        @JsonProperty(value = "UserData1")
        private String settlementReference;
        @JsonProperty(value = "UserData2")
        private String bookingReference;
        @JsonProperty(value = "UserData3")
        private String bookingChannel;
        @JsonProperty(value = "UserData4")
        private String walletType;
        @JsonProperty(value = "SCATransRef")
        private String scaTransRef;
        @JsonProperty(value = "MITType")
        private MitType mitType;
        @JsonProperty(value = "TransInitiator")
        private String transInitiator;
        @JsonProperty(value = "FraudCheckData")
        private FraudCheckData fraudCheckData;
    }
}
