package uk.co.whitbread.hotel.register.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.register.mapper.CustomerMapper;
import uk.co.whitbread.hotel.register.model.RegisterAccountRequest;
import uk.co.whitbread.hotel.register.model.RegisterAccountResponse;
import uk.co.whitbread.hotel.register.model.UpdatePreferencesRequest;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.ContactDetail;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;

@ExtendWith(MockitoExtension.class)
class PIUniversalLoginServiceTest {

  @Mock
  private CustomerDataService customerDataService;

  @Mock
  private CustomerMapper mapper;

  @Mock
  private MarketingService marketingService;

  @InjectMocks
  private PIUniversalLoginService underTest;

  @Test
  void registerAccount_success() {
    // Given
    RegisterAccountRequest request = new RegisterAccountRequest();
    request.setEmail("test@example.com");
    UpdatePreferencesRequest prefsRequest = new UpdatePreferencesRequest();
    request.setUpdatePreferencesRequest(prefsRequest);

    CustomerAccountRequest mappedRequest = new CustomerAccountRequest();
    mappedRequest.setContactDetail(new ContactDetail());
    CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();
    RegisterAccountResponse expectedResponse = new RegisterAccountResponse();

    when(mapper.toCdhRequest(request)).thenReturn(mappedRequest);
    when(customerDataService.createCustomerAccount(mappedRequest))
        .thenReturn(customerAccountResponse);
    when(mapper.toCustomerResponse(customerAccountResponse)).thenReturn(expectedResponse);

    // When
    RegisterAccountResponse actualResponse = underTest.registerAccount(request);

    // Then
    assertEquals(expectedResponse, actualResponse);
    verify(mapper).toCdhRequest(request);
    verify(customerDataService).createCustomerAccount(mappedRequest);
    verify(mapper).toCustomerResponse(customerAccountResponse);
    verify(marketingService).updateMarketingOptIn(prefsRequest, "test@example.com");
  }

  @Test
  void registerAccount_success_withNullPreferencesRequest() {
    // Given
    RegisterAccountRequest request = new RegisterAccountRequest();
    request.setEmail("test@example.com");
    request.setUpdatePreferencesRequest(null);

    CustomerAccountRequest mappedRequest = new CustomerAccountRequest();
    mappedRequest.setContactDetail(new ContactDetail());
    CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();
    RegisterAccountResponse expectedResponse = new RegisterAccountResponse();

    when(mapper.toCdhRequest(request)).thenReturn(mappedRequest);
    when(customerDataService.createCustomerAccount(mappedRequest))
        .thenReturn(customerAccountResponse);
    when(mapper.toCustomerResponse(customerAccountResponse)).thenReturn(expectedResponse);

    // When
    RegisterAccountResponse actualResponse = underTest.registerAccount(request);

    // Then
    assertEquals(expectedResponse, actualResponse);
    verify(marketingService).updateMarketingOptIn(null, "test@example.com");
  }

  @Test
  void registerAccount_cdhException_throwsCdhServiceException() {
    // Given
    RegisterAccountRequest request = new RegisterAccountRequest();
    CustomerAccountRequest mappedRequest = new CustomerAccountRequest();
    mappedRequest.setContactDetail(new ContactDetail());
    when(mapper.toCdhRequest(request)).thenReturn(mappedRequest);
    when(customerDataService.createCustomerAccount(mappedRequest))
        .thenThrow(new CDHException());

    // When & Then
    assertThrows(
        uk.co.whitbread.hotel.register.exceptions.CdhServiceException.class,
        () -> underTest.registerAccount(request)
    );
    verify(mapper).toCdhRequest(request);
    verify(customerDataService).createCustomerAccount(mappedRequest);
    verifyNoInteractions(marketingService);
  }
}
