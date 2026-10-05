package uk.co.whitbread.shared.auth.config.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessDeniedExceptionHandler {

    private final AuthenticatedUserService authenticatedUserService;

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException(): access denied exception caught", e);

        var httpStatus = authenticatedUserService.isUserAuthenticated() ?
            HttpStatus.FORBIDDEN :
            HttpStatus.UNAUTHORIZED;

        var response = ErrorResponse.builder()
            .errCode(httpStatus.value())
            .debugMessage(e.getMessage())
            .globalErrTextTemplate(ErrorCode.UNAUTHORIZED_EXCEPTION.getMessage())
            .build();

        return new ResponseEntity<>(response, httpStatus);
    }

}
