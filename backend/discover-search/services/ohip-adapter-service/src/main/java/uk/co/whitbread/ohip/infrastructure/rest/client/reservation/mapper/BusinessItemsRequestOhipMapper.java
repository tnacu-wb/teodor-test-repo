package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTRIBUTION_CHANNEL;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.PaymentUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.PromotionUtils;

@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {Arrays.class})
public abstract class BusinessItemsRequestOhipMapper {

  private BusinessItemsOhipMapper businessItemsOhipMapper;
  private BusinessItemsOhipTransformer businessItemsOhipTransformer;

  @Autowired
  public void toBusinessItemsOhipMapperForModel(
      final BusinessItemsOhipMapper businessItemsOhipMapper,
      final BusinessItemsOhipTransformer businessItemsOhipTransformer) {
    this.businessItemsOhipMapper = businessItemsOhipMapper;
    this.businessItemsOhipTransformer = businessItemsOhipTransformer;
  }

  @Mapping(expression =
      "java(injectHotelReservations(businessItemsRequest,hotelReservationType,allowanceRules))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(
      BusinessItemsRequest businessItemsRequest,
      HotelReservationType hotelReservationType,
      List<BusinessAllowanceRuleDto> allowanceRules);

  @Mapping(expression =
      "java(buildHotelReservations(hotelReservationType, allowanceRules, folioWindow,"
          + "reservationPaymentMethod, creditCardInfo, reservationAmounts))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(HotelReservationType hotelReservationType,
      String reservationPaymentMethod, List<BusinessAllowanceRuleDto> allowanceRules,
      CreditCardInfo creditCardInfo, ReservationAmounts reservationAmounts, int folioWindow);

  @Mapping(expression =
      "java(injectHotelReservations(hotelReservationType,companyId,folioWindow))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(HotelReservationType hotelReservationType, String companyId,
      int folioWindow);

  protected List<HotelReservationInstructionType> injectHotelReservations(
      BusinessItemsRequest businessItemsRequest,
      HotelReservationType hotelReservationType,
      List<BusinessAllowanceRuleDto> allowanceRules) {

    int folioWindowNo = PaymentUtils.resolveDistrFolioWindow(
        businessItemsRequest.getPibaCardPresent());

    List<HotelReservationInstructionType> instructions =
            List.of(businessItemsOhipMapper.fromDto(
                    businessItemsRequest.getBusinessItems(),
                    hotelReservationType,
                    DISTRIBUTION_CHANNEL.equals(businessItemsRequest.getChannel()),
                    allowanceRules,
                    businessItemsRequest.getCompanyId(),
                    folioWindowNo
            ));

    instructions.forEach(PromotionUtils::truncatePromotion);

    return instructions;

  }

  protected List<HotelReservationInstructionType> injectHotelReservations(HotelReservationType hotelReservationType,
      String companyId, int folioWindow) {
    List<HotelReservationInstructionType> instructions =
            List.of(businessItemsOhipMapper.fromDto(
                    hotelReservationType, companyId, folioWindow));

    instructions.forEach(PromotionUtils::truncatePromotion);

    return instructions;
  }

  protected List<HotelReservationInstructionType> buildHotelReservations(
      HotelReservationType hotelReservationType,
      List<BusinessAllowanceRuleDto> allowanceRules,
      int folioWindow, String reservationPaymentMethod,
      CreditCardInfo creditCardInfo, ReservationAmounts reservationAmounts) {
    var reservationPaymentMethodTypes = this.businessItemsOhipTransformer
        .buildPaymentMethodType(reservationPaymentMethod, creditCardInfo, reservationAmounts);
    hotelReservationType.setReservationPaymentMethods(reservationPaymentMethodTypes);

    List<HotelReservationInstructionType> instructions =
            List.of(businessItemsOhipMapper.fromDto(hotelReservationType, allowanceRules, folioWindow));

    instructions.forEach(PromotionUtils::truncatePromotion);

    return instructions;
  }
}
