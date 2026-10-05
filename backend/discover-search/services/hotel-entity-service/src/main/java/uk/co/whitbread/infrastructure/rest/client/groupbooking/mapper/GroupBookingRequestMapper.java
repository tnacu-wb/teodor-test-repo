package uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.in.GroupBookingRequestDynamicsDto;

@Mapper(componentModel = "spring")
public interface GroupBookingRequestMapper {

  String DEFAULT_CONTACT_RECORD = "/contacts(2fc6b1df-2814-ef11-9f89-000d3a4709b8)";
  String LANGUAGE_DE = "de";
  String SOURCE_DE = "Webform - DE";
  String SOURCE_EN = "Webform - GB";
  String TITLE_DE = "Webformular-Gruppenbuchungsanfrage";
  String TITLE_EN = "Webform Group Booking Enquiry";

  @Mapping(target = "ebecsWftitle", source = "title")
  @Mapping(target = "ebecsWffname", source = "firstName")
  @Mapping(target = "ebecsWflname", source = "lastName")
  @Mapping(target = "ebecsWfemail", source = "emailAddress")
  @Mapping(target = "ebecsWfphone", source = "phoneNumber")
  @Mapping(target = "ebecsWftypeofbooker", source = "bookerType")
  @Mapping(target = "ebecsWfpurposeofstay", source = "purposeOfStay")
  @Mapping(target = "ebecsWfcompanyname", source = "companyName")
  @Mapping(target = "ebecsWfreasonforvisit", source = "reasonForVisit")
  @Mapping(target = "ebecsWfreasonforvisitother", source = "reasonForVisitOther")
  @Mapping(target = "ebecsWfhotelname", source = "hotelName")
  @Mapping(target = "ebecsWfhotelcode", source = "hotelCode")
  @Mapping(target = "ebecsWfhotelbrand", source = "hotelBrand")
  @Mapping(target = "ebecsWfpackagetypemealdeal", source = "isPackageTypeMealDeal")
  @Mapping(target = "ebecsWfpackagetypebf", source = "isPackageTypeBf")
  @Mapping(target = "ebecsWfarrivaldate", source = "arrivalDate")
  @Mapping(target = "ebecsWfdeparturedate", source = "departureDate")
  @Mapping(target = "ebecsWfschoolyouthgroup", source = "isSchoolOrYouth")
  @Mapping(target = "ebecsWfsingleoccupancy", source = "singleOccupancy")
  @Mapping(target = "ebecsWfdoubleoccupancy", source = "doubleOccupancy")
  @Mapping(target = "ebecsWftwinrooms", source = "twinRooms")
  @Mapping(target = "ebecsWffamilyof2", source = "familyOf21A1C")
  @Mapping(target = "ebecsWffamilyof31a", source = "familyOf31A2C")
  @Mapping(target = "ebecsWffamilyof32a", source = "familyOf32A1C")
  @Mapping(target = "ebecsWffamilyof4", source = "familyOf42A2C")
  @Mapping(target = "ebecsWfaccessiblesingle", source = "accessibleSingle")
  @Mapping(target = "ebecsWfaccessibledouble", source = "accessibleDouble")
  @Mapping(target = "ebecsWfaccessibletwin", source = "accessibleTwin")
  @Mapping(target = "ebecsWfcomments", source = "additionalInformation")
  @Mapping(target = "title", expression = "java(getTitle(groupBookingRequest.getLanguage()))")
  @Mapping(target = "ebecsWfsource", expression = "java(getEbecsWfsource(groupBookingRequest.getLanguage()))")
  @Mapping(target = "customeridContactOdataBind", constant = DEFAULT_CONTACT_RECORD)
  GroupBookingRequestDynamicsDto toDynamicsDto(GroupBookingRequest groupBookingRequest);

  @Named("getTitle")
  default String getTitle(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return TITLE_DE;
    } else {
      return TITLE_EN;
    }
  }

  @Named("getEbecsWfsource")
  default String getEbecsWfsource(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return SOURCE_DE;
    } else {
      return SOURCE_EN;
    }
  }
}
