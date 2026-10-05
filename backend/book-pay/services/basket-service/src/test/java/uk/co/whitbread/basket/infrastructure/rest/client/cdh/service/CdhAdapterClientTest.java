package uk.co.whitbread.basket.infrastructure.rest.client.cdh.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchCriteriaDto;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CdhAdapterClientTest {

    @InjectMocks
    private CdhAdapterClient cdhAdapterClient;
    @Mock
    private CdhAdapterProperties cdhAdapterProperties;
    @Mock
    private WebClient webClient;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Test
    void testSearchCompanies_success() {
        when(cdhAdapterProperties.getCompaniesSearchEndpoint()).thenReturn("cdhUrl");
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("cdhUrl")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono((Class<Object>) any())).thenReturn(Mono.empty());

        cdhAdapterClient.searchCompanies(new CompanySearchCriteriaDto());

        verifyNoMoreInteractions(webClient);
    }

}