package uk.co.whitbread.hotel.register.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.hotel.register.model.RegisterAccountRequest;
import uk.co.whitbread.hotel.register.model.RegisterAccountResponse;
import uk.co.whitbread.hotel.register.service.PIUniversalLoginService;

@ExtendWith(MockitoExtension.class)
class PIUniversalLoginControllerTest {

  @Mock
  private PIUniversalLoginService universalLoginService;

  @InjectMocks
  private PIUniversalLoginController underTest;

  @Test
  void shouldRegisterAccount_success() {
    // Given
    var request = new RegisterAccountRequest();
    request.setEmail("test@example.com");
    var expectedResponse = RegisterAccountResponse.builder().customerId("cust-123").build();
    when(universalLoginService.registerAccount(request)).thenReturn(expectedResponse);

    // When
    var response = underTest.registerAccount(request);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    verify(universalLoginService).registerAccount(request);
  }
}
