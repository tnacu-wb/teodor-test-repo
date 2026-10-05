package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;


import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;

@Mapper(componentModel = "spring",
    uses = {
        BusinessItemsOhipTransformer.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class BusinessItemsOhipMapper {

  @Mapping(source = "businessItems.customReferenceNumber", target = "customReference")
  @Mapping(ignore = true, target = "roomStay")
  @Mapping(source = "businessItems", target = "userDefinedFields",
      qualifiedByName = "injectUserDefinedFields")
  @Mapping(expression =
      "java(businessItemsOhipTransformer.injectRoutingInstructions(businessItems, "
          + "hotelReservationType, isDistribution, allowanceRules, companyId, folioWindowNo))",
      target = "routingInstructions")
  @Mapping(expression =
      "java(businessItemsOhipTransformer.injectComments(businessItems, hotelReservationType))",
      target = "comments")
  abstract HotelReservationInstructionType fromDto(BusinessItems businessItems,
      HotelReservationType hotelReservationType, boolean isDistribution,
      List<BusinessAllowanceRuleDto> allowanceRules, String companyId, int folioWindowNo);

  @Mapping(expression = "java(businessItemsOhipTransformer.buildRoutingInstructions(hotelReservationType, "
      + "allowanceRules, folioWindow))",
      target = "routingInstructions")
  abstract HotelReservationInstructionType fromDto(HotelReservationType hotelReservationType,
      List<BusinessAllowanceRuleDto> allowanceRules,
      int folioWindow);

  @Mapping(expression = "java(businessItemsOhipTransformer.injectRoutingInstructions(hotelReservationType, companyId, "
       + "folioWindow))",
      target = "routingInstructions")
  abstract HotelReservationInstructionType fromDto(HotelReservationType hotelReservationType, String companyId, 
      int folioWindow);

}
