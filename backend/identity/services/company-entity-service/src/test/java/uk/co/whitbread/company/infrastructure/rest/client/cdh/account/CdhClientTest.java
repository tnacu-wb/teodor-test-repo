package uk.co.whitbread.company.infrastructure.rest.client.cdh.account;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.exceptions.CdhCompaniesException;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.config.CdhProperties;

@ExtendWith(MockitoExtension.class)
class CdhClientTest {

  @Mock
  private CdhProperties cdhProperties;

  @Test
  void getCompanies__HappyPath() {
    when(cdhProperties.getCompaniesEndpoint()).thenReturn("/companies");
    var cdhClient = new CdhClient(buildWebClient(HttpStatus.OK,
        "{\"companies\":[],\"totalResults\":0,\"hasMore\":false}", MediaType.APPLICATION_JSON),
        cdhProperties);

    var result = cdhClient.getCompanies(getCompaniesSearchRequest());
    assertThat(result).isNotNull();
  }

  @Test
  void getCompanies__ErrorStatusWithUnparseableBody_WrapsAsCdhCompaniesException() {
    when(cdhProperties.getCompaniesEndpoint()).thenReturn("/companies");
    var cdhClient = new CdhClient(buildWebClient(HttpStatus.BAD_GATEWAY,
        "<html>Bad gateway</html>", MediaType.TEXT_HTML), cdhProperties);
    var companiesSearchRequest = getCompaniesSearchRequest();
    var thrownException = assertThrows(CdhCompaniesException.class,
        () -> cdhClient.getCompanies(companiesSearchRequest));

    assertThat(thrownException.getDebugMessage()).contains("Error parsing response:");
    assertThat(thrownException.getErrorCode()).isEqualTo(502);
  }

  @Test
  void getCompanies__ErrorStatusWithJsonButIncompatibleSchema_WrapsAsCdhCompaniesException() {
    when(cdhProperties.getCompaniesEndpoint()).thenReturn("/companies");
    var cdhClient = new CdhClient(buildWebClient(HttpStatus.FORBIDDEN,
        "{\"message\":\"forbidden\"}", MediaType.APPLICATION_JSON), cdhProperties);
    var companiesSearchRequest = getCompaniesSearchRequest();
    var thrownException = assertThrows(CdhCompaniesException.class,
        () -> cdhClient.getCompanies(companiesSearchRequest));

    assertThat(thrownException.getDebugMessage()).contains("Error parsing response:");
    assertThat(thrownException.getErrorCode()).isEqualTo(403);
  }

  private CompaniesSearchRequest getCompaniesSearchRequest() {
    return CompaniesSearchRequest.builder()
        .companyName("test")
        .pageSize(50)
        .pageNumber(1)
        .build();
  }

  private WebClient buildWebClient(HttpStatus status, String body, MediaType mediaType) {
    ExchangeFunction exchangeFunction = request -> Mono.just(
        ClientResponse.create(status)
            .header("content-type", mediaType.toString())
            .body(body)
            .build()
    );
    return WebClient.builder().exchangeFunction(exchangeFunction).build();
  }

}
