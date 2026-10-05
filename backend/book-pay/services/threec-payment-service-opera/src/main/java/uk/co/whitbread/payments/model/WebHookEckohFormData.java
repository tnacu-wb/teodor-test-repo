package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebHookEckohFormData {
    @JsonProperty(value = "result")
    private String result;
    @JsonProperty(value = "result_code")
    private String resultCode;
    @JsonProperty(value = "payment_id")
    private String paymentId;
    @JsonProperty(value = "masked_pan")
    private String maskedPan;
    @JsonProperty(value = "expiry")
    private String expiryDate;
    @JsonProperty(value = "scheme")
    private String scheme;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "reference")
    private String reference;
    @JsonProperty(value = "token")
    private String token;
    @JsonProperty(value = "exception")
    private EckohWebhookError exception;

    @Data
    public static class EckohWebhookError {
        @JsonProperty(value = "message")
        private String message;
        @JsonProperty(value = "code")
        private String code;
    }
}
