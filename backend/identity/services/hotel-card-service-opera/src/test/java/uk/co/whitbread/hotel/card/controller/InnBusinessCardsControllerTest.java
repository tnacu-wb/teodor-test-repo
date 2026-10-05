package uk.co.whitbread.hotel.card.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.hotel.card.client.worldline.model.CostCentreData;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardRequest;
import uk.co.whitbread.hotel.card.exceptions.ValidateSameCompanyException;
import uk.co.whitbread.hotel.card.model.CardStatusEnum;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardAddRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceResponse;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardInviteRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardUpdateRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardsResponse;
import uk.co.whitbread.hotel.card.model.WorldlineCancelAndReplaceCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineCardHolderUserDetails;
import uk.co.whitbread.hotel.card.model.WorldlineRegisteredUser;
import uk.co.whitbread.hotel.card.service.CdhInnBusinessCardsService;
import uk.co.whitbread.hotel.card.service.PibaAccountService;
import uk.co.whitbread.hotel.card.service.worldline.WorldlineService;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.utils.NetworkUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListItemType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.PagingResultType;

@ExtendWith(MockitoExtension.class)
class InnBusinessCardsControllerTest {

  private static final String TETHERED_USER_ID = "9B1B3ED5-F61C-403E-8A56-59F0B625F856";
  private static final int CARD_ID = 11600;
  private static final String COUNTRY_CODE = "GB";
  private static final String CLIENT_IP = "1.1.1.1";
  public static final String AUTHORIZATION_TOKEN = "Authorization-Token";
  public static final String COMPANY_ACCOUNT_ID = "company-account-id";
  public static final String EMPLOYEE_ACCOUNT_ID = "employee-account-id";
  public static final String USER_EMAIL = "email";
  public static final String API_USER_GUID = "421CE8E5-4E87-4829-B78D-18EACE235A56";

  @Mock
  private WorldlineService worldlineService;

  @Mock
  private TokenService tokenService;

  @Mock
  private CdhInnBusinessCardsService cdhInnBusinessCardsService;

  @Mock
  private NetworkUtils networkUtils;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private PibaAccountService pibaAccountService;

  @InjectMocks
  private InnBusinessCardsController innBusinessCardsController;


  @Test
  void getRegisteredUsersShouldReturnReturn200Ok() {
    Mockito.when(worldlineService.getAccountRegisteredUsers(TETHERED_USER_ID, COUNTRY_CODE))
        .thenReturn(getWorldlineRegisteredUser());

    final ResponseEntity<List<WorldlineRegisteredUser>> worldlineRegisteredUsers =
        innBusinessCardsController.getPIBARegisteredUsers(TETHERED_USER_ID, COUNTRY_CODE);

    Assertions.assertNotNull(worldlineRegisteredUsers);
    assertEquals(HttpStatus.OK, worldlineRegisteredUsers.getStatusCode());
  }

  @Test
  void getPIBACardShouldReturnReturn200Ok() {
    Mockito.when(worldlineService.getPIBACard(TETHERED_USER_ID, CARD_ID, COUNTRY_CODE))
        .thenReturn(getWorldlineCardDetails());

    final ResponseEntity<WorldlineCardDetails> worldlineRegisteredUsers =
        innBusinessCardsController.getPIBACard(TETHERED_USER_ID, String.valueOf(CARD_ID), COUNTRY_CODE);

    Assertions.assertNotNull(worldlineRegisteredUsers);
    assertEquals(HttpStatus.OK, worldlineRegisteredUsers.getStatusCode());
  }

  @Test
  void updatePIBACardShouldReturnReturn200Ok() {
    Mockito.when(worldlineService.updatePIBACard(TETHERED_USER_ID, CARD_ID, COUNTRY_CODE, mockWorldlineAccountCardUpdateRequest()))
        .thenReturn(getWorldlineCardDetails());

    final ResponseEntity<WorldlineCardDetails> worldlineUpdatedCard =
        innBusinessCardsController.updatePIBACard(TETHERED_USER_ID, String.valueOf(CARD_ID), COUNTRY_CODE, mockWorldlineAccountCardUpdateRequest());

    Assertions.assertNotNull(worldlineUpdatedCard);
    assertEquals(HttpStatus.OK, worldlineUpdatedCard.getStatusCode());
  }

  @Test
  void addPIBACardShouldReturn200Ok() {
    Mockito.when(worldlineService.addPIBACard(TETHERED_USER_ID, COUNTRY_CODE, mockWorldlineAccountCardAddRequest()))
        .thenReturn(getWorldlineCardDetails());

    Mockito.when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
        .thenReturn(CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
            .userEmail(USER_EMAIL)
            .build());
    Mockito.when(networkUtils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);

    final ResponseEntity<WorldlineCardDetails> worldlineAddCard =
        innBusinessCardsController.addPIBACard(AUTHORIZATION_TOKEN, TETHERED_USER_ID, COUNTRY_CODE,
            mockWorldlineAccountCardAddRequest(), httpServletRequest);

    Assertions.assertNotNull(worldlineAddCard);
    assertEquals(HttpStatus.OK, worldlineAddCard.getStatusCode());
  }

  @Test
  void addPIBACardForOtherEmployeeShouldReturn200Ok() {
    Mockito.when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
        .thenReturn(CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
            .userEmail(USER_EMAIL)
            .build());
    Mockito.when(networkUtils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);

    Mockito.when(cdhInnBusinessCardsService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, USER_EMAIL))
        .thenReturn(createGetEmployeeResponse());

    Mockito.when(worldlineService.addCardHolder(any(), any(), any(), any()))
        .thenReturn(WorldlineCardHolderUserDetails.builder()
            .tetheredUserGuid(TETHERED_USER_ID)
            .apiUserGuid(API_USER_GUID)
            .build());
    Mockito.doNothing().when(pibaAccountService)
        .registerTetheredUser(any(), any(), any(), any(), any());
    Mockito.when(worldlineService.addPIBACard(any(), any(), any()))
        .thenReturn(getWorldlineCardDetails());

    final ResponseEntity<WorldlineCardDetails> worldlineAddCard =
        innBusinessCardsController.addPIBACard(AUTHORIZATION_TOKEN, TETHERED_USER_ID, COUNTRY_CODE,
            mockWorldlineAccountCardAddRequestForOtherEmployee(), httpServletRequest);

    Assertions.assertNotNull(worldlineAddCard);
    assertEquals(HttpStatus.OK, worldlineAddCard.getStatusCode());
  }

  @Test
  void addPIBACardForOtherEmployeeShouldThrowException() {
    WorldlineAccountCardAddRequest request = mockWorldlineAccountCardAddRequestForOtherEmployee();

    Mockito.when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
        .thenReturn(CdhEmployeeDetails.builder()
            .companyAccountId("other-company-account-id")
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
            .userEmail(USER_EMAIL)
            .build());
    Mockito.when(networkUtils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);

    assertThrows(ValidateSameCompanyException.class, () -> innBusinessCardsController.addPIBACard(
        AUTHORIZATION_TOKEN, TETHERED_USER_ID, COUNTRY_CODE, request, httpServletRequest));
  }

  @Test
  void getAccountCardsShouldReturnReturn200Ok() {
    WorldlineAccountCardRequest worldlineAccountCardRequest = WorldlineAccountCardRequest.builder()
        .userId(TETHERED_USER_ID)
        .includeCancelledCards(true)
        .showMyCards(true)
        .maxRows(10)
        .pageNumber(10)
        .build();

    Mockito.when(worldlineService.getAccountCards(COUNTRY_CODE, worldlineAccountCardRequest))
        .thenReturn(getWorldlineAccountCardsResponse(getWorldlineAccountCardsList()));

    final ResponseEntity<WorldlineAccountCardsResponse> worldlineAccountCards =
        innBusinessCardsController.getAllPIBACards(TETHERED_USER_ID, COUNTRY_CODE,
            worldlineAccountCardRequest);

    Assertions.assertNotNull(worldlineAccountCards);
    assertEquals(HttpStatus.OK, worldlineAccountCards.getStatusCode());
  }

  @Test
  void cancelAndReplacePIBACardShouldReturnReturn200Ok() {
    WorldlineAccountCardCancelAndReplaceRequest worldlineAccountCardCancelAndReplaceRequest = WorldlineAccountCardCancelAndReplaceRequest.builder()
        .issueReplacement(true)
        .scheme(Scheme.GB)
        .apiUserGuid(API_USER_GUID)
        .build();

    Mockito.when(worldlineService.cancelAndReplacePIBACard(TETHERED_USER_ID, CARD_ID, worldlineAccountCardCancelAndReplaceRequest))
        .thenReturn(WorldlineAccountCardCancelAndReplaceResponse.builder()
            .cancelledCardDetails(WorldlineCancelAndReplaceCardDetails.builder().build())
            .newCardDetails(WorldlineCancelAndReplaceCardDetails.builder().build())
            .build());

    final ResponseEntity<WorldlineAccountCardCancelAndReplaceResponse> worldlineAccountCards =
        innBusinessCardsController.cancelAndReplacePIBACard(AUTHORIZATION_TOKEN, TETHERED_USER_ID, String.valueOf(CARD_ID),
            worldlineAccountCardCancelAndReplaceRequest);

    Assertions.assertNotNull(worldlineAccountCards);
    assertEquals(HttpStatus.OK, worldlineAccountCards.getStatusCode());
  }

  @Test
  void inviteCardHolderShouldReturn200Ok() {
    WorldlineAccountCardInviteRequest worldlineAccountCardInviteRequest = WorldlineAccountCardInviteRequest.builder()
        .registrationInfoTitle("Mr")
        .registrationInfoForename("John")
        .registrationInfoSurname("Doe")
        .registrationInfoEmailAddress("email@email.com")
        .sendMeCopyOfInvite(true)
        .build();

    Mockito.when(worldlineService.inviteCardHolder(TETHERED_USER_ID, CARD_ID, worldlineAccountCardInviteRequest))
        .thenReturn("OK");

    final ResponseEntity<String> inviteCardHolderResponse =
        innBusinessCardsController.inviteCardHolder(TETHERED_USER_ID, String.valueOf(CARD_ID), worldlineAccountCardInviteRequest);

    Assertions.assertNotNull(inviteCardHolderResponse);
    assertEquals(HttpStatus.OK, inviteCardHolderResponse.getStatusCode());
  }

  @Test
  void getCostCentreDetailsShouldReturn200Ok() {
    Mockito.when(worldlineService.getCostCentreDetails(TETHERED_USER_ID, CLIENT_IP))
        .thenReturn(getCostCentreDataList());
    Mockito.when(networkUtils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);


    final ResponseEntity<List<CostCentreData>> getCostCentreDetailsResponse =
        innBusinessCardsController.getCostCentreDetails(TETHERED_USER_ID, httpServletRequest);

    Assertions.assertNotNull(getCostCentreDetailsResponse);
    assertEquals(HttpStatus.OK, getCostCentreDetailsResponse.getStatusCode());
  }

  @Test
  void replaceCardShouldReturn200Ok() {
    WorldlineReplaceCardRequest worldlineReplaceCardRequest = WorldlineReplaceCardRequest.builder()
        .uniqueCustomerCardId(CARD_ID)
        .shouldDespatchToCardholder(false)
        .build();

    Mockito.when(worldlineService.replaceCard(TETHERED_USER_ID, Scheme.GB, CLIENT_IP, CARD_ID, worldlineReplaceCardRequest))
        .thenReturn("OK");
    Mockito.when(networkUtils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);

    final ResponseEntity<String> replaceCardResponse =
        innBusinessCardsController.replaceCard(TETHERED_USER_ID, CARD_ID, Scheme.GB, worldlineReplaceCardRequest, httpServletRequest);

    Assertions.assertNotNull(replaceCardResponse);
    assertEquals(HttpStatus.OK, replaceCardResponse.getStatusCode());
  }

  private List<CostCentreData> getCostCentreDataList() {
    return List.of(CostCentreData.builder()
            .accountUniqueCustomerId(123)
            .costCentreUniqueCustomerId(456)
            .costCentreName("test")
            .costCentreCode("001")
        .build());
  }

  private List<WorldlineRegisteredUser> getWorldlineRegisteredUser() {
    new WorldlineRegisteredUser();
    return  List.of(WorldlineRegisteredUser.builder()
        .apiUserGuid("421CE8E5-4E87-4829-B78D-18EACE235A56")
        .displayName("mock1")
        .hasAddress(false)
        .build(), WorldlineRegisteredUser.builder()
        .apiUserGuid("9B1B3ED5-F61C-403E-8A56-59F0B625F856")
        .displayName("mock2")
        .hasAddress(true).build());

  }

  private WorldlineCardDetails getWorldlineCardDetails() {
    return WorldlineCardDetails.builder()
        .cardLimit(1000)
        .cardHolderName("John Doe").build();
  }

  private List<CustomerAccountCardListItemType> getWorldlineAccountCardsList() {
    var mockCard = new CustomerAccountCardListItemType();
    mockCard.setCardName("testName");
    mockCard.setCardId(3089);
    mockCard.setContextCan("CardActivate");
    mockCard.setStatus(CardStatusEnum.CURRENT.getValue());
    mockCard.setIsMyCard(true);
    mockCard.setCountRegistrations(1);
    mockCard.setIsActivated(true);
    var list = new ArrayList<CustomerAccountCardListItemType>();
    list.add(mockCard);
    return list;
  }

  private CustomerAccountCardListResponseType getWorldlineAccountCardsResponse(
      List<CustomerAccountCardListItemType> cardList) {
    var pagingResult = new PagingResultType();
    pagingResult.setFromRecord(1);
    pagingResult.setToRecord(2);
    pagingResult.setTotalRecordCount(2);
    pagingResult.setLastPage(1);

    var responseType = new CustomerAccountCardListResponseType();
    responseType.setResultCode("OK");
    responseType.getCustomerAccountCardListItem().addAll(cardList);
    responseType.setPagingResult(pagingResult);
    return responseType;
  }

  private WorldlineAccountCardUpdateRequest mockWorldlineAccountCardUpdateRequest() {
    return WorldlineAccountCardUpdateRequest.builder()
        .displayName("John Doe")
        .cardLimit(1000)
        .build();
  }

  private WorldlineAccountCardAddRequest mockWorldlineAccountCardAddRequest() {
    return WorldlineAccountCardAddRequest.builder()
        .displayName("John Doe")
        .cardLimit(1000)
        .primarySchemeCustomerId(7788435)
        .schemeCustomerId(7788435)
        .build();
  }

  private WorldlineAccountCardAddRequest mockWorldlineAccountCardAddRequestForOtherEmployee() {
    return WorldlineAccountCardAddRequest.builder()
        .displayName("John Doe")
        .cardLimit(1000)
        .primarySchemeCustomerId(7788435)
        .schemeCustomerId(7788435)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .build();
  }



  private GetEmployeeResponse createGetEmployeeResponse() {
    return GetEmployeeResponse.builder()
        .lastName("Doe")
        .firstName("John")
        .accessLevel("SUPER")
        .employeeAccountId("employee-account-id")
        .companyAccountId("company-account-id")
        .bartEmployeeId("7788435")
        .globalCompanyId("7788435")
        .emailAddress("email")
        .phoneNumber("1234567890")
        .build();
  }
}
