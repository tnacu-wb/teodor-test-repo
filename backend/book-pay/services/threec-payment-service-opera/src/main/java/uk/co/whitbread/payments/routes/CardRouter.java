package uk.co.whitbread.payments.routes;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import uk.co.whitbread.payments.handlers.CardHandler;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.SaveCardRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
public class CardRouter {
  private static final String CARD_PATH = "/payments/cards";
  private static final String IFRAME_PATH = "/iframe";

  @RouterOperations({
      @RouterOperation(method = RequestMethod.POST, path = CARD_PATH + IFRAME_PATH, operation = @Operation(tags = {
          "Create iframe"},
          requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = SaveCardRequest.class))),
          operationId = "createIframe", summary = "Creates a payment resource for saving the card and initiates a session with Planet, returning an iframe in response.",
          responses = {@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
          }))
  })
  @Bean
  public RouterFunction<ServerResponse> cardRoutes(CardHandler cardHandler) {
    return RouterFunctions
        .nest(path(CARD_PATH),
            route(POST(IFRAME_PATH).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), cardHandler::saveCard));
  }
}
