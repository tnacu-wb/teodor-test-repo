package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebHookFormData {
    @JsonProperty(value = "ref")
    private String merchantReference;
    @JsonProperty(value = "TxID")
    private String transactionId;
    @JsonProperty(value = "AuthorisationCode")
    private String authorisationCode;
    @JsonProperty(value = "CardNumberFirst6")
    private String first6Digits;
    @JsonProperty(value = "card_pan_last4digits")
    private String last4Digits;
    @JsonProperty(value = "SCATransRef")
    private String scaTransReference;
    @JsonProperty(value = "TokenNo")
    private String tokenNo;
    @JsonProperty(value = "3DSIndicator")
    private String threeDSIndicator;
    @JsonProperty(value = "CardType")
    private String cardType;
    @JsonProperty(value = "CardTypeName")
    private String cardTypeName;
    @JsonProperty(value = "CardExpiry")
    private String cardExpiry;
    @JsonProperty(value = "TokenExpiry")
    private String tokenExpiry;
    @JsonProperty(value = "FirstName")
    private String firstName;
    @JsonProperty(value = "LastName")
    private String lastName;
    @JsonProperty(value = "TxState")
    private String txState;
    @JsonProperty(value = "ReturnCode")
    private String returnCode;
    @JsonProperty(value = "fraud_check_decision")
    private String fraudCheckDecision;
    @JsonProperty(value = "fraud_check_result")
    private String fraudCheckResult;
    @JsonProperty(value = "fraud_check_result_reason")
    private String fraudCheckResultReason;
}
