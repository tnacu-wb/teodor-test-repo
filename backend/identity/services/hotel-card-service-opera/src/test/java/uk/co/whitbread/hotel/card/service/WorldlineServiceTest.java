package uk.co.whitbread.hotel.card.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feign.FeignException;
import jakarta.xml.bind.JAXBElement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.hotel.card.client.worldline.WorldlineClient;
import uk.co.whitbread.hotel.card.client.worldline.model.CostCentreData;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineAddress;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineContactDetails;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCostCentreDetailsResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineData;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardResponse;
import uk.co.whitbread.hotel.card.exceptions.CardHolderInvalidMobileException;
import uk.co.whitbread.hotel.card.exceptions.CardHolderNotCreatedException;
import uk.co.whitbread.hotel.card.mapper.worldline.WorldlineMapper;
import uk.co.whitbread.hotel.card.model.AddressCorrespondenceEnum;
import uk.co.whitbread.hotel.card.model.CardStatusEnum;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardAddRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceResponse;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardInviteRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardUpdateRequest;
import uk.co.whitbread.hotel.card.model.WorldlineCancelAndReplaceCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineCardRestriction;
import uk.co.whitbread.hotel.card.model.WorldlineRegisteredUser;
import uk.co.whitbread.hotel.card.properties.WorldlineRestProperties;
import uk.co.whitbread.hotel.card.properties.WorldlineRestProperties.PropertiesByLocation;
import uk.co.whitbread.hotel.card.service.worldline.WorldlineService;
import uk.co.whitbread.hotel.card.service.worldline.converter.IWorldlineSoapTransformer;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.service.worldline.validator.WorldlineResponseValidator;
import uk.co.whitbread.hotel.card.utils.WorldlineUtils;
import uk.co.whitbread.piba.api.exception.PibaException;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Piba;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Piba.Service;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivate;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAdd;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddResponse.Response;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAllDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancel;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardDespatchChoiceType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInfoType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInvite;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteCardholderResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListItemType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdate;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUsageRestrictionType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardView;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountOverviewType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserType;
import worldline.mst.bsm.api.b2b.pi.data.ECustomerAccountCardStatusType;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.PagingResultType;
import worldline.mst.bsm.api.b2b.pi.data.ProcessingMetaInfoType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetRequestType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WorldlineServiceTest {

  public static final String API_USER_GUID = "9B1B3ED5-F61C-403E-8A56-59F0B625F856";
  public static final String CLIENT_IP = "1.1.1.1";
  @InjectMocks
  WorldlineService worldlineService;

  @Mock
  WebServiceTemplate worldlineWebServiceTemplate;
  @Mock
  IWorldlineSoapTransformer worldlineSoapTransformer;
  @Mock
  WorldLineProperties worldLineProperties;
  @Mock
  WorldlineResponseValidator worldlineResponseValidator;
  @Mock
  WorldLineWebServiceMessageCallback worldLineWebServiceMessageCallback;
  @Mock
  WorldlineMapper worldlineMapper;
  @Mock
  WorldlineUtils worldlineUtils;
  @Mock
  WorldlineRestProperties worldlineRestProperties;
  @Mock
  WorldlineClient worldlineClient;

  private static final String URL = "https://test.worldline/mockurl";
  private static final String CLIENT_MESSAGE_ID = "mock";
  private static final String CULTURE_CODE = "en-GB";
  private static final String USERNAME = "gbtest";
  private static final String PASSWORD = "";
  private static final String TETHERED_USER_ID = "{mockdata}";
  private static final Integer CARD_ID = 11600;
  public static final int SCHEME_CUSTOMER_ID = 123456;
  public static final String RESULT_CODE = "OK";
  public static final String MESSAGE_GUID_TEST = "messageGuidTest";
  public static final String COUNTRY_CODE = "GB";
  public static final String WL_NAMESPACE_URI = "worldline.mst.bsm.api.b2b.pi.data.v1.1";


  @Test
  void getAccountRegisteredUsers_ShouldReturnUsers() {
    // Arrange
    Mockito.when(worldLineProperties.getPiba())
        .thenReturn(getPiba());
    Mockito.when(worldlineSoapTransformer.toGetAccountRegisteredUserListSoapRequest(any(), any(), anyInt()))
        .thenReturn(toGetAccountRegisteredUserListSoapRequest());
    Mockito.when(worldlineMapper.toWorldlineRegisteredUser(any()))
        .thenReturn(List.of(getWorldlineRegisteredUser(), getWorldlineRegisteredUser()));
    Mockito.when(worldlineSoapTransformer.toGetTetheredUserDetailsSoapRequest(any(), any()))
        .thenReturn(toGetTetheredUserDetailsDetailsRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(), any(TetheredUserDetailsGet.class), any()))
        .thenReturn(getTetheredUserDetailsGetResponseDetails());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(), any(CustomerAccountRegisteredUserList.class), any()))
        .thenReturn(getRegisteredUserListResponse());
    when(worldlineUtils.serializeObject(any(TetheredUserDetailsGetResponse.class)))
        .thenReturn("mockedSerializedObject");

    // Act
    var customerAccountRegisteredUserTypes = worldlineService.getAccountRegisteredUsers(
        TETHERED_USER_ID, COUNTRY_CODE);

    // Assert
    Assertions.assertNotNull(customerAccountRegisteredUserTypes);
    Assertions.assertEquals(2, customerAccountRegisteredUserTypes.size());
  }

  @Test
  void getTetheredUserDetails_ShouldReturnUserDetails() {

    // Arrange
    Mockito.when(worldLineProperties.getPiba())
        .thenReturn(getPiba());
    Mockito.when(worldlineSoapTransformer.toGetTetheredUserDetailsSoapRequest(any(), any()))
        .thenReturn(toGetTetheredUserDetailsDetailsRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(), any(), any()))
        .thenReturn(getTetheredUserDetailsGetResponseDetails());
    when(worldlineUtils.serializeObject(any(TetheredUserDetailsGetResponse.class)))
        .thenReturn("mockedSerializedObject");

    // Act
    var tetheredUserDetails = worldlineService.getTetheredUserDetails(TETHERED_USER_ID, COUNTRY_CODE);

    // Assert
    Assertions.assertNotNull(tetheredUserDetails);
    Assertions.assertEquals(SCHEME_CUSTOMER_ID,
        tetheredUserDetails.getTetheredUserDetails()
            .getCustomerAccountOverview().getSchemeCustomerId());

  }

  @Test
  void getPIBACard_ShouldReturnCardDetails() {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldlineSoapTransformer.toCustomerAccountCardView(any(), ArgumentMatchers.eq(CARD_ID), any()))
        .thenReturn(toCustomerAccountCardViewSoapRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardViewResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardViewResponse.class)))
        .thenReturn("mockedSerializedObject");
    when(worldlineMapper.toWorldlineCard(any(CustomerAccountCardViewResponseType.class))).thenReturn(getWorldlineCardDetails());

    var pibaCard = worldlineService.getPIBACard(TETHERED_USER_ID, CARD_ID, COUNTRY_CODE);

    Assertions.assertNotNull(pibaCard);
    Assertions.assertEquals(1000, pibaCard.getCardLimit());
    Assertions.assertEquals("John Doe", pibaCard.getCardHolderName());
    Assertions.assertNull(pibaCard.getCardRestriction());
  }

  @Test
  void activatePIBACard_ShouldActivateCard() {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldlineSoapTransformer.toCustomerAccountCardActivate(any(), ArgumentMatchers.eq(CARD_ID), any()))
        .thenReturn(toCustomerAccountCardActivateSoapRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardActivateResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardActivateResponse.class)))
        .thenReturn("mockedSerializedObject");

    var pibaCard = worldlineService.activatePIBACard(TETHERED_USER_ID, CARD_ID, COUNTRY_CODE);

    Assertions.assertNotNull(pibaCard);
    Assertions.assertEquals("OK", pibaCard);
  }

  private WorldlineCardDetails getWorldlineCardDetails() {
    return WorldlineCardDetails.builder().cardLimit(1000).cardHolderName("John Doe").build();
  }

  @Test
  void updatePIBACard_ShouldUpdateCardDetails() {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldlineMapper.toCustomerAccountCardUpdateType(any()))
            .thenReturn(mockWLCustomerAccountCardUpdateType());
    Mockito.when(worldlineSoapTransformer.toCustomerAccountCardUpdate(any(), any(), any()))
        .thenReturn(toCustomerAccountCardUpdateSoapRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardUpdateResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardUpdateResponse.class)))
        .thenReturn("mockedSerializedObject");
    when(worldlineMapper.toWorldlineCard(any(CustomerAccountCardUpdateResponseType.class))).thenReturn(getWorldlineCardDetailsUpdated());


    var updatedPIBACard = worldlineService.updatePIBACard(TETHERED_USER_ID, CARD_ID, COUNTRY_CODE, mockWorldlineAccountCardUpdateRequest());

    Assertions.assertNotNull(updatedPIBACard);
    Assertions.assertEquals(1000, updatedPIBACard.getCardLimit());
    Assertions.assertEquals("John Doe Updated", updatedPIBACard.getCardHolderName());
    Assertions.assertTrue(updatedPIBACard.getCardRestriction().getRestrictCardUsage());
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Assertions.assertEquals("2024-09-25", dateFormat.format(updatedPIBACard.getCardRestriction().getStartDate()));
    Assertions.assertEquals("2024-10-25", dateFormat.format(updatedPIBACard.getCardRestriction().getEndDate()));
  }

  @Test
  void addPIBACard_ShouldAddCard() {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldlineMapper.toCustomerAccountCardAddType(any()))
        .thenReturn(mockWLCustomerAccountCardAddType());
    Mockito.when(worldlineSoapTransformer.toCustomerAccountCardAdd(any(), any(), any()))
        .thenReturn(toCustomerAccountCardAddSoapRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardAddResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardAddResponse.class)))
        .thenReturn("mockedSerializedObject");
    when(worldlineMapper.toWorldlineCard(any(Response.class))).thenReturn(getWorldlineCardDetailsAdded());

    var addedPIBACard = worldlineService.addPIBACard(TETHERED_USER_ID, COUNTRY_CODE, mockWorldlineAccountCardAddRequest());

    Assertions.assertNotNull(addedPIBACard);
    Assertions.assertEquals(1000, addedPIBACard.getCardLimit());
    Assertions.assertEquals("John Doe Added", addedPIBACard.getCardHolderName());
    Assertions.assertTrue(addedPIBACard.getCardRestriction().getRestrictCardUsage());
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Assertions.assertEquals("2024-09-25", dateFormat.format(addedPIBACard.getCardRestriction().getStartDate()));
    Assertions.assertEquals("2024-10-25", dateFormat.format(addedPIBACard.getCardRestriction().getEndDate()));
    Assertions.assertEquals(7788435, addedPIBACard.getPrimaryUserId());
  }

  private WorldlineCardDetails getWorldlineCardDetailsUpdated() {
    return WorldlineCardDetails.builder()
        .cardHolderName("John Doe Updated")
        .cardLimit(1000)
        .cardRestriction(getWorldlineCardRestriction())
        .build();
  }

  private WorldlineCardDetails getWorldlineCardDetailsAdded() {
    return WorldlineCardDetails.builder()
        .cardHolderName("John Doe Added")
        .cardLimit(1000)
        .primaryUserId(7788435)
        .cardRestriction(getWorldlineCardRestriction())
        .build();
  }

  private WorldlineCardRestriction getWorldlineCardRestriction() {
    return WorldlineCardRestriction.builder()
        .restrictCardUsage(true)
        .startDate(getWLRestrictionCalendar("2024-09-25", "RestrictionStart").getValue().toGregorianCalendar().getTime())
        .endDate(getWLRestrictionCalendar("2024-10-25", "RestrictionEnd").getValue().toGregorianCalendar().getTime())
        .build();
  }

  @Test
  void getAccountPIBACards_ShouldReturnCards() {
    // Arrange
    WorldlineAccountCardRequest worldlineAccountCardRequest = WorldlineAccountCardRequest.builder()
        .userId(TETHERED_USER_ID)
        .includeCancelledCards(true)
        .showMyCards(true)
        .maxRows(10)
        .pageNumber(10)
        .build();
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldlineSoapTransformer.toGetCustomerAccountCardListSoapRequest(any(), any(), anyInt()))
        .thenReturn(toGetAccountCardsListSoapRequest());
    Mockito.when(worldlineSoapTransformer.toGetTetheredUserDetailsSoapRequest(any(), any()))
        .thenReturn(toGetTetheredUserDetailsDetailsRequest());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(), any(TetheredUserDetailsGet.class), any()))
        .thenReturn(getTetheredUserDetailsGetResponseDetails());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(CustomerAccountCardList.class), any())).thenReturn(getAccountCardsListResponse());
    when(worldlineUtils.serializeObject(any(TetheredUserDetailsGetResponse.class)))
        .thenReturn("mockedSerializedObject");

    // Act
    CustomerAccountCardListResponseType customerAccountCardListResponseType = worldlineService.getAccountCards(
        COUNTRY_CODE, worldlineAccountCardRequest);

    // Assert
    Assertions.assertNotNull(customerAccountCardListResponseType);
    Assertions.assertEquals(1,
        customerAccountCardListResponseType.getCustomerAccountCardListItem().size());

  }

  @Test
  void addCardHolders_ShouldAddCardHolderForGB() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John")
        .lastName("Doe")
        .isConsentGiven(false)
        .build();
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any()))
        .thenReturn(createWorldlineCardHolderUserResponse());

    // Act
    var response = worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.GB, CLIENT_IP);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(API_USER_GUID, response.getApiUserGuid());
    Assertions.assertEquals(TETHERED_USER_ID, response.getTetheredUserGuid());
  }

  @Test
  void addCardHolders_ShouldAddCardHolderForDE() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John")
        .lastName("Doe")
        .isConsentGiven(true)
        .build();
    when(worldlineRestProperties.getDe()).thenReturn(getDe());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any()))
        .thenReturn(createWorldlineCardHolderUserResponse());

    // Act
    var response = worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.DE, CLIENT_IP);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(API_USER_GUID, response.getApiUserGuid());
    Assertions.assertEquals(TETHERED_USER_ID, response.getTetheredUserGuid());
  }

  @Test
  void addCardHolders_With_Empty_Response_ShouldThrowError() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John")
        .lastName("Doe")
        .isConsentGiven(false)
        .build();
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any()))
        .thenReturn(WorldlineCardHolderUserResponse.builder().build());

    // Act & Assert
    Assertions.assertThrows(CardHolderNotCreatedException.class,
        () -> worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.GB, CLIENT_IP));

  }

  @Test
  void addCardHolders_With_Empty_TetheredUserGuid_ShouldThrowError() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John")
        .lastName("Doe")
        .isConsentGiven(false)
        .build();
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any()))
        .thenReturn(WorldlineCardHolderUserResponse.builder()
            .responseCode("200")
            .data(WorldlineData.builder()
                .apiUserGuid(API_USER_GUID)
                .build())
            .build());

    // Act & Assert
    Assertions.assertThrows(CardHolderNotCreatedException.class,
        () -> worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.GB, CLIENT_IP));

  }

  @Test
  void addCardHolders_With_Empty_ApiUserGuid_ShouldThrowError() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John")
        .lastName("Doe")
        .isConsentGiven(false)
        .build();
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any()))
        .thenReturn(WorldlineCardHolderUserResponse.builder()
            .responseCode("200")
            .data(WorldlineData.builder()
                .tetheredUserGuid(TETHERED_USER_ID)
                .build())
            .build());

    // Act & Assert
    Assertions.assertThrows(CardHolderNotCreatedException.class,
        () -> worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.GB, CLIENT_IP));

  }

  @Test
  void addCardHolder_WithInvalidMobile_ShouldThrowException() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John")
        .lastName("Doe")
        .emailAddress("email@test.com")
        .mobile("12312312300000000")
        .isConsentGiven(false)
        .build();
    var feignException = mock(FeignException.class);
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any())).thenThrow(feignException);
    when(feignException.contentUTF8()).thenReturn("...InvalidMobile...");

    // Act & Assert
    Assertions.assertThrows(CardHolderInvalidMobileException.class,
        () -> worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.GB, CLIENT_IP));
  }

  @Test
  void addCardHolder_WithOtherFeignException_ShouldThrowException() {
    // Arrange
    var request = WorldlineCardHolderUserRequest.builder()
        .title("Mr")
        .forename("John!@_1")
        .lastName("Doe")
        .emailAddress("email@test.com")
        .mobile("1234567890")
        .isConsentGiven(false)
        .build();
    var feignException = mock(FeignException.class);
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.createCardHolderUser(any(), any(), any(), any(), any(), any())).thenThrow(feignException);
    when(feignException.contentUTF8()).thenReturn("...InvalidForeName...");

    // Act & Assert
    Assertions.assertThrows(CardHolderNotCreatedException.class,
        () -> worldlineService.addCardHolder(request, TETHERED_USER_ID, Scheme.GB, CLIENT_IP));
  }

  @ParameterizedTest
  @CsvSource({"DE", "GB"})
  void cancelAndReplacePIBACard_ShouldCancelCard(Scheme scheme) {

    when(worldLineProperties.getPiba()).thenReturn(getPiba());
    when(worldlineSoapTransformer.toCustomerAccountCardCancelAndReplace(any(), anyInt(), any(), any()))
        .thenReturn(createCustomerAccountCardCancel());
    when(worldlineMapper.toCustomerAccountCardDespatchChoiceType(any()))
        .thenReturn(createCustomerAccountCardCancelRequestType());
    when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardCancelResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardActivateResponse.class)))
        .thenReturn("mockedSerializedObject");
    when(worldlineMapper.toWorldlineCardCancelAndReplace(any(CustomerAccountCardCancelResponseType.class)))
        .thenReturn(createWorldlineAccountCardCancelAndReplaceResponse());
    when(worldlineUtils.formatId(anyString())).thenReturn("formattedId");


    var response = worldlineService.cancelAndReplacePIBACard(TETHERED_USER_ID, CARD_ID,
        createWorldlineAccountCardCancelRequest(scheme));

    Assertions.assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({"DE", "GB"})
  void cancelAndReplacePIBACard_ShouldReplaceCard(Scheme scheme) {

    when(worldLineProperties.getPiba()).thenReturn(getPiba());
    when(worldlineSoapTransformer.toCustomerAccountCardCancelAndReplace(any(), anyInt(), any(), any()))
        .thenReturn(createCustomerAccountCardCancel());
    when(worldlineMapper.toCustomerAccountCardDespatchChoiceType(any()))
        .thenReturn(createCustomerAccountCardCancelRequestType());
    when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardCancelResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardActivateResponse.class)))
        .thenReturn("mockedSerializedObject");
    when(worldlineMapper.toWorldlineCardCancelAndReplace(any(CustomerAccountCardCancelResponseType.class)))
        .thenReturn(createWorldlineAccountCardCancelAndReplaceResponse());
    when(worldlineUtils.formatId(anyString())).thenReturn("formattedId");


    var response = worldlineService.cancelAndReplacePIBACard(TETHERED_USER_ID, CARD_ID,
        createWorldlineAccountCardReplaceRequest(scheme));

    Assertions.assertNotNull(response);
  }

  @Test
  void inviteCardHolder_ShouldInviteCardHolder() {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldlineMapper.toCustomerAccountCardInviteDetailsType(any()))
        .thenReturn(mockCustomerAccountCardInviteDetailsType());
    Mockito.when(worldlineSoapTransformer.toCustomerAccountCardInvite(any(), anyInt(), any(), any()))
        .thenReturn(createCustomerAccountCardInvite());
    Mockito.when(worldlineWebServiceTemplate.marshalSendAndReceive(any(),
        any(), any())).thenReturn(getCustomerAccountCardInviteResponse());
    when(worldlineUtils.serializeObject(any(CustomerAccountCardInviteResponse.class)))
        .thenReturn("mockedSerializedObject");

    var response = worldlineService.inviteCardHolder(TETHERED_USER_ID, CARD_ID, mockWorldlineAccountCardInviteRequest());

    Assertions.assertNotNull(response);
  }

  @Test
  void getCostCentreDetails_ShouldGetCostCentreDetails() {
    // Arrange
    when(worldlineRestProperties.getDe()).thenReturn(getDe());
    when(worldlineClient.costCentreDetails(any(), any(), any(), any(), any()))
        .thenReturn(createWorldlineCostCentreDetailsResponse());

    // Act
    var response = worldlineService.getCostCentreDetails(TETHERED_USER_ID, CLIENT_IP);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(123, response.get(0).getAccountUniqueCustomerId());
    Assertions.assertEquals(456, response.get(0).getCostCentreUniqueCustomerId());
    Assertions.assertEquals("001", response.get(0).getCostCentreCode());
    Assertions.assertEquals("test", response.get(0).getCostCentreName());
  }

  @Test
  void replaceCard_ShouldReplaceCard() {
    // Arrange
    when(worldlineRestProperties.getGb()).thenReturn(getGb());
    when(worldlineClient.replaceCard(any(), any(), any(), any(), any(), any()))
        .thenReturn(createWorldlineReplaceCardResponse());
    var request = createWorldlineReplaceCardRequest();

    // Act
    var response = worldlineService.replaceCard(
        TETHERED_USER_ID, Scheme.GB, CLIENT_IP, CARD_ID, request);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals("Card replaced successfully", response);

  }

  private WorldlineReplaceCardRequest createWorldlineReplaceCardRequest() {
    return WorldlineReplaceCardRequest.builder()
        .shouldDespatchToCardholder(true)
        .contactDetails(WorldlineContactDetails.builder()
            .title("Mr")
            .foreName("John")
            .lastName("Doe")
            .position("Manager")
            .telephone("1234567890")
            .mobile("0987654321")
            .email("email@test.com")
            .build())
        .address(WorldlineAddress.builder()
            .addressLine1("Line1")
            .addressLine2("Line2")
            .addressLine3("Line3")
            .addressLine4("Line4")
            .postcode("Postcode")
            .countryCode("GB")
            .build())
        .cancelCard(false)
        .build();
  }

  private WorldlineReplaceCardResponse createWorldlineReplaceCardResponse() {
    return WorldlineReplaceCardResponse.builder()
        .responseCode("200")
        .data("Card replaced successfully")
        .errors(null)
        .build();
  }

  private WorldlineCostCentreDetailsResponse createWorldlineCostCentreDetailsResponse() {
    return WorldlineCostCentreDetailsResponse.builder()
        .responseCode("200")
        .data(List.of(CostCentreData.builder()
            .accountUniqueCustomerId(123)
            .costCentreUniqueCustomerId(456)
            .costCentreCode("001")
            .costCentreName("test")
            .build()))
        .errors(null)
        .build();
  }

  private WorldlineAccountCardInviteRequest mockWorldlineAccountCardInviteRequest() {
    return WorldlineAccountCardInviteRequest.builder()
        .scheme(Scheme.GB)
        .registrationInfoTitle("Mr")
        .registrationInfoForename("John")
        .registrationInfoSurname("Doe")
        .registrationInfoEmailAddress("email@email.com")
        .sendMeCopyOfInvite(true)
        .build();
  }

  private CustomerAccountCardInviteResponse getCustomerAccountCardInviteResponse() {
    var responseType = new CustomerAccountCardInviteCardholderResponseType();
    responseType.setResultCode(RESULT_CODE);
    var response = new CustomerAccountCardInviteResponse();
    response.setResponse(responseType);
    return response;
  }

  private CustomerAccountCardInvite createCustomerAccountCardInvite() {
    return new CustomerAccountCardInvite();
  }

  private CustomerAccountCardInviteDetailsType mockCustomerAccountCardInviteDetailsType() {
    CustomerAccountCardInviteDetailsType customerAccountCardInviteDetailsType = new CustomerAccountCardInviteDetailsType();
    customerAccountCardInviteDetailsType.setRegInfoTitle("Mr");
    customerAccountCardInviteDetailsType.setRegInfoForename("John");
    customerAccountCardInviteDetailsType.setRegInfoSurname("Doe");
    customerAccountCardInviteDetailsType.setRegInfoEmailAddress("email@email.com");
    customerAccountCardInviteDetailsType.setSendMeACopyOfInvite(true);

    return customerAccountCardInviteDetailsType;
  }

  private CustomerAccountCardCancelRequestType createCustomerAccountCardCancelRequestType() {
    var requestType = new CustomerAccountCardCancelRequestType();
    requestType.setTetheredUserGuid(TETHERED_USER_ID);
    requestType.setCardId(CARD_ID);
    requestType.setIssueReplacement(false);
    requestType.setAPIUserGuid(API_USER_GUID);
    requestType.setDespatchChoice(new CustomerAccountCardDespatchChoiceType());
    return requestType;
  }

  private WorldlineAccountCardCancelAndReplaceResponse createWorldlineAccountCardCancelAndReplaceResponse() {
    return WorldlineAccountCardCancelAndReplaceResponse.builder()
        .cancelledCardDetails(WorldlineCancelAndReplaceCardDetails.builder()
            .firstName("John")
            .lastName("Doe")
            .build())
        .build();
  }

  private CustomerAccountCardCancelResponse getCustomerAccountCardCancelResponse() {
    var responseType = new CustomerAccountCardCancelResponseType();
    responseType.setResultCode(RESULT_CODE);
    var response = new CustomerAccountCardCancelResponse();
    response.setResponse(responseType);
    return response;
  }

  private CustomerAccountCardCancel createCustomerAccountCardCancel() {
    return new CustomerAccountCardCancel();
  }

  private WorldlineAccountCardCancelAndReplaceRequest createWorldlineAccountCardCancelRequest(Scheme scheme) {
    return WorldlineAccountCardCancelAndReplaceRequest.builder()
        .issueReplacement(false)
        .scheme(scheme)
        .apiUserGuid(API_USER_GUID)
        .build();
  }

  private WorldlineAccountCardCancelAndReplaceRequest createWorldlineAccountCardReplaceRequest(Scheme scheme) {
    return WorldlineAccountCardCancelAndReplaceRequest.builder()
        .issueReplacement(true)
        .scheme(scheme)
        .apiUserGuid(API_USER_GUID)
        .cardDeliveryAddressType(AddressCorrespondenceEnum.COMPANY_CORRESPONDENCE_ADDRESS)
        .cardCorrespondenceAddress(new uk.co.whitbread.hotel.card.model.InnBusinessCorrespondenceAddress(
            "Mr", "John", "Doe", "Line1", "Line2", "Line3",
            "Line4", "PostCode", "GB", false
        ))
        .build();
  }

  private WorldlineCardHolderUserResponse createWorldlineCardHolderUserResponse() {
    return WorldlineCardHolderUserResponse.builder()
        .responseCode("200")
        .data(WorldlineData.builder()
            .tetheredUserGuid(TETHERED_USER_ID)
            .apiUserGuid(API_USER_GUID)
            .build())
        .build();
  }

  private PropertiesByLocation getGb() {
    return new PropertiesByLocation("en-GB",
        "gbtest",
        "test",
        "35");
  }

  private PropertiesByLocation getDe() {
    return new PropertiesByLocation("de-DE",
        "gbtest",
        "test",
        "91");
  }

  private Piba getPiba() {
    Piba piba = new Piba();
    Service service = new Service();
    service.setUrl(URL);
    piba.setService(service);
    piba.setUsername("");
    piba.setPassword("");
    return piba;
  }

  private CustomerAccountRegisteredUserListResponse getRegisteredUserListResponse() {
    var responseType = new CustomerAccountRegisteredUserListResponseType();
    responseType.getRegisteredUsers().addAll(getCustomerAccountRegisteredUserType());
    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    CustomerAccountRegisteredUserListResponse response = new CustomerAccountRegisteredUserListResponse();
    response.setResponse(responseType);
    return response;
  }

  private List<CustomerAccountRegisteredUserType> getCustomerAccountRegisteredUserType() {
    CustomerAccountRegisteredUserType mock1 = new CustomerAccountRegisteredUserType();
    mock1.setAPIUserGuid("9B1B3ED5-F61C-403E-8A56-59F0B625F856");
    mock1.setDisplayName("mock1");
    mock1.setHasAddress(false);
    CustomerAccountRegisteredUserType mock2 = new CustomerAccountRegisteredUserType();
    mock1.setAPIUserGuid("421CE8E5-4E87-4829-B78D-18EACE235A56");
    mock1.setDisplayName("mock2");
    mock1.setHasAddress(true);
    List<CustomerAccountRegisteredUserType> customerAccountRegisteredUserTypes = new ArrayList<>();
    customerAccountRegisteredUserTypes.add(mock1);
    customerAccountRegisteredUserTypes.add(mock2);
    return customerAccountRegisteredUserTypes;
  }

  private TetheredUserDetailsGetResponse getTetheredUserDetailsGetResponseDetails() {
    var user = new TetheredUserDetailsGetResponseType();
    user.setTetheredUserDetails(getTetheredUserDetailsDetailsType());
    user.setResultCode(RESULT_CODE);
    user.setMetaInfo(createProcessingMetaInfoType());
    var response = new TetheredUserDetailsGetResponse();
    response.setResponse(user);
    return response;
  }

  private TetheredUserDetailsType getTetheredUserDetailsDetailsType() {
    var accountOverview = new CustomerAccountOverviewType();
    accountOverview.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
    var customer = new TetheredUserDetailsType();
    customer.setCustomerAccountOverview(accountOverview);
    return customer;
  }

  private TetheredUserDetailsGet toGetTetheredUserDetailsDetailsRequest() {
    var request = new TetheredUserDetailsGetRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    var tetheredUserDetailsGet = new TetheredUserDetailsGet();
    tetheredUserDetailsGet.setRequest(request);
    return tetheredUserDetailsGet;
  }

  private CustomerAccountCardActivateResponse getCustomerAccountCardActivateResponse() {
    var responseType = new CustomerAccountCardActivateResponseType();
    responseType.setResultCode("OK");
    var response = new CustomerAccountCardActivateResponse();
    response.setResponse(responseType);
    return response;
  }

  private CustomerAccountCardViewResponse getCustomerAccountCardViewResponse() {
    var responseType = new CustomerAccountCardViewResponseType();

    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    var card = new CustomerAccountCardInfoType();
    var cardDetails = new CustomerAccountCardAllDetailsType();
    card.setIsMyCard(true);
    card.setIsActivated(true);
    card.setPAN("111222333444");
    card.setStatus(ECustomerAccountCardStatusType.CURRENT);
    cardDetails.setDisplayName("John Doe");
    cardDetails.setInfo(card);
    responseType.setCard(cardDetails);
    var response = new CustomerAccountCardViewResponse();
    response.setResponse(responseType);
    return response;
  }

  private CustomerAccountCardUpdateResponse getCustomerAccountCardUpdateResponse() {
    var responseType = new CustomerAccountCardUpdateResponseType();

    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    var card = new CustomerAccountCardInfoType();
    var cardDetails = new CustomerAccountCardAllDetailsType();
    card.setIsMyCard(true);
    card.setIsActivated(true);
    card.setPAN("111222333444");
    card.setStatus(ECustomerAccountCardStatusType.CURRENT);
    cardDetails.setDisplayName("John Doe Updated");
    cardDetails.setInfo(card);
    responseType.setCard(cardDetails);
    var response = new CustomerAccountCardUpdateResponse();
    response.setResponse(responseType);
    return response;
  }

  private CustomerAccountCardAddResponse getCustomerAccountCardAddResponse() {
    var responseType = new Response();

    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    var card = new CustomerAccountCardInfoType();
    var cardDetails = new CustomerAccountCardAllDetailsType();
    card.setIsMyCard(true);
    card.setIsActivated(true);
    card.setPAN("111222333444");
    card.setStatus(ECustomerAccountCardStatusType.CURRENT);
    card.setPrimarySchemeCustomerId(7788435);
    card.setSchemeCustomerId(7788435);
    cardDetails.setDisplayName("John Doe Added");
    cardDetails.setInfo(card);
    responseType.setCard(cardDetails);
    var response = new CustomerAccountCardAddResponse();
    response.setResponse(responseType);
    return response;
  }

  private CustomerAccountRegisteredUserList toGetAccountRegisteredUserListSoapRequest() {
    CustomerAccountRegisteredUserListRequestType request = new CustomerAccountRegisteredUserListRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    request.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
    var customerAccountRegisteredUserList = new CustomerAccountRegisteredUserList();
    customerAccountRegisteredUserList.setRequest(request);
    return customerAccountRegisteredUserList;
  }

  private CustomerAccountCardView toCustomerAccountCardViewSoapRequest() {
    var request = new CustomerAccountCardViewRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    request.setCardId(11600);
    var customerAccountCardView = new CustomerAccountCardView();
    customerAccountCardView.setRequest(request);
    return customerAccountCardView;
  }

  private CustomerAccountCardActivate toCustomerAccountCardActivateSoapRequest() {
    var request = new CustomerAccountCardActivateRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    request.setCardId(11600);
    var customerAccountCardActivate = new CustomerAccountCardActivate();
    customerAccountCardActivate.setRequest(request);
    return customerAccountCardActivate;
  }

  CustomerAccountCardUpdate toCustomerAccountCardUpdateSoapRequest() {
    var request = new CustomerAccountCardUpdateRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    request.setCard(mockWLCustomerAccountCardUpdateType());
    var customerAccountCardUpdate = new CustomerAccountCardUpdate();
    customerAccountCardUpdate.setRequest(request);
    return customerAccountCardUpdate;
  }

  private CustomerAccountCardAdd toCustomerAccountCardAddSoapRequest() {
    var request = new CustomerAccountCardAddRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    request.setCard(mockWLCustomerAccountCardAddType());
    var customerAccountCardAdd = new CustomerAccountCardAdd();
    customerAccountCardAdd.setRequest(request);
    return customerAccountCardAdd;
  }

  private CustomerAccountCardListResponse getAccountCardsListResponse() {
    var responseType = new CustomerAccountCardListResponseType();
    responseType.getCustomerAccountCardListItem().addAll(getAccountCardsListItemType());
    responseType.setPagingResult(createPagingResultType(1, 1, 1, 1));
    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    CustomerAccountCardListResponse response = new CustomerAccountCardListResponse();
    response.setResponse(responseType);
    return response;
  }

  private List<CustomerAccountCardListItemType> getAccountCardsListItemType() {
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

  private CustomerAccountCardList toGetAccountCardsListSoapRequest() {
    CustomerAccountCardListRequestType request = new CustomerAccountCardListRequestType();
    request.setHeader(createHeaderType());
    request.setTrustedPartnerCredentials(createTrustedPartnerCredentialsType());
    request.setTetheredUserGuid(TETHERED_USER_ID);
    request.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
    var accountRegisteredUserList = new CustomerAccountCardList();
    accountRegisteredUserList.setRequest(request);
    return accountRegisteredUserList;
  }

  private ProcessingMetaInfoType createProcessingMetaInfoType() {
    var processingMetaInfoType = new ProcessingMetaInfoType();
    processingMetaInfoType.setMessageGuid(MESSAGE_GUID_TEST);
    return processingMetaInfoType;
  }

  private HeaderType createHeaderType() {
    var headerType = new HeaderType();
    headerType.setClientMessageId(CLIENT_MESSAGE_ID);
    headerType.setCultureCode(CULTURE_CODE);
    return headerType;
  }

  private TrustedPartnerCredentialsType createTrustedPartnerCredentialsType() {
    var trustedPartnerCredentialsType = new TrustedPartnerCredentialsType();
    trustedPartnerCredentialsType.setUsername(USERNAME);
    trustedPartnerCredentialsType.setPassword(PASSWORD);
    return trustedPartnerCredentialsType;
  }

  private PagingResultType createPagingResultType(int fromRecord, int toRecord, int totalRecordCount, int lastPage) {
    var pagingResultType = new PagingResultType();
    pagingResultType.setFromRecord(fromRecord);
    pagingResultType.setToRecord(toRecord);
    pagingResultType.setTotalRecordCount(totalRecordCount);
    pagingResultType.setLastPage(lastPage);
    return pagingResultType;
  }

  private WorldlineRegisteredUser getWorldlineRegisteredUser() {
    return WorldlineRegisteredUser.builder()
        .hasAddress(false)
        .displayName("mock1")
        .apiUserGuid("9B1B3ED5-F61C-403E-8A56-59F0B625F856")
        .build();
  }

  private WorldlineAccountCardUpdateRequest mockWorldlineAccountCardUpdateRequest() {
    return WorldlineAccountCardUpdateRequest.builder()
        .displayName("John Doe")
        .cardLimit(1000)
        .restrictCardUsage(true)
        .restrictionStart("2024-09-25")
        .restrictionEnd("2024-10-25")
        .build();
  }

  private WorldlineAccountCardAddRequest mockWorldlineAccountCardAddRequest() {
    return WorldlineAccountCardAddRequest.builder()
        .displayName("John Doe Added")
        .cardLimit(1000)
        .restrictCardUsage(true)
        .restrictionStart("2024-09-25")
        .restrictionEnd("2024-10-25")
        .primarySchemeCustomerId(7788435)
        .build();
  }

  private CustomerAccountCardUpdateType mockWLCustomerAccountCardUpdateType() {
    var mockCard = new CustomerAccountCardUpdateType();
    mockCard.setDisplayName("John Doe Updated");
    mockCard.setCardLimit(new JAXBElement<>(new QName(WL_NAMESPACE_URI, "CardLimit"),
        Integer.class, null, 1001));
    mockCard.setCardUsageRestrictions(getWLCardUsageRestrictions());
    return mockCard;
  }

  private CustomerAccountCardAddType mockWLCustomerAccountCardAddType() {
    var mockCard = new CustomerAccountCardAddType();
    mockCard.setDisplayName("John Doe Added");
    mockCard.setCardLimit(new JAXBElement<>(new QName(WL_NAMESPACE_URI, "CardLimit"),
        Integer.class, null, 1001));
    mockCard.setCardUsageRestrictions(getWLCardUsageRestrictions());
    mockCard.setPrimarySchemeCustomerId(7788435);
    mockCard.setSchemeCustomerId(7788435);
    return mockCard;
  }

  private CustomerAccountCardUsageRestrictionType getWLCardUsageRestrictions() {
    var cardRestriction = new CustomerAccountCardUsageRestrictionType();
    cardRestriction.setRestrictCardUsage(true);
    cardRestriction.setRestrictionStart(getWLRestrictionCalendar("2024-09-25", "RestrictionStart"));
    cardRestriction.setRestrictionEnd(getWLRestrictionCalendar("2024-10-25", "RestrictionEnd"));
    return cardRestriction;
  }

  private JAXBElement<XMLGregorianCalendar> getWLRestrictionCalendar(String value, String localPart) {
    try {
      return new JAXBElement<>(new QName(WL_NAMESPACE_URI, localPart),
          XMLGregorianCalendar.class, null, DatatypeFactory.newInstance().newXMLGregorianCalendar(value));
    } catch (DatatypeConfigurationException e) {
      throw new PibaException("Exception caught when trying to obtain a new instance of DatatypeFactory: " + e.getMessage());
    }
  }

}
