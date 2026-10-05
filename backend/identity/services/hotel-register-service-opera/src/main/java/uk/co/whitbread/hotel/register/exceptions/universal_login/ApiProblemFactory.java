package uk.co.whitbread.hotel.register.exceptions.universal_login;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

/**
 * Factory class for creating standardized ProblemDetail responses for Universal Login.
 * Centralizes error response creation to ensure consistency across all exception handlers.
 */
public final class ApiProblemFactory {

    private static final URI WB_ERROR_TYPE_DETAILS = URI.create(
            "https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/4077617174/Digital+error+codes"
    );

    // Utility class - prevent instantiation
    private ApiProblemFactory() {}

    /**
     * Creates a ProblemDetail response with the specified error code and detail message.
     *
     * @param errorCode the error code enum containing status, title, and code
     * @param detail    the detailed error message
     * @return ResponseEntity containing the ProblemDetail
     */
    public static ResponseEntity<ProblemDetail> createProblem(
            UniversalLoginErrorCode errorCode,
            String detail) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                errorCode.getHttpStatus(),
                detail
        );
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setType(WB_ERROR_TYPE_DETAILS);
        problemDetail.setProperty("errorCode", errorCode.getCode());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(problemDetail);
    }

    /**
     * Creates a validation ProblemDetail response with a list of validation error details.
     *
     * @param errorCode the error code enum containing status, title, and code
     * @param details   list of validation error messages
     * @return ResponseEntity containing the ProblemDetail
     */
    public static ResponseEntity<ProblemDetail> createValidationProblem(
            UniversalLoginErrorCode errorCode,
            List<String> details) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getHttpStatus());
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setType(WB_ERROR_TYPE_DETAILS);
        problemDetail.setProperty("errorCode", errorCode.getCode());
        problemDetail.setProperty("details", details);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(problemDetail);
    }
}