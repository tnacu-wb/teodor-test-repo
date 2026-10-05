package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.outlets.Consent;
import uk.co.whitbread.reservation.domain.model.out.outlets.ConsentStatement;
import uk.co.whitbread.reservation.domain.model.out.outlets.ConsentType;
import uk.co.whitbread.reservation.domain.model.out.outlets.Features;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Site;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.FeaturesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SiteDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.CompanyDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentStatementDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentTypeDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.OutletResponseDto;

@Mapper(componentModel = "spring")
public interface OutletResponseMapper {

  @Mapping(target = "companies", expression = "java(toOutletResponseDto(outletResponse))")
  OutletResponseDto toDto(OutletResponse outletResponse);

  default List<CompanyDto> toOutletResponseDto(OutletResponse outletResponse) {
    if (outletResponse.getCompanies() == null) {
      return new ArrayList<>();
    }

    return outletResponse.getCompanies().stream()
        .map(company -> CompanyDto.builder()
            .name(company.getName())
            .id(company.getId())
            .sites(toSitesToDto(company.getSites()))
            .consent(toConsentToDto(company.getConsent()))
            .termsAndConditions(company.getTermsAndConditions())
            .build())
        .toList();
  }

  default ConsentDto toConsentToDto(Consent consent) {
    if (consent == null) {
      return null;
    }
    return ConsentDto.builder()
        .email(toConsentTypeToDto(consent.getEmail()))
        .phone(toConsentTypeToDto(consent.getPhone()))
        .sms(toConsentTypeToDto(consent.getSms()))
        .postal(toConsentTypeToDto(consent.getPostal()))
        .pushNotification(toConsentTypeToDto(consent.getPushNotification()))
        .profiling(toConsentTypeToDto(consent.getProfiling()))
        .privacyStatement(toConsentStatementToDto(consent.getPrivacyStatement()))
        .consentStatement(toConsentStatementToDto(consent.getConsentStatement()))
        .build();
  }

  default ConsentTypeDto toConsentTypeToDto(ConsentType consentType) {
    if (consentType == null) {
      return null;
    }
    return ConsentTypeDto.builder()
        .text(consentType.getText())
        .isEnabled(consentType.isEnabled())
        .build();
  }

  default ConsentStatementDto toConsentStatementToDto(ConsentStatement consentStatement) {
    if (consentStatement == null) {
      return null;
    }
    return ConsentStatementDto.builder()
        .text(consentStatement.getText())
        .isEnabled(consentStatement.isEnabled())
        .url(consentStatement.getUrl())
        .build();
  }

  default List<SiteDto> toSitesToDto(List<Site> sites) {
    if (sites == null) {
      return new ArrayList<>();
    }

    return sites.stream()
        .map(site -> SiteDto.builder()
            .id(site.getId())
            .name(site.getName())
            .aztecSiteReference(site.getAztecSiteReference())
            .defaultOccasionId(site.getDefaultOccasionId())
            .timezone(site.getTimezone())
            .features(toFeatureToDto(site.getFeatures()))
            .build())
        .toList();
  }

  default FeaturesDto toFeatureToDto(Features features) {
    if (features == null) {
      return null;
    }
    return FeaturesDto.builder()
        .bookableAreas(features.isBookableAreas())
        .build();
  }

}
