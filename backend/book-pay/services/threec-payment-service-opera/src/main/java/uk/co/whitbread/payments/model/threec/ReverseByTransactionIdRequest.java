package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * {
 *     "Request": {
 *         "Type": "payreversebytxid",
 *         "Version": "W2MXG520",
 *         "Credentials":{
 *             "ValidationID" : "*",
 *             "ValidationCode" : "*"
 *         },
 *         "Params": {
 *             "TxID": "{0}"
 *         }
 *     }
 * }
 */
@Data
public class ReverseByTransactionIdRequest {

    @JsonProperty(value = "Request")
    private Request request;

    @Data
    @Builder
    public static class Request {
        @JsonProperty(value = "Type")
        private final String TYPE = "payreversebytxid";
        @JsonProperty(value = "Version")
        String version;
        @JsonProperty(value = "Credentials")
        private Credentials credentials;
    }

    @Data
    @Builder
    public static class Credentials {
        @JsonProperty(value = "ValidationID")
        private String validationId;
        @JsonProperty(value = "ValidationCode")
        private String validationCode;
    }
}
