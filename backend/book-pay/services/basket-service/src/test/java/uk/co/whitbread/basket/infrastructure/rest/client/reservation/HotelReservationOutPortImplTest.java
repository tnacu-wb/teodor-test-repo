package uk.co.whitbread.basket.infrastructure.rest.client.reservation;

import static java.math.BigDecimal.valueOf;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_NOW;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.model.basket.in.BookerAddress;
import uk.co.whitbread.basket.domain.model.basket.in.BookerDetails;
import uk.co.whitbread.basket.domain.model.basket.in.PackagesSelection;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.RoomsSelections;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuest;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuestAddress;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuestDetails;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.PaymentCard;
import uk.co.whitbread.basket.domain.model.business.in.BusinessAllowance;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessAccount;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswer;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;
import uk.co.whitbread.basket.domain.model.payments.in.Guest;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.generated.models.reservation.AttachReservationProfileRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.BusinessAllowanceDto;
import uk.co.whitbread.basket.generated.models.reservation.BusinessItemsDto;
import uk.co.whitbread.basket.generated.models.reservation.BusinessItemsRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.MarketingPreferencesResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.properties.InitiatePaymentProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.AttachReservationProfileRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.BusinessItemsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.CompanyQuestionAndAnswerDetailsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationAlertsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDto;
import uk.co.whitbread.basket.domain.model.reservation.in.ReservationAlertsRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationProfiles;
import uk.co.whitbread.basket.generated.models.reservation.DepositsResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationGuestRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.basket.domain.model.reservation.in.DepositFoliosRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.ReservationProfilesDto;
import uk.co.whitbread.basket.infrastructure.rest.utils.CacheHelper;

@ExtendWith(MockitoExtension.class)
class HotelReservationOutPortImplTest {

  private HotelReservationOutPortImpl hotelReservationOutPort;

  @Mock
  private ReservationClient reservationClient;

  @Mock
  private ReservationResponseMapper reservationResponseMapper;

  @Mock
  private DepositsResponseMapper depositsResponseMapper;

  @Mock
  private BusinessItemsMapper businessItemsMapper;

  @Mock
  private CompanyQuestionAndAnswerDetailsMapper companyQuestionAndAnswerDetailsMapper;

  @Mock
  private MarketingPreferencesResponseMapper marketingPreferencesResponseMapper;

  @Mock
  private AttachReservationProfileRequestMapper attachReservationProfileRequestMapper;

  @Mock
  private UpdateReservationRequestMapper updateReservationRequestMapper;

  @Mock
  private ReservationAlertsMapper reservationAlertsMapper;

  @Mock
  private DistributionProperties distributionProperties;

  @Mock
  private InitiatePaymentProperties  initiatePaymentProperties;

  @Mock
  private CacheHelper cacheHelper;

  @BeforeEach
  public void before() {
    hotelReservationOutPort = new HotelReservationOutPortImpl(reservationClient,
        reservationResponseMapper, businessItemsMapper, companyQuestionAndAnswerDetailsMapper,
        depositsResponseMapper, marketingPreferencesResponseMapper, attachReservationProfileRequestMapper,
        updateReservationRequestMapper, reservationAlertsMapper, initiatePaymentProperties, cacheHelper);
  }

  @Test
  void testGetReservationsByBasketReference_success() {
    // Arrange
    when(reservationClient.getReservationsByBasketReference(anyString(),
        any(ReservationByBasketRefRequestDto.class))).thenReturn(new ReservationByBasketRefResponseDto());

    when(
        reservationResponseMapper.toModel(any(ReservationByBasketRefResponseDto.class))).thenReturn(
        new ReservationByBasketRefResponse());

    // Act
    var reservations =
        hotelReservationOutPort.getReservationsByBasketReference("123", "false", true);

    // Assert
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);
    verify(reservationClient).getReservationsByBasketReference("123", requestDto);
    verify(reservationResponseMapper).toModel(any(ReservationByBasketRefResponseDto.class));
    assertNotNull(reservations);
  }

  @Test
  void testGetReservationsByBasketReference_withCache_cacheHit() {
    // Arrange
    String basketReference = "BKK-12345";
    String cacheName = "Payment-Methods-Entity-Service::ReservationsByBasketRef";
    ReservationByBasketRefResponse expectedResponse = new ReservationByBasketRefResponse();

    when(initiatePaymentProperties.getReservationsCache()).thenReturn(cacheName);
    when(cacheHelper.getCacheValue(cacheName, basketReference,
        ReservationByBasketRefResponse.class)).thenReturn(expectedResponse);

    // Act
    var reservations = hotelReservationOutPort.getReservationsByBasketReference(
        basketReference, "false", true, true);

    // Assert
    assertNotNull(reservations);
    verify(cacheHelper).getCacheValue(cacheName, basketReference, ReservationByBasketRefResponse.class);
  }

  @Test
  void testGetReservationsByBasketReference_withCache_cacheMiss() {
    // Arrange
    String basketReference = "BKK-12345";
    String cacheName = "Payment-Methods-Entity-Service::ReservationsByBasketRef";

    when(initiatePaymentProperties.getReservationsCache()).thenReturn(cacheName);
    when(cacheHelper.getCacheValue(cacheName, basketReference,
        ReservationByBasketRefResponse.class)).thenReturn(null);
    when(reservationClient.getReservationsByBasketReference(anyString(),
        any(ReservationByBasketRefRequestDto.class))).thenReturn(new ReservationByBasketRefResponseDto());
    when(reservationResponseMapper.toModel(any(ReservationByBasketRefResponseDto.class)))
        .thenReturn(new ReservationByBasketRefResponse());

    // Act
    var reservations = hotelReservationOutPort.getReservationsByBasketReference(
        basketReference, "false", true, true);

    // Assert
    assertNotNull(reservations);
    verify(cacheHelper).getCacheValue(cacheName, basketReference, ReservationByBasketRefResponse.class);
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);
    verify(reservationClient).getReservationsByBasketReference(basketReference, requestDto);
    verify(reservationResponseMapper).toModel(any(ReservationByBasketRefResponseDto.class));
  }

  @Test
  void testGetReservationsByBasketReference_withoutCache() {
    // Arrange
    String basketReference = "BKK-12345";

    when(reservationClient.getReservationsByBasketReference(anyString(),
        any(ReservationByBasketRefRequestDto.class))).thenReturn(new ReservationByBasketRefResponseDto());
    when(reservationResponseMapper.toModel(any(ReservationByBasketRefResponseDto.class)))
        .thenReturn(new ReservationByBasketRefResponse());

    // Act
    var reservations = hotelReservationOutPort.getReservationsByBasketReference(
        basketReference, "false", true, false);

    // Assert
    assertNotNull(reservations);
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);
    verify(reservationClient).getReservationsByBasketReference(basketReference, requestDto);
    verify(reservationResponseMapper).toModel(any(ReservationByBasketRefResponseDto.class));
  }

  @Test
  void testGetReservationsByBasketReference_withUseCacheNull() {
    // Arrange
    String basketReference = "BKK-12345";

    when(reservationClient.getReservationsByBasketReference(anyString(),
        any(ReservationByBasketRefRequestDto.class))).thenReturn(new ReservationByBasketRefResponseDto());
    when(reservationResponseMapper.toModel(any(ReservationByBasketRefResponseDto.class)))
        .thenReturn(new ReservationByBasketRefResponse());

    // Act
    var reservations = hotelReservationOutPort.getReservationsByBasketReference(
        basketReference, "false", true, null);

    // Assert
    assertNotNull(reservations);
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded("false");
    requestDto.setRateInfoNeeded(true);
    verify(reservationClient).getReservationsByBasketReference(basketReference, requestDto);
    verify(reservationResponseMapper).toModel(any(ReservationByBasketRefResponseDto.class));
  }

  @Test
  void testUpdateDiscount_success() {
    // Arrange
    var discountRequest = mockDiscountRequest();
    var updateDiscount = mockUpdateDiscount();

    // Act
    hotelReservationOutPort.updateDiscount(discountRequest, List.of("123456"), "ABCDEF", "EUR");

    // Assert
    verify(reservationClient).sendPutUpdateDiscount(updateDiscount);
    verifyNoMoreInteractions(reservationClient);
  }

  @Test
  void testUpdateCompanyQuestionAndAnswerDetails_success() {
    // Arrange
    var companyQuestionAndAnswerDetails = mockCompanyQuestionAndAnswerDetails();
    var companyQuestionAndAnswerDetailsDto = mockCompanyQuestionAndAnswerDetailsDto();

    var companyQuestionAndAnswerDetailsRequestDto = mockCompanyQuestionAndAnswerDetailsRequestDto();
    companyQuestionAndAnswerDetailsRequestDto.setCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsDto);

    when(companyQuestionAndAnswerDetailsMapper.toDto(any())).thenReturn(companyQuestionAndAnswerDetailsDto);

    // Act
    hotelReservationOutPort.updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetails, Set.of("123456"), "ABCDEF");

    // Assert
    verify(reservationClient).sendUpdateCompanyQuestionAndAnswerDetailsRequests(companyQuestionAndAnswerDetailsRequestDto);
    verifyNoMoreInteractions(reservationClient);
  }



  @Test
  void testUpdateBusinessItems_success() {
    // Arrange
    var businessItems = mockBusinessItems();
    var businessItemsRequestDto = mockBusinessItemsRequestDto();
    when(businessItemsMapper.toDto(any())).thenReturn(businessItemsRequestDto.getBusinessItems());

    // Act
    hotelReservationOutPort.updateBusinessItems(businessItems, List.of("123456"),
        "ABCDEF", null, null);

    // Assert
    verify(reservationClient).sendPutUpdateBusinessItems(businessItemsRequestDto);
    verifyNoMoreInteractions(reservationClient);
  }

  private BusinessItems mockBusinessItems() {
    return BusinessItems.builder()
        .purchaseOrderNumber("10101010")
        .customReferenceNumber("10101010")
        .businessAllowances(List.of(BusinessAllowance.builder()
            .allowance("allowance")
            .isAuthorised(Boolean.TRUE)
            .build()))
        .build();
  }

  private CompanyQuestionAndAnswerDetails mockCompanyQuestionAndAnswerDetails() {
    CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails = new CompanyQuestionAndAnswerDetails();
    List<CompanyQuestionAndAnswer> companyQuestionAndAnswers = List.of(createCompanyQuestionAndAnswer());
    companyQuestionAndAnswerDetails.setPurchaseOrderQuestionAndAnswer(companyQuestionAndAnswers.get(0));
    companyQuestionAndAnswerDetails.setUserDefinedQuestionAndAnswers(companyQuestionAndAnswers);
    companyQuestionAndAnswerDetails.setCustomerReferenceQuestionAndAnswer(companyQuestionAndAnswers.get(0));
    return companyQuestionAndAnswerDetails;
  }

  private CompanyQuestionAndAnswerDetailsDto mockCompanyQuestionAndAnswerDetailsDto() {
    CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto = new CompanyQuestionAndAnswerDetailsDto();
    List<CompanyQuestionAndAnswerDto> companyQuestionAndAnswers = List.of(createCompanyQuestionAndAnswerDto());
    companyQuestionAndAnswerDetailsDto.setPurchaseOrderQuestionAndAnswer(companyQuestionAndAnswers.get(0));
    companyQuestionAndAnswerDetailsDto.setUserDefinedQuestionAndAnswers(companyQuestionAndAnswers);
    companyQuestionAndAnswerDetailsDto.setCustomerReferenceQuestionAndAnswer(companyQuestionAndAnswers.get(0));
    return companyQuestionAndAnswerDetailsDto;
  }

  private CompanyQuestionAndAnswerDto createCompanyQuestionAndAnswerDto() {
    var companyQuestionAndAnswerDto = new CompanyQuestionAndAnswerDto();
    companyQuestionAndAnswerDto.setQuestion("Who am I?");
    companyQuestionAndAnswerDto.setAnswer("Test");
    return companyQuestionAndAnswerDto;
  }

  private CompanyQuestionAndAnswer createCompanyQuestionAndAnswer() {
    var companyQuestionAndAnswer = new CompanyQuestionAndAnswer();
    companyQuestionAndAnswer.setQuestion("Who am I?");
    companyQuestionAndAnswer.setAnswer("Test");
    return companyQuestionAndAnswer;
  }

  private CompanyQuestionAndAnswerDetailsRequestDto mockCompanyQuestionAndAnswerDetailsRequestDto() {
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto = new CompanyQuestionAndAnswerDetailsRequestDto();
    companyQuestionAndAnswerDetailsRequestDto.setReservationIds(Set.of("123456"));
    companyQuestionAndAnswerDetailsRequestDto.setHotelId("ABCDEF");
    return companyQuestionAndAnswerDetailsRequestDto;
  }

  private BusinessItemsRequestDto mockBusinessItemsRequestDto() {
    BusinessItemsRequestDto businessItemsRequestDto = new BusinessItemsRequestDto();
    businessItemsRequestDto.reservationIds(List.of("123456"));
    businessItemsRequestDto.hotelId("ABCDEF");

    BusinessItemsDto businessItemsDto = new BusinessItemsDto();
    businessItemsDto.purchaseOrderNumber("10101010");
    businessItemsDto.customReferenceNumber("10101010");
    BusinessAllowanceDto businessAllowanceDto = new BusinessAllowanceDto();
    businessAllowanceDto.allowance("ALLOWANCE");
    businessAllowanceDto.budget(BigDecimal.TEN);
    businessItemsDto.businessAllowances(List.of(businessAllowanceDto));

    businessItemsRequestDto.businessItems(businessItemsDto);
    return businessItemsRequestDto;
  }


  @Test
  void testUpdateSpecialRequests_success() {
    // Act
    hotelReservationOutPort.updateSpecialRequests(List.of("SING"), List.of("123456"),
        List.of("12345"), "ABCDEF");

    // Assert
    verify(reservationClient).sendPutReservationsSpecialRequests(any());
    verifyNoMoreInteractions(reservationClient);
  }


  @Test
  void testGetMarketingPreferences_success() {
    // Arrange
    when(reservationClient.getMarketingPreferences(anyString(), anyString())).thenReturn(
        new MarketingPreferencesResponseDto());

    when(
        marketingPreferencesResponseMapper.toModel(any(MarketingPreferencesResponseDto.class))).thenReturn(
        new MarketingPreferencesResponse());

    // Act
    var marketingPreferences =
        hotelReservationOutPort.getMarketingPreferences("hotelId", "1234456");

    // Assert
    verify(reservationClient).getMarketingPreferences("hotelId", "1234456");
    verify(marketingPreferencesResponseMapper).toModel(any(MarketingPreferencesResponseDto.class));
    assertNotNull(marketingPreferences);
  }

  @Test
  void testAttachProfileToReservations_success() {
    // Arrange
    doNothing().when(reservationClient).attachProfileToReservations(any());
    when(attachReservationProfileRequestMapper.toDto(eq("TEST"), eq("123"), anySet()))
        .thenReturn(new AttachReservationProfileRequestDto());
    // Act
    hotelReservationOutPort.attachProfileToReservations("TEST", "123", Set.of("123", "456"));

    // Assert
    verify(reservationClient).attachProfileToReservations(any());
    verifyNoMoreInteractions(reservationClient);
  }

  @Test
  void testDeleteRoutingInstructions_success() {
    // Act
    hotelReservationOutPort.deleteRoutingInstructions("HOTEL_ID", new HashSet<>(List.of("1234")));

    // Assert
    verify(reservationClient).deleteRoutingInstructions(anyString(), anySet());
    verifyNoMoreInteractions(reservationClient);
  }

  private DiscountRequest mockDiscountRequest() {
    return DiscountRequest.builder()
        .basketReference("ABC1234567")
        .discountAmount(BigDecimal.valueOf(10))
        .build();
  }

  private UpdateDiscountRequestDto mockUpdateDiscount() {
    UpdateDiscountRequestDto dto = new UpdateDiscountRequestDto();
    dto.reservationIds(List.of("123456"));
    dto.hotelId("ABCDEF");
    dto.currency("EUR");
    dto.discountAmount(BigDecimal.valueOf(10));
    return dto;
  }

  @Test
  void testUpdateReservationRequestHotel() {
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    final String reservationId = "101";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var finalBusinessItems = buildBusinessItemsFromBusinessAccount(
            paymentRequest.getBusinessAccount(),
            distributionProperties.getMeals());
    var basket = mockBasket(reference);
    var hotelPaymentInfo = mockHotelPaymentInfoResponse();
    var confirmReservationRequest = ConfirmReservationRequest.builder()
            .hotelId("Loneus")
            .reservationId("101")
            .paymentOption(PAY_NOW)
            .paymentType("VA")
            .paymentCard(PaymentCard
                    .builder()
                    .token(paymentRequest.getPayment().getCard().getToken())
                    .expirationDate("2024-01-31")
                    .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
                    .build()
            )
            .build();

    var specialRequests = List.of("specialRequest");
    var bookingNotes = List.of("bookingNotes");
    var  confirmReservationResponse =  hotelReservationOutPort.updateReservationRequest(guestReservationRequest,
            updatePackageReservationRequest, finalBusinessItems,
            specialRequests, bookingNotes, hotelId,
            reservationId, confirmReservationRequest);

    verify(reservationClient).sendPutUpdateReservation(any());
    verifyNoMoreInteractions(reservationClient);

  }
  private Basket mockBasket(String reference) {
    return Basket.builder().reference(reference)
            .basketId("ABC-".concat(UUID.randomUUID().toString()))
            .hotelId("Loneus")
            .channel("CCUI_BOOKING_CHANNEL")
            .status(BasketStatus.OPEN).
            items(List.of(BasketItem.builder().sourceId("101")
                    .build()))
            .userId("userId")
            .createdAt("2025-01-21T12:26:56Z")
            .sendMail(true)
            .paymentOption(PAY_NOW.name())
            .paymentID("paymentId")
            .build();
  }

  private ReservationGuestRequest createValidReservationGuestRequest() {

    final BookerAddress address = BookerAddress.builder()
            .postalCode("MZC AD")
            .addressType("HOME")
            .addressLine1("4 Brockley Avenue")
            .countryCode("UK")
            .cityName("London")
            .companyName("company")
            .build();
    final StayingGuestAddress stayingGuestAddress = StayingGuestAddress.builder()
            .postalCode("MZC AD")
            .addressType("HOME")
            .addressLine1("4 Brockley Avenue")
            .countryCode("UK")
            .cityName("London")
            .companyName("company")
            .build();

    final BookerDetails booker = BookerDetails.builder()
            .title("Mrs")
            .firstName("John")
            .lastName("McEnroe")
            .emailAddress("john.mcenroe@mail.com")
            .acceptFutureMailing(Boolean.FALSE)
            .mobile("+39567463783")
            .address(address)
            .build();

    final StayingGuestDetails stayingGuest = StayingGuestDetails.builder()
            .title("Mrs")
            .firstName("Debbie")
            .lastName("Doe")
            .address(stayingGuestAddress)
            .build();

    final StayingGuest stayingGuests = StayingGuest.builder()
            .reservationId("101")
            .sameAsBooker(false)
            .stayingGuestDetails(stayingGuest)
            .build();
    return ReservationGuestRequest.builder()
            .hotelId("Loneus")
            .booker(booker)
            .stayingGuests(List.of(stayingGuests))
            .sendEmailConfirmation(true)
            .sendEmailInvoice(true)
            .build();
  }
  private ReservationPackagesRequest createSavePackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(List.of("101"));
    reservationPackagesRequest.setHotelId("Loneus");
    reservationPackagesRequest.setArrivalDate("2022-04-02");
    reservationPackagesRequest.setDepartureDate("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
            Collections.singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }
  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(Collections.singletonList(createPackagesSelection()));

    return roomsSelections;
  }
  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoOfSelections(1);
    packagesSelection.setId("MDP");

    return packagesSelection;
  }

  private HotelPaymentInformation mockHotelPaymentInfoResponse() {
    return HotelPaymentInformation.builder()
            .acceptedCreditCards(List.of(
                    AcceptedCreditCard.builder().code("DL").code3CP("VS").codeOpera("VA").build(),
                    AcceptedCreditCard.builder().code("EL").code3CP("VS").codeOpera("VA").build()
            )).build();
  }
  @Test
  void getDepositsForReservationId_returnsDepositsResponse() {
    var depositsResponseDto = new DepositsResponseDto();
    var expectedDeposits = DepositsResponse.builder().build();

    when(reservationClient.getDepositsForReservationId("HOTEL_ID", "RES_001")).thenReturn(depositsResponseDto);
    when(depositsResponseMapper.toModel(depositsResponseDto)).thenReturn(expectedDeposits);

    var result = hotelReservationOutPort.getDepositsForReservationId("HOTEL_ID", "RES_001");

    assertNotNull(result);
    verify(reservationClient).getDepositsForReservationId("HOTEL_ID", "RES_001");
    verify(depositsResponseMapper).toModel(depositsResponseDto);
    verifyNoMoreInteractions(reservationClient);
  }

  @Test
  void getPreviewDepositsForReservationId_returnsDepositFoliosResponse() {
    var depositFoliosResponseDto = new DepositFoliosResponseDto();
    var expectedResponse = new DepositFoliosResponse();
    var reservationIds = Set.of("RES_001");

    when(reservationClient.getPreviewDepositsForReservationId("HOTEL_ID", reservationIds)).thenReturn(depositFoliosResponseDto);
    when(depositsResponseMapper.toDto(depositFoliosResponseDto)).thenReturn(expectedResponse);

    var result = hotelReservationOutPort.getPreviewDepositsForReservationId("HOTEL_ID", reservationIds);

    assertNotNull(result);
    verify(reservationClient).getPreviewDepositsForReservationId("HOTEL_ID", reservationIds);
    verify(depositsResponseMapper).toDto(depositFoliosResponseDto);
    verifyNoMoreInteractions(reservationClient);
  }

  @Test
  void saveDepositFolios_delegatesToClientWithMappedRequest() {
    var depositFoliosRequest = new DepositFoliosRequest();
    var depositFoliosRequestDto = new DepositFoliosRequestDto();

    when(depositsResponseMapper.toDepositModel(depositFoliosRequest)).thenReturn(depositFoliosRequestDto);

    hotelReservationOutPort.saveDepositFolios(depositFoliosRequest);

    verify(depositsResponseMapper).toDepositModel(depositFoliosRequest);
    verify(reservationClient).saveDepositsFolios(depositFoliosRequestDto);
    verifyNoMoreInteractions(reservationClient);
  }

  @Test
  void createProfileIds_returnsMappedReservationProfiles() {
    var guestReservationRequest = createValidReservationGuestRequest();
    var guestRequestDto = new ReservationGuestRequestDto();
    var profilesDto = new ReservationProfilesDto("id","compId");
    var expectedProfiles = ReservationProfiles.builder().bookerProfileId("BP-001").companyProfileId("CP-001").build();

    when(updateReservationRequestMapper.toDto(guestReservationRequest)).thenReturn(guestRequestDto);
    when(reservationClient.createProfileIds(guestRequestDto)).thenReturn(profilesDto);
    when(updateReservationRequestMapper.toModel(profilesDto)).thenReturn(expectedProfiles);

    var result = hotelReservationOutPort.createProfileIds(guestReservationRequest);

    assertNotNull(result);
    verify(reservationClient).createProfileIds(guestRequestDto);
    verify(updateReservationRequestMapper).toModel(profilesDto);
    verifyNoMoreInteractions(reservationClient);
  }

  @Test
  void updateReservationAlerts_delegatesToClientWithMappedRequest() {
    var reservationAlertsRequest = ReservationAlertsRequest.builder()
        .hotelId("HOTEL_ID")
        .reservationIds(Set.of("RES_001"))
        .build();
    var alertsRequestDto = new UpdateReservationAlertsRequestDto();

    when(reservationAlertsMapper.toDto(reservationAlertsRequest)).thenReturn(alertsRequestDto);

    hotelReservationOutPort.updateReservationAlerts(reservationAlertsRequest);

    verify(reservationAlertsMapper).toDto(reservationAlertsRequest);
    verify(reservationClient).updateReservationAlerts(alertsRequestDto);
    verifyNoMoreInteractions(reservationClient);
  }

  private static PaymentRequest getPaymentRequest() {
    var paymentRequest = PaymentRequest.builder()
            .requestId("111")
            .payment(Payment.builder()
                    .amount(Amount.builder()
                            .currency("GBP")
                            .minorUnits(valueOf(1000))
                            .build())
                    .billing(Billing.builder()
                            .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                                    .line1("120 Holborn")
                                    .line2("")
                                    .line3("")
                                    .line4("")
                                    .countryCode("gb")
                                    .postalCode("EC1N 2TD")
                                    .build())
                            .email("email@whitbread.com")
                            .firstName("Samuel")
                            .lastName("Whitbread")
                            .telephone("0777777777")
                            .title("Mr")
                            .build())
                    .card(Card.builder()
                            .cardholderName("Samuel Whitbread")
                            .cardType("VS")
                            .cnpRequired(false)
                            .expiryMonth("01")
                            .expiryYear("24")
                            .logoUrl("https://www.premierinn.com/logo")
                            .token("4943056398164344242")
                            .type("type")
                            .build())
                    .environment("https://www.premierinn.com")
                    .subType("ECOMM")
                    .type("CARD")
                    .pibaCardPresent(false)
                    .build())
            .booking(Booking.builder()
                    .arrivalDate("2022-05-17")
                    .businessSite(BusinessSite.builder()
                            .identifier("LONHOL")
                            .location("London")
                            .name("London Holborn Premier Inn")
                            .type("HOTEL")
                            .build())
                    .channel("PI")
                    .departureDate("2022-05-20")
                    .journey("BOOKING")
                    .language("en")
                    .leadGuest(Guest.builder()
                            .name("Samuel Whitbread")
                            .previousBookings(2)
                            .registered(true)
                            .registeredSince(LocalDate.of(2022, Month.APRIL, 1))
                            .build())
                    .reference("reference")
                    .rooms(of(RoomType.builder()
                            .adultsNumber(1)
                            .rate("SV344")
                            .type("DB")
                            .build()))
                    .type(PAY_NOW.name())
                    .build())
            .businessAccount(BusinessAccount.builder().customerReference("123456").purchaseOrder("7891011")
                    .carParkingAllowed("Yes").build())
            .build();
    return paymentRequest;
  }

}