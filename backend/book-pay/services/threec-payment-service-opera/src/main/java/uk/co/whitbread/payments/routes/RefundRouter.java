package uk.co.whitbread.payments.routes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import uk.co.whitbread.payments.handlers.RefundHandler;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.RefundResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RefundRouter {

    private static final String REFUND_PATH = "/refunds";


    @RouterOperations({
            @RouterOperation(method = RequestMethod.POST, path = REFUND_PATH, operation = @Operation(tags = {"Refund"},
                    operationId = "refund", summary = "Request to process refund.",
                    requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = RefundRequest.class), mediaType = APPLICATION_JSON_VALUE)),
                    responses = {@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RefundResponse.class))))}))
    })
    @Bean
    public RouterFunction<ServerResponse> refundRoutes(RefundHandler refundHandler) {
        return RouterFunctions.route(POST(REFUND_PATH).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), refundHandler::refund);
    }
}
