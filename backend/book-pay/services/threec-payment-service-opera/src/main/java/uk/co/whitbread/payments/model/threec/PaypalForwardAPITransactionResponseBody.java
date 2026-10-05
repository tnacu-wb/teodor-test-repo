package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaypalForwardAPITransactionResponseBody {

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
        String providerReference;
        @JsonProperty(value = "BankAuthCode")
        String authCode;
        @JsonProperty(value = "RedirectURL")
        String redirectURL;
        @JsonProperty(value = "AmountUsed")
        String amountUsed;
        @JsonProperty(value = "Amount")
        String amount;
        @JsonProperty(value = "CurrencyUsed")
        String currencyUsed;
        @JsonProperty(value = "Currency")
        String currency;
        @JsonProperty(value = "RequesterTransRefNum")
        String transactionReference;
        @JsonProperty(value = "UserData2")
        String userData2;
        @JsonProperty(value = "ResultReason")
        String reason;
        @JsonProperty(value = "TxState")
        String transactionState;
        @JsonProperty(value = "TxStateText")
        String transactionStateText;
        @JsonProperty(value = "CVV2Result")
        String cvv2Result;
        @JsonProperty(value = "AVSResult")
        String avsResult;
        @JsonProperty(value = "Result")
        String result;
        @JsonProperty(value = "CardNumberLast4")
        String last4Digits;
        @JsonProperty(value = "CardSchemeId")
        String cardType;
        @JsonProperty(value = "CardSchemeName")
        String cardTypeName;
        @JsonProperty(value = "CardExpiryDateMMYY")
        String expiry;
        @JsonProperty(value = "Token")
        String token;
        @JsonProperty(value = "TokenExpiryMMYY")
        String tokenExpiry;
        @JsonProperty(value = "BIN")
        String binRange;
        @JsonProperty(value = "AMOPID")
        String amopID;
        @JsonProperty(value = "AMOPRedirectURL")
        String amopRedirectURL;
        @JsonProperty(value = "AMOPCompleteURL")
        String amopCompleteURL;
        @JsonProperty(value = "SCATransRef")
        String scaReference;
        @JsonProperty(value = "CardFraudInfo")
        CardFraudInfo cardFraudInfo;
        @JsonProperty(value = "PaymentRequestBaseURL")
        String paymentRequestBaseURL;
        @JsonProperty(value = "PaymentRequestURL")
        String paymentRequestURL;
        @JsonProperty(value = "DGWTrxId")
        String dgwTrxId;
        @JsonProperty(value = "IssuerId")
        String issuerId;
        @JsonProperty(value = "BankTerminalId")
        String bankTerminalId;
        @JsonProperty(value = "BankMerchantId")
        String bankMerchantId;
        @JsonProperty(value = "DataTime")
        String dataTime;
        @JsonProperty(value = "AcquirerName")
        String acquirerName;

    }
    @Data
    public static class CardFraudInfo {
        @JsonProperty(value = "Decision")
        String decision;
        @JsonProperty(value = "RequestId")
        String requestId;
        @JsonProperty(value = "ReasonCode")
        String reasonCode;
        @JsonProperty(value = "RequestToken")
        String requestToken;
    }
}
