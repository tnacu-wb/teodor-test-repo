package uk.co.whitbread.payments.routes;

import io.swagger.v3.oas.annotations.Operation;
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
import uk.co.whitbread.payments.handlers.ReconciliationHandler;
import uk.co.whitbread.payments.model.ReconciliationRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class ReconciliationRouter {

    private static final String RECONCILE_PATH = "/reconcile";

    @RouterOperations({
            @RouterOperation(method = RequestMethod.POST, path = RECONCILE_PATH, operation = @Operation(tags = {"Reconciliation"},
                    operationId = "reconcileSite", summary = "Request to start reconciliation process for a site.",
                    requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ReconciliationRequest.class), mediaType = APPLICATION_JSON_VALUE)),
                    responses = {@ApiResponse(responseCode = "200", description = "Successful operation")}))
    })
    @Bean
    public RouterFunction<ServerResponse> reconciliationRoutes(ReconciliationHandler reconciliationHandler) {
        return RouterFunctions.route(POST(RECONCILE_PATH).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), reconciliationHandler::reconcile);
    }
}
