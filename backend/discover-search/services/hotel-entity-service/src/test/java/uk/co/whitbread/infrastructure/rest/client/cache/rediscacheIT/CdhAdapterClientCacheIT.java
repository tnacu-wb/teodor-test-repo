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
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchCriteriaDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySuppressRatesDto;
import uk.co.whitbread.infrastructure.rest.client.CdhAdapterClient;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {CdhAdapterClientCacheTestConfig.class, CdhAdapterClient.class})
class CdhAdapterClientCacheIT {

  private static final String COMPANY_ID = "COMP123";
  private static final String OTHER_COMPANY_ID = "COMP456";
  private static final Integer GLOBAL_COMPANY_ID = 1001;
  private static final Integer OTHER_GLOBAL_COMPANY_ID = 2002;

  @Autowired
  private CdhAdapterClient cdhAdapterClient;

  @Autowired
  private WebClient cdhAdapterWebClient;

  @Autowired
  @Qualifier("cacheManager24Hours")
  private CacheManager cacheManager24Hours;

  @Autowired
  @Qualifier("cacheManager5Minutes")
  private CacheManager cacheManager5Minutes;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @BeforeEach
  void setUp() {
    var cache = cacheManager5Minutes.getCache("CompanySuppressRatesCache");
    if (cache != null) {
      cache.clear();
    }
    var searchCache = cacheManager24Hours.getCache("CompanySearchCache");
    if (searchCache != null) {
      searchCache.clear();
    }

    reset(cdhAdapterWebClient);

    WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanySuppressRatesDto.class))
        .thenReturn(Mono.just(new CompanySuppressRatesDto()));

    WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    WebClient.RequestHeadersSpec postRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec postResponseSpec = mock(WebClient.ResponseSpec.class);

    when(cdhAdapterWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), any(Class.class))).thenReturn(postRequestHeadersSpec);
    when(postRequestHeadersSpec.retrieve()).thenReturn(postResponseSpec);
    when(postResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(postResponseSpec);
    when(postResponseSpec.bodyToMono(CompanySearchResponseDto.class))
        .thenReturn(Mono.just(new CompanySearchResponseDto()));
  }

  @Disabled("Flaky: embedded Redis cache intermittently not working in CI")
  @Test
  void getCompanySuppressRates_ShouldReturnCachedResult_OnSecondCallWithSameCompanyId() {
    cdhAdapterClient.getCompanySuppressRates(COMPANY_ID);
    cdhAdapterClient.getCompanySuppressRates(COMPANY_ID);

    verify(cdhAdapterWebClient, times(1)).get();
  }

  @Test
  void getCompanySuppressRates_ShouldCallClientAgain_WhenCalledWithDifferentCompanyId() {
    cdhAdapterClient.getCompanySuppressRates(COMPANY_ID);
    cdhAdapterClient.getCompanySuppressRates(OTHER_COMPANY_ID);

    verify(cdhAdapterWebClient, times(2)).get();
  }

  @Test
  void searchCompanies_ShouldReturnCachedResult_OnSecondCallWithSameGlobalCompanyId() {
    CompanySearchCriteriaDto request = new CompanySearchCriteriaDto("BB_CCUI", "hotel-entity-service")
        .globalCompanyId(GLOBAL_COMPANY_ID);

    cdhAdapterClient.searchCompanies(request);
    cdhAdapterClient.searchCompanies(request);

    verify(cdhAdapterWebClient, times(1)).post();
  }

  @Test
  void searchCompanies_ShouldCallClientAgain_WhenCalledWithDifferentGlobalCompanyId() {
    CompanySearchCriteriaDto request1 = new CompanySearchCriteriaDto("BB_CCUI", "hotel-entity-service")
        .globalCompanyId(GLOBAL_COMPANY_ID);
    CompanySearchCriteriaDto request2 = new CompanySearchCriteriaDto("BB_CCUI", "hotel-entity-service")
        .globalCompanyId(OTHER_GLOBAL_COMPANY_ID);

    cdhAdapterClient.searchCompanies(request1);
    cdhAdapterClient.searchCompanies(request2);

    verify(cdhAdapterWebClient, times(2)).post();
  }
}
