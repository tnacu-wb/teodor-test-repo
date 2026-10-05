package uk.co.whitbread.payments.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericErrorResponse {
    private String message;
    private String errorCode;
    private String timestamp;
    private String path;
    private String requestId;
}
