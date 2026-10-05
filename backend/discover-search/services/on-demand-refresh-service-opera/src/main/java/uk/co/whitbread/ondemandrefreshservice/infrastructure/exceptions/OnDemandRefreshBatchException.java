package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OnDemandRefreshBatchException extends RuntimeException {

    private HttpStatus status;

    public OnDemandRefreshBatchException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public OnDemandRefreshBatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnDemandRefreshBatchException(String message) {
        super(message);
    }

}
