package uk.co.whitbread.company.infrastructure.rest.client.ohip.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.domain.ports.secondary.ProfileOutPort;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.mapper.ProfileOhipMapper;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;

@RequiredArgsConstructor
@Slf4j
public class ProfileOutPortImpl implements ProfileOutPort {

  private final OhipProfileClient ohipProfileClient;
  private final ProfileOhipMapper profileOhipMapper;

  @Override
  public CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest) {
    var companiesProfileDto = ohipProfileClient.getCompaniesProfile(
        companiesProfileRequest.getHotelId(),
        companiesProfileRequest.getArNumber(), companiesProfileRequest.getCompanyName(),
        companiesProfileRequest.getLimit());
    return profileOhipMapper.toCompaniesProfileRequestDto(
        companiesProfileDto);
  }

  @Override
  public CompanyProfile getCompanyProfileByCorporateId(final String corporateId,
      final boolean isNegotiatedRatesExcluded) {
    var companyProfileDto = ohipProfileClient.getCompanyProfileByCorporateId(corporateId);
    return getCompanyProfile(companyProfileDto, isNegotiatedRatesExcluded);
  }

  @Override
  public CompanyProfile getCompanyProfileByCompanyId(final String companyId,
      final boolean isNegotiatedRatesExcluded) {
    var companyProfileDto = ohipProfileClient.getCompanyProfileByCompanyId(companyId);
    return getCompanyProfile(companyProfileDto, isNegotiatedRatesExcluded);
  }

  private CompanyProfile getCompanyProfile(CompanyProfileDto companyProfileDto,
      boolean isNegotiatedRatesExcluded) {
    var companyResponseDto = profileOhipMapper.toCompanyProfileDto(companyProfileDto);
    var isEmptyCompanyId = Strings.EMPTY.equals(companyProfileDto.getCompanyId());
    var addNegotiatedRates = !(isNegotiatedRatesExcluded || isEmptyCompanyId);
    if (addNegotiatedRates) {
      var negotiatedRatesResponseDto = ohipProfileClient
          .getNegotiatedRatesForCompanyProfile(companyProfileDto.getCompanyId());
      if (negotiatedRatesResponseDto != null) {
        companyResponseDto.setNegotiatedRateEnabled(CollectionUtils.isNotEmpty(
            negotiatedRatesResponseDto.getNegotiatedRates()));
      }
    }
    return companyResponseDto;
  }

}
