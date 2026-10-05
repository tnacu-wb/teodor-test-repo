package uk.co.whitbread.payments.routes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import uk.co.whitbread.payments.exception.GenericErrorResponse;
import uk.co.whitbread.payments.handlers.TokensHandler;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.CreateTokenResponse;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
@RequiredArgsConstructor
public class TokensRouter {

    private static final String TOKENS_PATH = "/tokens";
    private static final String PAYPAL_CLIENT_TOKEN_PAYMENTS_PATH= "/paypal-cltoken";

    private final TokensHandler tokensHandler;

    @RouterOperations({
            @RouterOperation(method = RequestMethod.POST, path = "/tokenise",
                    operation = @Operation(operationId = "tokeniseCard",
                            deprecated = true,
                            tags = {"Tokenisation"},
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = CreateTokenRequest.class), mediaType = APPLICATION_JSON_VALUE)),
                            summary = "Deprecated: This endpoint will be decommissioned, please use POST /token instead. Tokenises a payment card.",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = CreateTokenResponse.class), mediaType = APPLICATION_JSON_VALUE)),
                                    @ApiResponse(responseCode = "400", description = "Error Response", content = @Content(schema = @Schema(implementation = GenericErrorResponse.class), mediaType = APPLICATION_JSON_VALUE)),
                                    @ApiResponse(responseCode = "500", description = "Error Response", content = @Content(schema = @Schema(implementation = GenericErrorResponse.class), mediaType = APPLICATION_JSON_VALUE))
                            })),
            @RouterOperation(method = RequestMethod.POST, path = TOKENS_PATH,
                    operation = @Operation(operationId = "createToken", tags = {"Tokens"},
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = CreateTokenRequest.class), mediaType = APPLICATION_JSON_VALUE)),
                            summary = "Create a token for a payment card.",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = CreateTokenResponse.class), mediaType = APPLICATION_JSON_VALUE)),
                                    @ApiResponse(responseCode = "400", description = "Error Response", content = @Content(schema = @Schema(implementation = GenericErrorResponse.class), mediaType = APPLICATION_JSON_VALUE)),
                                    @ApiResponse(responseCode = "500", description = "Error Response", content = @Content(schema = @Schema(implementation = GenericErrorResponse.class), mediaType = APPLICATION_JSON_VALUE))
                            })),
            @RouterOperation(method = RequestMethod.PUT, path = TOKENS_PATH,
                    operation = @Operation(operationId = "updateToken", tags = {"Tokens"},
                            requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UpdateTokenRequest.class), mediaType = APPLICATION_JSON_VALUE)),
                            summary = "Update a token with cardholder information.",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = UpdateTokenResponse.class), mediaType = APPLICATION_JSON_VALUE)),
                                    @ApiResponse(responseCode = "400", description = "Error Response", content = @Content(schema = @Schema(implementation = GenericErrorResponse.class), mediaType = APPLICATION_JSON_VALUE)),
                                    @ApiResponse(responseCode = "500", description = "Error Response", content = @Content(schema = @Schema(implementation = GenericErrorResponse.class), mediaType = APPLICATION_JSON_VALUE))
                            }))
    })
    @Bean
    public RouterFunction<ServerResponse> tokensRoutes() {
        return RouterFunctions
                .route(POST("/tokenise").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), tokensHandler::createToken)
                .andRoute(POST(TOKENS_PATH).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), tokensHandler::createToken)
                .andRoute(PUT(TOKENS_PATH).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), tokensHandler::updateToken)
                .andRoute(GET(TOKENS_PATH+PAYPAL_CLIENT_TOKEN_PAYMENTS_PATH).and(accept(APPLICATION_JSON)), tokensHandler::createPaypalClientToken);
    }
}
