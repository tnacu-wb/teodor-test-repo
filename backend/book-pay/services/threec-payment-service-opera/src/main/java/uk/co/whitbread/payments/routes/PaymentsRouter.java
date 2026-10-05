package uk.co.whitbread.payments.routes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import uk.co.whitbread.payments.handlers.PaymentsHandler;
import uk.co.whitbread.payments.model.*;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
public class PaymentsRouter {

    private static final String PAYMENTS_PATH = "/payments";
    private static final String PAYMENT_PATH_PARAM = "/{paymentId}";
    private static final String WEBHOOK_PATH = "/webhook";
    private static final String TRANSACTIONAL_REFUND_PATH = "/refund";
    private static final String REDIRECT_PATH = "/complete";
    private static final String ECKOH_PATH = "/eckoh";

    @RouterOperations({
            @RouterOperation(method = RequestMethod.POST, path = PAYMENTS_PATH, operation = @Operation(tags = {"Payments"},
                    requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaymentRequest.class))),
                    operationId = "createPayment", summary = "Creates a payment resource.",
                    responses = {@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
                    })),
            @RouterOperation(method = RequestMethod.GET, path = PAYMENTS_PATH + PAYMENT_PATH_PARAM, operation = @Operation(tags = {"Payments"},
                    operationId = "getPayment", summary = "Retrieves a payment resource.",
                    parameters = {@Parameter(in = ParameterIn.PATH, name = "paymentId", description = "Payment resource id"),
                                  @Parameter(in = ParameterIn.QUERY, name= "action", description = "Action", required = false)},
                    responses = {@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
                    })),
            @RouterOperation(method = RequestMethod.POST, path = PAYMENTS_PATH + PAYMENT_PATH_PARAM + WEBHOOK_PATH, operation = @Operation(tags = {"Payments"},
                    parameters = @Parameter(in = ParameterIn.PATH, name = "paymentId", description = "Payment resource id"),
                    requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_FORM_URLENCODED_VALUE, schema = @Schema(implementation = WebHookFormData.class))),
                    operationId = "paymentWebhook", summary = "Webhook from 3CP post iPage submission existing payment resource.",
                    responses = {@ApiResponse(responseCode = "204", description = "Successful operation"),
                    })),
            @RouterOperation(method = RequestMethod.POST, path = PAYMENTS_PATH + ECKOH_PATH + WEBHOOK_PATH, operation = @Operation(tags = {"Payments"},
                    requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_FORM_URLENCODED_VALUE, schema = @Schema(implementation = WebHookEckohFormData.class))),
                    operationId = "paymentWebhook", summary = "Webhook from Eckoh CallGuard service submission existing payment resource.",
                    responses = {@ApiResponse(responseCode = "204", description = "Successful operation"),
                    })),
            @RouterOperation(method = RequestMethod.POST, path = PAYMENTS_PATH + PAYMENT_PATH_PARAM + TRANSACTIONAL_REFUND_PATH, operation = @Operation(tags = {"Payments"},
                    parameters = @Parameter(in = ParameterIn.PATH, name = "paymentId", description = "Payment resource id"),
                    operationId = "transactionRefund", summary = "Transactional refund for existing payment resource.",
                    responses = {@ApiResponse(responseCode = "204", description = "Successful operation"),
                    })),
            @RouterOperation(method = RequestMethod.GET, path = PAYMENTS_PATH + PAYMENT_PATH_PARAM + REDIRECT_PATH, operation = @Operation(tags = {"Payments"},
                    parameters = @Parameter(in = ParameterIn.PATH, name = "paymentId", description = "Payment resource id"),
                    operationId = "paymentRedirect", summary = "Redirect from iPage existing payment resource.",
                    responses = {@ApiResponse(responseCode = "200", description = "Successful operation"),
                    })),
            @RouterOperation(method = RequestMethod.PUT, path = PAYMENTS_PATH, operation = @Operation(tags = {"Payments"},
                    requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateBookingReferenceRequest.class))),
                    operationId = "updateBookingReference", summary = "Updates booking reference for Payments.",
                    responses = {@ApiResponse(responseCode = "202", description = "Successful operation")
                    }))
    })
    @Bean
    public RouterFunction<ServerResponse> paymentsRoutes(PaymentsHandler paymentsHandler) {
        return RouterFunctions
                .nest(path(PAYMENTS_PATH),
                        route(POST("").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), paymentsHandler::createPayment)
                                .andRoute(GET(PAYMENT_PATH_PARAM).and(accept(APPLICATION_JSON)), paymentsHandler::getPayment)
                                .andRoute(POST(PAYMENT_PATH_PARAM + WEBHOOK_PATH).and(contentType(MediaType.APPLICATION_FORM_URLENCODED)), paymentsHandler::webhook)
                                .andRoute(POST(ECKOH_PATH + WEBHOOK_PATH).and(contentType(MediaType.APPLICATION_JSON)), paymentsHandler::webhookEckoh)
                                .andRoute(POST(PAYMENT_PATH_PARAM + TRANSACTIONAL_REFUND_PATH), paymentsHandler::transactionalRefund)
                                .andRoute(GET(PAYMENT_PATH_PARAM + REDIRECT_PATH), paymentsHandler::redirect)
                                .andRoute(PUT("").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), paymentsHandler::updateBookingReference));
    }
}
