package uk.co.whitbread.hotel.card.controller;


import static java.util.Objects.isNull;
import static uk.co.whitbread.hotel.card.utils.ValidationUtils.isSameCompany;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.CostCentreData;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardRequest;
import uk.co.whitbread.hotel.card.exceptions.ValidateSameCompanyException;
import uk.co.whitbread.hotel.card.model.CardStatusEnum;
import uk.co.whitbread.hotel.card.model.InnBusinessPayCard;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardAddRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceResponse;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardInviteRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardUpdateRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardsResponse;
import uk.co.whitbread.hotel.card.model.WorldlineCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineRegisteredUser;
import uk.co.whitbread.hotel.card.service.CdhInnBusinessCardsService;
import uk.co.whitbread.hotel.card.service.PibaAccountService;
import uk.co.whitbread.hotel.card.service.worldline.WorldlineService;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.utils.NetworkUtils;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/innb")
@Tag(name = "InnBusiness PIBA Cards operations")
public class InnBusinessCardsController {

  private final WorldlineService worldlineService;
  private final TokenService tokenService;
  private final CdhInnBusinessCardsService cdhInnBusinessCardsService;
  private final PibaAccountService pibaAccountService;
  private final NetworkUtils networkUtils;

  @Operation(method = "getPIBARegisteredUsers", summary = "Get all registered users for a PIBA tethered user",
      description = "This endpoint provides the functionality to retrieve all payment cards for an existing PIBA account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(path = "/account/{tetheredUserId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<WorldlineRegisteredUser>> getPIBARegisteredUsers(
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @RequestParam("countryCode") String countryCode) {

    log.info("Called '/innb/account/{tetheredUserId}/users' (GET) with  tetheredUserId: {} countryCode: {}",
        LogFormatUtils.formatValue(tetheredUserId, true), LogFormatUtils.formatValue(countryCode, true));
    var accountRegisteredUsers = worldlineService.getAccountRegisteredUsers(tetheredUserId, countryCode);

    return new ResponseEntity<>(accountRegisteredUsers, HttpStatus.OK);
  }

  @Operation(method = "getPIBACard", summary = "Get a single PIBA card for the tethered user",
      description = "This endpoint provides the functionality to retrieve a single PIBA card for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(path = "/account/{tetheredUserId}/cards/{card-id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WorldlineCardDetails> getPIBACard(
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @PathVariable("card-id") String cardId,
      @Parameter(required = true) @RequestParam("countryCode") String countryCode) {

    log.info("Called '/account/{}/cards/{}' (GET) for  countryCode: {}", LogFormatUtils.formatValue(tetheredUserId, true),
        LogFormatUtils.formatValue(cardId, true), LogFormatUtils.formatValue(countryCode, true));

    return new ResponseEntity<>(worldlineService.getPIBACard(tetheredUserId, Integer.parseInt(cardId),
        countryCode), HttpStatus.OK);
  }

  @Operation(method = "addPIBACard", summary = "Add a PIBA card for the tethered user",
      description = "This endpoint provides the functionality to add a PIBA card for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/account/{tetheredUserId}/card", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WorldlineCardDetails> addPIBACard(
      @Parameter(required = true) @RequestHeader("Authorization") String authorizationToken,
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @RequestParam("countryCode") String countryCode,
      @Parameter(required = true) @Valid @RequestBody WorldlineAccountCardAddRequest worldlineAccountCardAddRequest,
      HttpServletRequest httpServletRequest) {

    log.info("Called 'innb/account/{}/card' (POST) for  countryCode: {} and worldlineAccountCardAddRequest: {}",
        LogFormatUtils.formatValue(tetheredUserId, true),
        LogFormatUtils.formatValue(countryCode, true),
        LogFormatUtils.formatValue(worldlineAccountCardAddRequest, true));

    var cdhEmployee = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorizationToken);
    var clientIp = networkUtils.getClientIp(httpServletRequest);

    boolean isForOtherEmployee = Strings.isNotBlank(worldlineAccountCardAddRequest.getEmployeeAccountId())
        && Strings.isNotBlank(worldlineAccountCardAddRequest.getCompanyAccountId());

    //validate if the authenticated user is not in the same company as the cardholder throw exception
    if (Strings.isNotBlank(worldlineAccountCardAddRequest.getCompanyAccountId()) && !isSameCompany(
        cdhEmployee.getCompanyAccountId(),
        worldlineAccountCardAddRequest.getCompanyAccountId())) {
      throw new ValidateSameCompanyException();
    }
    if (isForOtherEmployee){
      var employee = cdhInnBusinessCardsService.getEmployee(worldlineAccountCardAddRequest.getCompanyAccountId(),
          worldlineAccountCardAddRequest.getEmployeeAccountId(), cdhEmployee.getUserEmail());
      Scheme scheme = extractScheme(countryCode);

      var cardHolderUserRequest = WorldlineCardHolderUserRequest.builder()
          .title(employee.getTitle())
          .forename(employee.getFirstName())
          .lastName(employee.getLastName())
          .emailAddress(employee.getEmailAddress())
          .mobile(employee.getPhoneNumber())
          .isConsentGiven(Scheme.DE.equals(scheme))
          .build();
      var response = worldlineService.addCardHolder(cardHolderUserRequest, tetheredUserId, scheme, clientIp);

      pibaAccountService.registerTetheredUser(employee.getGlobalCompanyId(), scheme,
          employee.getBartEmployeeId(), response.getTetheredUserGuid(), authorizationToken);
      worldlineAccountCardAddRequest.setApiUserGuid(response.getApiUserGuid());
    }

    WorldlineCardDetails worldlineCardDetails = worldlineService.addPIBACard(tetheredUserId,
        countryCode,
        worldlineAccountCardAddRequest);

    return new ResponseEntity<>(worldlineCardDetails, HttpStatus.OK);
  }

  @Operation(method = "updatePIBACard", summary = "Update a single PIBA card for the tethered user",
      description = "This endpoint provides the functionality to update a single PIBA card for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PutMapping(path = "/account/{tetheredUserId}/cards/{card-id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WorldlineCardDetails> updatePIBACard(
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @PathVariable("card-id") String cardId,
      @Parameter(required = true) @RequestParam("countryCode") String countryCode,
      @Parameter(required = true) @Valid @RequestBody WorldlineAccountCardUpdateRequest worldlineAccountCardUpdateRequest) {

    log.info("Called 'innb/account/{}/cards/{}' (PUT) for  countryCode: {} and worldlineAccountCardUpdateRequest: {}",
        LogFormatUtils.formatValue(tetheredUserId, true),
        LogFormatUtils.formatValue(cardId, true),
        LogFormatUtils.formatValue(countryCode, true),
        LogFormatUtils.formatValue(worldlineAccountCardUpdateRequest, true));

    return new ResponseEntity<>(worldlineService.updatePIBACard(tetheredUserId, Integer.parseInt(cardId),
        countryCode, worldlineAccountCardUpdateRequest), HttpStatus.OK);
  }

  @Operation(method = "getAllCards", summary = "Get all PIBA cards of an user for a PIBA tethered user",
      description = "This endpoint provides the functionality to retrieve all PIBA cards for an existing PIBA account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/account/{tetheredUserId}/cards", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WorldlineAccountCardsResponse> getAllPIBACards(
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @RequestParam("countryCode") String countryCode,
      @Parameter(required = true) @RequestBody WorldlineAccountCardRequest worldlineAccountCardRequest) {

    log.info("Called '/innb/account/{tetheredUserId}/cards' (GET) with  tetheredUserId: {} "
            + "countryCode: {} worldlineAccountCardRequest: {}",
        LogFormatUtils.formatValue(tetheredUserId, true),
        LogFormatUtils.formatValue(countryCode, true),
        LogFormatUtils.formatValue(worldlineAccountCardRequest, true));
    var accountCards = worldlineService.getAccountCards(countryCode,
        worldlineAccountCardRequest);

    WorldlineAccountCardsResponse worldlineAccountCards = mapToWorldlineAccountCards(accountCards);
    return new ResponseEntity<>(worldlineAccountCards, HttpStatus.OK);
  }

  @Operation(method = "activatePIBACard", summary = "Activate a single PIBA card for the tethered user",
      description = "This endpoint provides the functionality to activate a single PIBA card for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/account/{tetheredUserId}/cards/{card-id}/activate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> activatePIBACard(
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @PathVariable("card-id") String cardId,
      @Parameter(required = true) @RequestParam("countryCode") String countryCode) {

    log.info("Called '/account/{}/cards/{}/activate' (POST) for  countryCode: {}", LogFormatUtils.formatValue(tetheredUserId, true),
        LogFormatUtils.formatValue(cardId, true), LogFormatUtils.formatValue(countryCode, true));

    var result = worldlineService.activatePIBACard(tetheredUserId, Integer.parseInt(cardId), countryCode);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(method = "cancelAndReplacePIBACard", summary = "Cancel and replace a single PIBA card for the tethered user",
      description = "This endpoint provides the functionality to cancel and replace a single PIBA card for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/account/{tetheredUserId}/cards/{card-id}/cancel-replace", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WorldlineAccountCardCancelAndReplaceResponse> cancelAndReplacePIBACard(
      @Parameter(required = true) @RequestHeader("Authorization") String authorizationToken,
      @Parameter(required = true) @PathVariable("tetheredUserId") String tetheredUserId,
      @Parameter(required = true) @PathVariable("card-id") String cardId,
      @Parameter(required = true) @RequestBody WorldlineAccountCardCancelAndReplaceRequest worldlineAccountCardCancelAndReplaceRequest){

    log.info("Called '/account/{}/cards/{}/cancel-replace' (POST) for  worldlineAccountCardCancelAndReplaceRequest: {}",
        LogFormatUtils.formatValue(tetheredUserId, true),
        LogFormatUtils.formatValue(cardId, true),
        LogFormatUtils.formatValue(worldlineAccountCardCancelAndReplaceRequest, true));
    tokenService.retrieveEmailAndVerifyToken(authorizationToken);
    var response = worldlineService.cancelAndReplacePIBACard(tetheredUserId, Integer.parseInt(cardId),
        worldlineAccountCardCancelAndReplaceRequest);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(method = "inviteCardHolder", summary = "Invite someone to be a registered card holder",
      description = "This endpoint provides the functionality to invite someone to be a registered card holder")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/account/{tetheredUserGuid}/cards/{cardId}/invite", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> inviteCardHolder(
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
      @Parameter(required = true) @PathVariable("cardId") String cardId,
      @Parameter(required = true) @Valid @RequestBody WorldlineAccountCardInviteRequest worldlineAccountCardInviteRequest) {

    log.info("Called 'innb/account/{}/cards/{}/invite' (POST) for worldlineAccountCardInviteRequest: {}",
        LogFormatUtils.formatValue(tetheredUserGuid, true),
        LogFormatUtils.formatValue(cardId, true),
        LogFormatUtils.formatValue(worldlineAccountCardInviteRequest, true));

    var result = worldlineService.inviteCardHolder(tetheredUserGuid, Integer.parseInt(cardId), worldlineAccountCardInviteRequest);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(method = "getCostCentreDetails", summary = "Get cost centre details for the tethered user",
      description = "This endpoint provides the functionality to retrieve cost centre details for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(path = "/account/{tetheredUserGuid}/costCentreDetails", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CostCentreData>> getCostCentreDetails(
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
      HttpServletRequest httpServletRequest) {

    log.info("Called '/account/{}/costCentreDetails' (GET)", LogFormatUtils.formatValue(tetheredUserGuid, true));

    var clientIp = networkUtils.getClientIp(httpServletRequest);

    return new ResponseEntity<>(worldlineService.getCostCentreDetails(tetheredUserGuid, clientIp), HttpStatus.OK);
  }

  @Operation(method = "replaceCard", summary = "Replace a card for the tethered user",
      description = "This endpoint provides the functionality to replace a card for the tethered user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
  @PutMapping(path = "/account/{tetheredUserGuid}/card/{cardId}/replace", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> replaceCard(
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
      @Parameter(required = true) @PathVariable("cardId") Integer cardId,
      @Parameter(required = true) @RequestParam("scheme") Scheme scheme,
      @Parameter(required = true) @Valid @RequestBody WorldlineReplaceCardRequest worldlineReplaceCardRequest,
      HttpServletRequest httpServletRequest) {

    log.info("Called '/account/{}/card/{}/replace' (PUT), for scheme={} and worldlineReplaceCardRequest: {}",
        LogFormatUtils.formatValue(tetheredUserGuid, true),
        LogFormatUtils.formatValue(cardId, true),
        LogFormatUtils.formatValue(scheme, true),
        LogFormatUtils.formatValue(worldlineReplaceCardRequest, true));

    var clientIp = networkUtils.getClientIp(httpServletRequest);

    return new ResponseEntity<>(worldlineService.replaceCard(
        tetheredUserGuid, scheme, clientIp, cardId, worldlineReplaceCardRequest), HttpStatus.OK);
  }

  private WorldlineAccountCardsResponse mapToWorldlineAccountCards(
      CustomerAccountCardListResponseType accountCards) {
    var accountCardsList = accountCards.getCustomerAccountCardListItem().stream()
        .map(card -> new InnBusinessPayCard(card.isIsMyCard(), String.valueOf(card.getCardId()),
            card.getCardName(),
            card.getContextCan(), card.getPAN(), CardStatusEnum.fromValue(card.getStatus()),
            card.getCountRegistrations(),
            card.isIsActivated()))
        .toList();
    List<InnBusinessPayCard> innBusinessPaymentCardList = new ArrayList<>(accountCardsList);
    return getWorldlineAccountCardsResponse(accountCards, innBusinessPaymentCardList);
  }

  private WorldlineAccountCardsResponse getWorldlineAccountCardsResponse(
      CustomerAccountCardListResponseType accountCards,
      List<InnBusinessPayCard> innBusinessPaymentCardList) {
    WorldlineAccountCardsResponse accountCardResponse = new WorldlineAccountCardsResponse();
    accountCardResponse.setInnBusinessPayCardList(innBusinessPaymentCardList);
    accountCardResponse.setFrom(accountCards.getPagingResult().getFromRecord());
    accountCardResponse.setTo(accountCards.getPagingResult().getToRecord());
    accountCardResponse.setTotalCount(accountCards.getPagingResult().getTotalRecordCount());
    accountCardResponse.setLastPage(accountCards.getPagingResult().getLastPage());
    return accountCardResponse;
  }

  private Scheme extractScheme(String languageCode) {
    Scheme scheme = EnumUtils.getEnum(Scheme.class, parseCountryCode(languageCode));
    log.info("Scheme in extractScheme - {}", scheme);
    return isNull(scheme) ? Scheme.GB : scheme;
  }

  private String parseCountryCode(String countryCode) {
    var sanitizedCountryCode = countryCode.replaceAll("[^a-zA-Z0-9]", "");
    var returnStr = (sanitizedCountryCode.length() > 2
        ? sanitizedCountryCode.substring(sanitizedCountryCode.length() - 2)
        : sanitizedCountryCode).toUpperCase();
    log.info("returnStr {} from parse countryCode - {}", returnStr, sanitizedCountryCode);
    return returnStr;
  }

}
