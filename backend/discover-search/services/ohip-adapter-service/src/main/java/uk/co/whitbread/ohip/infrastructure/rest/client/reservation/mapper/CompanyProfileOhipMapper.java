package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class CompanyProfileOhipMapper {

  @Mapping(target = "profileId", expression = "java(injectProfileId(profile))")
  public abstract ProfileType toModel(Profile profile);

  @Mapping(target = "profileIdList", expression = "java(injectProfileIdList(profileType))")
  @Mapping(target = "profileDetails",
      expression = "java(injectProfileDetails(bookerDetailsCnpRequest))")
  public abstract Profile toDto(ProfileType profileType, BookerDetailsCnpRequest bookerDetailsCnpRequest);

  protected uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType injectProfileDetails(
      BookerDetailsCnpRequest bookerDetailsCnpRequest) {

    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileDetails.setCompany(injectCompany(bookerDetailsCnpRequest));

    return profileDetails;
  }

  protected CompanyType injectCompany(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    final var companyType = new CompanyType();

    // company name
    companyType.setCompanyName(bookerDetailsCnpRequest.getBooker().getCompanyName());

    return companyType;
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

}
