package uk.co.whitbread.shared.cdh.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.whitbread.common.exceptions.http.MALHttpException;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OauthClientException extends RuntimeException implements MALHttpException {
    private int status;

    private String message;

    private String errorCode;
}
