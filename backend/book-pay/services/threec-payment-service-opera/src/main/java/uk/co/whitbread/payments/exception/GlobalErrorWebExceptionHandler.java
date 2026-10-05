package uk.co.whitbread.payments.exception;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static uk.co.whitbread.payments.exception.ErrorCodes.THREEC_TIMEOUT;
import static uk.co.whitbread.payments.exception.ErrorCodes.UNKNOWN_ERROR;

enum handleErrorTypes {
    SERVICEUNAVAILABLE("ServiceUnavailable"),
    PAYMENTSERVICEEXCEPTION("PaymentServiceException"),
    READTIMEOUTEXCEPTION("ReadTimeoutException"),
    WRITETIMEOUTEXCEPTION("WriteTimeoutException"),
    CONNECTTIMEOUTEXCEPTION("ConnectTimeoutException"),
    INTERNALSERVERERROR("InternalServerError"),
    NULLPOINTEREXCEPTION("NullPointerException"),
    UNKNOWNEXCEPTION("UnknownException");

    public final String value;
    private handleErrorTypes(String value) {
        this.value = value;
    }

}

@Component
@Order(-2)
@Slf4j
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred.";
    private static final String TIMEOUT_ERROR_MESSAGE = "Timeout occurred while connecting to 3C.";

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resourceProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        var options = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.STACK_TRACE);
        var errorPropertiesMap = getErrorAttributes(request, options);
        var exception = super.getError(request);
        var message = (String) errorPropertiesMap.get("message");
        GenericErrorResponse genericErrorResponse = GenericErrorResponse
                .builder()
                .errorCode(deriveErrorCode(exception))
                .message(isNotEmpty(message) ? message : (isNotEmpty(exception.getMessage()) ? exception.getMessage() : DEFAULT_ERROR_MESSAGE))
                .path((String) errorPropertiesMap.get("path"))
                .requestId((String) errorPropertiesMap.get("requestId"))
                .timestamp(errorPropertiesMap.get("timestamp").toString())
                .build();

        if (exception instanceof TimeoutException || exception instanceof ConnectTimeoutException) {
            genericErrorResponse.setMessage(TIMEOUT_ERROR_MESSAGE);
        }
        log.error("Returning response {} for request with path {}.", errorPropertiesMap, request.path());
        return ServerResponse.status(deriveHttpStatus(exception))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(genericErrorResponse));
    }

    private HttpStatus deriveHttpStatus(Throwable exception) {
        if (exception instanceof PaymentServiceException) {
            return HttpStatus.valueOf(((PaymentServiceException) exception).getStatusCode().value());
        } else {
            return INTERNAL_SERVER_ERROR;
        }
    }

    private String deriveErrorCode(Throwable exception) {
        handleErrorTypes classType = handleErrorTypes.UNKNOWNEXCEPTION;
        if(EnumUtils.isValidEnum(handleErrorTypes.class, exception.getClass().getSimpleName().toUpperCase())){
            classType = handleErrorTypes.valueOf(exception.getClass().getSimpleName().toUpperCase());
        }
        switch (classType) {
            case PAYMENTSERVICEEXCEPTION:
                return ((PaymentServiceException) exception).getErrorCode().getErrorCode();
            case READTIMEOUTEXCEPTION:
            case WRITETIMEOUTEXCEPTION:
            case CONNECTTIMEOUTEXCEPTION:
            case SERVICEUNAVAILABLE:
                return THREEC_TIMEOUT.getErrorCode();
            case UNKNOWNEXCEPTION:
            default:
                return UNKNOWN_ERROR.getErrorCode();
        }
    }
}