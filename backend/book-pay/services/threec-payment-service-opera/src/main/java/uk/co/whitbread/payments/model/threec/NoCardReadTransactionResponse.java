package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *     "Response": {
 *         "Type": "payrequestnocardread",
 *         "Version": "W2MXG520",
 *         "Params": {
 *             "TxID": "C6612357-3674-40AB-9835-3217F685FE88",
 *             "BankAuthCode": "",
 *             "RedirectURL": "",
 *             "AmountUsed": "10",
 *             "Amount": "10",
 *             "CurrencyUsed": "GBP",
 *             "Currency": "",
 *             "RequesterTransRefNum": "test11118",
 *             "ResultReason": "APPROVED FOR PARTIAL AMOUNT",
 *             "TxState": "CQ",
 *             "TxStateText": "Capture Queued",
 *             "CVV2Result": "",
 *             "AVSResult": "U",
 *             "Result": "10",
 *             "CardNumberLast4": "4242",
 *             "CardSchemeId": "VS",
 *             "CardSchemeName": "VISA",
 *             "CardExpiryDateMMYY": "1221",
 *             "Token": "4943056398164344242",
 *             "TokenExpiryMMYY": "1221",
 *             "BIN": "424242",
 *             "AMOPID": "",
 *             "AMOPRedirectURL": "",
 *             "AMOPCompleteURL": "",
 *             "SCATransRef": "",
 *             "SCAExemptionInd": "",
 *             "CardFraudInfo": {
 *                 "Decision": "",
 *                 "RequestId": "",
 *                 "ReasonCode": "",
 *                 "RequestToken": ""
 *             },
 *             "PaymentRequestBaseURL": "",
 *             "PaymentRequestURL": "",
 *             "DGWTrxId": ""
 *         }
 *     }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoCardReadTransactionResponse {

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
        @JsonProperty(value = "AmountUsed")
        private String amountUsed;
        @JsonProperty(value = "Amount")
        private String amount;
        @JsonProperty(value = "currencyUsed")
        private String currency;
        @JsonProperty(value = "RequesterTransRefNum")
        private String transactionReference;
        @JsonProperty(value = "ResultReason")
        private String reason;
        @JsonProperty(value = "TxState")
        private String transactionState;
        @JsonProperty(value = "TxStateText")
        private String transactionStateText;
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
        @JsonProperty(value = "BIN")
        private String binRange;
        @JsonProperty(value = "SCATransRef")
        private String scaReference;
        @JsonProperty(value = "CardFraudInfo")
        private FraudResponse fraudResponse;
        @JsonProperty("RedirectURL")
        private String redirectUrl;

        @JsonProperty("CurrencyUsed")
        private String currencyUsed;

        @JsonProperty("UserData2")
        private String userData2;

        @JsonProperty("CVV2Result")
        private String cvv2Result;

        @JsonProperty("AcquirerActionCode")
        private String acquirerActionCode;

        @JsonProperty("COFIndicator")
        private String cofIndicator;

        @JsonProperty("AMOPID")
        private String amopId;

        @JsonProperty("AMOPRedirectURL")
        private String amopRedirectUrl;

        @JsonProperty("AMOPCompleteURL")
        private String amopCompleteUrl;

        @JsonProperty("TokenExpiryMMYY")
        private String tokenExpiry;

        @JsonProperty("IssuerId")
        private String issuerId;

        @JsonProperty("BankTerminalId")
        private String bankTerminalId;

        @JsonProperty("BankMerchantId")
        private String bankMerchantId;

        @JsonProperty("DataTime")
        private String dataTime;

        @JsonProperty("AcquirerName")
        private String acquirerName;

        @JsonProperty("PaymentRequestBaseURL")
        private String paymentRequestBaseUrl;

        @JsonProperty("PaymentRequestURL")
        private String paymentRequestUrl;

        @JsonProperty("DGWTrxId")
        private String dgwTrxId;
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
