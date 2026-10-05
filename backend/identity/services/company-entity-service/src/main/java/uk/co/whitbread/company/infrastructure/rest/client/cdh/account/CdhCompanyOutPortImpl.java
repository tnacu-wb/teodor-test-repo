package uk.co.whitbread.company.infrastructure.rest.client.cdh.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.domain.ports.secondary.CdhCompanyOutPort;
import uk.co.whitbread.company.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.exceptions.NegotiatedRateNotAvailableException;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhCompanyMapper;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.profile.OhipProfileClient;

@RequiredArgsConstructor
@Slf4j
public class CdhCompanyOutPortImpl implements CdhCompanyOutPort {
  private final CdhClient cdhClient;
  private final OhipProfileClient ohipProfileClient;
  private final CdhCompanyMapper cdhCompanyMapper;

  public CompaniesProfile getCompaniesFromCdh(CompaniesSearchRequest companiesSearchRequest) {
    var companiesProfileDto = cdhClient.getCompanies(companiesSearchRequest);
    var cdhCompanySearchResponse =  cdhCompanyMapper.toCompaniesResponseDto(companiesProfileDto);

    var validCdhCompanies = cdhCompanySearchResponse
        .getCompanies()
        .stream()
        .filter(companyProfile -> StringUtils.isNotEmpty(companyProfile.getCorpId()))
        .toList();

    //Making additional Opera calls for each search result until all required information needed
    //like negotiatedRateFlag, company id available in cdh response
    var companyDetailList = Flux
        .fromIterable(validCdhCompanies
            .stream()
            .map(CompanyProfile::getCorpId)
            .toList())
        .map(corporateId -> {
          try {
            return ohipProfileClient.getCompanyProfileByCorporateId(corporateId);
          } catch (CompanyNotFoundException companyNotFoundException) {
            log.error(companyNotFoundException.getLocalizedMessage(), companyNotFoundException);
            return null;
          }
        })
        .filter(companyProfileDto -> companyProfileDto != null)
        .map(cdhCompanyMapper::toCompanyProfileDto)
        .map(companyProfile -> {
          try {
            var negotiatedRatesResponseDto = ohipProfileClient
                .getNegotiatedRatesForCompanyProfile(companyProfile.getCompanyId());
            if (negotiatedRatesResponseDto != null) {
              companyProfile.setNegotiatedRateEnabled(CollectionUtils.isNotEmpty(
                  negotiatedRatesResponseDto.getNegotiatedRates()));
            }
          } catch (NegotiatedRateNotAvailableException negotiatedRateNotAvailableException) {
            log.error(negotiatedRateNotAvailableException.getLocalizedMessage(), negotiatedRateNotAvailableException);
          }
          return companyProfile;
        })
        .collectList()
        .block();

    if (companiesSearchRequest.isNegotiatedRateCompanies()) {
      companyDetailList = companyDetailList
          .stream()
          .filter(CompanyProfile::isNegotiatedRateEnabled)
          .toList();
    }

    return cdhCompanySearchResponse
        .toBuilder()
        .totalResults(companyDetailList.size())
        .offset(companiesSearchRequest.getPageNumber())
        .limit(companiesSearchRequest.getPageSize())
        .companies(companyDetailList)
        .build();
  }

}
