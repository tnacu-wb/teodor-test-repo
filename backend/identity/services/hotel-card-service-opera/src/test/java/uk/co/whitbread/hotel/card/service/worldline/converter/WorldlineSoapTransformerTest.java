package uk.co.whitbread.hotel.card.service.worldline.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.ConcurrentMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.model.WorldlineHeadersData;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.utils.WorldlineUtils;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.De;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Gb;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Piba;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Piba.Service;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardDespatchChoiceType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserList;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

@ExtendWith(MockitoExtension.class)
class WorldlineSoapTransformerTest {

  private static final String TETHERED_USER_ID = "9B1B3ED5-F61C-403E-8A56-59F0B625F856";
  private static final String API_USER_GUID = "9B1B3ED5-F61C-403E-8A56-59F0EWQDFS56";
  private static final Integer CARD_ID = 11600;
  private static final String COUNTRY_CODE = "GB";
  private static final int SCHEMA_CUSTOMER_ID = 783582;
  private static final String URL = "https://test.worldline/mockurl";

  @Mock
  private WorldLineProperties worldLineProperties;

  @Mock
  private WorldlineUtils worldlineUtils;

  @Mock
  private ConcurrentMap<Scheme, WorldlineHeadersData> worldlineConfigs;


  @Test
  void toGetAccountRegisteredUserListSoapRequest() {

    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());

    WorldlineSoapTransformer worldlineSoapTransformer = new WorldlineSoapTransformer(
        worldLineProperties, worldlineUtils);
    CustomerAccountRegisteredUserList getAccountRegisteredUserListSoapRequest = worldlineSoapTransformer.toGetAccountRegisteredUserListSoapRequest(
        TETHERED_USER_ID, COUNTRY_CODE, SCHEMA_CUSTOMER_ID);

    assertNotNull(getAccountRegisteredUserListSoapRequest);
    assertEquals(TETHERED_USER_ID,
        getAccountRegisteredUserListSoapRequest.getRequest().getTetheredUserGuid());
  }

  @Test
  void toCustomerAccountCardViewSoapRequest() {

    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    Mockito.when(worldlineUtils.formatId(ArgumentMatchers.anyString()))
        .thenReturn("{" + TETHERED_USER_ID +"}");
    var worldlineHeadersData = Mockito.mock(WorldlineHeadersData.class);
    Mockito.when(worldlineConfigs.get(ArgumentMatchers.any(Scheme.class)))
        .thenReturn(worldlineHeadersData);
    Mockito.when(worldlineHeadersData.headerType()).thenReturn(Mockito.mock(HeaderType.class));
    Mockito.when(worldlineHeadersData.trustedPartnerCredentialsType()).thenReturn(Mockito.mock(
        TrustedPartnerCredentialsType.class));
    var worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineConfigs", worldlineConfigs);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineUtils", worldlineUtils);
    Mockito.when(worldlineUtils.serializeObject(ArgumentMatchers.any()))
        .thenReturn("mockedSerializedObject");

    var customerAccountCardView = worldlineSoapTransformer.toCustomerAccountCardView(
        TETHERED_USER_ID, CARD_ID, COUNTRY_CODE);

    assertNotNull(customerAccountCardView);
    assertEquals("{" + TETHERED_USER_ID + "}",
        customerAccountCardView.getRequest().getTetheredUserGuid());
  }

  @Test
  void toCustomerAccountCardActivateSoapRequest() {

    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    Mockito.when(worldlineUtils.formatId(ArgumentMatchers.anyString()))
        .thenReturn("{" + TETHERED_USER_ID +"}");
    var worldlineHeadersData = Mockito.mock(WorldlineHeadersData.class);
    Mockito.when(worldlineConfigs.get(ArgumentMatchers.any(Scheme.class)))
        .thenReturn(worldlineHeadersData);
    Mockito.when(worldlineHeadersData.headerType()).thenReturn(Mockito.mock(HeaderType.class));
    Mockito.when(worldlineHeadersData.trustedPartnerCredentialsType()).thenReturn(Mockito.mock(
        TrustedPartnerCredentialsType.class));
    var worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineConfigs", worldlineConfigs);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineUtils", worldlineUtils);
    Mockito.when(worldlineUtils.serializeObject(ArgumentMatchers.any()))
        .thenReturn("mockedSerializedObject");

    var customerAccountCardActivate = worldlineSoapTransformer.toCustomerAccountCardActivate(
        TETHERED_USER_ID, CARD_ID, COUNTRY_CODE);

    assertNotNull(customerAccountCardActivate);
    assertEquals(CARD_ID, customerAccountCardActivate.getRequest().getCardId());
    assertEquals("{" + TETHERED_USER_ID + "}",
        customerAccountCardActivate.getRequest().getTetheredUserGuid());
  }

  @ParameterizedTest
  @ValueSource(strings = {"DE", "GB"})
  void toCustomerAccountCardUpdateSoapRequest(String countryCode) {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    Mockito.when(worldlineUtils.formatId(ArgumentMatchers.anyString()))
        .thenReturn("{" + TETHERED_USER_ID +"}");
    var worldlineHeadersData = Mockito.mock(WorldlineHeadersData.class);
    Mockito.when(worldlineConfigs.get(ArgumentMatchers.any(Scheme.class)))
        .thenReturn(worldlineHeadersData);
    Mockito.when(worldlineHeadersData.headerType()).thenReturn(Mockito.mock(HeaderType.class));
    Mockito.when(worldlineHeadersData.trustedPartnerCredentialsType()).thenReturn(Mockito.mock(
        TrustedPartnerCredentialsType.class));
    var worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineConfigs", worldlineConfigs);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineUtils", worldlineUtils);
    Mockito.when(worldlineUtils.serializeObject(ArgumentMatchers.any()))
        .thenReturn("mockedSerializedObject");
    var customerAccountCardUpdateType = Mockito.mock(CustomerAccountCardUpdateType.class);

    var customerAccountCardUpdate = worldlineSoapTransformer.toCustomerAccountCardUpdate(
        TETHERED_USER_ID, countryCode, customerAccountCardUpdateType);

    assertNotNull(customerAccountCardUpdate);
    assertEquals("{" + TETHERED_USER_ID + "}",
        customerAccountCardUpdate.getRequest().getTetheredUserGuid());
  }

  @ParameterizedTest
  @ValueSource(strings = {"DE", "GB"})
  void toCustomerAccountCardAddSoapRequest(String countryCode) {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    Mockito.when(worldlineUtils.formatId(ArgumentMatchers.anyString()))
        .thenReturn("{" + TETHERED_USER_ID +"}");
    var worldlineHeadersData = Mockito.mock(WorldlineHeadersData.class);
    Mockito.when(worldlineConfigs.get(ArgumentMatchers.any(Scheme.class)))
        .thenReturn(worldlineHeadersData);
    Mockito.when(worldlineHeadersData.headerType()).thenReturn(Mockito.mock(HeaderType.class));
    Mockito.when(worldlineHeadersData.trustedPartnerCredentialsType()).thenReturn(Mockito.mock(
        TrustedPartnerCredentialsType.class));
    var worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineConfigs", worldlineConfigs);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineUtils", worldlineUtils);
    Mockito.when(worldlineUtils.serializeObject(ArgumentMatchers.any()))
        .thenReturn("mockedSerializedObject");
    var customerAccountCardAddType = Mockito.mock(CustomerAccountCardAddType.class);

    var customerAccountCardAdd = worldlineSoapTransformer.toCustomerAccountCardAdd(
        TETHERED_USER_ID, countryCode, customerAccountCardAddType);

    assertNotNull(customerAccountCardAdd);
    assertEquals("{" + TETHERED_USER_ID + "}",
        customerAccountCardAdd.getRequest().getTetheredUserGuid());
  }

  @Test
  void toGetCustomerAccountCardListSoapRequest() {
    WorldlineAccountCardRequest worldlineAccountCardRequest = WorldlineAccountCardRequest.builder()
        .userId(TETHERED_USER_ID)
        .includeCancelledCards(true)
        .showMyCards(true)
        .maxRows(10)
        .pageNumber(10)
        .build();
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());

    WorldlineSoapTransformer worldlineSoapTransformer = new WorldlineSoapTransformer(
        worldLineProperties, worldlineUtils);
    CustomerAccountCardList getCustomerAccountCardListSoapRequest = worldlineSoapTransformer.toGetCustomerAccountCardListSoapRequest(
        worldlineAccountCardRequest ,COUNTRY_CODE, SCHEMA_CUSTOMER_ID);

    assertNotNull(getCustomerAccountCardListSoapRequest);
    assertEquals(TETHERED_USER_ID,
        getCustomerAccountCardListSoapRequest.getRequest().getTetheredUserGuid());

  }

  @Test
  void toGetTetheredUserDetailsSoapRequest(){
    // Arrange
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());

    // Act
    WorldlineSoapTransformer worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    TetheredUserDetailsGet userDetailsGet = worldlineSoapTransformer
        .toGetTetheredUserDetailsSoapRequest(TETHERED_USER_ID, COUNTRY_CODE);

    // Assert
    assertNotNull(userDetailsGet);
    assertEquals(TETHERED_USER_ID, userDetailsGet.getRequest().getTetheredUserGuid());

  }

  @ParameterizedTest
  @CsvSource({"DE", "GB"})
  void toCustomerAccountCardCancelAndReplaceRequest(Scheme scheme) {
    // Arrange
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    Mockito.when(worldlineUtils.formatId(ArgumentMatchers.anyString()))
        .thenReturn("{" + TETHERED_USER_ID +"}");
    var worldlineHeadersData = Mockito.mock(WorldlineHeadersData.class);
    Mockito.when(worldlineConfigs.get(ArgumentMatchers.any(Scheme.class)))
        .thenReturn(worldlineHeadersData);
    Mockito.when(worldlineHeadersData.headerType()).thenReturn(Mockito.mock(HeaderType.class));
    Mockito.when(worldlineHeadersData.trustedPartnerCredentialsType()).thenReturn(Mockito.mock(
        TrustedPartnerCredentialsType.class));
    var worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineConfigs", worldlineConfigs);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineUtils", worldlineUtils);

    // Act
    var request = worldlineSoapTransformer.toCustomerAccountCardCancelAndReplace(TETHERED_USER_ID,
        CARD_ID,
        createWorldlineAccountCardCancelAndReplaceRequest(scheme),
        createCustomerAccountCardCancelRequestType().getDespatchChoice());

    // Assert
    assertNotNull(request);
    assertEquals(TETHERED_USER_ID, request.getRequest().getTetheredUserGuid());
  }

  @ParameterizedTest
  @ValueSource(strings = {"DE", "GB"})
  void toCustomerAccountCardInvite(Scheme scheme) {
    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    Mockito.when(worldlineUtils.formatId(ArgumentMatchers.anyString()))
        .thenReturn("{" + TETHERED_USER_ID +"}");
    var worldlineHeadersData = Mockito.mock(WorldlineHeadersData.class);
    Mockito.when(worldlineConfigs.get(ArgumentMatchers.any(Scheme.class)))
        .thenReturn(worldlineHeadersData);
    Mockito.when(worldlineHeadersData.headerType()).thenReturn(Mockito.mock(HeaderType.class));
    Mockito.when(worldlineHeadersData.trustedPartnerCredentialsType()).thenReturn(Mockito.mock(
        TrustedPartnerCredentialsType.class));
    var worldlineSoapTransformer = new WorldlineSoapTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineConfigs", worldlineConfigs);
    ReflectionTestUtils.setField(worldlineSoapTransformer, "worldlineUtils", worldlineUtils);
    Mockito.when(worldlineUtils.serializeObject(ArgumentMatchers.any()))
        .thenReturn("mockedSerializedObject");
    var customerAccountCardInviteDetailsType = Mockito.mock(CustomerAccountCardInviteDetailsType.class);

    var customerAccountCardInvite = worldlineSoapTransformer.toCustomerAccountCardInvite(
        TETHERED_USER_ID, CARD_ID, scheme, customerAccountCardInviteDetailsType);

    assertNotNull(customerAccountCardInvite);
    assertEquals("{" + TETHERED_USER_ID + "}",
        customerAccountCardInvite.getRequest().getTetheredUserGuid());
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

  private WorldlineAccountCardCancelAndReplaceRequest createWorldlineAccountCardCancelAndReplaceRequest(Scheme scheme) {
    return WorldlineAccountCardCancelAndReplaceRequest.builder()
        .issueReplacement(false)
        .scheme(scheme)
        .apiUserGuid(API_USER_GUID)
        .build();
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
}
