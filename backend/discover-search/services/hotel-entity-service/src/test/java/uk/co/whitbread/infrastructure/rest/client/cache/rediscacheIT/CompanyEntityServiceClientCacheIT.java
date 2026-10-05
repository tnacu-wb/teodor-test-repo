package uk.co.whitbread.infrastructure.rest.client.cache.rediscacheIT;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.companyentity.generated.models.CompanyResponseDto;
import uk.co.whitbread.infrastructure.rest.client.CompanyEntityServiceClient;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {CompanyEntityServiceClientCacheTestConfig.class, CompanyEntityServiceClient.class})
class CompanyEntityServiceClientCacheIT {

  private static final String COMPANY_ID = "COMPANY_123";
  private static final String OTHER_COMPANY_ID = "COMPANY_456";

  @Autowired
  private CompanyEntityServiceClient companyEntityServiceClient;

  @Autowired
  private WebClient companyEntityServiceWebClient;

  @Autowired
  @Qualifier("cacheManager24Hours")
  private CacheManager cacheManager24Hours;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @BeforeEach
  void setUp() {
    var cache = cacheManager24Hours.getCache("CompanyByIdCache");
    if (cache != null) {
      cache.clear();
    }

    reset(companyEntityServiceWebClient);

    WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(companyEntityServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyResponseDto.class))
        .thenReturn(Mono.just(new CompanyResponseDto()));
  }

  @Disabled("Flaky: embedded Redis cache intermittently not working in CI")
  @Test
  void getCompanyById_ShouldReturnCachedResult_OnSecondCallWithSameId() {
    companyEntityServiceClient.getCompanyById(COMPANY_ID);
    companyEntityServiceClient.getCompanyById(COMPANY_ID);

    verify(companyEntityServiceWebClient, times(1)).get();
  }

  @Test
  void getCompanyById_ShouldCallClientAgain_WhenCalledWithDifferentId() {
    companyEntityServiceClient.getCompanyById(COMPANY_ID);
    companyEntityServiceClient.getCompanyById(OTHER_COMPANY_ID);

    verify(companyEntityServiceWebClient, times(2)).get();
  }
}
