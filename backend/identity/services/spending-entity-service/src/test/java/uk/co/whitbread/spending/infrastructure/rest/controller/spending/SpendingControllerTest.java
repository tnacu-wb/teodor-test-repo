package uk.co.whitbread.spending.infrastructure.rest.controller.spending;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.SpendingReportFile;
import uk.co.whitbread.spending.domain.model.out.UpcomingSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;
import uk.co.whitbread.spending.domain.model.out.cdh.CompanySpending;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.domain.ports.primary.ReportingInPort;
import uk.co.whitbread.spending.domain.ports.primary.SpendingInPort;
import uk.co.whitbread.spending.infrastructure.config.WorldlineProperties;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.AccountSpendingRequestDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.AccountSpendingResponseDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.CompanySpendingRequestDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.CompanySpendingResponseDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.EmployeeSpendRequestDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.EmployeeSpendResponseDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.PaymentInfoDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper.UpcomingSpendingResponseDtoMapper;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.AccountSpendingRequestDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.CompanySpendingRequestDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.PaymentInfoQueryParamsDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.AccountSpendingDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.AccountSpendingResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.CompanySpendingDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.CompanySpendingResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.UpcomingSpendingResponseDto;

@ExtendWith(MockitoExtension.class)
class SpendingControllerTest {

  private static final String AUTHORIZATION = "Bearer token==";
  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String PIBA_ACCOUNT_ID = "PIBA_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String FROM_MONTH_YEAR = "11-2024";
  private static final String TO_MONTH_YEAR = "09-2025";
  private static final Integer YEAR = 2024;
  private static final Integer MONTH = 11;
  private static final Integer NUMBER_OF_BOOKINGS = 5;
  private static final BigDecimal BOOKING_VALUE = BigDecimal.valueOf(2369.36);
  private static final String LANGUAGE = "en";
  private static final String CSV_CONTENT = "csv,data,example";
  private static final String FILE_NAME = "account_spending.csv";
  private static final String TEXT_CSV = "text/csv";

  @InjectMocks
  private SpendingController spendingController;

  @Mock
  private CompanySpendingRequestDtoMapper companySpendingRequestDtoMapper;

  @Mock
  private CompanySpendingResponseDtoMapper companySpendingResponseDtoMapper;

  @Mock
  private AccountSpendingRequestDtoMapper accountSpendingRequestDtoMapper;

  @Mock
  private AccountSpendingResponseDtoMapper accountSpendingResponseDtoMapper;

  @Mock
  private EmployeeSpendRequestDtoMapper employeeSpendRequestDtoMapper;

  @Mock
  private EmployeeSpendResponseDtoMapper employeeSpendResponseDtoMapper;

  @Mock
  private UpcomingSpendingResponseDtoMapper upcomingSpendingResponseDtoMapper;

  @Mock
  private WorldlineProperties worldlineProperties;

  @Mock
  private SpendingInPort spendingInPort;

  @Mock
  private ReportingInPort reportingInPort;

  @Mock
  private PaymentInfoDtoMapper paymentInfoDtoMapper;



  @Test
  void getCompanySpending__ShouldReturnOK200() {
    // Arrange
    var getCompanySpendingRequestDto = createCompanySpendingRequestDto();
    var getCompanySpendingRequest = createCompanySpendingRequest();
    var getCompanySpendingResponse = createCompanySpendingResponse();
    var getCompanySpendingResponseDto = createCompanySpendingResponseDto();

    when(companySpendingRequestDtoMapper.toModel(getCompanySpendingRequestDto)).thenReturn(getCompanySpendingRequest);
    when(spendingInPort.getCompanySpending(getCompanySpendingRequest)).thenReturn(getCompanySpendingResponse);
    when(companySpendingResponseDtoMapper.toDto(getCompanySpendingResponse)).thenReturn(getCompanySpendingResponseDto);

    // Act
    final ResponseEntity<CompanySpendingResponseDto> response =
        spendingController.getCompanySpending(AUTHORIZATION, getCompanySpendingRequestDto);

    // Assertions
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void getCompanySpending__ShouldFindCompanySpending() {
    // Arrange
    var getCompanySpendingRequestDto = createCompanySpendingRequestDto();
    var getCompanySpendingRequest = createCompanySpendingRequest();
    var getCompanySpendingResponse = createCompanySpendingResponse();
    var getCompanySpendingResponseDto = createCompanySpendingResponseDto();

    when(companySpendingRequestDtoMapper.toModel(getCompanySpendingRequestDto)).thenReturn(getCompanySpendingRequest);
    when(spendingInPort.getCompanySpending(getCompanySpendingRequest)).thenReturn(getCompanySpendingResponse);
    when(spendingInPort.getCompanySpending(getCompanySpendingRequest)).thenReturn(getCompanySpendingResponse);
    when(companySpendingResponseDtoMapper.toDto(getCompanySpendingResponse)).thenReturn(getCompanySpendingResponseDto);

    // Act
    final var request = companySpendingRequestDtoMapper.toModel(getCompanySpendingRequestDto);
    final var companySpendingInformation = spendingInPort.getCompanySpending(request);
    final var responseDto = companySpendingResponseDtoMapper.toDto(companySpendingInformation);
    final ResponseEntity<CompanySpendingResponseDto> response =
        spendingController.getCompanySpending(AUTHORIZATION, getCompanySpendingRequestDto);

    // Assertions
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), responseDto.getCompanySpendingDtoList().get(0).getCompanyAccountId(),
        Objects.requireNonNull(response.getBody()).getCompanySpendingDtoList().get(0).getCompanyAccountId());
    assertEquals(response.toString(), responseDto.getCompanySpendingDtoList().get(0).getYear(),
        Objects.requireNonNull(response.getBody()).getCompanySpendingDtoList().get(0).getYear());
    assertEquals(response.toString(), responseDto.getCompanySpendingDtoList().get(0).getMonth(),
        Objects.requireNonNull(response.getBody()).getCompanySpendingDtoList().get(0).getMonth());
    assertEquals(response.toString(), responseDto.getCompanySpendingDtoList().get(0).getNoOfBookings(),
        Objects.requireNonNull(response.getBody()).getCompanySpendingDtoList().get(0).getNoOfBookings());
    assertEquals(response.toString(), responseDto.getCompanySpendingDtoList().get(0).getBookingValue(),
        Objects.requireNonNull(response.getBody()).getCompanySpendingDtoList().get(0).getBookingValue());
  }

  @Test
  void getAccountSpending__ShouldReturnOK200() {
    // Arrange
    var httpServletRequestMock = mock(HttpServletRequest.class);

    when(httpServletRequestMock.getHeader("Accept")).thenReturn(MediaType.APPLICATION_JSON_VALUE);
    when(accountSpendingRequestDtoMapper.toModel(createAccountSpendingDto())).thenReturn(
        createAccountSpendingRequest());
    when(spendingInPort.getAccountSpending(createAccountSpendingRequest(), AUTHORIZATION)).thenReturn(
        createAccountSpendingResponse());
    when(accountSpendingResponseDtoMapper.toDto(createAccountSpendingResponse())).thenReturn(
        createAccountSpendingResponseDto());

    // Act
    var response = spendingController.getAccountSpending(
        AUTHORIZATION, createAccountRequestDto(), httpServletRequestMock);

    // Assertions
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void getAccountSpending__ShouldFindAccountSpending() {
    // Arrange
    var httpServletRequestMock = mock(HttpServletRequest.class);

    when(httpServletRequestMock.getHeader("Accept")).thenReturn(MediaType.APPLICATION_JSON_VALUE);
    when(accountSpendingRequestDtoMapper.toModel(createAccountSpendingDto())).thenReturn(
        createAccountSpendingRequest());
    when(spendingInPort.getAccountSpending(createAccountSpendingRequest(), AUTHORIZATION)).thenReturn(
        createAccountSpendingResponse());
    when(accountSpendingResponseDtoMapper.toDto(createAccountSpendingResponse())).thenReturn(
        createAccountSpendingResponseDto());

    // Act
    final var request = accountSpendingRequestDtoMapper.toModel(createAccountRequestDto());
    final var accountSpendingInformation = spendingInPort.getAccountSpending(request, AUTHORIZATION);
    final var responseDto = accountSpendingResponseDtoMapper.toDto(accountSpendingInformation);
    final ResponseEntity<Object> response = spendingController.getAccountSpending(
        AUTHORIZATION, createAccountSpendingDto(), httpServletRequestMock);

    // Assertions
    Assertions.assertNotNull(response);
    assertInstanceOf(AccountSpendingResponseDto.class, response.getBody());

    assertEquals(response.toString(), responseDto.getAccountSpendingDtoList().get(0).getPibaAccountId(),
        Objects.requireNonNull((AccountSpendingResponseDto) response.getBody()).getAccountSpendingDtoList().get(0).getPibaAccountId());

    assertEquals(response.toString(), responseDto.getAccountSpendingDtoList().get(0).getYear(),
        Objects.requireNonNull((AccountSpendingResponseDto) response.getBody()).getAccountSpendingDtoList().get(0).getYear());

    assertEquals(response.toString(), responseDto.getAccountSpendingDtoList().get(0).getMonth(),
        Objects.requireNonNull((AccountSpendingResponseDto) response.getBody()).getAccountSpendingDtoList().get(0).getMonth());

    assertEquals(response.toString(), responseDto.getAccountSpendingDtoList().get(0).getNoOfBookings(),
        Objects.requireNonNull((AccountSpendingResponseDto) response.getBody()).getAccountSpendingDtoList().get(0).getNoOfBookings());

    assertEquals(response.toString(), responseDto.getAccountSpendingDtoList().get(0).getBookingValue(),
        Objects.requireNonNull((AccountSpendingResponseDto) response.getBody()).getAccountSpendingDtoList().get(0).getBookingValue());
  }

  @Test
  void getAccountSpending_ShouldReturnCsvResponse_WhenAcceptHeaderIsCsv() {
    // Arrange
    var requestDto = createAccountRequestDto();
    var request = createAccountSpendingRequest();
    var response = createAccountSpendingResponse();
    var reportFile = new SpendingReportFile(FILE_NAME, CSV_CONTENT);
    var httpServletRequestMock = mock(HttpServletRequest.class);

    when(accountSpendingRequestDtoMapper.toModel(requestDto)).thenReturn(request);
    when(spendingInPort.getAccountSpending(request, AUTHORIZATION)).thenReturn(response);
    when(reportingInPort.generateAccountSpendingCsv(response, LANGUAGE, PIBA_ACCOUNT_ID, Scheme.GB)).thenReturn(reportFile);
    when(httpServletRequestMock.getHeader("Accept")).thenReturn(TEXT_CSV);

    // Act
    ResponseEntity<Object> responseEntity = spendingController.getAccountSpending(AUTHORIZATION, requestDto, httpServletRequestMock);

    // Assert
    Assertions.assertNotNull(responseEntity);
    assertEquals("Status code is not 200", HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    assertEquals("Content type is not text/csv", TEXT_CSV, Objects.requireNonNull(responseEntity.getHeaders().getContentType()).toString());
    assertEquals("Content-Disposition header is incorrect", "attachment; filename=" + FILE_NAME,
        Objects.requireNonNull(responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)));
    Assertions.assertArrayEquals(CSV_CONTENT.getBytes(),
        ((String) Objects.requireNonNull(responseEntity.getBody())).getBytes(), "CSV content does not match");
  }

  @Test
  void getEmployeeSpend__ShouldReturnOK200() {
    var requestDto = createEmployeeSpendRequestDto();
    var request = createEmployeeSpendRequest();
    var response = createEmployeeSpendReports();
    var responseDto = createEmployeeSpendResponseDtoList();

    when(employeeSpendRequestDtoMapper.toModel(requestDto)).thenReturn(request);
    when(spendingInPort.getEmployeeSpend(request)).thenReturn(response);
    when(employeeSpendResponseDtoMapper.toDto(response)).thenReturn(responseDto);

    ResponseEntity<List<EmployeeSpendResponseDto>> actualResponse =
        spendingController.getEmployeeSpend(AUTHORIZATION, requestDto);

    Assertions.assertNotNull(actualResponse);
    assertEquals(actualResponse.toString(), 200, actualResponse.getStatusCode().value());
    assertEquals(actualResponse.toString(), responseDto, actualResponse.getBody());
  }

  @Test
  void getEmployeeSpend_WhenInPortThrows_ThenExceptionPropagates() {
    var requestDto = createEmployeeSpendRequestDto();
    var request = createEmployeeSpendRequest();

    when(employeeSpendRequestDtoMapper.toModel(requestDto)).thenReturn(request);
    when(spendingInPort.getEmployeeSpend(request)).thenThrow(new RuntimeException("boom"));

    var thrown = Assertions.assertThrows(RuntimeException.class,
        () -> spendingController.getEmployeeSpend(AUTHORIZATION, requestDto));

    assertEquals("Unexpected exception message", "boom", thrown.getMessage());
  }

  @Test
  void getUpcomingSpending_WhenNoException_ShouldReturn200() {
    var unsanitizedAccountId = System.lineSeparator() + "123" + System.lineSeparator() + "456";
    var domainModel = UpcomingSpendingResponse.builder().build();
    var mappedResponse = UpcomingSpendingResponseDto.builder().accountStatus("ok").build();
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("1.1.1.1");
    when(upcomingSpendingResponseDtoMapper.toDto(any())).thenReturn(mappedResponse);
    when(spendingInPort.getUpcomingSpending(eq("Bearer token"), any(), eq("1.1.1.1"),eq(null))).thenReturn(domainModel);
    var httpServletRequestMock = mock(HttpServletRequest.class);

    var response = spendingController.getUpcomingSpending("Bearer token",
          unsanitizedAccountId, null,httpServletRequestMock);

    assertEquals("Status code not matching", HttpStatus.OK.toString(), response.getStatusCode().toString());
    assertEquals("Result is not mapped correctly", mappedResponse, response.getBody());
    verify(upcomingSpendingResponseDtoMapper).toDto(any());
    var accountIdArgCaptor = ArgumentCaptor.forClass(String.class);
    verify(spendingInPort).getUpcomingSpending(any(), accountIdArgCaptor.capture(), any(),isNull());
    assertEquals("AccountId not sanitized correctly", "123456", accountIdArgCaptor.getValue());
  }

  @Test
  void getPaymentInfo_WhenNoException_ShouldReturn200() {
    var request = new PaymentInfoQueryParamsDto("12345", null, 1, 1, true);
    var mappedResponse = new PaymentInfoModel("12345", 1, 1, true, "authorization", "1.1.1.1");
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("1.1.1.1");
    when(paymentInfoDtoMapper.toModel(any(),anyString(),anyString())).thenReturn(mappedResponse);
    when(spendingInPort.getPaymentInfo(mappedResponse)).thenReturn(new PaymentInfoResponse());
    var httpServletRequestMock = mock(HttpServletRequest.class);

    spendingController.getPaymentInfo("Bearer token",
        request, httpServletRequestMock);

    verify(paymentInfoDtoMapper).toPaymentInfoResponseDto(any());
    verify(spendingInPort).getPaymentInfo(any());
  }

  private AccountSpendingRequestDto createAccountRequestDto() {
    return AccountSpendingRequestDto.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .language(LANGUAGE)
        .scheme(Scheme.GB)
        .build();
  }

  private EmployeeSpendRequestDto createEmployeeSpendRequestDto() {
    return EmployeeSpendRequestDto.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  private EmployeeSpendRequest createEmployeeSpendRequest() {
    return EmployeeSpendRequest.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  private List<EmployeeSpendReport> createEmployeeSpendReports() {
    return List.of(EmployeeSpendReport.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .year(YEAR)
        .month(MONTH)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

  private List<EmployeeSpendResponseDto> createEmployeeSpendResponseDtoList() {
    return List.of(EmployeeSpendResponseDto.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .year(YEAR)
        .month(MONTH)
        .noOfBookings(NUMBER_OF_BOOKINGS)
        .bookingValue(BOOKING_VALUE)
        .build());
  }

  private AccountSpendingResponseDto createAccountSpendingResponseDto() {
    return AccountSpendingResponseDto.builder()
        .accountSpendingDtoList(List.of(AccountSpendingDto.builder()
            .pibaAccountId(PIBA_ACCOUNT_ID)
            .month(MONTH)
            .year(YEAR)
            .noOfBookings(NUMBER_OF_BOOKINGS)
            .bookingValue(BOOKING_VALUE)
            .build())).build();
  }

  private AccountSpendingResponse createAccountSpendingResponse() {
    return AccountSpendingResponse.builder()
        .accountSpendingList(List.of(AccountSpending.builder()
            .pibaAccountId(PIBA_ACCOUNT_ID)
            .month(MONTH)
            .year(YEAR)
            .bookingValue(BOOKING_VALUE)
            .noOfBookings(NUMBER_OF_BOOKINGS)
            .build()))
        .build();
  }

  private AccountSpendingRequest createAccountSpendingRequest() {
    return AccountSpendingRequest.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .toMonthYear(TO_MONTH_YEAR)
        .fromMonthYear(FROM_MONTH_YEAR)
        .build();
  }

  private AccountSpendingRequestDto createAccountSpendingDto() {
    return AccountSpendingRequestDto.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .language(LANGUAGE)
        .scheme(Scheme.GB)
        .build();
  }

  CompanySpendingRequestDto createCompanySpendingRequestDto() {
    return CompanySpendingRequestDto.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  CompanySpendingRequest createCompanySpendingRequest() {
    return CompanySpendingRequest.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build();
  }

  CompanySpendingResponse createCompanySpendingResponse() {
    return CompanySpendingResponse.builder()
        .companySpendingList(List.of(CompanySpending.builder()
                .companyAccountId(COMPANY_ACCOUNT_ID)
                .year(YEAR)
                .month(MONTH)
                .noOfBookings(NUMBER_OF_BOOKINGS)
                .bookingValue(BOOKING_VALUE)
            .build()))
        .build();
  }

  CompanySpendingResponseDto createCompanySpendingResponseDto() {
    return CompanySpendingResponseDto.builder()
        .companySpendingDtoList(List.of(CompanySpendingDto.builder()
                .companyAccountId(COMPANY_ACCOUNT_ID)
                .year(YEAR)
                .month(MONTH)
                .noOfBookings(NUMBER_OF_BOOKINGS)
                .bookingValue(BOOKING_VALUE)
            .build()))
        .build();
  }

}
