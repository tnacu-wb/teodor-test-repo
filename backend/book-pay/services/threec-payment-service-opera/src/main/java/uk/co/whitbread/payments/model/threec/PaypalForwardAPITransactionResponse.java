package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaypalForwardAPITransactionResponse {
    @JsonProperty(value = "status")
    Long status;
    @JsonProperty(value = "headers")
    private Headers headers;
    @JsonProperty(value = "body")
    String body;
    @JsonProperty(value = "requestTime")
    Long requestTime;
    @Data
    public static class Headers {
        @JsonProperty(value = "cacheControl")
        String cacheControl;
        @JsonProperty(value = "pragma")
        String pragma;
        @JsonProperty(value = "contentType")
        String contentType;
        @JsonProperty(value = "expires")
        String expires;
        @JsonProperty(value = "date")
        String date;
        @JsonProperty(value = "connection")
        String connection;
        @JsonProperty(value = "contentLength")
        String contentLength;
        @JsonProperty(value = "setCookie")
        List<String> setCookie;
    }
}
