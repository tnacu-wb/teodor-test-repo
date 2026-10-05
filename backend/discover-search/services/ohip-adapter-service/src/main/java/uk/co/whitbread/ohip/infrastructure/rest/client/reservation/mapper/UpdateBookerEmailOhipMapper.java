package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.EmailTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class UpdateBookerEmailOhipMapper {

  @Mapping(target = "profileId", expression = "java(injectProfileId(profile))")
  @Mapping(target = "email", expression = "java(injectEmail(profile))")
  public abstract ProfileType toModel(Profile profile);

  @Mapping(target = "profileIdList", expression = "java(injectProfileIdList(profileType))")
  @Mapping(target = "profileDetails",
      expression = "java(injectProfileDetails(profileType, updateBookerEmailRequest))")
  public abstract Profile toDto(ProfileType profileType, UpdateBookerEmailRequest updateBookerEmailRequest);

  protected uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType injectProfileDetails(
      ProfileType profileType,
      UpdateBookerEmailRequest updateBookerEmailRequest) {

    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileDetails.setEmails(injectEmails(profileType, updateBookerEmailRequest));

    return profileDetails;
  }

  protected List<UniqueIDType> injectProfileIdList(ProfileType profileType) {
    if (profileType.getProfileId() == null) {
      return new ArrayList<>();
    }
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId(profileType.getProfileId().getId());
    uniqueIdType.setType(profileType.getProfileId().getType());
    return List.of(uniqueIdType);
  }

  protected CompanyProfileTypeEmails injectEmails(ProfileType profileType,
                                                  UpdateBookerEmailRequest updateBookerEmailRequest) {
    var email = updateBookerEmailRequest.getEmailAddress();
    if (email == null) {
      return null;
    }
    var emailType = new EmailType();
    emailType.setEmailAddress(email);

    EmailInfoType emailInfoType = new EmailInfoType();
    if (profileType.getEmail() != null) {
      emailInfoType.setId(profileType.getEmail().getId());
      emailInfoType.setType(profileType.getEmail().getType());
    }
    emailInfoType.setEmail(emailType);

    CompanyProfileTypeEmails companyProfileTypeEmails = new CompanyProfileTypeEmails();
    companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
    return companyProfileTypeEmails;
  }

  protected ProfileIdResponse injectProfileId(Profile profile) {
    return profile.getProfileIdList()
        .stream()
        .findFirst()
        .map(uniqueIDType -> ProfileIdResponse
            .builder()
            .id(uniqueIDType.getId())
            .type(uniqueIDType.getType())
            .build()
        ).orElse(null);
  }

  protected EmailTypeResponse injectEmail(Profile profile) {
    return profile.getProfileDetails()
        .getEmails()
        .getEmailInfo() ==  null ? null : profile.getProfileDetails()
        .getEmails()
        .getEmailInfo()
        .stream()
        .findFirst()
        .map(emailInfoType -> EmailTypeResponse
            .builder()
            .id(emailInfoType.getId())
            .type(emailInfoType.getType() == null ? emailInfoType.getEmail().getType()
                : emailInfoType.getType())
            .build()
        ).orElse(null);
  }
}
