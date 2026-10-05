package uk.co.whitbread.company.infrastructure.rest.client.ohip.profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.company.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.exceptions.CompanyServiceException;
import uk.co.whitbread.company.infrastructure.rest.utils.CustomTestResponseSpec;
import uk.co.whitbread.ohip.generated.models.CompaniesProfileDto;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;
import uk.co.whitbread.ohip.generated.models.NegotiatedRatesResponseDto;

@ExtendWith(MockitoExtension.class)
class OhipProfileClientTest {

  @Mock
  private WebClient webClientMock;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpecMock;
  @Mock
  private WebClient.ResponseSpec responseSpecMock;
  @InjectMocks
  private OhipProfileClient ohipProfileClient;

  @Test
  void getCompaniesProfile__HappyPath() {
    //Arrange
    when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(CompaniesProfileDto.class)).thenReturn(
        Mono.just(new CompaniesProfileDto()));
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    //Act
    var companiesProfileResult = ohipProfileClient.getCompaniesProfile("Test", "AR-123", "WB", 50);
    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(new CompaniesProfileDto());
  }

  @Test
  void getCompaniesProfile__CompanyServiceException() {
    //Arrange
    when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(CompaniesProfileDto.class)).thenReturn(
        Mono.error(
            new CompanyServiceException("message", "debug message", new Exception(), 100)));

    //Act & Assert
    var thrownException = assertThrows(CompanyServiceException.class,
        () -> ohipProfileClient.getCompaniesProfile("Test", "AR-123", "WB", 50));
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals("debug message", debugMessage);
  }

  @Test
  void getCompanyProfile__ByCorporateId__HappyPath() {
    //Arrange
    when(webClientMock.get())
        .thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri((Function<UriBuilder, URI>) any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(CompanyProfileDto.class))
        .thenReturn(Mono.just(new CompanyProfileDto()));

    //Act
    var companiesProfileResult = ohipProfileClient.getCompanyProfileByCorporateId("123456");
    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(new CompanyProfileDto());
  }

  @Test
  void getCompanyProfileByCompanyId__HappyPathByCorporateId() {
    //Arrange
    when(webClientMock.get())
        .thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri((Function<UriBuilder, URI>) any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(CompanyProfileDto.class))
        .thenReturn(Mono.just(new CompanyProfileDto()));

    //Act
    var companiesProfileResult = ohipProfileClient.getCompanyProfileByCompanyId("123456");
    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(new CompanyProfileDto());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getCompaniesProfile__Exception(boolean isClient) {
    //Arrange
    when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(
        isClient ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR);

    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(CompanyProfileDto.class)).thenReturn(
        Mono.error(
            isClient ? new CompanyNotFoundException("message", "debug message", new Exception(),
                100) :
                new CompanyServiceException("message", "debug message", new Exception(), 100)));

    //Act & Assert
    var thrownException = assertThrows(Exception.class,
        () -> ohipProfileClient.getCompanyProfileByCorporateId("123456"));
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("debug message", debugMessage);
  }

  @Test
  void getNegotiatedRatesForCompanyProfile__HappyPath() {
    //Arrange
    when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri((Function<UriBuilder, URI>) any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(NegotiatedRatesResponseDto.class)).thenReturn(
        Mono.just(new NegotiatedRatesResponseDto()));

    //Act
    var negotiatedRatesResponseResult = ohipProfileClient.getNegotiatedRatesForCompanyProfile(
        "123456");
    //Assert
    assertThat(negotiatedRatesResponseResult).usingRecursiveComparison()
        .isEqualTo(new NegotiatedRatesResponseDto());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getNegotiatedRatesForCompanyProfile__Exception(boolean isClient) {
    //Arrange
    when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(any(Function.class)))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(
        isClient ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR);

    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(NegotiatedRatesResponseDto.class)).thenReturn(
        Mono.error(
            isClient ? new CompanyNotFoundException("message", "debug message", new Exception(),
                100) :
                new CompanyServiceException("message", "debug message", new Exception(), 100)));

    //Act & Assert
    var thrownException = assertThrows(Exception.class,
        () -> ohipProfileClient.getNegotiatedRatesForCompanyProfile(
            "123456"));
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("debug message", debugMessage);
  }
}