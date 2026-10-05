package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.events.Consent;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EnquiryResponseDto;

@Mapper(componentModel = "spring")
public interface EnquiryResponseMapper {

  default EnquiryResponseDto toDto(EnquiryResponse enquiryResponse) {
    if (enquiryResponse == null) {
      return null;
    }

    return EnquiryResponseDto.builder()
        .adults(enquiryResponse.getAdults())
        .bookingReference(enquiryResponse.getBookingReference())
        .cancelLink(enquiryResponse.getCancelLink())
        .children(enquiryResponse.getChildren())
        .consent(toConsentDto(enquiryResponse.getConsent()))
        .date(enquiryResponse.getDate())
        .editLink(enquiryResponse.getEditLink())
        .emailAddress(enquiryResponse.getEmailAddress())
        .firstname(enquiryResponse.getFirstname())
        .id(enquiryResponse.getId())
        .lastname(enquiryResponse.getLastname())
        .occasionId(enquiryResponse.getOccasionId())
        .occasionName(enquiryResponse.getOccasionName())
        .paymentLink(enquiryResponse.getPaymentLink())
        .siteId(enquiryResponse.getSiteId())
        .siteName(enquiryResponse.getSiteName())
        .telephoneNumber(enquiryResponse.getTelephoneNumber())
        .time(enquiryResponse.getTime())
        .turnTimeMinutes(enquiryResponse.getTurnTimeMinutes())
        .build();
  }

  default ConsentDto toConsentDto(Consent consent) {
    if (consent == null) {
      return null;
    }

    return ConsentDto.builder()
        .email(consent.isEmail())
        .phone(consent.isPhone())
        .sms(consent.isSms())
        .postal(consent.isPostal())
        .pushNotification(consent.isPushNotification())
        .profiling(consent.isProfiling())
        .privacyStatement(consent.isPrivacyStatement())
        .consentStatement(consent.isConsentStatement())
        .termsAndConditions(consent.isTermsAndConditions())
        .build();
  }

}

