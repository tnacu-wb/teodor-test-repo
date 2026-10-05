package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPackageTransactionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPostingAttributesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResDepositPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPoliciesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.rules.agent.generated.models.TransactionCodeDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCityTaxInfo;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ReservationOhipProperties.class,
    DepositFoliosCriteriaTypeOhipMapperImpl.class,
    DepositFoliosRequestMapperImpl.class, PaymentMethodOhipMapperImpl.class})
class DepositFoliosCriteriaTypeOhipMapperTest {

  @Autowired
  DepositFoliosCriteriaTypeOhipMapper depositFoliosCriteriaTypeOhipMapper;

  @Autowired
  DepositFoliosRequestMapper depositFoliosRequestMapper;

  @Test
  void createRequestToDepositFolios__ShouldReturnOK() {
    //Arrange
    ConfirmReservationRequest confirmReservationRequest = createRequest();
    HotelReservationType reservationDetails = createReservationDetails();
    VatRuleResponseDto vatRuleDetails = createVatRules();
    List<ReservationCityTaxInfo> cityTaxInfoList = createCityTaxList();

    //Act
    var depositFoliosRequest =
        depositFoliosRequestMapper.toDepositFolioRequestModel(confirmReservationRequest);
    var depositFoliosCriteriaRequest =
        depositFoliosCriteriaTypeOhipMapper.toDepositFolioCriteriaModel(depositFoliosRequest,
            reservationDetails, vatRuleDetails, cityTaxInfoList, null);
    //Assert
    assertEquals("BERALX", depositFoliosCriteriaRequest.getHotelId());
    assertEquals(2, depositFoliosCriteriaRequest.getCharges().size());
    assertEquals(CardTypeType.AX.getValue(),
        depositFoliosCriteriaRequest.getPayments().get(0).getPaymentMethod().getPaymentCard().getCardType().getValue());
    assertEquals("DAX", depositFoliosCriteriaRequest.getPaymentMethod().getPaymentMethod());
    assertEquals(CardProcessingType.MANUAL.getValue(),
        depositFoliosCriteriaRequest.getPaymentMethod().getPaymentCard().getProcessing()
            .getValue());
    assertEquals("3492404973517136665",
        depositFoliosCriteriaRequest.getPayments().get(0).getPaymentMethod().getPaymentCard()
            .getCardNumber());
    assertEquals("DRV", depositFoliosCriteriaRequest.getGuaranteeCode());
    assertEquals(BigDecimal.valueOf(463), depositFoliosCriteriaRequest.getCashierId());

  }

  @Test
  void createRequestToDepositFolios_WhenPackageIsIncludedInRate_ShouldExcludePackageCharge() {
    //Arrange
    ConfirmReservationRequest confirmReservationRequest = createRequest();
    HotelReservationType reservationDetails = createReservationDetails();
    VatRuleResponseDto vatRuleDetails = createVatRules();
    List<ReservationCityTaxInfo> cityTaxInfoList = createCityTaxList();

    reservationDetails.getReservationPackages().get(0).getPackageHeaderType()
        .getPostingAttributes().setPrintSeparateLine(false);

    //Act
    var depositFoliosRequest =
        depositFoliosRequestMapper.toDepositFolioRequestModel(confirmReservationRequest);
    var depositFoliosCriteriaRequest =
        depositFoliosCriteriaTypeOhipMapper.toDepositFolioCriteriaModel(depositFoliosRequest,
            reservationDetails, vatRuleDetails, cityTaxInfoList, null);

    //Assert
    assertEquals(1, depositFoliosCriteriaRequest.getCharges().size());
    assertEquals("9012", depositFoliosCriteriaRequest.getCharges().get(0).getTransactionCode());
  }

  private ConfirmReservationRequest createRequest() {
    var paymentCard = PaymentCard.builder()
        .token("3492404973517136665")
        .cardType("AX")
        .expirationDate("2025-03-31")
        .cardNumberLast4Digits("1234")
        .cardHolderName("Test")
        .cardNumberLast4Digits("6665")
        .build();
    return ConfirmReservationRequest.builder().reservationId("767926").hotelId("BERALX")
        .paymentOption(PaymentOption.PAY_NOW)
        .paymentMethod("DAX")
        .paymentType("AX")
        .paymentCard(paymentCard)
        .build();
  }

  private HotelReservationType createReservationDetails() {
    HotelReservationType reservation = new HotelReservationType();
    ReservationPaymentMethodType paymentMethod = new ReservationPaymentMethodType();
    paymentMethod.setPaymentMethod("DAX");
    reservation.setReservationPaymentMethods(List.of(paymentMethod));

    ReservationPoliciesType policies = new ReservationPoliciesType();
    ResDepositPolicyType depositPolicy = new ResDepositPolicyType();
    UniqueIDType id = new UniqueIDType();
    id.setId("827432");
    depositPolicy.setPolicyId(id);
    policies.setDepositPolicies(List.of(depositPolicy));
    reservation.setReservationPolicies(policies);

    ReservationPackageType reservationPackage = new ReservationPackageType();
    PackageCodeHeaderType header = new PackageCodeHeaderType();
    ConfigPostingAttributesType postingAttributes = new ConfigPostingAttributesType();
    postingAttributes.setAddToRate(false);
    postingAttributes.setPrintSeparateLine(true);
    header.setPostingAttributes(postingAttributes);
    ConfigPackageTransactionType transactionDetails = new ConfigPackageTransactionType();
    transactionDetails.setCurrency("EUR");
    header.setTransactionDetails(transactionDetails);
    ReservationPackageScheduleType item = new ReservationPackageScheduleType();
    item.setConsumptionDate(LocalDate.parse("2023-02-27"));
    item.setComputedResvPrice(BigDecimal.valueOf(55.05));
    reservationPackage.setPackageHeaderType(header);
    reservationPackage.setPackageCode("CITYTAX");
    reservationPackage.setScheduleList(List.of(item));
    reservation.setReservationPackages(List.of(reservationPackage));

    RoomStayType roomStay = new RoomStayType();
    RoomRateType roomRate = new RoomRateType();
    RatesType rate = new RatesType();
    AmountType amountType = new AmountType();
    TotalType base = new TotalType();
    base.setAmountBeforeTax(BigDecimal.valueOf(1100.99));
    base.setCurrencyCode("EUR");
    amountType.setBase(base);
    amountType.setStart(LocalDate.parse("2023-02-27"));
    rate.setRate(List.of(amountType));
    roomRate.setRates(rate);
    roomStay.setRoomRates(List.of(roomRate));
    reservation.setRoomStay(roomStay);

    return reservation;
  }

  private VatRuleResponseDto createVatRules() {
    TransactionCodeDto tr1 = new TransactionCodeDto();
    tr1.setPkgCode("CITYTAX");
    tr1.setTranCode("9062");
    tr1.setVatBearing(true);
    TransactionCodeDto tr2 = new TransactionCodeDto();
    tr2.setPkgCode("CITYTAX");
    tr2.setTranCode("9060");
    tr2.setVatBearing(false);
    TransactionCodeDto tr3 = new TransactionCodeDto();
    tr3.setPkgCode("__ACCMOD__");
    tr3.setTranCode("9012");
    VatRuleResponseDto vatRules = new VatRuleResponseDto();
    vatRules.setTranCodes(List.of(tr1, tr2, tr3));
    return vatRules;
  }

  List<ReservationCityTaxInfo> createCityTaxList() {
    ReservationCityTaxInfo cityTaxInfo =
        ReservationCityTaxInfo.builder().referenceDate(LocalDate.parse("2023-02-27")).vatAmount(BigDecimal.valueOf(7))
            .build();
    return List.of(cityTaxInfo);
  }


}