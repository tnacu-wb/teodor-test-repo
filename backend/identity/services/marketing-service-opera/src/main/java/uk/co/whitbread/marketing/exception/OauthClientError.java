package uk.co.whitbread.marketing.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthClientError {

    private int status;
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    @JsonProperty("error_codes")
    private long[] errorCodes;
    private String timestamp;
    @JsonProperty("trace_id")
    private String traceId;
    @JsonProperty("correlation_id")
    private String correlationId;
    @JsonProperty("error_uri")
    private String errorUri;
    public OauthClientError(int status, String error) {
        this.status = status;
        this.error = error;
    }
}
