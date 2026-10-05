package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *     "Response": {
 *         "Type": "payreversebytxid",
 *         "Version": "W2MXG520",
 *         "Params": {
 *             "TxID": "D717F214-FECA-4984-9258-F568D0301A04",
 *             "BankAuthCode": "120329",
 *             "RedirectURL": "NONE",
 *             "AmountUsed": "2100",
 *             "Amount": "2100",
 *             "CurrencyUsed": "GBP",
 *             "Currency": "",
 *             "RequesterTransRefNum": "125ae59b-9404-42f4-a433-80772c31344b",
 *             "ResultReason": "Success",
 *             "TxState": "CR",
 *             "TxStateText": "Capture Reversed (no capture submitted to bank)",
 *             "CVV2Result": "N",
 *             "AVSResult": "U",
 *             "Result": "0",
 *             "CardNumberLast4": "1103",
 *             "CardSchemeId": "VS",
 *             "CardSchemeName": "VISA",
 *             "CardExpiryDateMMYY": "1026",
 *             "Token": "4216333880397891103",
 *             "TokenExpiryMMYY": "",
 *             "BIN": "411111",
 *             "AMOPID": "",
 *             "AMOPRedirectURL": "",
 *             "AMOPCompleteURL": "",
 *             "SCATransRef": "V0202106021203293UGA",
 *             "CardFraudInfo": {
 *                 "Decision": "",
 *                 "RequestId": "",
 *                 "ReasonCode": "",
 *                 "RequestToken": ""
 *             },
 *             "PaymentRequestBaseURL": "",
 *             "PaymentRequestURL": "",
 *             "DGWTrxId": "",
 *             "IssuerId": ""
 *         }
 *     }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReverseByTransactionIdResponse {

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
    }
}
