package uk.co.whitbread.digitalkey.infrastructure.rest.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CustomTestResponseSpec implements WebClient.ResponseSpec {

  public abstract HttpStatus getStatus();

  public WebClient.ResponseSpec onStatus(
      Predicate<HttpStatusCode> statusPredicate,
      Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction) {
    if (statusPredicate.test(this.getStatus())) {
      exceptionFunction.apply(ClientResponse.create(HttpStatus.OK).build()).block();
    }
    return this;
  }

}
