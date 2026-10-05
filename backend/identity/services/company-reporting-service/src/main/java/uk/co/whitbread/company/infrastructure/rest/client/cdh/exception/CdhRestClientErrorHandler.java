package uk.co.whitbread.company.infrastructure.rest.client.cdh.exception;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.ResponseSpec;

public class CdhRestClientErrorHandler implements ResponseSpec.ErrorHandler {

  public void handle(HttpRequest request, ClientHttpResponse response) {
    throw new CDHException("Error while trying to get MI Report from CDH");
  }

}
