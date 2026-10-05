package uk.co.whitbread.hotel.card.mapper.worldline;

import jakarta.xml.bind.JAXBElement;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uk.co.whitbread.hotel.card.model.AddressCorrespondenceEnum;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardAddRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceResponse;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardInviteRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardUpdateRequest;
import uk.co.whitbread.hotel.card.model.WorldlineCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineRegisteredUser;
import uk.co.whitbread.piba.api.exception.PibaException;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddResponse.Response;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardBaseDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserType;
import worldline.mst.bsm.api.b2b.pi.data.EDespatchChoiceType;

@Mapper(componentModel = "spring")
public interface WorldlineMapper {

  String WL_NAMESPACE_URI = "worldline.mst.bsm.api.b2b.pi.data.v1.1";

  List<WorldlineRegisteredUser> toWorldlineRegisteredUser(final List<CustomerAccountRegisteredUserType> accountRegisteredUsers);

  @Mapping(target = "apiUserGuid", source = "APIUserGuid")
  WorldlineRegisteredUser map(CustomerAccountRegisteredUserType registeredUserType);

  @Mapping(target = "cardHolderName", source = "card.displayName")
  @Mapping(target = "cardLimit", expression = "java(mapCardLimit(customerAccountCardViewResponseType))")
  @Mapping(target = "cardRestriction.startDate", source = "card.cardUsageRestrictions.restrictionStart")
  @Mapping(target = "cardRestriction.endDate", source = "card.cardUsageRestrictions.restrictionEnd")
  @Mapping(target = "cardRestriction.restrictCardUsage", source = "card.cardUsageRestrictions.restrictCardUsage")
  @Mapping(target = "registeredUsers", source = "card.info.registeredUsers")
  @Mapping(target = "cardId", source = "card.info.cardId")
  @Mapping(target = "cardNumber", source = "card.info.PAN")
  @Mapping(target = "isMyCard", source = "card.info.isMyCard")
  @Mapping(target = "isActivated", source = "card.info.isActivated")
  @Mapping(target = "status", source = "card.info.status")
  @Mapping(target = "primaryUserId", source = "card.info.primarySchemeCustomerId")
  @Mapping(target = "userId", source = "card.info.schemeCustomerId")
  @Mapping(target = "expiryDate", source = "card.info.expiryDate")
  @Mapping(target = "email", source = "card.info.regInfoEmail")
  @Mapping(target = "lastName", source = "card.info.regInfoSurname")
  @Mapping(target = "firstName", source = "card.info.regInfoForename")
  @Mapping(target = "title", source = "card.info.regInfoTitle")
  @Mapping(target = "cardAction", source = "card.info.contextCan")
  @Mapping(target = "amountSpend.currencyCode", source = "card.info.cardCurrentSpend.currencyCode")
  @Mapping(target = "amountSpend.amount", source = "card.info.cardCurrentSpend.amount")
  WorldlineCardDetails toWorldlineCard(final CustomerAccountCardViewResponseType customerAccountCardViewResponseType);

  @Mapping(target = "cardLimit", expression = "java(mapCardLimit(worldlineAccountCardAddRequest.getCardLimit()))")
  @Mapping(target = "cardUsageRestrictions.restrictCardUsage", source = "restrictCardUsage")
  @Mapping(target = "cardUsageRestrictions.restrictionStart", expression = "java(mapDate(worldlineAccountCardAddRequest.getRestrictionStart(), \"RestrictionStart\"))")
  @Mapping(target = "cardUsageRestrictions.restrictionEnd", expression = "java(mapDate(worldlineAccountCardAddRequest.getRestrictionEnd(), \"RestrictionEnd\"))")
  @Mapping(target = "despatchChoice.despatchChoice", source = "cardDeliveryAddressType")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.title", source = "cardCorrespondenceAddress.title")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.forename", source = "cardCorrespondenceAddress.forename")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.surname", source = "cardCorrespondenceAddress.surname")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.associateAddressWithFutureCardholder", source = "cardCorrespondenceAddress.associateAddressWithFutureCardholder")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line1", source = "cardCorrespondenceAddress.line1")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line2", source = "cardCorrespondenceAddress.line2")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line3", source = "cardCorrespondenceAddress.line3")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line4", source = "cardCorrespondenceAddress.line4")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.ZIPCode", source = "cardCorrespondenceAddress.postCode")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.ISO3166CountryCode", source = "cardCorrespondenceAddress.countryCodeISO")
  @Mapping(target = "APIUserGuid", qualifiedByName = "apiUserGuid", source = "apiUserGuid")
  CustomerAccountCardAddType toCustomerAccountCardAddType(WorldlineAccountCardAddRequest worldlineAccountCardAddRequest);

  @Mapping(target = "displayName", source = "displayName")
  @Mapping(target = "cardLimit", expression = "java(mapCardLimit(worldlineAccountCardUpdateRequest.getCardLimit()))")
  @Mapping(target = "cardUsageRestrictions.restrictCardUsage", source = "restrictCardUsage", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "cardUsageRestrictions.restrictionStart", expression = "java(mapDate(worldlineAccountCardUpdateRequest.getRestrictionStart(), \"RestrictionStart\"))")
  @Mapping(target = "cardUsageRestrictions.restrictionEnd", expression = "java(mapDate(worldlineAccountCardUpdateRequest.getRestrictionEnd(), \"RestrictionEnd\"))")
  @Mapping(target = "APIUserGuid", qualifiedByName = "apiUserGuid", source = "apiUserGuid", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  CustomerAccountCardUpdateType toCustomerAccountCardUpdateType(WorldlineAccountCardUpdateRequest worldlineAccountCardUpdateRequest);

  @Mapping(target = "cardHolderName", source = "card.displayName")
  @Mapping(target = "cardLimit", expression = "java(mapCardLimit(customerAccountCardUpdateResponseType))")
  @Mapping(target = "cardRestriction.startDate", source = "card.cardUsageRestrictions.restrictionStart")
  @Mapping(target = "cardRestriction.endDate", source = "card.cardUsageRestrictions.restrictionEnd")
  @Mapping(target = "cardRestriction.restrictCardUsage", source = "card.cardUsageRestrictions.restrictCardUsage")
  @Mapping(target = "registeredUsers", source = "card.info.registeredUsers")
  @Mapping(target = "cardId", source = "card.info.cardId")
  @Mapping(target = "cardNumber", source = "card.info.PAN")
  @Mapping(target = "isMyCard", source = "card.info.isMyCard")
  @Mapping(target = "isActivated", source = "card.info.isActivated")
  @Mapping(target = "status", source = "card.info.status")
  @Mapping(target = "primaryUserId", source = "card.info.primarySchemeCustomerId")
  @Mapping(target = "userId", source = "card.info.schemeCustomerId")
  @Mapping(target = "expiryDate", source = "card.info.expiryDate")
  @Mapping(target = "email", source = "card.info.regInfoEmail")
  @Mapping(target = "lastName", source = "card.info.regInfoSurname")
  @Mapping(target = "firstName", source = "card.info.regInfoForename")
  @Mapping(target = "title", source = "card.info.regInfoTitle")
  @Mapping(target = "cardAction", source = "card.info.contextCan")
  @Mapping(target = "amountSpend.currencyCode", source = "card.info.cardCurrentSpend.currencyCode")
  @Mapping(target = "amountSpend.amount", source = "card.info.cardCurrentSpend.amount")
  WorldlineCardDetails toWorldlineCard(final CustomerAccountCardUpdateResponseType customerAccountCardUpdateResponseType);

  @Mapping(target = "cardHolderName", source = "card.displayName")
  @Mapping(target = "cardLimit", expression = "java(mapCardLimit(response))")
  @Mapping(target = "cardRestriction.startDate", source = "card.cardUsageRestrictions.restrictionStart")
  @Mapping(target = "cardRestriction.endDate", source = "card.cardUsageRestrictions.restrictionEnd")
  @Mapping(target = "cardRestriction.restrictCardUsage", source = "card.cardUsageRestrictions.restrictCardUsage")
  @Mapping(target = "registeredUsers", source = "card.info.registeredUsers")
  @Mapping(target = "cardId", source = "card.info.cardId")
  @Mapping(target = "cardNumber", source = "card.info.PAN")
  @Mapping(target = "isMyCard", source = "card.info.isMyCard")
  @Mapping(target = "isActivated", source = "card.info.isActivated")
  @Mapping(target = "status", source = "card.info.status")
  @Mapping(target = "primaryUserId", source = "card.info.primarySchemeCustomerId")
  @Mapping(target = "userId", source = "card.info.schemeCustomerId")
  @Mapping(target = "expiryDate", source = "card.info.expiryDate")
  @Mapping(target = "email", source = "card.info.regInfoEmail")
  @Mapping(target = "lastName", source = "card.info.regInfoSurname")
  @Mapping(target = "firstName", source = "card.info.regInfoForename")
  @Mapping(target = "title", source = "card.info.regInfoTitle")
  @Mapping(target = "cardAction", source = "card.info.contextCan")
  @Mapping(target = "amountSpend.currencyCode", source = "card.info.cardCurrentSpend.currencyCode")
  @Mapping(target = "amountSpend.amount", source = "card.info.cardCurrentSpend.amount")
  WorldlineCardDetails toWorldlineCard(final Response response);

  @Mapping(target = "newCardDetails.cardRestriction.restrictCardUsage", source = "newCardDetails.cardUsageRestrictions.restrictCardUsage")
  @Mapping(target = "newCardDetails.cardRestriction.startDate", source = "newCardDetails.cardUsageRestrictions.restrictionStart")
  @Mapping(target = "newCardDetails.cardRestriction.endDate", source = "newCardDetails.cardUsageRestrictions.restrictionEnd")
  @Mapping(target = "cancelledCardDetails.cardRestriction.restrictCardUsage", source = "cancelledCardDetails.cardUsageRestrictions.restrictCardUsage")
  @Mapping(target = "cancelledCardDetails.cardRestriction.startDate", source = "cancelledCardDetails.cardUsageRestrictions.restrictionStart")
  @Mapping(target = "cancelledCardDetails.cardRestriction.endDate", source = "cancelledCardDetails.cardUsageRestrictions.restrictionEnd")
  @Mapping(target = "newCardDetails.cardId", source = "newCardDetails.info.cardId")
  @Mapping(target = "cancelledCardDetails.cardId", source = "cancelledCardDetails.info.cardId")
  @Mapping(target = "newCardDetails.pan", source = "newCardDetails.info.PAN")
  @Mapping(target = "cancelledCardDetails.pan", source = "cancelledCardDetails.info.PAN")
  @Mapping(target = "newCardDetails.primarySchemeCustomerId", source = "newCardDetails.info.primarySchemeCustomerId")
  @Mapping(target = "cancelledCardDetails.primarySchemeCustomerId", source = "cancelledCardDetails.info.primarySchemeCustomerId")
  @Mapping(target = "newCardDetails.isMyCard", source = "newCardDetails.info.isMyCard")
  @Mapping(target = "cancelledCardDetails.isMyCard", source = "cancelledCardDetails.info.isMyCard")
  @Mapping(target = "newCardDetails.status", source = "newCardDetails.info.status")
  @Mapping(target = "cancelledCardDetails.status", source = "cancelledCardDetails.info.status")
  @Mapping(target = "newCardDetails.isActivated", source = "newCardDetails.info.isActivated")
  @Mapping(target = "cancelledCardDetails.isActivated", source = "cancelledCardDetails.info.isActivated")
  @Mapping(target = "newCardDetails.expiryDate", source = "newCardDetails.info.expiryDate")
  @Mapping(target = "cancelledCardDetails.expiryDate", source = "cancelledCardDetails.info.expiryDate")
  @Mapping(target = "newCardDetails.title", source = "newCardDetails.info.regInfoTitle")
  @Mapping(target = "cancelledCardDetails.title", source = "cancelledCardDetails.info.regInfoTitle")
  @Mapping(target = "newCardDetails.firstName", source = "newCardDetails.info.regInfoForename")
  @Mapping(target = "cancelledCardDetails.firstName", source = "cancelledCardDetails.info.regInfoForename")
  @Mapping(target = "newCardDetails.lastName", source = "newCardDetails.info.regInfoSurname")
  @Mapping(target = "cancelledCardDetails.lastName", source = "cancelledCardDetails.info.regInfoSurname")
  @Mapping(target = "newCardDetails.email", source = "newCardDetails.info.regInfoEmail")
  @Mapping(target = "cancelledCardDetails.email", source = "cancelledCardDetails.info.regInfoEmail")
  @Mapping(target = "newCardDetails.registeredUsers", source = "newCardDetails.info.registeredUsers")
  @Mapping(target = "cancelledCardDetails.registeredUsers", source = "cancelledCardDetails.info.registeredUsers")
  @Mapping(target = "newCardDetails.cardHolderName", source = "newCardDetails.displayName")
  @Mapping(target = "cancelledCardDetails.cardHolderName", source = "cancelledCardDetails.displayName")
  WorldlineAccountCardCancelAndReplaceResponse toWorldlineCardCancelAndReplace( final CustomerAccountCardCancelResponseType response);


  @Mapping(target = "despatchChoice.despatchChoice", source = "cardDeliveryAddressType")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.title", source = "cardCorrespondenceAddress.title")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.forename", source = "cardCorrespondenceAddress.forename")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.surname", source = "cardCorrespondenceAddress.surname")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.associateAddressWithFutureCardholder", source = "cardCorrespondenceAddress.associateAddressWithFutureCardholder")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line1", source = "cardCorrespondenceAddress.line1")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line2", source = "cardCorrespondenceAddress.line2")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line3", source = "cardCorrespondenceAddress.line3")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.line4", source = "cardCorrespondenceAddress.line4")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.ZIPCode", source = "cardCorrespondenceAddress.postCode")
  @Mapping(target = "despatchChoice.customerAccountCardAlternativeDespatchDetail.address.ISO3166CountryCode", source = "cardCorrespondenceAddress.countryCodeISO")
  CustomerAccountCardCancelRequestType toCustomerAccountCardDespatchChoiceType(
      final WorldlineAccountCardCancelAndReplaceRequest worldlineAccountCardCancelAndReplaceRequest);

  @Mapping(target = "regInfoTitle", source = "registrationInfoTitle")
  @Mapping(target = "regInfoForename", source = "registrationInfoForename")
  @Mapping(target = "regInfoSurname", source = "registrationInfoSurname")
  @Mapping(target = "regInfoEmailAddress", source = "registrationInfoEmailAddress")
  @Mapping(target = "sendMeACopyOfInvite", source = "sendMeCopyOfInvite")
  CustomerAccountCardInviteDetailsType toCustomerAccountCardInviteDetailsType(
      WorldlineAccountCardInviteRequest worldlineAccountCardInviteRequest);

  default Integer mapCardLimit(CustomerAccountCardViewResponseType response) {
    return Optional.ofNullable(response)
        .map(CustomerAccountCardViewResponseType::getCard)
        .map(CustomerAccountCardBaseDetailsType::getCardLimit)
        .map(JAXBElement::getValue).orElse(null);
  }

  default Integer mapCardLimit(CustomerAccountCardUpdateResponseType response) {
    return Optional.ofNullable(response)
        .map(CustomerAccountCardUpdateResponseType::getCard)
        .map(CustomerAccountCardBaseDetailsType::getCardLimit)
        .map(JAXBElement::getValue).orElse(null);
  }

  default Integer mapCardLimit(Response response) {
    return Optional.ofNullable(response)
        .map(Response::getCard)
        .map(CustomerAccountCardBaseDetailsType::getCardLimit)
        .map(JAXBElement::getValue).orElse(null);
  }

  default JAXBElement<Integer> mapCardLimit(Integer cardLimit) {
    if(cardLimit == null) {
      return null;
    }
    return new JAXBElement<>(new QName(WL_NAMESPACE_URI, "CardLimit"),
        Integer.class, null, cardLimit);
  }

  default JAXBElement<XMLGregorianCalendar> mapDate(String value, String localPart){
    if(value == null) {
      return null;
    }
    try {
      return new JAXBElement<>(new QName(WL_NAMESPACE_URI, localPart),
          XMLGregorianCalendar.class, null, DatatypeFactory.newInstance().newXMLGregorianCalendar(value));
    } catch (DatatypeConfigurationException e) {
      throw new PibaException("Exception caught when trying to obtain a new instance of DatatypeFactory: " + e.getMessage());
    }
  }

  default Date mapCalendar(JAXBElement<XMLGregorianCalendar> value) {
    if(value == null) {
      return null;
    }
    return value.getValue().toGregorianCalendar().getTime();
  }

  default EDespatchChoiceType mapEDespatchChoice(
      AddressCorrespondenceEnum addressCorrespondenceEnum) {
    if(addressCorrespondenceEnum == null) {
      return null;
    }
    if(addressCorrespondenceEnum.equals(AddressCorrespondenceEnum.COMPANY_REGISTERED_ADDRESS)) {
      return EDespatchChoiceType.REGISTERED_USER_ADDRESS;
    }
    if(addressCorrespondenceEnum.equals(AddressCorrespondenceEnum.COMPANY_CORRESPONDENCE_ADDRESS)) {
      return EDespatchChoiceType.ACCOUNT_CORRESPONDENCE_ADDRESS;
    }
    if(addressCorrespondenceEnum.equals(AddressCorrespondenceEnum.CARDHOLDER_ALTERNATIVE_ADDRESS)) {
      return EDespatchChoiceType.ALTERNATIVE_ADDRESS;
    }
    throw new PibaException("Unknown address correspondence type.");
  }

  @Named("apiUserGuid")
  default String mapApiUserGuid(String apiUserGuid) {
    return Optional.ofNullable(apiUserGuid).map(val -> String.format("{%s}", val))
        .orElse(null);
  }

}
