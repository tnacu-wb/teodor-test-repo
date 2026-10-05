package uk.co.whitbread.spending.infrastructure.rest.controller.spending;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.spending.domain.model.out.SpendingReportFile;
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
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.PaymentInfoQueryParamsDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.CompanySpendingResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentInfoResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.UpcomingSpendingResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.utils.WorldlineUtils;

@RequestMapping("/v1/spending")
@RestController
@Slf4j
@RequiredArgsConstructor
public class SpendingController implements SpendingApi {
  private final CompanySpendingRequestDtoMapper companySpendingRequestDtoMapper;
  private final AccountSpendingRequestDtoMapper accountSpendingRequestDtoMapper;
  private final SpendingInPort spendingInPort;
  private final CompanySpendingResponseDtoMapper companySpendingResponseDtoMapper;
  private final AccountSpendingResponseDtoMapper accountSpendingResponseDtoMapper;
  private final EmployeeSpendRequestDtoMapper employeeSpendRequestDtoMapper;
  private final EmployeeSpendResponseDtoMapper employeeSpendResponseDtoMapper;
  private final UpcomingSpendingResponseDtoMapper upcomingSpendingResponseDtoMapper;
  private final WorldlineProperties worldlineProperties;
  private final ReportingInPort reportingInPort;
  private final PaymentInfoDtoMapper paymentInfoDtoMapper;

  private static final String TEXT_CSV = "text/csv";

  @Override
  @GetMapping(value = "/companySpending", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated() and authentication.account.accessLevel == 'SUPER'")
  public ResponseEntity<CompanySpendingResponseDto> getCompanySpending(
        @NotEmpty @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
              example = "Bearer ===", required = true, schema = @Schema(type = "string"))
        @RequestHeader(value = WB_AUTHORIZATION) String authorization,
        @Valid @ParameterObject CompanySpendingRequestDto companySpendingRequestDto) {
    String sanitizedFromMonthYear = companySpendingRequestDto.getFromMonthYear()
        .replace("\n", "").replace("\r", "");
    String sanitizedToMonthYear = companySpendingRequestDto.getToMonthYear()
        .replace("\n", "").replace("\r", "");
    log.debug("Called GET /companySpending with fromMonthYear={}, toMonthYear={}",
        sanitizedFromMonthYear,
        sanitizedToMonthYear);

    final var companySpendingRequest = companySpendingRequestDtoMapper.toModel(companySpendingRequestDto);

    var companySpendingDto = companySpendingResponseDtoMapper.toDto(
        spendingInPort.getCompanySpending(companySpendingRequest));

    return ResponseEntity.status(HttpStatus.OK).body(companySpendingDto);
  }

  @Override
  @GetMapping(value = "/accountSpending", produces = {MediaType.APPLICATION_JSON_VALUE, TEXT_CSV})
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Object> getAccountSpending(
        @NotEmpty @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
              example = "Bearer ===", required = true, schema = @Schema(type = "string"))
        @RequestHeader(value = WB_AUTHORIZATION) String authorization,
        @Valid @ParameterObject AccountSpendingRequestDto accountSpendingRequestDto,
        HttpServletRequest request) {

    String sanitizedPibaAccountId = sanitizeInput(accountSpendingRequestDto.getPibaAccountId());
    String sanitizedFromMonthYear = sanitizeInput(accountSpendingRequestDto.getFromMonthYear());
    String sanitizedToMonthYear = sanitizeInput(accountSpendingRequestDto.getToMonthYear());
    String sanitizedLanguage = sanitizeInput(accountSpendingRequestDto.getLanguage());
    log.debug("Called GET /accountSpending with pibaAccountId={}, fromMonthYear={}, toMonthYear={}",
        sanitizedPibaAccountId,
        sanitizedFromMonthYear,
        sanitizedToMonthYear);

    final var accountSpendingRequest = accountSpendingRequestDtoMapper.toModel(accountSpendingRequestDto);

    var accountSpending = spendingInPort.getAccountSpending(accountSpendingRequest, authorization);

    String acceptHeader = request.getHeader("Accept");
    if (acceptHeader != null && acceptHeader.contains(TEXT_CSV)) {
      SpendingReportFile spendingReportFile =
          reportingInPort.generateAccountSpendingCsv(accountSpending, sanitizedLanguage, sanitizedPibaAccountId,
              accountSpendingRequestDto.getScheme());

      return ResponseEntity.ok()
          .header("Content-Disposition", "attachment; filename=" + spendingReportFile.filename())
          .contentType(MediaType.parseMediaType(TEXT_CSV))
          .body(spendingReportFile.content());
    } else {
      var accountSpendingDto = accountSpendingResponseDtoMapper.toDto(accountSpending);
      return ResponseEntity.status(HttpStatus.OK).body(accountSpendingDto);
    }
  }

  @Override
  @GetMapping(value = "/employeeSpend", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<EmployeeSpendResponseDto>> getEmployeeSpend(
      @NotEmpty @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
          example = "Bearer ===", required = true, schema = @Schema(type = "string"))
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @Valid @ParameterObject EmployeeSpendRequestDto employeeSpendRequestDto) {

    String sanitizedFromMonthYear = sanitizeInput(employeeSpendRequestDto.getFromMonthYear());
    String sanitizedToMonthYear = sanitizeInput(employeeSpendRequestDto.getToMonthYear());
    log.debug("Called GET /employeeSpend with fromMonthYear={}, toMonthYear={}",
        sanitizedFromMonthYear, sanitizedToMonthYear);

    final var employeeSpendRequest = employeeSpendRequestDtoMapper.toModel(employeeSpendRequestDto);
    var employeeSpendDto = employeeSpendResponseDtoMapper.toDto(
        spendingInPort.getEmployeeSpend(employeeSpendRequest));

    return ResponseEntity.status(HttpStatus.OK).body(employeeSpendDto);
  }

  @Override
  @GetMapping(value = "/upcomingSpending", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UpcomingSpendingResponseDto> getUpcomingSpending(
      @NotEmpty @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
          example = "Bearer ===", required = true, schema = @Schema(type = "string"))
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @NotEmpty @Parameter(in = ParameterIn.QUERY, name = "accountId", example = "123456",
          required = true, schema = @Schema(type = "string"))
      @RequestParam(name = "accountId") String accountId,
      @Parameter(in = ParameterIn.QUERY, name = "tetheredUserGuid", example = "123456",
          schema = @Schema(type = "string"))
      @RequestParam(name = "tetheredUserGuid", required = false) @Nullable String tetheredUserGuid,
      HttpServletRequest httpServletRequest) {
    String sanitizedAccountId = accountId
          .replace("\n", "").replace("\r", "");

    log.info("Called GET /v1/spending/upcomingSpending for account id={}.", sanitizedAccountId);

    var upcomingSpendingResponse = upcomingSpendingResponseDtoMapper.toDto(spendingInPort
        .getUpcomingSpending(authorization, sanitizedAccountId,
            WorldlineUtils.getClientIp(httpServletRequest,
                worldlineProperties.getDefaultIpAddress()), tetheredUserGuid));

    return ResponseEntity.status(HttpStatus.OK).body(upcomingSpendingResponse);
  }

  @GetMapping(value = "/paymentInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(code = HttpStatus.OK)
  public PaymentInfoResponseDto getPaymentInfo(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      PaymentInfoQueryParamsDto paymentInfoQueryParamsDto,
      HttpServletRequest httpServletRequest) {

    log.info("Called GET /v1/spending/paymentInfo with query params={}.",
        paymentInfoQueryParamsDto);
    var ipAddress = WorldlineUtils.getClientIp(httpServletRequest,
        worldlineProperties.getDefaultIpAddress());

    final var paymentInfoModel = paymentInfoDtoMapper.toModel(paymentInfoQueryParamsDto,
        authorization, ipAddress);

    var worldLineResponse = spendingInPort.getPaymentInfo(paymentInfoModel);
    return paymentInfoDtoMapper.toPaymentInfoResponseDto(worldLineResponse);
  }

  private String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }
    return input.replace("\n", "").replace("\r", "");
  }
}
