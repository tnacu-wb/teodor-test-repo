package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.DepositFolioRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCityTaxInfo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
    DepositFoliosCriteriaTypeOhipMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class DepositFoliosRequestOhipMapper {

  private DepositFoliosCriteriaTypeOhipMapper depositFoliosCriteriaTypeOhipMapper;

  @Autowired
  public final void toDepositFoliosOhipPropertiesForModel(
      final DepositFoliosCriteriaTypeOhipMapper depositFoliosCriteriaTypeOhipMapper) {
    this.depositFoliosCriteriaTypeOhipMapper = depositFoliosCriteriaTypeOhipMapper;
  }

  @Mapping(expression =
      "java(mapDepositFolioCriteriaType(depositFolioRequest,reservationDetails,vatRuleDetails,"
              + "cityTaxInfoList, charges))", target = "criteria")
  public abstract DepositFolioCriteria toDepositFolioCriteriaModel(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails,
      VatRuleResponseDto vatRuleDetails, List<ReservationCityTaxInfo> cityTaxInfoList,
      List<DepositFolioCharge> charges);

  @Mapping(expression =
      "java(mapDepositFolioCriteriaType(depositFolio, hotelReservationType, resPaymentMethod))",
      target = "criteria")
  public abstract DepositFolioCriteria toDepositFolioCriteriaModel(
      DepositFolio depositFolio, HotelReservationType hotelReservationType,
      ReservationPaymentMethodType resPaymentMethod);

  @Mapping(expression =
      "java(mapDepositFolioCriteriaTypeForAmend("
          + "depositFolioRequest,reservationDetails,vatRuleDetails,cityTaxInfoList))",
      target = "criteria")
  public abstract DepositFolioCriteria toDepositFolioCriteriaAmendModel(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails,
      VatRuleResponseDto vatRuleDetails, List<ReservationCityTaxInfo> cityTaxInfoList);

  protected DepositFolioCriteriaType mapDepositFolioCriteriaType(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails,
      VatRuleResponseDto vatRuleDetails, List<ReservationCityTaxInfo> cityTaxInfoList,
      List<DepositFolioCharge> charges) {
    return depositFoliosCriteriaTypeOhipMapper.toDepositFolioCriteriaModel(depositFolioRequest,
        reservationDetails, vatRuleDetails, cityTaxInfoList, charges);
  }

  protected DepositFolioCriteriaType mapDepositFolioCriteriaType(
      DepositFolio depositFolio, HotelReservationType hotelReservationType,
      ReservationPaymentMethodType resPaymentMethod) {
    return depositFoliosCriteriaTypeOhipMapper
        .toDepositFolioCriteriaModel(depositFolio, hotelReservationType, resPaymentMethod);
  }

  protected DepositFolioCriteriaType mapDepositFolioCriteriaTypeForAmend(
      DepositFolioRequest depositFolioRequest, HotelReservationType reservationDetails,
      VatRuleResponseDto vatRuleDetails, List<ReservationCityTaxInfo> cityTaxInfoList) {
    return depositFoliosCriteriaTypeOhipMapper.toDepositFolioCriteriaAmendModel(depositFolioRequest,
        reservationDetails, vatRuleDetails, cityTaxInfoList);
  }

}
