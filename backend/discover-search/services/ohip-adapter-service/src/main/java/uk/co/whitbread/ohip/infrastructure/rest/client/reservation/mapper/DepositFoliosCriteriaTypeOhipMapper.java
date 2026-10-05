package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CashieringPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChargeCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPostingAttributesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CurrencyAmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PaymentCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResDepositPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationId;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.rules.agent.generated.models.TransactionCodeDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.DepositFolioRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCityTaxInfo;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {PaymentMethodOhipMapper.class})
public abstract class DepositFoliosCriteriaTypeOhipMapper {

  private ReservationOhipProperties reservationOhipProperties;
  private PaymentMethodOhipMapper paymentMethodOhipMapper;

  @Autowired
  public final void toDepositFolioCriteriaOhipPropertiesForModel(
      ReservationOhipProperties reservationOhipProperties,
      PaymentMethodOhipMapper paymentMethodOhipMapper) {
    this.reservationOhipProperties = reservationOhipProperties;
    this.paymentMethodOhipMapper = paymentMethodOhipMapper;
  }

  @Mapping(expression = "java(injectCashierId())", target = "cashierId")
  @Mapping(expression = "java(injectGuaranteeCode())", target = "guaranteeCode")
  @Mapping(expression = "java(injectPaymentMethod(depositFolioRequest, reservationDetails))", target = "paymentMethod")
  @Mapping(expression = "java(injectReservationDetails(depositFolioRequest))", target = "reservationId")
  @Mapping(expression = "java(injectDepositPolicyId(reservationDetails))", target = "depositPolicyId")
  @Mapping(expression = "java(injectPayments(depositFolioRequest, reservationDetails))", target = "payments")
  @Mapping(expression = "java(injectCharges(depositFolioRequest, reservationDetails, "
      + "vatRuleDetails, cityTaxInfoList, basketCharges))", target = "charges")
  @Mapping(source = "depositFolioRequest.hotelId", target = "hotelId")
  public abstract DepositFolioCriteriaType toDepositFolioCriteriaModel(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails,
      VatRuleResponseDto vatRuleDetails,
      List<ReservationCityTaxInfo> cityTaxInfoList, List<DepositFolioCharge> basketCharges);

  @Mapping(expression = "java(injectCashierId())", target = "cashierId")
  @Mapping(expression = "java(injectGuaranteeCode())", target = "guaranteeCode")
  @Mapping(target = "paymentMethod", ignore = true)
  @Mapping(expression = "java(injectReservationDetails(depositFolio))", target = "reservationId")

  @Mapping(expression = "java(injectDepositPolicyId(reservationDetails))", target = "depositPolicyId")
  @Mapping(expression = "java(injectPayments(depositFolio, resPaymentMethod))", target = "payments")
  @Mapping(expression = "java(injectCharges(depositFolio))", target = "charges")
  @Mapping(source = "reservationDetails.hotelId", target = "hotelId")
  public abstract DepositFolioCriteriaType toDepositFolioCriteriaModel(
      DepositFolio depositFolio, HotelReservationType reservationDetails,
      ReservationPaymentMethodType resPaymentMethod);

  @Mapping(expression = "java(injectCashierId())", target = "cashierId")
  @Mapping(expression = "java(injectGuaranteeCode())", target = "guaranteeCode")
  @Mapping(target = "paymentMethod", ignore = true)
  @Mapping(expression = "java(injectReservationDetails(depositFolioRequest))", target = "reservationId")
  // needed
  @Mapping(expression = "java(injectDepositPolicyId(reservationDetails))", target = "depositPolicyId")
  @Mapping(expression = "java(injectPayments(depositFolioRequest, reservationDetails))", target = "payments")
  @Mapping(expression = "java(injectCharges(depositFolioRequest, reservationDetails, vatRuleDetails, "
      + "cityTaxInfoList, null))", target = "charges")
  @Mapping(source = "depositFolioRequest.hotelId", target = "hotelId")
  public abstract DepositFolioCriteriaType toDepositFolioCriteriaAmendModel(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails,
      VatRuleResponseDto vatRuleDetails,
      List<ReservationCityTaxInfo> cityTaxInfoList);

  public abstract List<ChargeCriteriaType> toChargeCriteriaListTypeModel(List<DepositFolioCharge> basketCharges);

  @Mapping(source = "reference", target = "postingReference")
  @Mapping(source = "quantity", target = "postingQuantity")
  @Mapping(expression = "java(injectChargePayments(basketCharges))", target = "price")
  public abstract ChargeCriteriaType toChargeCriteriaTypeModel(DepositFolioCharge basketCharges);

  protected CurrencyAmountType injectChargePayments(DepositFolioCharge basketCharges) {
    CurrencyAmountType targetAmountCharge = new CurrencyAmountType();
    return targetAmountCharge
        .currencyCode(basketCharges.getCurrencyAmount().getCurrencyCode())
        .amount(basketCharges.getCurrencyAmount().getAmount().negate());
  }

  protected ReservationPaymentMethodType injectPaymentMethod(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails) {
    final var reservationPaymentMethodType = new ReservationPaymentMethodType();
    if (!depositFolioRequest.isCancelRequest()) {
      reservationPaymentMethodType.setPaymentCard(
          getResPaymentCardType(depositFolioRequest.getPaymentCard()));
      reservationPaymentMethodType.setPaymentMethod(depositFolioRequest.getPaymentMethod());
    } else {
      reservationDetails.getReservationPaymentMethods().stream().filter(rsv -> rsv.getPaymentCard() != null)
          .findFirst().ifPresent(resDetailsPaymentMethod -> {
            if (resDetailsPaymentMethod.getPaymentMethod().length() == 2
                && StringUtils.isNotEmpty(depositFolioRequest.getPaymentMethod())) {
              reservationPaymentMethodType.setPaymentMethod(
                  depositFolioRequest.getPaymentMethod());
            } else {
              reservationPaymentMethodType.setPaymentMethod(
                  resDetailsPaymentMethod.getPaymentMethod());
            }
          });
    }

    reservationPaymentMethodType.setFolioView(reservationOhipProperties.getFolio());

    return reservationPaymentMethodType;

  }

  protected ReservationId injectReservationDetails(
      DepositFolio depositFolio) {
    final var reservationId = new ReservationId();

    reservationId.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationId.setId(depositFolio.getReservationId());

    return reservationId;
  }

  protected ReservationId injectReservationDetails(
      DepositFolioRequest depositFolioRequest) {
    final var reservationId = new ReservationId();

    reservationId.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationId.setId(depositFolioRequest.getReservationId());

    return reservationId;
  }

  protected BigDecimal injectCashierId() {
    return reservationOhipProperties.getDepositCashierId();
  }

  protected String injectGuaranteeCode() {
    return reservationOhipProperties.getDepositReceivedGuaranteeCode();
  }

  protected UniqueIDType injectDepositPolicyId(HotelReservationType reservationDetails) {
    var depositPolicy =
        reservationDetails.getReservationPolicies().getDepositPolicies().stream().findFirst()
            .orElse(null);
    return depositPolicy != null ? depositPolicy.getPolicyId() : null;

  }

  protected List<PaymentCriteriaType> injectPayments(DepositFolio depositFolio,
      ReservationPaymentMethodType resPaymentMethod) {
    final var paymentCriteriaType = new PaymentCriteriaType();
    final var paymentMethod = new CashieringPaymentMethodType();

    var totalAmount = new CurrencyAmountType();

    BigDecimal amount = depositFolio.getCharges().stream()
        .map(d -> d.getCurrencyAmount().getAmount())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    totalAmount.setAmount(amount);
    paymentCriteriaType.setPostingAmount(totalAmount);
    paymentMethod.setPaymentCard(resPaymentMethod.getPaymentCard());
    paymentMethod.getPaymentCard().setProcessing(CardProcessingType.MANUAL);

    paymentMethod.setPaymentMethod(resPaymentMethod.getPaymentMethod());

    paymentMethod.setFolioView(reservationOhipProperties.getFolio());
    paymentCriteriaType.setPaymentMethod(paymentMethod);
    paymentCriteriaType.setPostingReference(depositFolio.getPaymentId());

    paymentCriteriaType.setFolioWindowNo(reservationOhipProperties.getFolio());
    return List.of(paymentCriteriaType);
  }

  protected List<PaymentCriteriaType> injectPayments(
      DepositFolioRequest depositFolioRequest,
      HotelReservationType reservationDetails) {
    final var paymentCriteriaType = new PaymentCriteriaType();
    final var paymentMethod = new CashieringPaymentMethodType();

    if (!depositFolioRequest.isCancelRequest()) {
      var totalAmount = new CurrencyAmountType();
      totalAmount.setAmount(depositFolioRequest.getTotalCostOfStay());
      paymentCriteriaType.setPostingAmount(totalAmount);
      paymentMethod.setPaymentCard(getResPaymentCardType(depositFolioRequest.getPaymentCard()));
      paymentMethod.setPaymentMethod(depositFolioRequest.getPaymentMethod());
    } else {
      paymentCriteriaType.setPostingAmount(getTotalAmountNegative(reservationDetails));
      reservationDetails.getReservationPaymentMethods().stream().filter(rsv -> rsv.getPaymentCard() != null)
          .findFirst().ifPresent(resDetailsPaymentMethod -> {
            if (resDetailsPaymentMethod.getPaymentMethod().length() == 2
                && StringUtils.isNotEmpty(depositFolioRequest.getPaymentMethod())) {
              paymentMethod.setPaymentMethod(
                  depositFolioRequest.getPaymentMethod());
            } else {
              paymentMethod.setPaymentMethod(
                  resDetailsPaymentMethod.getPaymentMethod());
            }
          });
    }
    paymentMethod.setFolioView(reservationOhipProperties.getFolio());
    paymentCriteriaType.setPaymentMethod(paymentMethod);
    paymentCriteriaType.setPostingReference(depositFolioRequest.getPaymentId());

    paymentCriteriaType.setFolioWindowNo(reservationOhipProperties.getFolio());
    return List.of(paymentCriteriaType);
  }

  private ResPaymentCardType getResPaymentCardType(
      PaymentCard paymentCard) {
    final var resPaymentCard =
        paymentMethodOhipMapper.toModel(paymentCard);
    if (resPaymentCard != null) {
      resPaymentCard.setCardOrToken(CardNumberTypeType.TOKEN);
      resPaymentCard.setProcessing(CardProcessingType.MANUAL);
    }
    return resPaymentCard;
  }

  protected List<ChargeCriteriaType> injectCharges(DepositFolio depositFolio) {
    return depositFolio.getCharges().stream().map(c -> {
      ChargeCriteriaType chargeItem = new ChargeCriteriaType();
      chargeItem.setTransactionCode(c.getTransactionCode());
      chargeItem.setPostingQuantity(c.getQuantity());
      chargeItem.setPostingReference(c.getReference());
      var price = new CurrencyAmountType();
      price.setCurrencyCode(c.getCurrencyAmount().getCurrencyCode());
      price.setAmount(c.getCurrencyAmount().getAmount());
      chargeItem.setPrice(price);
      return chargeItem;
    }).toList();
  }

  protected List<ChargeCriteriaType> injectCharges(DepositFolioRequest depositFolioRequest,
      HotelReservationType reservationDetails, VatRuleResponseDto vatRuleDetails,
      List<ReservationCityTaxInfo> cityTaxInfoList, List<DepositFolioCharge> basketCharges) {
    if (Objects.nonNull(basketCharges)) {
      return toChargeCriteriaListTypeModel(basketCharges);
    }
    Map<String, Map<String, List<ChargeCriteriaType>>> chargeslistGrouped =
        (reservationDetails.getReservationPackages() != null)
            ? reservationDetails.getReservationPackages().stream()
            .filter(this::shouldIncludeDepositPackageCharge)
            .flatMap(
                reservationPackageType -> reservationPackageType.getScheduleList().stream().map(
                    reservationPackageScheduleType -> {

                      var chargeItem = new ChargeCriteriaType();
                      var price = new CurrencyAmountType();
                      TransactionCodeDto vatTransactionCode;

                      if ("CITYTAX".equals(reservationPackageType.getPackageCode())) {

                        ReservationCityTaxInfo cityTaxInfo = cityTaxInfoList.stream().filter(info ->
                            info.getReferenceDate()
                                .compareTo(reservationPackageScheduleType.getConsumptionDate())
                                == 0)
                            .findFirst().get();
                        vatTransactionCode = getTransactionCode(
                            reservationPackageType.getPackageCode(), vatRuleDetails, cityTaxInfo);

                        price.setAmount(
                            reservationPackageScheduleType.getComputedResvPrice());
                      } else {
                        vatTransactionCode = getTransactionCode(
                            reservationPackageType.getPackageCode(), vatRuleDetails);
                        price.setAmount(
                            reservationPackageScheduleType.getComputedResvPrice());
                      }

                      price.setCurrencyCode(
                          reservationPackageType.getPackageHeaderType().getTransactionDetails()
                              .getCurrency());

                      chargeItem.setTransactionCode(
                          vatTransactionCode != null ? vatTransactionCode.getTranCode() : null);
                      chargeItem.setPostingReference(
                          reservationPackageScheduleType.getConsumptionDate().toString());
                      chargeItem.setPrice(price);
                      return chargeItem;
                    })
                    .filter(chargeItem -> Objects.nonNull(chargeItem.getTransactionCode()))
            ).toList().stream().collect(
                Collectors.groupingBy(ChargeCriteriaType::getPostingReference,
                    Collectors.groupingBy(ChargeCriteriaType::getTransactionCode))) : null;

    var groupedCharges = new ArrayList<ChargeCriteriaType>();
    if (chargeslistGrouped != null) {
      chargeslistGrouped.forEach((postingRef, transCodeMap) -> {
        transCodeMap.forEach((transactionCode, values) -> {
          var charge = new ChargeCriteriaType();
          charge.setPostingReference(postingRef);
          charge.setTransactionCode(transactionCode);
          var price = new CurrencyAmountType();
          price.setCurrencyCode(values.get(0).getPrice().getCurrencyCode());
          price.setAmount(values.stream().map(item -> depositFolioRequest.isCancelRequest()
              ? item.getPrice().getAmount().negate() : item.getPrice().getAmount())
              .reduce(BigDecimal.ZERO, BigDecimal::add));
          charge.setPrice(price);
          charge.setPostingQuantity(1);
          if (price.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            groupedCharges.add(charge);
          }
        });
      });
    }
    var vatAccMod = vatRuleDetails.getTranCodes().stream()
        .filter(transactionCode -> "__ACCMOD__".equals(transactionCode.getPkgCode())).findAny()
        .orElse(null);
    reservationDetails.getRoomStay().getRoomRates().forEach(rateType -> {
      var charge = new ChargeCriteriaType();
      charge.setTransactionCode(vatAccMod != null ? vatAccMod.getTranCode() : null);
      charge.setPostingReference(rateType.getRates().getRate().get(0).getStart().toString());
      var price = new CurrencyAmountType();
      price.setCurrencyCode(rateType.getRates().getRate().get(0).getBase().getCurrencyCode());
      price.setAmount(depositFolioRequest.isCancelRequest()
          ? rateType.getRates().getRate().get(0).getBase().getAmountBeforeTax().negate()
          : rateType.getRates().getRate().get(0).getBase().getAmountBeforeTax());
      charge.setPrice(price);
      charge.setPostingQuantity(1);
      groupedCharges.add(charge);
    });

    return groupedCharges;
  }

  private boolean shouldIncludeDepositPackageCharge(ReservationPackageType reservationPackageType) {
    if (reservationPackageType.getScheduleList() == null || reservationPackageType.getScheduleList()
        .isEmpty()) {
      return false;
    }

    var hasNonZeroComputedPrice = reservationPackageType.getScheduleList().get(0)
        .getComputedResvPrice().compareTo(BigDecimal.ZERO) != 0;

    var postingAttributes = Optional.ofNullable(reservationPackageType.getPackageHeaderType())
        .map(PackageCodeHeaderType::getPostingAttributes);
    var addToRate = postingAttributes.map(ConfigPostingAttributesType::getAddToRate)
        .orElse(Boolean.FALSE);
    var printSeparateLine = postingAttributes.map(ConfigPostingAttributesType::getPrintSeparateLine)
        .orElse(Boolean.FALSE);

    return hasNonZeroComputedPrice && Boolean.FALSE.equals(addToRate)
        && Boolean.TRUE.equals(printSeparateLine);
  }

  private BigDecimal computeAmount(List<ResDepositPolicyType> depositPolicies,
      Function<ResDepositPolicyType, CurrencyAmountType> depositPoliciesMapper) {
    return depositPolicies
        .stream()
        .filter(Objects::nonNull)
        .map(depositPoliciesMapper)
        .filter(Objects::nonNull)
        .map(CurrencyAmountType::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }


  private TransactionCodeDto getTransactionCode(String packageCode,
      VatRuleResponseDto vatRuleDetails, ReservationCityTaxInfo cityTaxInfoList) {

    return vatRuleDetails.getTranCodes().stream()
        .filter(transactionCodeDto -> transactionCodeDto.getPkgCode()
            .equals(packageCode) && transactionCodeDto.getVatBearing() == cityTaxInfoList
            .getVatAmount().compareTo(BigDecimal.ZERO) > 0).findAny().orElse(null);
  }

  private TransactionCodeDto getTransactionCode(String packageCode,
      VatRuleResponseDto vatRuleDetails) {
    return vatRuleDetails.getTranCodes().stream().filter(
        transactionCodeDto -> transactionCodeDto.getPkgCode()
            .equals(packageCode)).findAny()
        .orElse(null);
  }

  protected CurrencyAmountType getTotalAmountNegative(HotelReservationType reservationDetails) {
    var totalAmount = new CurrencyAmountType();
    totalAmount.setAmount(computeAmount(reservationDetails.getReservationPolicies()
        .getDepositPolicies(), ResDepositPolicyType::getAmountPaid).negate());
    return totalAmount;
  }
}
