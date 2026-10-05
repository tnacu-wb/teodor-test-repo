package uk.co.whitbread.company.employee.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.EmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.InnBusinessEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.innbusiness.ApproveRejectRequest;
import uk.co.whitbread.company.employee.model.innbusiness.SendActivationRequest;
import uk.co.whitbread.company.employee.service.EmployeeActivationService;
import uk.co.whitbread.company.employee.service.InnBusinessService;
import uk.co.whitbread.company.employee.utils.BookingChannel;

@ExtendWith(MockitoExtension.class)
class InnBusinessControllerTest {

  private static final String COMPANY_NAME = "Test Company";
  private static final String LANGUAGE = "en";
  private static final String EMAIL = "test@example.com";
  private static final String AUTHORIZATION = "Bearer token===";

  @Mock
  private InnBusinessService innBusinessService;
  @Mock
  private EmployeeMapper employeeMapper;
  @Mock
  private EmployeeActivationService employeeActivationService;
  @InjectMocks
  private InnBusinessController innBusinessController;
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @ParameterizedTest
  @MethodSource("provideActivationRequests")
  void sendActivationRequest_WhenEmailIsNull_ThenViolation(SendActivationRequest request) {
    Set<ConstraintViolation<SendActivationRequest>> violations = validator.validate(request);

    assertEquals(1, violations.size());
  }

  @ParameterizedTest
  @MethodSource("provideInvalidLanguagesWithExpectedMessages")
  void sendActivationRequest_WhenLanguageIsInvalid_ThenViolation(String invalidLanguage) {
    var request = new SendActivationRequest(EMAIL, COMPANY_NAME, invalidLanguage);

    Set<ConstraintViolation<SendActivationRequest>> violations = validator.validate(request);

    assertEquals(1, violations.size());
  }

  @Test
  void sendActivationEmail_WhenValidRequest_ThenReturnOk() {
    var request = new SendActivationRequest();
    request.setCompanyName(COMPANY_NAME);
    request.setLanguage(LANGUAGE);
    request.setEmail(EMAIL);
    doNothing().when(innBusinessService).sendActivationEmail(any(SendActivationRequest.class));

    var response = innBusinessController.sendActivationEmail(request);

    verify(innBusinessService).sendActivationEmail(request);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void sendActivationEmail_WhenServiceThrowsException_ThenReturnInternalServerError() {
    var request = new SendActivationRequest();
    request.setCompanyName(COMPANY_NAME);
    request.setLanguage(LANGUAGE);
    request.setEmail(EMAIL);
    doThrow(new RuntimeException("Service error"))
        .when(innBusinessService).sendActivationEmail(any(SendActivationRequest.class));

    assertThrows(RuntimeException.class, () -> {
      innBusinessController.sendActivationEmail(request);
    });
  }

  @ParameterizedTest
  @MethodSource("provideApproveRejectRequests")
  void approveRejectEmployee_WhenEmailIsNull_ThenViolation(ApproveRejectRequest request) {
    Set<ConstraintViolation<ApproveRejectRequest>> violations = validator.validate(request);

    assertEquals(1, violations.size());
  }

  @Test
  void approveRejectEmployee_WhenValidRequest_ThenReturnOk() {
    var request = new ApproveRejectRequest(EMAIL, AccessLevel.SELF, true, LANGUAGE);
    doNothing().when(innBusinessService).approveRejectEmployee(request, AUTHORIZATION);

    var response = innBusinessController.approveRejectEmployee(AUTHORIZATION, request);

    verify(innBusinessService).approveRejectEmployee(request, AUTHORIZATION);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void approveRejectEmployee_WhenServiceThrowsException_ThenReturnInternalServerError() {
    var request = new ApproveRejectRequest(EMAIL, AccessLevel.SELF, true, LANGUAGE);
    doThrow(new RuntimeException("Service error"))
        .when(innBusinessService).approveRejectEmployee(request, AUTHORIZATION);

    assertThrows(RuntimeException.class, () -> {
      innBusinessController.approveRejectEmployee(AUTHORIZATION, request);
    });
  }

  @Test
  void getActivationDetails_WhenValidRequest_ThenReturnResponse() {
    String activationKey = "test-key";
    String countryCode = "gb";
    String languageCode = "en";
    BookingChannel bookingChannel = BookingChannel.CBT;

    InnBusinessEmployeeActivationResponse serviceResponse = new InnBusinessEmployeeActivationResponse();
    EmployeeActivationResponse mappedResponse = new EmployeeActivationResponse();

    when(employeeActivationService.getInnBusinessEmployeeActivationResponse(activationKey))
        .thenReturn(serviceResponse);
    when(employeeMapper.toEmployeeActivationResponse(serviceResponse)).thenReturn(mappedResponse);

    ResponseEntity<EmployeeActivationResponse> response = innBusinessController.getInnBusinessActivationDetails(
        bookingChannel, countryCode, languageCode, activationKey);

    verify(employeeActivationService).getInnBusinessEmployeeActivationResponse(activationKey);
    verify(employeeMapper).toEmployeeActivationResponse(serviceResponse);
    assertEquals(ResponseEntity.ok(mappedResponse), response);
  }

  @Test
  void getActivationDetails_WhenServiceThrowsException_ThenThrowException() {
    String activationKey = "test-key";
    String countryCode = "gb";
    String languageCode = "en";
    BookingChannel bookingChannel = BookingChannel.CBT;

    when(employeeActivationService.getInnBusinessEmployeeActivationResponse(activationKey))
        .thenThrow(new RuntimeException("Service error"));

    assertThrows(RuntimeException.class, () -> {
      innBusinessController.getInnBusinessActivationDetails(bookingChannel, countryCode, languageCode, activationKey);
    });

    verify(employeeActivationService).getInnBusinessEmployeeActivationResponse(activationKey);
  }

  private static Stream<SendActivationRequest> provideActivationRequests() {
    return Stream.of(
        new SendActivationRequest("", "a", "en"),
        new SendActivationRequest("a", "b", "en"),
        new SendActivationRequest("correct@email.com", "", "en"),
        new SendActivationRequest("correct@email.com", "b", "")
    );
  }

  private static Stream<String> provideInvalidLanguagesWithExpectedMessages() {
    return Stream.of(
        "fr",
        "es",
        "123",
        "",
        null
    );
  }

  private static Stream<ApproveRejectRequest> provideApproveRejectRequests() {
    return Stream.of(
        new ApproveRejectRequest("invalidemail", AccessLevel.SELF, true, "en"),
        new ApproveRejectRequest("correct@email.com", null, true, "en"),
        new ApproveRejectRequest("correct@email.com", AccessLevel.SUPER, null, "en"),
        new ApproveRejectRequest("correct@email.com", AccessLevel.SUPER, false, null)
    );
  }
}