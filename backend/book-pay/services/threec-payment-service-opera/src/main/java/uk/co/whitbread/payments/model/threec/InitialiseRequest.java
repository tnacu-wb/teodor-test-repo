package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InitialiseRequest {

    @JsonProperty(value = "security_emerchant_id")
    String securityEMerchantId;
    @JsonProperty(value = "security_validation_code")
    String securityValidationCode;
    @JsonProperty(value = "trx_merchant_reference")
    String trxMerchantReference;
    @JsonProperty(value = "trx_amount_currency_code")
    String trxAmountCurrencyCode;
    @JsonProperty(value = "trx_amount_value")
    String trxAmountValue;
    @JsonProperty(value = "trx_authentication_amount_value")
    String trxAuthenticationAmountValue;
    @JsonProperty(value = "template_id")
    String templateId;
    @JsonProperty(value = "posturl_success")
    String postUrlSuccess;
    @JsonProperty(value = "posturl_failure")
    String postUrlFailure;
    @JsonProperty(value = "redirect_approved")
    String redirectApproved;
    @JsonProperty(value = "redirect_declined")
    String redirectDeclined;
    @JsonProperty(value = "service_action")
    String serviceAction;
    @JsonProperty(value = "trx_options")
    String trxOptions;
    @JsonProperty(value = "fraud_mode")
    String fraudMode;
    @JsonProperty(value = "card_holder_address_line_1")
    String cardholderAddressLine1;
    @JsonProperty(value = "card_holder_address_line_2")
    String cardholderAddressLine2;
    @JsonProperty(value = "card_holder_address_line_3")
    String cardholderAddressLine3;
    @JsonProperty(value = "card_holder_address_line_4")
    String cardholderAddressLine4;
    @JsonProperty(value = "card_holder_address_city")
    String cardholderAddressCity;
    @JsonProperty(value = "card_holder_address_state")
    String cardholderAddressState;
    @JsonProperty(value = "card_holder_address_postal_code")
    String cardholderAddressPostalCode;
    @JsonProperty(value = "card_holder_address_country")
    String cardholderAddressCountry;
    @JsonProperty(value = "card_holder_first_name")
    String cardholderFirstName;
    @JsonProperty(value = "card_holder_last_name")
    String cardholderLastName;
    @JsonProperty(value = "card_holder_email")
    String cardholderEmail;
    @JsonProperty(value = "card_holder_telephone")
    String cardholderTelephone;
    @JsonProperty(value = "content_language")
    String language;
    @JsonProperty(value = "cof_indicator")
    String cardOnFileIndicator;
    @JsonProperty(value = "token_no")
    String token;
    @JsonProperty(value = "token_expiry_mm")
    String cardExpiryMonth;
    @JsonProperty(value = "token_expiry_yy")
    String cardExpiryYear;
    @JsonProperty(value = "token_injection_action")
    String tokenInjectionAction;
    @JsonProperty(value = "fraud_check_data")
    FraudCheckData fraudCheckData;
    @JsonProperty(value = "user_data_4")
    String walletType;
}
