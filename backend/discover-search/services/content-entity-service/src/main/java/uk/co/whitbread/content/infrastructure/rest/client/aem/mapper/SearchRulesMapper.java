package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.AcceptedRoomTypes;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.AllowedRoomTypesByOccupancy;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.Offer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SearchRulesMapper {

  String EMPLOYEE_OFFER = "employee-offer";
  String CHANNEL_EMPLOYEE = "employee";
  String TRAVEL_INDUSTRY_RATE_OFFER = "travel-industry-rate";
  String CHANNEL_TRAVEL_INDUSTRY_RATE = "FCDNLR30";

  @Mapping(source = "globalConfigDto.bookingWidgetConfig.allowedRoomTypesByOccupancy", target = "roomOccupancies")
  @Mapping(source = "globalConfigDto.bookingWidgetConfig.numberOfNights", target = "maxNights")
  @Mapping(source = "globalConfigDto.bookingWidgetConfig.maxRooms", target = "maxRooms")
  @Mapping(source = "globalConfigDto.bookingWidgetConfig.maxRoomsAmend", target = "maxRoomsAmend")
  @Mapping(source = "globalConfigDto.bookingWidgetConfig.maxArrivalDate", target = "maxArrivalDate")
  SearchRules toDomainModel(GlobalConfigDto globalConfigDto, GlobalConfigRequest globalConfigRequest);


  @AfterMapping
  default void toEmployeeOfferFieldsModel(GlobalConfigDto globalConfigDto,
      GlobalConfigRequest globalConfigRequest,
      @MappingTarget SearchRules searchRules) {

    toEmployeeOfferFieldsModel(globalConfigDto.getOffers(), globalConfigRequest, searchRules);
  }

  default void toEmployeeOfferFieldsModel(List<Offer> offers,
      GlobalConfigRequest globalConfigRequest,
      SearchRules searchRules) {

    if (isEmployeeChannel(globalConfigRequest)) {
      offers.stream()
          .filter(this::isEmployeeOffer)
          .findFirst()
          .ifPresent(offer -> mapOfferToSearchRules(offer, searchRules));
    } else if (isTravelIndustryChannel(globalConfigRequest)) {
      offers.stream()
          .filter(this::isTravelIndustryOffer)
          .findFirst()
          .ifPresent(offer -> mapOfferToSearchRules(offer, searchRules));
    }
  }

  // Map the specific offer fields to SearchRules
  private void mapOfferToSearchRules(Offer offer, SearchRules searchRules) {
    setMaxRooms(offer, searchRules);
    setMaxNights(offer, searchRules);
    setMaxArrivalDate(offer, searchRules);
    setRoomOccupancies(offer, searchRules);
    setMaxRoomsAmend(offer, searchRules);
  }

  private void setMaxRooms(Offer offer, SearchRules searchRules) {
    if (offer.getMaxRooms() != null) {
      searchRules.setMaxRooms(offer.getMaxRooms());
    }
  }

  private void setMaxRoomsAmend(Offer offer, SearchRules searchRules) {
    if (offer.getMaxRoomsAmend() != null) {
      searchRules.setMaxRoomsAmend(offer.getMaxRoomsAmend());
    }
  }

  private void setMaxNights(Offer offer, SearchRules searchRules) {
    if (offer.getNumberOfNights() != null) {
      searchRules.setMaxNights(offer.getNumberOfNights());
    }
  }

  private void setMaxArrivalDate(Offer offer, SearchRules searchRules) {
    if (offer.getMaxArrivalDate() != null) {
      searchRules.setMaxArrivalDate(offer.getMaxArrivalDate());
    }
  }

  private void setRoomOccupancies(Offer offer, SearchRules searchRules) {
    if (CollectionUtils.isNotEmpty(offer.getAllowedRoomTypesByOccupancy())) {
      List<AcceptedRoomTypes> acceptedRoomTypesList = offer.getAllowedRoomTypesByOccupancy().stream()
          .map(this::toAcceptedRoomTypes)
          .toList();
      searchRules.setRoomOccupancies(acceptedRoomTypesList);
    }
  }

  private AcceptedRoomTypes toAcceptedRoomTypes(AllowedRoomTypesByOccupancy allowedRoomTypesByOccupancy) {
    AcceptedRoomTypes acceptedRoomTypes = new AcceptedRoomTypes();
    acceptedRoomTypes.setAcceptedRoomTypes(allowedRoomTypesByOccupancy.getAcceptedRoomTypes());
    acceptedRoomTypes.setAdultsNumber(allowedRoomTypesByOccupancy.getAdultsNumber());
    acceptedRoomTypes.setChildrenNumber(allowedRoomTypesByOccupancy.getChildrenNumber());
    return acceptedRoomTypes;
  }

  private boolean isEmployeeChannel(GlobalConfigRequest globalConfigRequest) {
    return CHANNEL_EMPLOYEE.equalsIgnoreCase(globalConfigRequest.getChannelId());
  }

  // check if employee website offer is available
  private boolean isEmployeeOffer(Offer offer) {
    return EMPLOYEE_OFFER.equalsIgnoreCase(offer.getPage());
  }

  private boolean isTravelIndustryChannel(GlobalConfigRequest globalConfigRequest) {
    return CHANNEL_TRAVEL_INDUSTRY_RATE.equalsIgnoreCase(globalConfigRequest.getChannelId());
  }

  private boolean isTravelIndustryOffer(Offer offer) {
    return TRAVEL_INDUSTRY_RATE_OFFER.equalsIgnoreCase(offer.getPage());
  }

}
