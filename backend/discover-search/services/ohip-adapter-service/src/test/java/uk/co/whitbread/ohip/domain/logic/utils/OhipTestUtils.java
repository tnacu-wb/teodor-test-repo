package uk.co.whitbread.ohip.domain.logic.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInDetailsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationArrivalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPreCheckInDetailsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.med.FileToUpload;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationOverrideReason;
import uk.co.whitbread.ohip.domain.model.reservation.out.BillingResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmationCustomer;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmationRoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.ExternalReferenceType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetails;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetailsEnhancedResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationId;

import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.Reservations;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.UniqueIdType;

public class OhipTestUtils {

  public static ReservationDetailsEnhancedResponse createReservationDetailsEnhancedResponse(String hotelId, String externalReferenceId){
    return ReservationDetailsEnhancedResponse.builder()
        .reservationsDetailsResponse(createReservationsDetailsResponse(hotelId, externalReferenceId))
        .billing(createBillingResponse())
        .totalCost(BigDecimal.valueOf(123))
        .previousTotal(BigDecimal.ZERO)
        .balanceOutstanding(BigDecimal.valueOf(123))
        .currencyCode("GBP")
        .policyCode("OA")
        .build();
  }

  public static ReservationIdDetailsResponse createReservationByIdDetailsResponse(String hotelId, String externalReferenceId){
    return ReservationIdDetailsResponse.builder()
        .reservationIdResponse(createReservationIdDetailsResponse(hotelId, externalReferenceId))
        .build();
  }

  private static BillingResponse createBillingResponse() {
    return BillingResponse.builder()
        .lastName("Potter")
        .firstName("Harry")
        .title("Mr")
        .email("harry.potter@hogwarts.com")
        .build();
  }

  public static ReservationsDetailsResponse createReservationsDetailsResponse(String hotelId,
      String externalReferenceId) {
    return ReservationsDetailsResponse.builder()
        .reservations(createReservations(hotelId, externalReferenceId)).build();
  }


  public static ReservationIdResponse createReservationIdDetailsResponse(String hotelId,
      String reservationId) {
    return ReservationIdResponse.builder()
        .reservations(createReservation(hotelId, reservationId)).build();
  }

  public static ReservationIdDetailsResponse createReservationIdDetailResponse(String hotelId,
      String reservationId) {
    return ReservationIdDetailsResponse.builder()
        .reservationIdResponse(createReservationIdDetailsResponse(hotelId, reservationId)).build();
  }

  private static List<ReservationById> createReservationByIdList(String hotelId, String externalReferenceId) {
    return List.of(ReservationById.builder()
        .billing(BillingResponse.builder().lastName("Jhon").build())
        .build());
  }

  private static Reservations createReservations(String hotelId, String externalReferenceId) {
    return Reservations.builder()
        .reservationInfo(createReservationInfoList(hotelId, externalReferenceId))
        .totalPages(1)
        .offset(20)
        .limit(20)
        .hasMore(false)
        .totalResults(2)
        .build();
  }

  private static ReservationId createReservation(String hotelId, String reservationId) {
    return ReservationId.builder()
        .reservation(createReservationDetailsList(hotelId, reservationId))
        .totalPages(1)
        .offset(20)
        .limit(20)
        .hasMore(false)
        .totalResults(2)
        .build();
  }


  private static List<ReservationInfo> createReservationInfoList(String hotelId,
      String externalReferenceId) {
    return List.of(createReservationInfo(hotelId, "DIGI Euston", externalReferenceId),
        createReservationInfo(hotelId, "Hotel Test", externalReferenceId));
  }

  private static List<ReservationDetails> createReservationDetailsList(String hotelId,
      String reservationId) {
    return List.of(createReservationId(hotelId, "DIGI Euston", reservationId),
        createReservationId(hotelId, "Hotel Test", reservationId));
  }

  private static ReservationDetails createReservationId(String hotelId, String hotelName,
      String reservationId) {
    return ReservationDetails.builder()
        .reservationIdList(createReservationId())
        .roomStay(createRoomStay())
        .hotelId(hotelId)
        .hotelName(hotelName)
        .roomStayReservation(true)
        .build();
  }

  private static ReservationInfo createReservationInfo(String hotelId, String hotelName,
      String externalReferenceId) {
    return ReservationInfo.builder()
        .reservationIdList(createReservationIdList())
        .externalReferences(createExternalReferences(externalReferenceId))
        .roomStay(createRoomStay())
        .reservationGuest(createReservationGuest())
        .hotelId(hotelId)
        .hotelName(hotelName)
        .roomStayReservation(true)
        .build();
  }

  private static List<UniqueIdType> createReservationIdList() {
    return List.of(createUniqueIDType("147", "Reservation"),
        createUniqueIDType("852", "Confirmation"));
  }

  private static List<UniqueIdType> createReservationId() {
    return List.of(createUniqueIDType("123456", "Reservation"),
        createUniqueIDType("852", "Confirmation"));
  }

  private static UniqueIdType createUniqueIDType(String id, String type) {
    return UniqueIdType.builder()
        .id(id)
        .type(type)
        .build();
  }

  private static List<ExternalReferenceType> createExternalReferences(String externalReferenceId) {
    return List.of(createExternalReferenceType(externalReferenceId, "Basket"),
        createExternalReferenceType("456", "otherExternalRef"));
  }

  private static ExternalReferenceType createExternalReferenceType(String id, String idContext) {
    return ExternalReferenceType.builder()
        .id(id)
        .idContext(idContext)
        .build();
  }

  private static RoomStay createRoomStay() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now())
        .adultCount(2)
        .childCount(1)
        .roomClass("ST")
        .roomType("DOUBLE")
        .numberOfRooms(1)
        .ratePlanCode("FLEXRATE")
        .rateAmount(createCurrencyAmountType(BigDecimal.valueOf(100)))
        .rateSuppressed(false)
        .bookingChannelCode("PMS")
        .fixedRate(true)
        .totalAmount(createCurrencyAmountType(BigDecimal.valueOf(100)))
        .marketCode("OTH")
        .sourceCode("PHONE")
        .roomTypeCharged("DOUBLE")
        .roomNumberLocked(false)
        .pseudoRoom(false)
        .build();
  }

  private static CurrencyAmountType createCurrencyAmountType(BigDecimal amount) {
    return CurrencyAmountType.builder()
        .amount(amount)
        .currencyCode("GBP")
        .build();
  }

  private static ReservationGuest createReservationGuest() {
    return ReservationGuest.builder()
        .givenName("John")
        .surname("Doe")
        .nameTitle("Mr")
        .fullName("John Doe")
        .phoneNumber("12345678")
        .email("john.doe@mail.com")
        .birthDate(LocalDate.now())
        .language("English")
        .guestRestricted(false)
        .id("31787")
        .type("Profile")
        .build();
  }

  public static ConfirmReservationRequest createConfirmReservationRequest() {
    return ConfirmReservationRequest.builder()
        .reservationId("34865")
        .hotelId("LONEUS")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .paymentCard(createPaymentCardRequest())
        .build();
  }

  private static PaymentCard createPaymentCardRequest() {
    return PaymentCard.builder()
        .token("4111111111111111")
        .cardType("VA")
        .expirationDate("2025-03-31")
        .cardHolderName("Test")
        .build();
  }

  public static ConfirmReservationResponse mockConfirmReservationResponse() {
    return ConfirmReservationResponse.builder()
        .reservationIdList(createReservationIdList())
        .roomStay(createConfirmationRoomStayResponse())
        .reservationGuest(createConfirmationCustomer())
        .hotelId("hotelId")
        .reservationStatus("Reserved")
        .build();
  }

  private static ConfirmationRoomStay createConfirmationRoomStayResponse() {
    return ConfirmationRoomStay.builder()
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now()).build();
  }

  public static ConfirmationCustomer createConfirmationCustomer() {
    return ConfirmationCustomer.builder()
        .givenName("Emma")
        .surName("Smith").build();
  }

  public static CancelReservationRequest createCancelReservationRequest() {
    return CancelReservationRequest.builder()
        .hotelId("MANOLD")
        .reservationIds(Collections.singletonList("123456"))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
            .chargesByReservationIds(Map.of("123456", List.of(DepositFolioCharge.builder().build())))
        .build();
  }

  public static CancelReservationRequest createCancelReservationRequestWithOverride() {
    var reservationOverrideReason = ReservationOverrideReason.builder()
        .reasonCode("ILL")
        .reasonName("Illness")
        .callerName("Jane Doe")
        .managerName("John Smith").build();
    return CancelReservationRequest.builder()
        .hotelId("MANOLD")
        .reservationIds(Collections.singletonList("123456"))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .reservationOverrideReason(reservationOverrideReason)
        .build();
  }

  public static CancelReservationResponse mockCancelReservationResponse() {
    return CancelReservationResponse.builder()
        .cancellationIds(Collections.singletonList("9876"))
        .build();
  }

  public static ReservationFileAttachmentRequest mockReservationFileAttachmentRequest()
      throws IOException {
    byte[] validPdfBytes = createValidPdf();
    return ReservationFileAttachmentRequest.builder()
        .fileAttachment(Base64.getEncoder()
            .encodeToString(validPdfBytes))
        .description("Test attachment")
        .fileName("REG_RES1234567_ID232323_P76767676.pdf")
        .global(false)
        .reservationId("1613333")
        .overwriteExistingFile(true)
        .hotelId("STUAIR")
        .build();
  }

  private static byte[] createValidPdf() throws IOException {
    try (PDDocument document = new PDDocument()) {
      document.addPage(new PDPage());
      byte[] pdfBytes;
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        document.save(outputStream);
        pdfBytes = outputStream.toByteArray();
      }
      return pdfBytes;
    }
  }

  public static FileToUpload mockFileAttachmentRequest() {
    FileToUpload fileToUpload = new FileToUpload();

    fileToUpload.setFileAttachment("sample file content".getBytes());
    fileToUpload.setDescription("Test attachment");
    fileToUpload.setFileName("REG_RES1234567_ID232323_P76767676.pdf");
    fileToUpload.setGlobalYN("N");
    fileToUpload.setLinkId("1613333");
    fileToUpload.setOverwriteExistingFileYN("Y");
    fileToUpload.setUserName("HOTEL_USER");
    fileToUpload.setLinkType("Reservation");
    fileToUpload.setHotelId("STUAIR");

    return fileToUpload;
  }

  public static PreCheckInRequest mockPreCheckInRequest() {
    return PreCheckInRequest.builder()
        .arrivalTime(new Date())
        .hotelId("STUAIR")
        .reservationId("123456")
        .language("EN")
        .build();
  }

  public static PreCheckInReservation mockReservationPreCheckInInfo() {
    uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationId reservationId = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationId();
    reservationId.setType("Reservation");

    ReservationArrivalInfoType reservationArrivalInfoType = new ReservationArrivalInfoType();
    reservationArrivalInfoType.setArrivalTime(new Date());

    PreCheckInDetailsType preCheckInDetailsType = new PreCheckInDetailsType();
    preCheckInDetailsType.setArrival(reservationArrivalInfoType);

    ReservationPreCheckInDetailsType reservationPreCheckInDetailsType = new ReservationPreCheckInDetailsType();
    reservationPreCheckInDetailsType.setReservationId(reservationId);
    reservationPreCheckInDetailsType.setHotelId("STUAIR");
    reservationPreCheckInDetailsType.setPreCheckInDetails(preCheckInDetailsType);

    PreCheckInReservation preCheckInReservation = new PreCheckInReservation();
    preCheckInReservation.setReservation(reservationPreCheckInDetailsType);
    return preCheckInReservation;
  }
}
