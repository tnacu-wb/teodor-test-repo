package uk.co.whitbread.basket.infrastructure.rest.client;

import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.generated.models.payments.GenericErrorResponseDto;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

public abstract class CustomTestResponsePaypal0Spec implements WebClient.ResponseSpec {

  public abstract HttpStatus getStatus();

  @NotNull
  public WebClient.ResponseSpec onStatus(Predicate<HttpStatusCode> statusPredicate,
      @NotNull Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction) {

    if (statusPredicate.test(this.getStatus())) {
      GenericErrorResponseDto errorResponse = new GenericErrorResponseDto();
      errorResponse.setErrorCode("PAYPAL0");
      errorResponse.setMessage(
          "paypal customer creation is unsuccessful: Unknown or expired payment_method_nonce.: 61781766425D");

      DataBuffer dataBuffer;
      try {
        dataBuffer = new DefaultDataBufferFactory().wrap(new ObjectMapper().writeValueAsBytes(errorResponse));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      ClientResponse clientResponse = ClientResponse.create(this.getStatus())
          .header("Content-Type", "application/json")
          .body(Flux.just(dataBuffer))
          .build();
      exceptionFunction.apply(clientResponse).block();
    }

    return this;
  }
}