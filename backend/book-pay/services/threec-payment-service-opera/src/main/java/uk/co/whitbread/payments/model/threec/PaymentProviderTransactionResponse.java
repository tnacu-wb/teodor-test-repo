package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProviderTransactionResponse {

    @JsonProperty(value = "Response")
    private Response response;

    @Data
    public static class Response {
        @JsonProperty(value = "Type")
        String type;
        @JsonProperty(value = "Version")
        String version;
        @JsonProperty(value = "Params")
        private Params params;
    }

    @Data
    public static class Params {
        @JsonProperty(value = "TxID")
        private String providerReference;
        @JsonProperty(value = "BankAuthCode")
        private String authCode;
        @JsonProperty(value = "RedirectURL")
        private String redirectURL;
        @JsonProperty(value = "AmountUsed")
        private String amountUsed;
        @JsonProperty(value = "Amount")
        private String amount;
        @JsonProperty(value = "CurrencyUsed")
        private String currencyUsed;
        @JsonProperty(value = "Currency")
        private String currency;
        @JsonProperty(value = "RequesterTransRefNum")
        private String transactionReference;
        @JsonProperty(value = "ResultReason")
        private String reason;
        @JsonProperty(value = "TxState")
        private String transactionState;
        @JsonProperty(value = "TxStateText")
        private String transactionStateText;
        @JsonProperty(value = "CVV2Result")
        private String cvv2Result;
        @JsonProperty(value = "AVSResult")
        private String avsResult;
        @JsonProperty(value = "Result")
        private String result;
        @JsonProperty(value = "CardNumberLast4")
        private String last4Digits;
        @JsonProperty(value = "CardSchemeId")
        private String cardType;
        @JsonProperty(value = "CardSchemeName")
        private String cardTypeName;
        @JsonProperty(value = "CardExpiryDateMMYY")
        private String expiry;
        @JsonProperty(value = "Token")
        private String token;
        @JsonProperty(value = "TokenExpiryMMYY")
        private String tokenExpiry;
        @JsonProperty(value = "BIN")
        private String binRange;
        @JsonProperty(value = "SCATransRef")
        private String scaReference;
        @JsonProperty(value = "ThreeDSIndicator")
        private String threeDSIndicator;
        @JsonProperty(value = "CardFraudInfo")
        private FraudResponse fraudResponse;
    }

    @Data
    public static class FraudResponse {
        @JsonProperty(value = "Decision")
        private String decision;
        @JsonProperty(value = "RequestId")
        private String requestId;
        @JsonProperty(value = "ReasonCode")
        private String reasonCode;
        @JsonProperty(value = "RequestToken")
        private String requestToken;
    }
}
