package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentProviderTransactionRequest {

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
        @JsonProperty(value = "RequesterTransRefNum")
        private String transactionReference;
    }
}
