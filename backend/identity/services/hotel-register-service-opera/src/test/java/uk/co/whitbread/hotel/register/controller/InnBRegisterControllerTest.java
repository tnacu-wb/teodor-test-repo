package uk.co.whitbread.hotel.register.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoResponse;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;

@ExtendWith(MockitoExtension.class)
class InnBRegisterControllerTest {
  @Mock
  private HotelRegisterService registerService;

  @Mock
  private PasswordByConfigValidator passwordValidator;

  @InjectMocks
  private InnBRegisterController underTest;

  @Test
  void shouldCreateInnBCustomer_success() {
    // Given
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    InnBRegistrationStepOneResponse response = InnBRegistrationStepOneResponse.builder()
        .existingCompany(false)
        .existingEmployee(false)
        .existingCompanyType(null)
        .build();

    when(registerService.registerInnBStepOne(request)).thenReturn(response);

    // When
    ResponseEntity<InnBRegistrationStepOneResponse> responseEntity = underTest.registerInnBStepOne(request);

    // Then
    verify(registerService).registerInnBStepOne(request);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(responseEntity.getBody(), is(response));
  }

  @Test
  void shouldRegisterInnBStepTwo_success() {
    // Given
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();
    InnBRegistrationStepTwoResponse response = new InnBRegistrationStepTwoResponse("valid.email@example.com", "companyId123");

    when(registerService.registerInnBStepTwo(request)).thenReturn(response);
    when(passwordValidator.isValid(any(), any(), any())).thenReturn(true);

    // When
    ResponseEntity<InnBRegistrationStepTwoResponse> responseEntity = underTest.registerInnBStepTwo(request);

    // Then
    verify(registerService).registerInnBStepTwo(request);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), is(response));
  }

  private static InnBRegistrationStepOneRequest createValidInnBRegistrationStepOneRequest() {
    InnBRegistrationStepOneRequest request = new InnBRegistrationStepOneRequest();
    request.setEmail("valid.email@example.com");
    request.setCompanyName("Valid Company Name");
    request.setAddress(InnBCompanyAddress.builder()
        .line1("123 Street")
        .postCode("12345")
        .countryCode("Uk")
        .build());
    request.setLanguage("en");
    return request;
  }

  private static InnBRegistrationStepTwoRequest createValidInnBRegistrationStepTwoRequest() {
    InnBRegistrationStepTwoRequest request = new InnBRegistrationStepTwoRequest();
    request.setPassword("ValidPassword123!");
    request.setActivationKey("ValidActivationKey");
    return request;
  }

}