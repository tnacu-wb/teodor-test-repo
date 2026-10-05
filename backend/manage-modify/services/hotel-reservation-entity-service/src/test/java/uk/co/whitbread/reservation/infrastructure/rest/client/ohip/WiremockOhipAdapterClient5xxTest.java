package uk.co.whitbread.reservation.infrastructure.rest.client.ohip;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.*;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.changelog.exception.ChangeLogException;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.SearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.model.in.PackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReservationSingleCallResponseDto;

import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockOhipAdapterClient5xxTest {

  @Mock
  private OhipAdapterProperties ohipAdapterProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private final Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errCode\":900}".getBytes());

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));
    wm.stubFor(post(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.stubFor(put(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.stubFor(delete(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipAdapterClientCreateReservation_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final ReservationRequestDto reservationRequestDto = new ReservationRequestDto();
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.createReservation(reservationRequestDto));
  }

  @Test
  void testOhipAdapterClientReservationByBasketRef_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getReservationsByBasketReference("hotelId", "basketRef",
            1, 1));
  }

  @Test
  void testOhipAdapterClientConfirmReservation_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final ConfirmReservationRequestDto confirmReservationRequestDto = new ConfirmReservationRequestDto();
    when(ohipAdapterProperties.getConfirmReservationEndpoint()).thenReturn(
        "/v1/reservations/confirm");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendConfirmReservationRequest(confirmReservationRequestDto));
  }

  @Test
  void testOhipAdapterClientReservationById_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    List<String> reservationIds = List.of("a", "b", "c");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetReservationsByIds("hotelId", reservationIds,
            true, false, true));
  }

  @Test
  void testOhipAdapterClientUpdateResPackages_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final ReservationPackagesRequestDto reservationPackagesRequestDto = new ReservationPackagesRequestDto();
    when(ohipAdapterProperties.getReservationsPackagesEndpoint()).thenReturn(
        "/v1/reservations/ancillaries");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservationPackagesRequest(
            reservationPackagesRequestDto));
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void testOhipAdapterClientResPackagesByIds_ShouldReturnException(boolean mealInclusiveRate) {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationsPackagesEndpoint()).thenReturn(
        "/v1/reservations/ancillaries");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getReservationsPackagesByIdsRequest("hotelId", List.of("123"),
            mealInclusiveRate));
  }

  @Test
  void testOhipAdapterClientResGuests_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final ReservationGuestRequestDto reservationGuestRequestDto = new ReservationGuestRequestDto();
    when(ohipAdapterProperties.getReservationGuestEndpoint()).thenReturn(
        "/v1/reservations/guests");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendReservationGuestRequest(reservationGuestRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateRateCode_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final RatePlanChangeRequestDto ratePlanChangeRequestDto = new RatePlanChangeRequestDto();
    when(ohipAdapterProperties.getUpdateReservationRateCodeEndpoint()).thenReturn(
        "/v1/reservations/rate-code");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateRateCodeRequest(ratePlanChangeRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateRoomType_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final RoomTypeChangeRequestDto roomTypeChangeRequestDto = new RoomTypeChangeRequestDto();
    when(ohipAdapterProperties.getUpdateRoomTypeEndpoint()).thenReturn(
        "/v1/reservations/roomTypeUpdate");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateRoomTypeRequest(roomTypeChangeRequestDto));
  }

  @Test
  void testOhipAdapterClientCancelInfo_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getCancelEndpoint()).thenReturn(
        "/v1/reservations/cancel");
    //Act
    Set<String> reservationIds = Set.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetCancelInformationRequest("hotelId", reservationIds,
            "userDateTime"));
  }

  @Test
  void testOhipAdapterClientCancelReservation_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final CancelReservationRequestDto cancelReservationRequestDto = new CancelReservationRequestDto();
    when(ohipAdapterProperties.getCancelReservationEndpoint()).thenReturn(
        "/v1/reservations/cancellations");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendCancelReservationRequest(cancelReservationRequestDto));
  }

  @Test
  void testOhipAdapterClientHotelInformation_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getHotelInfoEndpoint()).thenReturn(
        "/hotels/{hotelId}/info");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetHotelInformationRequest("hotelId"));
  }

  @Test
  void testOhipAdapterClientReservationByResId_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationIdEndpoint()).thenReturn(
        "/v1/reservation/reservationId");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetReservationsByReservationId("resId", "hotelId"));
  }

  @Test
  void testOhipAdapterClientResByRefId_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getExternalReservationEndpoint()).thenReturn(
        "/v1/reservations/external");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetReservationsByExternalReferenceId("externalRefId"));
  }

  @Test
  void testOhipAdapterClientUpdateDiscount_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateDiscountRequestDto updateDiscountRequestDto = new UpdateDiscountRequestDto();
    when(ohipAdapterProperties.getUpdateDiscountEndpoint()).thenReturn(
        "/v1/reservations/discount");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateDiscountRequest(updateDiscountRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateCompanyQuestionAndAnswer_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto =
        new CompanyQuestionAndAnswerDetailsRequestDto();
    when(ohipAdapterProperties.getUpdateCompanyQuestionAndAnswerRequestEndpoint()).thenReturn(
        "/v1/reservations/questions-and-answers");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient
            .sendUpdateCompanyQuestionAndAnswerDetailsRequests(
                companyQuestionAndAnswerDetailsRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateBusinessItems_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final BusinessItemsRequestDto businessItemsRequestDto = new BusinessItemsRequestDto();
    when(ohipAdapterProperties.getUpdateBusinessItemsEndpoint()).thenReturn(
        "/v1/reservations/business");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateBusinessItemsRequest(businessItemsRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateSpecialRequests_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final SpecialRequestsDto specialRequestsDto = new SpecialRequestsDto();
    when(ohipAdapterProperties.getUpdateSpecialRequestsEndpoint()).thenReturn(
        "/v1/reservations/special-requests");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateSpecialRequests(specialRequestsDto));
  }

  @Test
  void testOhipAdapterClientSearchBookings_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final SearchBookingsRequestDto searchBookingsRequestDto = new SearchBookingsRequestDto();
    when(ohipAdapterProperties.getSearchBookingsEndpoint()).thenReturn(
        "/v1/reservations/search");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendSearchBookings(searchBookingsRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateReasonForStay_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateReasonForStayRequestDto updateReasonForStayRequestDto = new UpdateReasonForStayRequestDto();
    when(ohipAdapterProperties.getUpdateReasonForStayEndpoint()).thenReturn(
        "/v1/reservations/reasonForStay");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReasonForStayRequest(updateReasonForStayRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateReservationOverrideReasons_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto =
        new UpdateReservationOverrideReasonsRequestDto();
    when(ohipAdapterProperties.getUpdateReservationOverrideReasonsEndpoint()).thenReturn(
        "/v1/reservations/overrideReasons");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservationOverrideReasons(
            updateReservationOverrideReasonsRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateReservationAmend_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateReservationsRequestDto updateReservationsRequestDto = new UpdateReservationsRequestDto();
    updateReservationsRequestDto.setUpdateReservationsRequest(List.of());
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservationAmend(updateReservationsRequestDto));

  }

  @Test
  void testOhipAdapterClientCancellationPolicies_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getCancelPoliciesEndpoint()).thenReturn(
        "/v1/reservations/cancellationPolicies"
    );
    //Act
    Set<String> reservationIds = Set.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getCancellationPolicies(reservationIds, "hotelId", "rateCode",
            "arrivalDate"));
  }

  @Test
  void testOhipAdapterClientDepositsByReservationId_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getDepositsEndpoint()).thenReturn(
        "/v1/reservations/deposits");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getDepositsByReservationId("hotelId", "reservationId"));
  }

  @Test
  void testOhipAdapterClientMarketingPreferences_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getMarketingPreferencesEndpoint()).thenReturn(
        "/v1/reservations/marketingPreferences");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getMarketingPreferences("hotelId", "reservationId"));
  }

  @Test
  void testOhipAdapterClientDeleteReservation_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getReservationEndpoint()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.deleteReservationRequest("hotelId", "reservationId"));
  }

  @Test
  void testOhipAdapterClientCopyReservations_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final CopyReservationsRequestDto copyReservationsRequestDto = new CopyReservationsRequestDto();
    when(ohipAdapterProperties.getCopyReservationsEndpoint()).thenReturn(
        "/v1/reservations/copy");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendCopyReservationsRequest(copyReservationsRequestDto));
  }

  @Test
  void testOhipAdapterClientAmendSummaryDetails_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    AmendSummaryAmountRequest amendSummaryAmountRequest = AmendSummaryAmountRequest.builder()
        .hotelId("HOTELTEST")
        .reservationIds(List.of("12234"))
        .build();
    when(ohipAdapterProperties.getAmendSummaryDetails()).thenReturn(
        "/v1/reservations/amend/getDetailsForAmend");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getAmendSummaryDetails(amendSummaryAmountRequest));
  }

  @Test
  void testOhipAdapterClientBookingAllowances_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    List<String> basketBookingAllowances = List.of("accommodation");
    when(ohipAdapterProperties.getBookingAllowancesEndpoint()).thenReturn(
        "/v1/reservations/bookingAllowances");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getBookingAllowances("hotelId", "reservationId",
            basketBookingAllowances));
  }

  @Test
  void testOhipAdapterClientConfirmAmend_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final ConfirmAmendOnReservationsRequestDto confirmAmendOnReservationsRequestDto =
        new ConfirmAmendOnReservationsRequestDto();
    when(ohipAdapterProperties.getConfirmAmend()).thenReturn(
        "/v1/reservations/confirmAmend");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendConfirmAmend(confirmAmendOnReservationsRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateReservations_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateReservationsRequestDto updateReservationsRequestDto = new UpdateReservationsRequestDto();
    when(ohipAdapterProperties.getUpdateReservations()).thenReturn(
        "/v1/reservations");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.updateReservations(updateReservationsRequestDto));
  }

  @Test
  void testOhipAdapterClientCharityPackagesDetails_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getCharityPackagesDetailsEndpoint()).thenReturn(
        "/hotels/{hotelId}/packages/donations");
    //Act
    List<String> packageCodes = List.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendCharityPackagesDetailsRequest("hotelId", packageCodes));
  }

  @Test
  void testOhipAdapterClientRatePlans_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getRatePlansEndpoint()).thenReturn(
        "/ratePlans");
    //Act
    List<String> ratePlanCodes = List.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendGetRatePlansRequest(ratePlanCodes, "hotelId"));
  }

  @Test
  void testOhipAdapterClientGeneratedDepositFolios_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getGeneratedDepositFoliosEndpoint()).thenReturn(
        "/v1/reservations/deposit-folios");
    //Act
    Set<String> reservationIds = Set.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getGeneratedDepositFolios("hotelId", reservationIds));
  }

  @Test
  void testOhipAdapterClientMovePaymentDetails_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getMoveReservationPaymentEndpoint()).thenReturn(
        "/v1/reservations/movePaymentDetails");
    //Act
    Set<String> reservationIds = Set.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.movePaymentDetails("hotelId", reservationIds));
  }

  @Test
  void testOhipAdapterClientUpdateBookerDetails_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final BookerDetailsCnpRequestDto bookerDetailsCnpRequestDto = new BookerDetailsCnpRequestDto();
    when(ohipAdapterProperties.getReservationBookerEndpoint()).thenReturn(
        "/v1/reservations/booker");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateBookerDetailsRequest(bookerDetailsCnpRequestDto));
  }

  @Test
  void testOhipAdapterClientUpdateBookerEmail_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateBookerEmailRequestDto updateBookerEmailRequestDto = new UpdateBookerEmailRequestDto();
    when(ohipAdapterProperties.getUpdateEmailReservationEndpoint()).thenReturn(
        "/v1/reservations/email");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateBookerEmailRequest(updateBookerEmailRequestDto));
  }

  @Test
  void testOhipAdapterClientDeleteRoutingInstructions_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getRoutingInstructionsEndpoint()).thenReturn(
        "/v1/reservations/routingInstructions");
    //Act
    Set<String> reservationIds = Set.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.deleteRoutingInstructions("hotelId", reservationIds));
  }

  @Test
  void testOhipAdapterClientSaveCharges_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final DepositFoliosRequestDto depositFoliosRequestDto = new DepositFoliosRequestDto();
    when(ohipAdapterProperties.getGeneratedDepositFoliosEndpoint()).thenReturn(
        "/v1/reservations/deposit-folios");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.saveCharges(depositFoliosRequestDto));
  }

  @Test
  void testOhipAdapterClientCreateMemo_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final CreateMemoRequestDto createMemoRequestDto = new CreateMemoRequestDto();
    when(ohipAdapterProperties.getMemosEndpoint()).thenReturn(
        "/v1/reservations/memos");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.createMemo(createMemoRequestDto));
  }

  @Test
  void testOhipAdapterClientGetMemos_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getMemosEndpoint()).thenReturn(
        "/v1/reservations/memos");
    //Act
    Set<String> reservationIds = Set.of("123");
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.getMemos("hotelId", reservationIds));
  }

  @Test
  void testOhipAdapterClientAttachProfileToReservations_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final AttachReservationProfileRequestDto attachReservationProfileRequestDto =
        new AttachReservationProfileRequestDto();
    when(ohipAdapterProperties.getAttachProfileToReservationsEndpoint()).thenReturn(
        "/v1/reservations/profiles");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.attachProfileToReservations(attachReservationProfileRequestDto));
  }

  @Test
  void testOhipAdapterClientGetPackages_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final PackagesRequestDto packagesRequestDto = new PackagesRequestDto("hotelId", "2023-10-05",
        "2023-10-06", 1, 1, 1);
    when(ohipAdapterProperties.getPackagesEndpoint()).thenReturn(
        "/hotels/{hotelId}/packages");
    //Act
    assertThrows(PackagesException.class,
        () -> ohipAdapterClient.getPackages(packagesRequestDto));
  }

  @Test
  void testOhipAdapterClientChangeLog_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    when(ohipAdapterProperties.getChangeLogEndpoint()).thenReturn(
        "/v1/hotels/{hotelId}/reservations/changeLog");
    //Act
    assertThrows(ChangeLogException.class,
        () -> ohipAdapterClient.getChangeLog("hotelId", "reservationId", 1, 1));
  }

  @Test
  void testOhipAdapterClientUpdateReservationSingleCall_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final UpdateReservationSingleCallResponseDto updateReservationSingleCallResponseDto =
        new UpdateReservationSingleCallResponseDto();
    updateReservationSingleCallResponseDto.setPaymentDetails(
        new uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto());
    when(ohipAdapterProperties.getUpdateReservationSingleCall()).thenReturn(
        "/v1/reservations/update");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendUpdateReservation(updateReservationSingleCallResponseDto));
  }

  @Test
  void testOhipAdapterClientCreateProfiles_ShouldReturnException() {
    final OhipAdapterClient ohipAdapterClient = new OhipAdapterClient(webClient,
        ohipAdapterProperties);
    final ReservationGuestRequestDto reservationGuestRequestDto = new ReservationGuestRequestDto();
    when(ohipAdapterProperties.getCreateProfilesEndPoint()).thenReturn(
        "/v1/reservations/create/profile");
    //Act
    assertThrows(HotelReservationOhipException.class,
        () -> ohipAdapterClient.sendCreateProfiles(reservationGuestRequestDto));
  }

}
