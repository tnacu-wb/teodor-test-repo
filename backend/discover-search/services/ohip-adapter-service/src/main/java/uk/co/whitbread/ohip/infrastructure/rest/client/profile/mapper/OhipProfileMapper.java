package uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfileWrapper;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyWrapper;

@Mapper(componentModel = "spring", uses = {
    OhipProfileTransformer.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OhipProfileMapper {

  @Mapping(target = "totalResults", source = "profileSummaries.totalResults")
  @Mapping(target = "hasMore", source = "profileSummaries.hasMore")
  @Mapping(target = "limit", source = "profileSummaries.limit")
  @Mapping(target = "offset", source = "profileSummaries.offset")
  @Mapping(target = "companies", source = "profileSummaries.profileInfo", qualifiedByName = "extractCompaniesFrom")
  CompaniesProfile toCompaniesProfileModel(ProfileSummaries profileSummaries);

  @Mapping(target = "companyProfile", source = "company", qualifiedByName = "extractCompanyFrom")
  CompanyProfileWrapper toCompanyProfileModel(CompanyWrapper companyWrapper);

}
