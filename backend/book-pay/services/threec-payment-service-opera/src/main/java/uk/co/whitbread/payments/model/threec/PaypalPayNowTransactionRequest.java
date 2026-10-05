package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaypalPayNowTransactionRequest {

    @JsonProperty(value = "merchant_id")
    String merchantId;
    @JsonProperty(value = "payment_method_token")
    String paymentMethodToken;
    @JsonProperty(value = "device_data")
    String deviceData;
    @JsonProperty(value = "tsp")
    private Tsp tsp;
    @JsonProperty(value = "url")
    String url;
    @JsonProperty(value = "method")
    String method;
    @JsonProperty(value = "sensitive_data")
    private SensitiveData sensitiveData;
    @JsonProperty(value = "data")
    private PayNowData data;
    @JsonProperty(value = "config")
    private Config config;
    @Data
    public static class Tsp {
        @JsonProperty(value = "currency_code")
        String currencyCode;
        @JsonProperty(value = "expire_at")
        String expireAt;
    }
    @Data
    public static class SensitiveData {
        @JsonProperty(value = "ValidationID")
        String validationID;
        @JsonProperty(value = "ValidationCode")
        String validationCode;
        @JsonProperty(value = "ValidationCodeHash")
        String validationCodeHas;
    }
    @Data
    public static class PayNowData {
        @JsonProperty(value = "amount")
        String amount;
        @JsonProperty(value = "currency")
        String currency;
    }

    @Data
    public static class Config {
        @JsonProperty(value = "name")
        String name;
        @JsonProperty(value = "methods")
        List<String> methods;
        @JsonProperty(value = "url")
        String url;
        @JsonProperty(value = "request_format")
        private RequestFormat requestFormat;
        @JsonProperty(value = "types")
        List<String> types;
        @JsonProperty(value = "transformations")
        private List<Transformation> transformations;
    }

    @Data
    public static class RequestFormat {
        @JsonProperty(value = "/body")
        String body;
    }

    @Data
    public static class Transformation {
        @JsonProperty(value = "path")
        String path;
        @JsonProperty(value = "value")
        Object value;
    }

}
