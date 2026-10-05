package uk.co.whitbread.basket.infrastructure.rest.client.rules.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceSourceType;
import uk.co.whitbread.basket.generated.models.rules.BusinessAllowanceRuleDto;
import uk.co.whitbread.basket.generated.models.rules.BusinessAllowanceRuleResponseDto;
import uk.co.whitbread.basket.generated.models.rules.VatRuleResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.exception.RulesAgentException;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.service.properties.RulesAgentClientProperties;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class RulesAgentClientTest {

  @InjectMocks
  private RulesAgentClient rulesAgentClient;
  @Mock
  private WebClient webClient;
  @Mock
  @SuppressWarnings("rawtypes")
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  @SuppressWarnings("rawtypes")
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @Mock
  private RulesAgentClientProperties rulesAgentClientProperties;
  @Mock
  private UriBuilder uriBuilder;

  @Test
  void getBusinessAllowances_shouldReturnOk() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(BusinessAllowanceRuleResponseDto.class)).thenReturn(mockBusinessAllowanceRuleResponse());

    // Act
    var response = rulesAgentClient.getBusinessAllowances();

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getBusinessAllowances().size());
    assertEquals("BFADBF", response.getBusinessAllowances().get(0).getSourceId());
    assertEquals("11", response.getBusinessAllowances().get(0).getTargetId());
  }

  @Test
  void getBusinessAllowances_shouldReturnException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(BusinessAllowanceRuleResponseDto.class))
        .thenReturn(Mono.error(new RulesAgentException("message",
            "An error was returned calling the Rules service", new Exception(), 1)));

    //Act
    Exception exception = assertThrows(RulesAgentException.class,
        () -> rulesAgentClient.getBusinessAllowances());

    //Assert
    String expectedMessage = "An error was returned calling the Rules service";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void getVatCodes_shouldReturnOk() {
    // Arrange
    var vatCodesEndpoint = "/vat-codes";
    var vatRegion = "GB";
    var packageCodes = List.of("CITYTAX");
    when(rulesAgentClientProperties.getVatCodesEndpoint()).thenReturn(vatCodesEndpoint);
    when(uriBuilder.path(vatCodesEndpoint)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam("vatRegion", vatRegion)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam("pkgCodeArr", packageCodes)).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create("/rules"));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      uriFunction.apply(uriBuilder);
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(VatRuleResponseDto.class)).thenReturn(mockVatRuleResponse());

    // Act
    var response = rulesAgentClient.getVatCodes(vatRegion, packageCodes);

    // Assert
    assertNotNull(response);
    assertEquals("GB", response.getVatRegion());
    verify(uriBuilder).path(vatCodesEndpoint);
    verify(uriBuilder).queryParam("vatRegion", vatRegion);
    verify(uriBuilder).queryParam("pkgCodeArr", packageCodes);
    verify(uriBuilder).build();
  }

  @Test
  void getVatCodes_shouldReturnException() {
    // Arrange
    var vatCodesEndpoint = "/vat-codes";
    var vatRegion = "GB";
    var packageCodes = List.of("CITYTAX");
    when(rulesAgentClientProperties.getVatCodesEndpoint()).thenReturn(vatCodesEndpoint);
    when(uriBuilder.path(vatCodesEndpoint)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam("vatRegion", vatRegion)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam("pkgCodeArr", packageCodes)).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(URI.create("/rules"));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      uriFunction.apply(uriBuilder);
      return requestHeadersSpec;
    });

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    when(responseSpecMock.bodyToMono(VatRuleResponseDto.class))
        .thenReturn(Mono.error(new RulesAgentException("message",
            "An error was returned calling the Rules service", new Exception(), 1)));
    //Act
    Exception exception = assertThrows(RulesAgentException.class,
        () -> rulesAgentClient.getVatCodes(vatRegion, packageCodes));

    //Assert
    String expectedMessage = "An error was returned calling the Rules service";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verify(uriBuilder).path(vatCodesEndpoint);
    verify(uriBuilder).queryParam("vatRegion", vatRegion);
    verify(uriBuilder).queryParam("pkgCodeArr", packageCodes);
    verify(uriBuilder).build();
  }

  private Mono<BusinessAllowanceRuleResponseDto> mockBusinessAllowanceRuleResponse() {

    var businessAllowanceRuleDto = new BusinessAllowanceRuleDto();
    businessAllowanceRuleDto.setPms("OP");
    businessAllowanceRuleDto.setSourceId("BFADBF");
    businessAllowanceRuleDto.setSourceType(BusinessAllowanceSourceType.PACKAGE.name());
    businessAllowanceRuleDto.setTargetId("11");
    businessAllowanceRuleDto.setIsTransactionCode(true);
    businessAllowanceRuleDto.setIsNotesMandatory(true);

    var businessAllowanceRuleResponseDto = new BusinessAllowanceRuleResponseDto();
    businessAllowanceRuleResponseDto.setBusinessAllowances(Collections.singletonList(businessAllowanceRuleDto));

    return Mono.just(businessAllowanceRuleResponseDto);
  }

  private Mono<VatRuleResponseDto> mockVatRuleResponse() {
    var vatRuleResponseDto = new VatRuleResponseDto();
    vatRuleResponseDto.setVatRegion("GB");
    return Mono.just(vatRuleResponseDto);
  }

}
