package uk.co.whitbread.company.domain.logic;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.domain.ports.primary.CompaniesProfilePort;
import uk.co.whitbread.company.domain.ports.secondary.ProfileOutPort;

@AllArgsConstructor
public class CompaniesProfilePortBusinessCase implements CompaniesProfilePort {

  private static final int RESULTS_SEARCH_CAP = 50;
  private final ProfileOutPort profileOutPort;

  public CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest) {
    var extendedProfileRequest = companiesProfileRequest.toBuilder()
        //business decision was to cap this at max 50 records
        .limit(Math.min(companiesProfileRequest.getLimit(), RESULTS_SEARCH_CAP))
        .build();
    return profileOutPort.getCompaniesProfile(extendedProfileRequest);
  }

  @Override
  public CompanyProfile getCompanyProfile(final String corporateId,
      final boolean isExcludeNegotiated) {
    return profileOutPort.getCompanyProfileByCorporateId(corporateId, isExcludeNegotiated);
  }

  @Override
  public CompanyProfile getCompanyWithNegotiatedRatesById(String id) {
    var companyProfile = getCompanyById(id, false);

    return isCompanyWithNegotiatedRates(companyProfile) ? companyProfile : null;
  }

  @Override
  public CompanyProfile getCompanyByOperaId(String companyId) {
    return profileOutPort.getCompanyProfileByCompanyId(companyId, true);
  }

  private CompanyProfile getCompanyById(String id, boolean isNegotiatedRatesExcluded) {
    //id can be represent either companyId or corporateId, we try to fetch data for each case
    var companyProfileByCorporateId = profileOutPort.getCompanyProfileByCorporateId(id,
        isNegotiatedRatesExcluded);

    if (isCompanyProfilePopulated(companyProfileByCorporateId)) {
      return companyProfileByCorporateId;
    }

    return profileOutPort.getCompanyProfileByCompanyId(id, isNegotiatedRatesExcluded);
  }

  private boolean isCompanyWithNegotiatedRates(CompanyProfile companyProfile) {
    return isCompanyProfilePopulated(companyProfile) && companyProfile.isNegotiatedRateEnabled();
  }

  private boolean isCompanyProfilePopulated(final CompanyProfile companyProfile) {
    return companyProfile != null && StringUtils.isNotBlank(companyProfile.getCompanyId());
  }


}
