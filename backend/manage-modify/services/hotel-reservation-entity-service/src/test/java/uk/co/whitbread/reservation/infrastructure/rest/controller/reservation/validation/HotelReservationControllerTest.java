package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosResponseDto;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.domain.model.in.AddressInfoType;
import uk.co.whitbread.reservation.domain.model.in.AddressType;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnp;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.Country;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.LeadGuest;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelectionScheduled;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.PreferencesCollection;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeAddresses;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;
import uk.co.whitbread.reservation.domain.model.in.RoomReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelections;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.StayingGuest;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAddress;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestDetails;
import uk.co.whitbread.reservation.domain.model.in.UpdateCnpReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateEmailReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomOccupancyRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomRateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdatedReservationsDistribution;
import uk.co.whitbread.reservation.domain.model.out.AmendStayDatesResponse;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryDetails;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationCustomer;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationRoomStay;
import uk.co.whitbread.reservation.domain.model.out.Customer;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationGuestResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.TempBookingRefResponse;
import uk.co.whitbread.reservation.domain.model.out.UniqueIDType;
import uk.co.whitbread.reservation.domain.model.out.UpdateCnpReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.CdhSearchBookingInPort;
import uk.co.whitbread.reservation.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.DepositFoliosMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.HotelReservationController;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AddNewRoomRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendDistributionPackagesMappper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendDistributionRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendStayDatesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendStayDatesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendSummaryDetailsMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookerDetailsMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingAllowancesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingChannelRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CancelReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CancelReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CdhSearchBookingRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CdhSearchBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CompanyQuestionAndAnswerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ConfirmAmendRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ConfirmReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ConfirmReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CreateReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.EditRoomRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.LinkReservationToLeisureCustomerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.MemosMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.PreCheckInRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationByBasketRefResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationFileAttachmentRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationGuestRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationGuestResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationPreferencesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SpecialRequestsMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.TempBookingRefResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateCnpReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateCnpReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateDiscountRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateEmailReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AddNewRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionGuestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionReservationDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendPackagesDistributionDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendStayDatesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CdhSearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AlertsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmAmendRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CreateMemoRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.EditRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.GuestAddressDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LeadGuestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.PackagesSelectionDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.PreCheckInRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestByIdDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesScheduledRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPreferencesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomOccupancyDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomStayDistributionDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomsSelectionsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.StayingGuestAdditionalDetailsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.StayingGuestAddressDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.StayingGuestDetailsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.StayingGuestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateCnpReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateEmailReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateRateCodeRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReasonForStayRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateRoomTypeRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendStayDatesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendSummaryDetailsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CdhSearchBookingsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ConfirmReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ConfirmationCustomerDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ConfirmationRoomStayDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CustomerDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.DepositsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.MarketingPreferencesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.MemosResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.PaymentCardDetailsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationGuestResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationsDetailsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SaveReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.TempBookingRefResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UniqueIDTypeDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateCnpReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReasonForStayResponseDto;

@ExtendWith(MockitoExtension.class)
public class HotelReservationControllerTest {

  @InjectMocks
  HotelReservationController hotelReservationControllerUnderTest;
  @Mock
  UpdateReservationRequestMapper updateReservationRequestMapper;

  @Mock
  CancelResponseOhipMapper cancelResponseOhipMapper;
  @Mock
  UpdateDiscountRequestMapper updateDiscountRequestMapper;

  @Mock
  CompanyQuestionAndAnswerRequestMapper companyQuestionAndAnswerRequestMapper;
  @Mock
  UpdateReasonForStayRequestMapper updateReasonForStayRequestMapper;
  @Mock
  UpdateReasonForStayResponseMapper updateReasonForStayResponseMapper;
  @Mock
  BookingChannelRequestMapper bookingChannelRequestMapper;
  @Mock
  ReservationGuestRequestMapper reservationGuestRequestMapper;
  @Mock
  ReservationGuestResponseMapper reservationGuestResponseMapper;
  @Mock
  UpdateEmailReservationRequestMapper updateEmailReservationRequestMapper;
  @Mock
  private HotelReservationInPort reservationPortBusinessCase;
  @Mock
  private ManageBookingInPort manageBookingInPort;
  @Mock
  private AmendLogicInPort amendLogicInPort;
  @Mock
  private ReservationResponseMapper reservationResponseMapper;
  @Mock
  private CreateReservationResponseMapper createReservationResponseMapper;
  @Mock
  private ReservationRequestMapper reservationRequestMapper;
  @Mock
  private ConfirmReservationResponseMapper confirmReservationResponseMapper;
  @Mock
  private ConfirmReservationRequestMapper confirmReservationRequestMapper;
  @Mock
  private ReservationByBasketRefResponseMapper reservationByBasketRefResponseMapper;
  @Mock
  private CancelReservationRequestMapper cancelReservationRequestMapper;
  @Mock
  private CancelReservationResponseMapper cancelReservationResponseMapper;
  @Mock
  private DepositsResponseMapper depositsResponseMapper;
  @Mock
  private MarketingPreferencesResponseMapper marketingPreferencesResponseMapper;
  @Mock
  private BookingAllowancesResponseMapper bookingAllowancesResponseMapper;
  @Mock
  private TempBookingRefResponseMapper tempBookingRefResponseMapper;
  @Mock
  private EditRoomRequestMapper editRoomRequestMapper;
  private EditRoomRequestDto editRoomRequestDto;
  @Mock
  private UpdateCnpReservationRequestMapper updateCnpReservationRequestMapper;
  @Mock
  private UpdateCnpReservationResponseMapper updateCnpReservationResponseMapper;
  @Mock
  private SpecialRequestsMapper specialRequestsMapper;
  @Mock
  private AddNewRoomRequestMapper addNewRoomRequestMapper;
  @Mock
  private AmendSummaryDetailsMapper amendSummaryDetailsMapper;
  @Mock
  private AmendStayDatesRequestMapper amendStayDatesRequestMapper;
  @Mock
  private AmendStayDatesResponseMapper amendStayDatesResponseMapper;
  @Mock
  private ConfirmAmendRequestMapper confirmAmendRequestMapper;
  @Mock
  private AmendDistributionRequestMapper amendDistributionRequestMapper;
  @Mock
  private AmendDistributionPackagesMappper amendDistributionPackagesMappper;
  @Mock
  private MemosMapper memosMapper;
  @Mock
  private CdhSearchBookingRequestMapper cdhSearchBookingRequestMapper;
  @Mock
  private CdhSearchBookingResponseMapper cdhSearchBookingResponseMapper;
  @Mock
  private CdhSearchBookingInPort cdhSearchBookingInPort;
  @Mock
  private BookerDetailsMapper bookerDetailsMapper;
  @Mock
  private ReservationFileAttachmentRequestMapper reservationFileAttachmentRequestMapper;
  @Mock
  private PreCheckInRequestMapper preCheckInRequestMapper;
  @Mock
  private ReservationPreferencesRequestMapper reservationPreferencesRequestMapper;

  @Mock
  private LinkReservationToLeisureCustomerRequestMapper linkReservationToLeisureCustomerRequestMapper;
  @Mock
  private DepositFoliosMapper depositFoliosMapper;

  private static UniqueIDType createUniqueIDType(String id, String type) {
    return UniqueIDType.builder()
        .id(id)
        .type(type)
        .build();
  }

  private static ConfirmationRoomStay getConfirmationRoomStayResponse() {
    return ConfirmationRoomStay.builder()
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now()).build();
  }

  public static ConfirmationCustomer getConfirmationCustomer() {
    return ConfirmationCustomer.builder()
        .givenName("Emma")
        .surName("Smith").build();
  }

  private static UniqueIDTypeDto createUniqueIDTypeDto(String id, String type) {
    return new UniqueIDTypeDto(id, type);
  }

  private static ConfirmationRoomStayDto getDtoConfirmationRoomStayResponse() {
    return new ConfirmationRoomStayDto(LocalDate.now(), LocalDate.now());
  }

  public static ConfirmationCustomerDto getDtoConfirmationCustomer() {
    return new ConfirmationCustomerDto("Emma", "Smith");
  }

  public void setUp() {
    var roomOccupancy = new RoomOccupancyDto();
    roomOccupancy.setAdultsNumber(1);
    roomOccupancy.setChildrenNumber(0);
    roomOccupancy.setCotRequired(false);
    var guestAddress = GuestAddressDto.builder()
        .addressLine1("Line 1")
        .addressLine2("Line 2")
        .cityName("London")
        .postalCode("BS8 2UX")
        .countryCode("GB").build();
    var leadGuest = new LeadGuestDto();
    leadGuest.setTitle("mrs");
    leadGuest.setFirstName("dog");
    leadGuest.setLastName("cat");
    leadGuest.setEmailAddress("dog@cat.com");
    leadGuest.setAddress(guestAddress);
    var bookingChannel = new BookingChannelDto();
    bookingChannel.setChannel("PI");
    bookingChannel.setSubchannel("WEB");
    bookingChannel.setLanguage("en");
    editRoomRequestDto = new EditRoomRequestDto();
    editRoomRequestDto.setTempBookingRef("53464564");
    editRoomRequestDto.setRoomType("DB");
    editRoomRequestDto.setRoomOccupancy(roomOccupancy);
    editRoomRequestDto.setBookingChannel(bookingChannel);
    editRoomRequestDto.setLeadGuest(leadGuest);
  }

  @Test
  void createReservation_ShouldReturnReservation() {
    //Arrange
    String hotelId = "FRAMTI";
    ReservationRequestDto createReservationRequestDto = new ReservationRequestDto();
    ReservationRequest reservationRequest = ReservationRequest.builder().build();
    Reservation reservation = Reservation.builder().build();
    reservation.setHotelId(hotelId);
    Mockito.when(reservationRequestMapper.toModel(createReservationRequestDto))
        .thenReturn(reservationRequest);
    Mockito.when(reservationPortBusinessCase.createReservation(reservationRequest))
        .thenReturn(getReservationResponse());
    Mockito.when(createReservationResponseMapper.toDto(getReservationResponse()))
        .thenReturn(getReservationResponseDto());

    //act
    ReservationRequest request = reservationRequestMapper.toModel(createReservationRequestDto);
    ReservationResponse reservationResponse = reservationPortBusinessCase.createReservation(
        request);
    var reservationResponseDto = createReservationResponseMapper.toDto(reservationResponse);
    final ResponseEntity<ReservationResponseDto> response = hotelReservationControllerUnderTest.createReservation(
        createReservationRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 201, response.getStatusCode().value());
    assertEquals(response.toString(), reservationResponseDto.getHotelId(),
        response.getBody().getHotelId());
  }

  @Test
  void getReservationsByBasketReference__ShouldReturnReservationByBasketReference() {
    //Arrange
    String hotelId = "FRAMTI";
    String basketReference = "001";
    int limit = 20;
    int offset = 0;

    Mockito.when(
        reservationPortBusinessCase.getReservationsByBasketReference(hotelId, basketReference,
            limit, offset)).thenReturn(getReservationDetailResponse());
    Mockito.when(reservationResponseMapper.toDto(getReservationDetailResponse()))
        .thenReturn(getReservationsDetailResponseDto());

    //act
    final ReservationsDetailsResponse reservations = reservationPortBusinessCase.getReservationsByBasketReference(
        hotelId, basketReference, limit, offset);
    final var reservationsDto = reservationResponseMapper.toDto(reservations);
    final ResponseEntity<ReservationsDetailsResponseDto> response = hotelReservationControllerUnderTest.getReservationsByBasketReference(
        hotelId, basketReference, limit, offset);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), reservationsDto.getReservations().getLimit(),
        response.getBody().getReservations().getLimit());
    assertEquals(response.toString(), reservationsDto.getReservations().getOffset(),
        response.getBody().getReservations().getOffset());
    assertEquals(response.toString(), reservationsDto.getReservations().getTotalResults(),
        response.getBody().getReservations().getTotalResults());
    assertEquals(response.toString(), reservationsDto.getReservations().isHasMore(),
        response.getBody().getReservations().isHasMore());
  }

  @Test
  void confirmReservation__ShouldReturnConfirmation() {
    //Arrange
    ConfirmReservationRequestDto confirmReservationRequestDto = new ConfirmReservationRequestDto();
    confirmReservationRequestDto.setPibaCardPresent(Boolean.TRUE);
    ConfirmReservationRequest confirmReservationRequest = ConfirmReservationRequest.builder()
        .hotelId("FRAMTI").paymentOption(PaymentOption.PAY_NOW).pibaCardPresent(Boolean.TRUE).build();

    Mockito.when(confirmReservationRequestMapper.toModel(confirmReservationRequestDto))
        .thenReturn(confirmReservationRequest);
    Mockito.when(reservationPortBusinessCase.confirmReservation(confirmReservationRequest,
            Optional.empty())).thenReturn(getConfirmReservationResponse());
    Mockito.when(confirmReservationResponseMapper.toDto(getConfirmReservationResponse()))
        .thenReturn(getConfirmReservationResponseDto());

    //act
    final var request = confirmReservationRequestMapper.toModel(confirmReservationRequestDto);
    final var reservationResponse = reservationPortBusinessCase.confirmReservation(request,
        Optional.empty());
    final var reservationResponseDto = confirmReservationResponseMapper.toDto(reservationResponse);
    final ResponseEntity<ConfirmReservationResponseDto> response = hotelReservationControllerUnderTest.confirmReservation(
        confirmReservationRequestDto);

    //Assert
    assertNotNull(response);
    Assertions.assertTrue(request.getPibaCardPresent());
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), reservationResponseDto.getReservationStatus(),
        response.getBody().getReservationStatus());
  }

  @Test
  void getAllReservationsJustByBasketReference__ShouldReturnAllReservationsByBasketReference() {
    //Arrange
    String basketReference = "001";
    ConfirmReservationRequestDto confirmReservationRequestDto = new ConfirmReservationRequestDto();
    Mockito.when(
            reservationPortBusinessCase.getAllReservationsJustByBasketReference(basketReference, false))
        .thenReturn(getReservationByBasketRefResponse());
    Mockito.when(reservationByBasketRefResponseMapper.toDto(getReservationByBasketRefResponse()))
        .thenReturn(getReservationByBasketRefResponseDto());

    //act
    final var request = reservationPortBusinessCase.getAllReservationsJustByBasketReference(
        basketReference, false);
    final var reservationByBasketRefResponseDto = reservationByBasketRefResponseMapper.toDto(
        request);
    final ResponseEntity<ReservationByBasketRefResponseDto> response = hotelReservationControllerUnderTest
        .getAllReservationsJustByBasketReference(basketReference, "false");

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), reservationByBasketRefResponseDto.getHotelId(),
        response.getBody().getHotelId());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"token", ""})
  void getAllReservationsJustByBookingReferenceAuthenticated__ShouldReturnAllReservationsByBookingReference(String token) {
    //Arrange
    String bookingReference = "001";
    Mockito.when(reservationPortBusinessCase.getAllReservationsJustByBookingReferenceAuthenticated(
            bookingReference, false))
        .thenReturn(getReservationByBasketRefResponse());
    Mockito.when(reservationByBasketRefResponseMapper.toDto(getReservationByBasketRefResponse()))
        .thenReturn(getReservationByBasketRefResponseDto());

    //act
    final var request = reservationPortBusinessCase.getAllReservationsJustByBookingReferenceAuthenticated(
        bookingReference, false);
    final var reservationByBasketRefResponseDto = reservationByBasketRefResponseMapper.toDto(
        request);
    ResponseEntity<ReservationByBasketRefResponseDto> response = null;
    HttpHeaders headers = new HttpHeaders();
    if(Objects.isNull(token) || token.isEmpty()) {
      assertThrows(InvalidTokenException.class,
          () -> hotelReservationControllerUnderTest
              .getAllReservationsJustByBookingReferenceWithToken(headers, bookingReference));
    }else{
      headers.set("WB-token", token);
      response = hotelReservationControllerUnderTest
          .getAllReservationsJustByBookingReferenceWithToken(headers, bookingReference);
      //Assert
      assertResponse(response, reservationByBasketRefResponseDto);
    }

  }

  @Test
  void saveReservation__ShouldSaveReservation() {
    //Arrange
    Mockito.when(reservationPortBusinessCase.updateReservationPackages(createSavePackagesRequest()))
        .thenReturn(getSaveReservationResponse());
    Mockito.when(reservationResponseMapper.toDto(getSaveReservationResponse()))
        .thenReturn(getSaveReservationResponseDto());

    //act
    final var reservationResponse = reservationPortBusinessCase.updateReservationPackages(
        createSavePackagesRequest());
    final var reservationResponseDto = reservationResponseMapper.toDto(reservationResponse);
    final ResponseEntity<SaveReservationResponseDto> response = hotelReservationControllerUnderTest.saveReservation(
        createSavePackagesRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void cancelOnHoldReservation() {

    //Arrange
    Mockito.when(cancelReservationRequestMapper.toModel(any())).thenReturn(
        CancelReservationRequest.builder()
            .basketReference("ABCDCBA")
            .hotelId("TEST")
            .build());
    Mockito.when(cancelReservationResponseMapper.toDto(any()))
        .thenReturn(new CancelReservationResponseDto("ABCDCBA"));

    //Act

    final ResponseEntity<CancelReservationResponseDto> response =
        hotelReservationControllerUnderTest.cancelOnHoldReservation(
            CancelReservationRequestDto.builder().basketReference("ABCDCBA").hotelId("TEST")
                .build());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());

  }

  @Test
  void updateCompanyQuestionAndAnswer() {
    //Arrange

    Mockito.when(companyQuestionAndAnswerRequestMapper.toModel(any()))
        .thenReturn(createCompanyQuestionAndAnswerDetailsRequest());

    //act
    final ResponseEntity<Void> response = hotelReservationControllerUnderTest.
        updateQuestionsAndAnswers(createCompanyQuestionAndAnswerDetailsRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verifyNoMoreInteractions(updateDiscountRequestMapper);
  }

  @Test
  void updateDiscount() {
    //Arrange

    Mockito.when(updateDiscountRequestMapper.toModel(any()))
        .thenReturn(createUpdateDiscountRequest());

    //act
    final ResponseEntity<Void> response = hotelReservationControllerUnderTest.updateDiscount(
        createUpdateDiscountRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verifyNoMoreInteractions(updateDiscountRequestMapper);
  }

  @Test
  void updateSpecialRequests__ShouldReturnOk() {
    //Arrange

    Mockito.when(specialRequestsMapper.toModel(any()))
        .thenReturn(createSpecialRequestsMock());

    //act
    final ResponseEntity<Void> response = hotelReservationControllerUnderTest.updateReservationsSpecialRequests(
        updateSpecialRequestsDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verifyNoMoreInteractions(specialRequestsMapper);
  }

  private SpecialRequestsDto updateSpecialRequestsDto() {
    return SpecialRequestsDto.builder()
        .hotelId("MANOLD")
        .reservationIds(List.of("12345"))
        .specialRequests(List.of("SING"))
        .build();
  }

  private SpecialRequests createSpecialRequestsMock() {
    return SpecialRequests.builder()
        .hotelId("MANOLD")
        .reservationIds(List.of("12345"))
        .specialRequests(List.of("SING"))
        .build();
  }

  @Test
  void removeRoom_Success() {
    //Arrange
    String tempBookingRef = "tempBasket";
    var mapperResponse = new TempBookingRefResponseDto();
    mapperResponse.setTempBookingRef(tempBookingRef);

    when(tempBookingRefResponseMapper.toDto(any())).thenReturn(mapperResponse);
    when(reservationPortBusinessCase.removeRoom(anyString(), anyString(), anyString(), anyBoolean(),
        any(), any())).thenReturn(
        TempBookingRefResponse.builder()
            .tempBookingRef(tempBookingRef).build());

    //Act
    var response = hotelReservationControllerUnderTest
        .removeRoom("1234", " 1234", "token", new BookingChannelDto());

    //Assert
    assertNotNull(response);
    org.junit.jupiter.api.Assertions.assertEquals(tempBookingRef, response.getTempBookingRef());
    verifyNoMoreInteractions(tempBookingRefResponseMapper);
  }

  @Test
  void updateReasonForStay() {
    //Arrange
    Mockito.when(updateReasonForStayRequestMapper.toModel(any()))
        .thenReturn(createReasonForStayRequest());
    Mockito.when(updateReasonForStayResponseMapper.toDto(any()))
        .thenReturn(new UpdateReasonForStayResponseDto());

    //act
    var response = hotelReservationControllerUnderTest.updateReasonForStay(
        createReasonForStayRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verifyNoMoreInteractions(updateReasonForStayRequestMapper);
  }

  @Test
  void createReservationGuest__success() {
    //Arrange
    var reservationGuestRequestDto = createReservationGuestRequestDto();
    var reservationGuestRequest = mockReservationGuestRequest();

    Mockito.when(reservationGuestRequestMapper.toModel(any())).thenReturn(reservationGuestRequest);
    Mockito.when(reservationPortBusinessCase.createReservationGuest(anyString(), any()))
        .thenReturn(new ReservationGuestResponse());
    Mockito.when(reservationGuestResponseMapper.toDto(any()))
        .thenReturn(new ReservationGuestResponseDto());

    //act
    var response = hotelReservationControllerUnderTest
        .createReservationGuest(reservationGuestRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 201, response.getStatusCode().value());
  }

  @Test
  void deposits__ShouldReturnDepositsResponse() {
    //Arrange
    String hotelId = "FRAMTI";
    String resNo = "12345678";

    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(Arrays.asList(Deposits.builder().paymentReference("3CPREFERENCE")
        .build()));

    var depositsResponseDto = new DepositsResponseDto(
        Arrays.asList(DepositsDto.builder().paymentReference("3CPREFERENCE")
            .build()));

    Mockito.when(reservationPortBusinessCase.getDepositsForReservationId(hotelId, resNo))
        .thenReturn(depositsResponse);
    Mockito.when(depositsResponseMapper.toDto(depositsResponse))
        .thenReturn(depositsResponseDto);

    //act
    final ResponseEntity<DepositsResponseDto> response = hotelReservationControllerUnderTest.getDepositsForReservationId(
        hotelId, resNo);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(),
        depositsResponseDto.getDeposits().get(0).getPaymentReference(),
        response.getBody().getDeposits().get(0).getPaymentReference());
  }

  @Test
  void marketingPreferences__ShouldReturnMarketingPreferencesResponse() {
    //Arrange
    String hotelId = "FRAMTI";
    String resNo = "12345678";

    var marketingPreferencesResponse = new MarketingPreferencesResponse();
    marketingPreferencesResponse.setContactValue("mail@mail.com");
    marketingPreferencesResponse.setOptIn(true);
    marketingPreferencesResponse.setCustomer(new Customer("Mr", "Sarah", "Smith", "GB", "en"));

    var customerDto = new CustomerDto("Mr", "Sarah", "Smith", "GB", "en");
    var marketingPreferencesResponseDto = new MarketingPreferencesResponseDto(true, customerDto,
        "mail@mail.com");

    Mockito.when(reservationPortBusinessCase.getMarketingPreferences(hotelId, resNo))
        .thenReturn(marketingPreferencesResponse);
    Mockito.when(marketingPreferencesResponseMapper.toDto(marketingPreferencesResponse))
        .thenReturn(marketingPreferencesResponseDto);

    //act
    final ResponseEntity<MarketingPreferencesResponseDto> response = hotelReservationControllerUnderTest.getMarketingPreferences(
        hotelId, resNo);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), marketingPreferencesResponseDto.getOptIn(), Objects
        .requireNonNull(response.getBody()).getOptIn());
    assertEquals(response.toString(), marketingPreferencesResponseDto.getContactValue(), Objects
        .requireNonNull(response.getBody()).getContactValue());
  }

  @Test
  void getBookingAllowancesByBasketReference__ShouldReturnOk() {
    //Arrange
    String basketReference = "ABC1234567";
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    Mockito.when(reservationPortBusinessCase.getBookingAllowances(anyString()))
        .thenReturn(bookingAllowancesResponse);

    //Act
    var response =
        hotelReservationControllerUnderTest.getBookingAllowancesByBasketReference(basketReference);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());

  }

  private BookingAllowancesResponse mockCreateBookingAllowancesResponse() {
    return BookingAllowancesResponse.builder()
        .bookingAllowances(Collections.singletonList(BookingAllowance.builder()
            .allowance("dinner")
            .budget(BigDecimal.TEN)
            .build()
        ))
        .build();
  }

  @Test
  void amendEditRoom_success() {

    //Setup
    setUp();
    //Arrange
    Mockito.when(editRoomRequestMapper.toModel(any()))
        .thenReturn(createUpdateReservationsRequest());
    Mockito.when(tempBookingRefResponseMapper.toDto(any()))
        .thenReturn(new TempBookingRefResponseDto());

    //act
    var response = hotelReservationControllerUnderTest.amendEditRoom(
        editRoomRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verifyNoMoreInteractions(editRoomRequestMapper);
  }

  @Test
  void saveReservationPackagesByReservationId_success() {
    //Arrange
    when(updateReservationRequestMapper
        .toModel(any(ReservationPackagesRequestByIdDto.class))).thenReturn(
        new UpdateReservationPackagesByIdRequest());
    when(reservationPortBusinessCase.updateReservationPackagesById(any(), anyBoolean())).thenReturn(
        new SaveReservationResponse("12345"));
    when(reservationResponseMapper.toDto(any(SaveReservationResponse.class)))
        .thenReturn(new SaveReservationResponseDto("12345"));

    //Act

    final ResponseEntity<SaveReservationResponseDto> response =
        hotelReservationControllerUnderTest.saveReservationPackagesByReservationId(
            new ReservationPackagesRequestByIdDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void updateReservationRatePlanCode_success() {
    //Arrange
    when(updateReservationRequestMapper
        .toModel(any(UpdateRateCodeRequestDto.class))).thenReturn(new UpdateRequest());
    when(reservationPortBusinessCase.updateReservationRateCode(any())).thenReturn(
        new SaveReservationResponse("12345"));
    when(reservationResponseMapper.toDto(any(SaveReservationResponse.class)))
        .thenReturn(new SaveReservationResponseDto("12345"));

    //Act

    final ResponseEntity<SaveReservationResponseDto> response =
        hotelReservationControllerUnderTest.updateReservationRatePlanCode(
            new UpdateRateCodeRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void updateRoomType_success() {
    //Arrange
    when(updateReservationRequestMapper
        .toModel(any(UpdateRoomTypeRequestDto.class))).thenReturn(new UpdateRequest());
    when(reservationPortBusinessCase.updateRoomType(any())).thenReturn(
        new SaveReservationResponse("12345"));
    when(reservationResponseMapper.toDto(any(SaveReservationResponse.class)))
        .thenReturn(new SaveReservationResponseDto("12345"));

    //Act
    final ResponseEntity<SaveReservationResponseDto> response =
        hotelReservationControllerUnderTest.updateRoomType(
            new UpdateRoomTypeRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void cancelReservation_success() {
    //Arrange
    when(cancelReservationRequestMapper
        .toModel(any(CancelReservationRequestDto.class))).thenReturn(
        CancelReservationRequest.builder().build());
    when(reservationPortBusinessCase.cancelReservation(any())).thenReturn(
        new CancelReservationResponse());
    when(cancelReservationResponseMapper.toDto(any(CancelReservationResponse.class)))
        .thenReturn(new CancelReservationResponseDto("12345"));

    //Act

    final ResponseEntity<CancelReservationResponseDto> response =
        hotelReservationControllerUnderTest.cancelReservation(new CancelReservationRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void rollbackReservation_success() {
    //Arrange
    when(cancelReservationRequestMapper
        .toModel(any(CancelReservationRequestDto.class))).thenReturn(
        CancelReservationRequest.builder().build());
    when(reservationPortBusinessCase.rollbackReservation(any())).thenReturn(
        new CancelReservationResponse());
    when(cancelReservationResponseMapper.toDto(any(CancelReservationResponse.class)))
        .thenReturn(new CancelReservationResponseDto("1345"));

    //Act

    final ResponseEntity<CancelReservationResponseDto> response =
        hotelReservationControllerUnderTest.rollbackReservation(new CancelReservationRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void updateCnpReservations_success() {
    //Arrange
    when(updateCnpReservationRequestMapper.toModel(any())).thenReturn(
        new UpdateCnpReservationRequest());
    when(reservationPortBusinessCase.updateCnpReservation(any(), any())).thenReturn(
        new UpdateCnpReservationResponse());
    when(updateCnpReservationResponseMapper.toDto(any()))
        .thenReturn(new UpdateCnpReservationResponseDto("1345"));

    //Act

    final ResponseEntity<UpdateCnpReservationResponseDto> response =
        hotelReservationControllerUnderTest.updateCnpReservations("1234",
            new UpdateCnpReservationRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void addNewRoomToExistingBasket_success() {
    //Arrange
    when(addNewRoomRequestMapper.toModel(any())).thenReturn(ReservationRequest.builder().build());
    when(reservationPortBusinessCase.addNewRoomToExistingBasket(any(), any(), any())).thenReturn(
        TempBookingRefResponse.builder().build());
    when(tempBookingRefResponseMapper.toDto(any())).thenReturn(new TempBookingRefResponseDto());

    //Act

    final TempBookingRefResponseDto response =
        hotelReservationControllerUnderTest.addNewRoomToExistingBasket(new AddNewRoomRequestDto());

    //Assert
    assertNotNull(response);
  }

  @Test
  void getAmendSummaryDetails_success() {
    //Arrange
    when(amendLogicInPort.getAmendSummaryDetails(any())).thenReturn(new AmendSummaryDetails());
    when(amendSummaryDetailsMapper.toDto(any())).thenReturn(mockAmendSummaryDetailsDto());

    //Act

    final AmendSummaryDetailsDto response =
        hotelReservationControllerUnderTest.getAmendSummaryDetails(
            AmendSummaryRequest.builder().build());

    //Assert
    assertNotNull(response);
  }

  @Test
  void amendStayDates_success() {
    //Arrange
    when(amendStayDatesRequestMapper.toModel(any())).thenReturn(new AmendStayDatesRequest());
    when(reservationPortBusinessCase.amendStayDates(any(), any())).thenReturn(
        AmendStayDatesResponse.builder().build());
    when(amendStayDatesResponseMapper.toDto(any())).thenReturn(new AmendStayDatesResponseDto());

    //Act

    final ResponseEntity<AmendStayDatesResponseDto> response =
        hotelReservationControllerUnderTest.amendStayDates(new AmendStayDatesRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void confirmAmend_success() {
    //Arrange
    when(confirmAmendRequestMapper.toModel(any())).thenReturn(
        new ConfirmAmendRequest("1234", "1234", "1234", BookingChannel.builder().build(),
            false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", ""));
    when(reservationPortBusinessCase.confirmAmend(any(), anyBoolean())).thenReturn(
        ReservationByBasketRefResponse.builder().build());
    when(reservationByBasketRefResponseMapper.toDto(
        any(ReservationByBasketRefResponse.class))).thenReturn(
        new ReservationByBasketRefResponseDto());

    //Act

    final ResponseEntity<ReservationByBasketRefResponseDto> response =
        hotelReservationControllerUnderTest.confirmAmend(new ConfirmAmendRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 201, response.getStatusCode().value());
  }

  @Test
  void amendDistribution_success() {
    //Arrange
    String basketReference = "AWM-3c3fe963-5c2c-403d-9640-345f2eeef5c2";
    AmendDistributionRequestDto request = mockAmendDistributionRequest();
    ReservationRequest reservationRequest = mockReservationRequest();
    UpdateReservationsRequest updateReservationsRequest = createUpdateReservationsRequest();
    UpdatedReservationsDistribution updateRequest = UpdatedReservationsDistribution.builder()
        .updatedReservations(List.of(updateReservationsRequest)).build();

    when(amendDistributionRequestMapper.toReservationRequestModel(any())).thenReturn(
        reservationRequest);
    when(amendDistributionRequestMapper.toUpdateReservationsDistrModel(any()))
        .thenReturn(updateRequest);
    when(bookerDetailsMapper.toModel(any())).thenReturn(new BookerDetailsCnp());

    //Act
    final ResponseEntity<ReservationByBasketRefResponseDto> response =
        hotelReservationControllerUnderTest.amendDistribution(basketReference, request);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void updateEmailReservation() {

    String basketReference = "AWM12345";
    //Arrange

    Mockito.when(updateEmailReservationRequestMapper.toModel(any()))
        .thenReturn(createUpdateEmailreservationRequest());

    //act
    final ResponseEntity<String> response = hotelReservationControllerUnderTest
        .updateEmailReservations(basketReference, createUpdateEmailreservationRequestDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verifyNoMoreInteractions(updateDiscountRequestMapper);
  }

  private AmendSummaryDetailsDto mockAmendSummaryDetailsDto() {
    return AmendSummaryDetailsDto.builder()
        .paymentCardDetails(PaymentCardDetailsDto.builder()
            .cardNumberLast4Digits("1100")
            .cardHolderName("Testerson").build()).build();
  }

  @Test
  void createMemo_Success() {
    // Arrange
    when(memosMapper.toModel(any(CreateMemoRequestDto.class))).thenReturn(new CreateMemoRequest());
    when(memosMapper.toDto(any())).thenReturn(new MemosResponseDto());

    // Act
    var response = hotelReservationControllerUnderTest.createMemo(new CreateMemoRequestDto());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMemos_Success() {
    // Arrange
    when(memosMapper.toDto(any())).thenReturn(new MemosResponseDto());

    // Act
    var response = hotelReservationControllerUnderTest.getMemos("basketReference");

    // Assert
    assertNotNull(response);
  }

  @Test
  void verifySearchBookingFromCdhReturnSuccess() {

    when(cdhSearchBookingRequestMapper.toModel(any())).thenReturn(new CdhSearchBookingsRequest());
    when(cdhSearchBookingInPort.searchBookingsFromCdh(any())).thenReturn(
        CdhSearchBookingsResponse.builder().build());
    when(cdhSearchBookingResponseMapper.toDto(any())).thenReturn(
        new CdhSearchBookingsResponseDto());

    var response = hotelReservationControllerUnderTest.searchBookingFromCdh(
        new CdhSearchBookingsRequestDto());

    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void testReservationUpdateSingleCall_Success() {
    when(updateReservationRequestMapper.toModel(
        new UpdateReservationSingleCallRequestDto())).thenReturn(
        new UpdateReservationSingleCallRequest());
    when(reservationPortBusinessCase.updateReservation(any()))
        .thenReturn(new ConfirmReservationResponse());

    var response = hotelReservationControllerUnderTest.updateReservation(
        new UpdateReservationSingleCallRequestDto());

    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void deleteRoutingInstructions_Success() {

    //act
    final ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.deleteRoutingInstructions("HOTEL_ID",
            new HashSet<>(List.of("1234")));

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 204, response.getStatusCode().value());
  }

  @Test
  void addAttachmentToReservation_success() {
    // Arrange
    ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto = createReservationFileAttachmentRequestDto();
    ReservationFileAttachmentRequest reservationFileAttachmentRequest = createReservationFileAttachmentRequest();
    PreCheckInResponse preCheckInResponse = createPreCheckInResponse();

    when(reservationFileAttachmentRequestMapper.toModel(any())).thenReturn(
        reservationFileAttachmentRequest);
    when(reservationPortBusinessCase.addAttachmentToReservation(any())).thenReturn(
        preCheckInResponse);

    // Act
    ResponseEntity<PreCheckInResponse> response = hotelReservationControllerUnderTest.addAttachmentToReservation(
        reservationFileAttachmentRequestDto);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void saveReservationPreCheckIn_success() {
    // Arrange
    PreCheckInRequestDto preCheckInRequestDto = createPreCheckInRequestDto();
    PreCheckInRequest preCheckInRequest = createPreCheckInRequest();
    PreCheckInResponse preCheckInResponse = createPreCheckInResponse();

    when(preCheckInRequestMapper.toModel(any())).thenReturn(preCheckInRequest);
    when(reservationPortBusinessCase.saveReservationPreCheckIn(any())).thenReturn(
        preCheckInResponse);

    // Act
    ResponseEntity<PreCheckInResponse> response = hotelReservationControllerUnderTest.saveReservationPreCheckIn(
        preCheckInRequestDto);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void updateReservationPackages_success() {
    // Arrange
    var requestDto = new ReservationPackagesScheduledRequestDto();
    var request = createReservationPackagesScheduledRequest();

    when(updateReservationRequestMapper.toModel(
        any(ReservationPackagesScheduledRequestDto.class))).thenReturn(request);
    when(reservationPortBusinessCase.updateReservationPackageScheduled(request))
        .thenReturn(new SaveReservationResponse("basketRef"));
    when(reservationResponseMapper.toDto(any(SaveReservationResponse.class)))
        .thenReturn(new SaveReservationResponseDto("basketRef"));

    // Act
    var response = hotelReservationControllerUnderTest.updateReservationPackages(requestDto);

    // Assert
    assertNotNull(response);
    assertEquals("check basketRef", "basketRef", response.getBasketReference());
  }

  @Test
  void updateReservationPreferences_success() {
    // Arrange
    var requestDto = new ReservationPreferencesRequestDto();
    var request = createReservationPreferencesRequest();

    when(reservationPreferencesRequestMapper.toModel(
        any(ReservationPreferencesRequestDto.class))).thenReturn(request);

    // Act
    var response = hotelReservationControllerUnderTest.updatePreferences(requestDto);

    // Assert
    assertNotNull(response);
    assertEquals("status code", HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void linkReservationToLeisureCustomer_ReturnsSuccess() {
    //Arrange
    var linkReservationToLeisureCustomerRequestDto =
        LinkReservationToLeisureCustomerRequestDto.builder()
            .basketReference("BKS-1234a567-qq7b-2024-123s-aa5ssbn557gh")
            .customerAccountId("CUST-12234")
            .build();

    var linkReservationToLeisureCustomerRequest =
        LinkReservationToLeisureCustomerRequest.builder()
            .basketReference("BKS-1234a567-qq7b-2024-123s-aa5ssbn557gh")
            .customerAccountId("CUST-12234")
            .build();

    Mockito.when(linkReservationToLeisureCustomerRequestMapper.toModel(any()))
        .thenReturn(linkReservationToLeisureCustomerRequest);

    //act
    final ResponseEntity<Void> response = hotelReservationControllerUnderTest
        .linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 204, response.getStatusCode().value());
    verifyNoMoreInteractions(linkReservationToLeisureCustomerRequestMapper);
  }

  @Test
  void updateReservationUdfs_success() {
    // Arrange
    var requestDto = new UpdateReservationAlertsRequestDto(Set.of("rsvId"),
        "hotelid",
        List.of(new AlertsDto("id","area", "code",
            "description", true, false)));
    var request = new UpdateReservationAlertsRequest();

    when(reservationRequestMapper.toAlertModel(
        any(UpdateReservationAlertsRequestDto.class))).thenReturn(request);

    // Act
    hotelReservationControllerUnderTest.updateReservationAlerts(requestDto);

    // Assert
    verify(reservationPortBusinessCase, times(1)).updateReservationAlerts(request);
  }


  private PreCheckInRequestDto createPreCheckInRequestDto() {
    return PreCheckInRequestDto.builder()
        .hotelId("STAUIR")
        .reservationId("123456")
        .arrivalTime(LocalDate.of(2024, 7, 13))
        .build();
  }

  private PreCheckInRequest createPreCheckInRequest() {
    return PreCheckInRequest.builder()
        .hotelId("STAUIR")
        .reservationId("123456")
        .arrivalTime(LocalDate.of(2024, 7, 13))
        .build();
  }

  private ReservationFileAttachmentRequestDto createReservationFileAttachmentRequestDto() {
    return ReservationFileAttachmentRequestDto.builder()
        .fileName("REG_RES1234567_ID232323_P76767676.pdf")
        .reservationId("1234567")
        .overwriteExistingFile(false)
        .description("Test description")
        .hotelId("STUAIR")
        .global(false)
        .fileAttachment("Base64 encoded string")
        .build();
  }

  private ReservationFileAttachmentRequest createReservationFileAttachmentRequest() {
    return ReservationFileAttachmentRequest.builder()
        .fileName("REG_RES1234567_ID232323_P76767676.pdf")
        .reservationId("1234567")
        .overwriteExistingFile(false)
        .description("Test description")
        .hotelId("STUAIR")
        .global(false)
        .fileAttachment("Base64 encoded string")
        .build();
  }

  private PreCheckInResponse createPreCheckInResponse() {
    return PreCheckInResponse.builder()
        .status("Success")
        .message("File attached successfully")
        .build();
  }

  private UpdateEmailReservationRequest createUpdateEmailreservationRequest() {
    return UpdateEmailReservationRequest.builder()
        .email("secondEmail@domain.uk").build();
  }

  private UpdateEmailReservationRequestDto createUpdateEmailreservationRequestDto() {
    return UpdateEmailReservationRequestDto.builder()
        .email("secondEmail@domain.uk").build();
  }

  private UpdateReservationsRequest createUpdateReservationsRequest() {
    var roomOccupancy = new UpdateRoomOccupancyRequest();
    roomOccupancy.setAdultCount(1);
    roomOccupancy.setChildCount(0);

    var roomRate = new UpdateRoomRateRequest();
    roomRate.setRoomOccupancy(roomOccupancy);
    roomRate.setEndDate("2030-03-29");
    roomRate.setStartDate("2030-03-25");
    roomRate.setRoomType("DB");
    roomRate.setRatePlanCode("FLEXRATE");

    var roomStay = new UpdateRoomStayRequest();
    roomStay.setRoomRates(List.of(roomRate));
    roomStay.setRoomOccupancy(roomOccupancy);
    roomStay.setArrivalDate("2030-03-25");
    roomStay.setDepartureDate("2030-03-29");

    var addressType = AddressType.builder()
        .addressLine(List.of("line1", "line2", "line3"))
        .postalCode("BS7 2EP")
        .cityName("Bristol")
        .country(new Country("GB")).build();
    var addressInfoType = AddressInfoType.builder()
        .address(addressType).build();
    var profileTypeAddresses = ProfileTypeAddresses.builder()
        .addressInfo(List.of(addressInfoType)).build();
    var profileType = ProfileType.builder()
        .addresses(profileTypeAddresses).build();
    var profileInfo = ProfileInfo.builder()
        .profile(profileType).build();
    ReservationGuests reservationGuests = ReservationGuests.builder()
        .profileInfo(profileInfo).build();

    var updateReservationRequest = new UpdateReservationRequest();
    updateReservationRequest.setReservationId("1234578");
    updateReservationRequest.setHotelId("TestHotelId");
    updateReservationRequest.setRoomStay(roomStay);
    updateReservationRequest.setReservationGuests(List.of(reservationGuests));

    var updateReservationsRequest = new UpdateReservationsRequest();
    updateReservationsRequest.setReservations(List.of(updateReservationRequest));

    return updateReservationsRequest;
  }

  private ReservationResponse getReservationResponse() {
    ReservationResponse reservationResponse = new ReservationResponse();
    reservationResponse.setHotelId("FRAMTI");
    return reservationResponse;
  }

  private ReservationResponseDto getReservationResponseDto() {
    ReservationResponseDto reservationResponseDto = new ReservationResponseDto();
    reservationResponseDto.setHotelId("FRAMTI");
    return reservationResponseDto;
  }

  private ReservationsDetailsResponse getReservationDetailResponse() {
    ReservationsDetailsResponse reservationsDetailsResponse = new ReservationsDetailsResponse();
    reservationsDetailsResponse.setReservations(getReservations());
    return reservationsDetailsResponse;
  }

  private Reservations getReservations() {
    Reservations reservations = new Reservations(null, 1, 0, 20, false, 20);
    return reservations;
  }

  private ReservationsDetailsResponseDto getReservationsDetailResponseDto() {
    ReservationsDetailsResponseDto reservationsDetailsResponseDto = new ReservationsDetailsResponseDto(
        getReservationsDto());
    return reservationsDetailsResponseDto;
  }

  private ReservationsDto getReservationsDto() {
    ReservationsDto reservationsDto = new ReservationsDto(null, 1, 0, 20, false, 20);
    return reservationsDto;
  }

  private ConfirmReservationResponse getConfirmReservationResponse() {
    ConfirmReservationResponse confirmReservationResponse = new ConfirmReservationResponse(
        getConfirmedReservationIds(),
        getConfirmationRoomStayResponse(),
        getConfirmationCustomer(),
        "LONEUS",
        "Reserved",
        true
    );
    return confirmReservationResponse;
  }

  private List<UniqueIDType> getConfirmedReservationIds() {
    return List.of(createUniqueIDType("147", "Reservation"),
        createUniqueIDType("852", "Confirmation"));
  }

  private List<UniqueIDTypeDto> getDtoConfirmedReservatinIds() {
    return List.of(createUniqueIDTypeDto("34865", "Reservation"),
        createUniqueIDTypeDto("264873", "Confirmation"));
  }

  private ConfirmReservationResponseDto getConfirmReservationResponseDto() {
    ConfirmReservationResponseDto confirmReservationResponseDto = new ConfirmReservationResponseDto(
        getDtoConfirmedReservatinIds(),
        getDtoConfirmationRoomStayResponse(),
        getDtoConfirmationCustomer(),
        "LONEUS",
        "Reserved", true
    );
    return confirmReservationResponseDto;
  }

  private ReservationByBasketRefResponse getReservationByBasketRefResponse() {
    ReservationByBasketRefResponse reservationByBasketRefResponse = new ReservationByBasketRefResponse();
    reservationByBasketRefResponse.setHotelId("FRAMTI");
    return reservationByBasketRefResponse;
  }

  private ReservationByBasketRefResponseDto getReservationByBasketRefResponseDto() {
    return new ReservationByBasketRefResponseDto(
        null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
        BigDecimal.TEN, "P1", "FRAMTI",
        "GBP", "BSKT123456", "WEB", null, false, false, "12244", "123", "123", "completed", null,
        null, null,null,false);
  }

  private SaveReservationResponse getSaveReservationResponse() {
    SaveReservationResponse saveReservationResponse = new SaveReservationResponse("TestId1234567");
    return saveReservationResponse;
  }

  private SaveReservationResponseDto getSaveReservationResponseDto() {
    SaveReservationResponseDto saveReservationResponseDto = new SaveReservationResponseDto("001");
    return saveReservationResponseDto;
  }

  private ReservationPackagesRequest createSavePackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setBasketReference("TestId1234567");
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
        Collections.singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }

  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("MDP");

    return packagesSelection;
  }

  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(Collections.singletonList(createPackagesSelection()));

    return roomsSelections;
  }

  private ReservationPackagesRequestDto createSavePackagesRequestDto() {
    ReservationPackagesRequestDto savePackagesRequest = new ReservationPackagesRequestDto();

    savePackagesRequest.setBasketReference("TestId1234567");
    savePackagesRequest.setHotelId("HOTELCODE");
    savePackagesRequest.setArrival("2022-04-02");
    savePackagesRequest.setDeparture("2022-04-03");

    savePackagesRequest.setRoomsSelections(Collections.singletonList(createRoomsSelectionsDto()));
    return savePackagesRequest;
  }

  private PackagesSelectionDto createPackagesSelectionDto() {
    PackagesSelectionDto packagesSelection = new PackagesSelectionDto();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("MDP");

    return packagesSelection;
  }

  private RoomsSelectionsDto createRoomsSelectionsDto() {
    RoomsSelectionsDto roomsSelections = new RoomsSelectionsDto();

    roomsSelections.setPackagesSelection(Collections.singletonList(createPackagesSelectionDto()));

    return roomsSelections;
  }

  private ReservationRequest mockReservationRequest() {
    BookingChannel bookingChannel = BookingChannel.builder()
        .channel("DISTR")
        .subchannel("WEB")
        .language("EN")
        .build();

    RoomRate roomRate = RoomRate.builder()
        .startDate("2023-10-20")
        .endDate("2023-10-21")
        .pmsRoomType("SDB")
        .ratePlanCode("DAILY")
        .cellCode("ABC")
        .specialRequests(List.of("SING"))
        .build();

    Reservation reservation = Reservation.builder()
        .externalReferenceId("123456")
        .hotelId("MANOLD")
        .roomRates(roomRate)
        .leadGuest(LeadGuest.builder()
            .firstName("Marrie")
            .lastName("Smith")
            .title("Mrs")
            .emailAddress("marrie_smith@example.com")
            .build())
        .build();

    return ReservationRequest.builder()
        .reservations(List.of(reservation))
        .token("")
        .bookingChannel(bookingChannel)
        .build();
  }

  private AmendDistributionRequestDto mockAmendDistributionRequest() {
    BookingChannelDto bookingChannelDto = new BookingChannelDto();
    bookingChannelDto.setChannel("DISTR");
    bookingChannelDto.setSubchannel("WEB");
    bookingChannelDto.setLanguage("EN");

    RoomStayDistributionDto roomStayDistributionDto = RoomStayDistributionDto.builder()
        .arrivalDate("2023-06-01")
        .departureDate("2023-06-05")
        .adultsNumber(2)
        .childrenNumber(1)
        .roomType("Standard")
        .ratePlanCode("RP123")
        .cot(true)
        .specialRequests(List.of("SING"))
        .build();

    AmendDistributionGuestDto guestDto = AmendDistributionGuestDto.builder()
        .givenName("John")
        .surName("Doe")
        .nameTitle("Mr.")
        .email("johndoe@example.com")
        .build();

    PackagesSelectionDto packagesSelection = new PackagesSelectionDto();
    packagesSelection.setId("MDP");
    packagesSelection.setNoSelections(1);

    RoomsSelectionsDto roomsSelections = new RoomsSelectionsDto();
    roomsSelections.setPackagesSelection(List.of(packagesSelection));

    AmendPackagesDistributionDto packages = new AmendPackagesDistributionDto("id", 1);

    AmendDistributionReservationDto amendDistributionReservationDto = AmendDistributionReservationDto.builder()
        .reservationId("123456")
        .hotelId("MANOLD")
        .roomStay(roomStayDistributionDto)
        .reservationGuestList(List.of(guestDto))
        .reservationPackageList(List.of(packages))
        .build();

    return AmendDistributionRequestDto.builder()
        .reservations(List.of(amendDistributionReservationDto))
        .bookingChannel(bookingChannelDto)
        .token(
            "dtecI7A2eVY6+YQowqNbi1yWNU+rACbzWbX3iHPgZVKKZc7/UWCgXIxQAqmqfpNiPAFOK9pwWxahCqtmmer4i8RYCqOCMS8EnZUg+piQVBvXwrY=")
        .bookingNotes(List.of("BookingNote1"))
        .build();
  }

  private CompanyQuestionAndAnswerDetailsRequest createCompanyQuestionAndAnswerDetailsRequest() {
    return CompanyQuestionAndAnswerDetailsRequest.builder()
        .hotelId("TestHotelId")
        .reservationIds(Set.of("123456"))
        .build();
  }

  private UpdateDiscountRequest createUpdateDiscountRequest() {
    return UpdateDiscountRequest.builder()
        .discountAmount(BigDecimal.valueOf(10L))
        .currency("USD")
        .hotelId("TestHotelId")
        .reservationIds(List.of("123456", "1234578"))
        .build();
  }

  private CompanyQuestionAndAnswerDetailsRequestDto createCompanyQuestionAndAnswerDetailsRequestDto() {
    return CompanyQuestionAndAnswerDetailsRequestDto.builder()
        .hotelId("TestHotelId")
        .reservationIds(Set.of("123456"))
        .build();
  }

  private UpdateDiscountRequestDto createUpdateDiscountRequestDto() {
    return UpdateDiscountRequestDto.builder()
        .discountAmount(BigDecimal.valueOf(10L))
        .currency("USD")
        .hotelId("TestHotelId")
        .reservationIds(List.of("123456", "1234578"))
        .build();
  }


  private UpdateReasonForStayRequest createReasonForStayRequest() {
    return UpdateReasonForStayRequest.builder()
        .hotelId("TestHotelId")
        .reservationIds(Collections.singletonList("1234578"))
        .reasonForStay("LEI")
        .build();
  }

  private UpdateReasonForStayRequestDto createReasonForStayRequestDto() {
    UpdateReasonForStayRequestDto updateReasonForStayRequestDto = new UpdateReasonForStayRequestDto();
    updateReasonForStayRequestDto.setReasonForStay("LEI");
    updateReasonForStayRequestDto.setBasketReference("TestBasketReference");
    updateReasonForStayRequestDto.setHotelId("TestHotelId");
    return updateReasonForStayRequestDto;

  }

  private ReservationGuestRequest mockReservationGuestRequest() {
    final StayingGuestAddress address = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .cityName("London")
        .companyName("company")
        .build();

    final StayingGuestAdditionalDetails additionalDetails = StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.parse("1996-07-13"))
        .nationality("Briton")
        .passportNumber("PR123JDS")
        .build();

    var stayingGuestDetails = StayingGuestDetails.builder()
        .firstName("John")
        .lastName("Doe")
        .title("Mr")
        .address(address)
        .additionalDetails(additionalDetails)
        .build();

    var stayingGuest = StayingGuest.builder()
        .sameAsBooker(Boolean.TRUE)
        .stayingGuestDetails(stayingGuestDetails)
        .build();

    return ReservationGuestRequest.builder()
        .basketReference("test")
        .hotelId("LONEUS")
        .reasonForStay("LEI")
        .stayingGuests(List.of(stayingGuest))
        .sendEmailInvoice(true)
        .sendEmailConfirmation(true)
        .build();
  }

  private ReservationGuestRequestDto createReservationGuestRequestDto() {
    final StayingGuestAddressDto address = StayingGuestAddressDto.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .cityName("London")
        .companyName("company")
        .build();

    final StayingGuestAdditionalDetailsDto additionalDetails = StayingGuestAdditionalDetailsDto.builder()
        .dob(LocalDate.parse("1996-07-13"))
        .nationality("Briton")
        .passportNumber("PR123JDS")
        .build();

    var stayingGuestDetails = new StayingGuestDetailsDto();
    stayingGuestDetails.setFirstName("John");
    stayingGuestDetails.setLastName("Doe");
    stayingGuestDetails.setTitle("Mr");
    stayingGuestDetails.setAddress(address);
    stayingGuestDetails.setAdditionalDetails(additionalDetails);

    var stayingGuest = new StayingGuestDto();
    stayingGuest.setSameAsBooker(Boolean.TRUE);
    stayingGuest.setStayingGuestDetails(stayingGuestDetails);

    var reservationGuestRequest = new ReservationGuestRequestDto();
    reservationGuestRequest.setBasketReference("test");
    reservationGuestRequest.setHotelId("LONEUS");
    reservationGuestRequest.setReasonForStay("LEI");
    reservationGuestRequest.setStayingGuests(List.of(stayingGuest));
    reservationGuestRequest.setSendEmailConfirmation(true);
    reservationGuestRequest.setSendEmailInvoice(true);

    return reservationGuestRequest;
  }

  private ReservationPackagesScheduledRequest createReservationPackagesScheduledRequest() {

    PackagesSelectionScheduled addPackage2 = new PackagesSelectionScheduled("pkg2", 1,
        List.of(LocalDate.of(2022, 5, 5)));

    PackagesSelection removePackage1 = new PackagesSelection("pkg3", 1);
    PackagesSelection removePackage2 = new PackagesSelection("pkg4", 2);

    RoomReservationPackagesScheduledRequest roomReservation = new RoomReservationPackagesScheduledRequest(
        "11111111", List.of(addPackage2), Arrays.asList(removePackage1, removePackage2));

    return new ReservationPackagesScheduledRequest("HotelId", List.of(roomReservation));
  }

  private ReservationPreferencesRequest createReservationPreferencesRequest() {

    PreferencesCollection preferencesCollection = new PreferencesCollection();
    preferencesCollection.setPreferenceType("preferenceType");
    preferencesCollection.setPreferences(List.of("BDAY"));

    return new ReservationPreferencesRequest("HotelId", List.of("123456"),
        List.of(preferencesCollection));
  }

  private static void assertResponse(ResponseEntity<ReservationByBasketRefResponseDto> response,
      ReservationByBasketRefResponseDto reservationByBasketRefResponseDto) {
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), reservationByBasketRefResponseDto.getHotelId(),
        response.getBody().getHotelId());
  }

  @Test
  void previewDepositFolios_shouldReturnOk() {
    // Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = Set.of("12345", "67890");
    DepositFoliosResponse depositFoliosResponse = Mockito.mock(DepositFoliosResponse.class);
    DepositFoliosResponseDto depositFoliosResponseDto = Mockito.mock(DepositFoliosResponseDto.class);
    Mockito.when(reservationPortBusinessCase.getGeneratedDepositFolios(hotelId, reservationIds))
            .thenReturn(depositFoliosResponse);
    Mockito.when(depositFoliosMapper.toDto(depositFoliosResponse)).thenReturn(depositFoliosResponseDto);

    // Act
    ResponseEntity<DepositFoliosResponseDto> response = hotelReservationControllerUnderTest
            .getDepositFolioForReservations(hotelId, reservationIds);

    // Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    verify(reservationPortBusinessCase, Mockito.times(1))
            .getGeneratedDepositFolios(hotelId, reservationIds);
    verify(depositFoliosMapper, Mockito.times(1)).toDto(depositFoliosResponse);
  }

  @Test
  void saveDepositFolios_shouldReturnCreated() {
    // Arrange
    DepositFoliosRequestDto requestDto = Mockito.mock(DepositFoliosRequestDto.class);
    DepositFoliosRequest depositFoliosRequest = Mockito.mock(DepositFoliosRequest.class);
    Mockito.when(depositFoliosMapper.toDto(requestDto)).thenReturn(depositFoliosRequest);

    // Act
    ResponseEntity<Void> response = hotelReservationControllerUnderTest.saveDepositFolios(requestDto);

    // Assert
    assertNotNull(response);
    assertEquals(response.toString(), HttpStatus.CREATED.value(), response.getStatusCode().value());

    Mockito.verify(depositFoliosMapper, Mockito.times(1)).toDto(requestDto);
    Mockito.verify(reservationPortBusinessCase, Mockito.times(1)).saveDepositFolios(depositFoliosRequest);
    Mockito.verifyNoMoreInteractions(depositFoliosMapper, reservationPortBusinessCase);
  }
}
