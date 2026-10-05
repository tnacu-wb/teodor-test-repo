package uk.co.whitbread.hotel.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.account.mapper.AccountInfoMapper;
import uk.co.whitbread.hotel.account.model.AccountInfoResponseDto;
import uk.co.whitbread.hotel.account.model.NotificationsResponse;
import uk.co.whitbread.hotel.account.service.InnBusinessService;
import uk.co.whitbread.hotel.account.service.cdh.CdhService;
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.account.utils.NetworkUtils;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "InnBusiness operations")
public class InnBusinessController {

  private final TokenService tokenService;
  private final CdhService cdhService;
  private final NetworkUtils networkUtils;
  private final Executor cdhExecutor;
  private final AccountInfoMapper accountInfoMapper;
  private final InnBusinessService innBusinessService;

  @Operation(method = "getNotifications", summary = "Get notifications for an account",
      description = "This endpoint provides the functionality to retrieve the notifications for account")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input", content = {
          @Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side", content = {
          @Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(path = "/innb/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<NotificationsResponse> getNotifications(
      @RequestHeader(name = "Authorization") String authorization) {

    log.info(
        "Called '/innb/notifications' (GET)");

    var cdhEmployeeDetails = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    var companyDetailsFuture = CompletableFuture.supplyAsync(() ->
        cdhService.getCompanyDetails(cdhEmployeeDetails), cdhExecutor);
    var employeeQuestionsFuture = CompletableFuture.supplyAsync(() ->
        cdhService.getEmployeeQuestions(cdhEmployeeDetails), cdhExecutor);

    var notificationResponse = CompletableFuture.allOf(companyDetailsFuture,
            employeeQuestionsFuture)
        .thenApply(result -> {
          var response = new NotificationsResponse();
          var companyDetails = companyDetailsFuture.join();
          var employeeQuestions = employeeQuestionsFuture.join();
          response.setProfileUpdateRequired(
              cdhService.isProfileUpdateRequired(companyDetails, employeeQuestions,
                  cdhEmployeeDetails));
          return response;
        });

    return new ResponseEntity<>(notificationResponse.join(), HttpStatus.OK);
  }

  @Operation(method = "getAccountInfo", summary = "Get get account info",
      description = "This endpoint provides the functionality to retrieve the account information")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input", content = {
          @Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side", content = {
          @Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(path = "/v1/hotel-account/innb/account", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountInfoResponseDto> getAccountInfo(
      @RequestHeader(name = "Authorization") String authorization,
      @RequestParam(name = "tetheredUserId") String tetheredUserId,
      @RequestParam(name = "scheme") Scheme scheme,
      HttpServletRequest httpServletRequest) {

    log.info(
        "Called '/v1/hotel-account/innb/account' (GET) with  tetheredUserId: {} ",
        LogFormatUtils.formatValue(tetheredUserId, true));

    tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);

    var accountInfo = accountInfoMapper.toResponseDto(
        innBusinessService.getAccountInfo(tetheredUserId, scheme,
            networkUtils.getClientIp(httpServletRequest)));

    return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
  }

}
