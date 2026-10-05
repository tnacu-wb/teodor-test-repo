package uk.co.whitbread.basket.infrastructure.rest.client.cdh;

import uk.co.whitbread.basket.domain.model.payments.out.CdhSearchCompaniesResponse;
import uk.co.whitbread.basket.domain.model.payments.out.Company;
import uk.co.whitbread.basket.domain.model.payments.out.CompanyAddress;
import uk.co.whitbread.basket.generated.models.cdh.CompaniesDto;
import uk.co.whitbread.basket.generated.models.cdh.CompanyAddressDto;

import java.util.Collections;

public class CdhTestUtils {

    public static CdhSearchCompaniesResponse createCdhSearchCompaniesResponse() {
        return CdhSearchCompaniesResponse.builder()
                .continuationToken("mockToken123")
                .results(Collections.singletonList(
                        Company.builder()
                                .companyAddress(CompanyAddress.builder()
                                        .companyName("Company A_M")
                                        .alternateCompanyName("Comp A")
                                        .addressLine1("123 Main St")
                                        .addressLine2("Suite 100")
                                        .addressLine3(null)
                                        .addressLine4(null)
                                        .addressLine5(null)
                                        .countryCode("gb")
                                        .postCode("EC1N 2TD")
                                        .build())
                                .build()
                ))
                .searchResults(1)
                .totalResults(1)
                .build();
    }

    public static CdhSearchCompaniesResponse createCdhSearchCompaniesResponseInvalidAddressLine() {
        return CdhSearchCompaniesResponse.builder()
                .continuationToken("mockToken123")
                .results(Collections.singletonList(
                        Company.builder()
                                .companyAddress(CompanyAddress.builder()
                                        .companyName("Company A_M")
                                        .alternateCompanyName("Comp A")
                                        .addressLine1(null)
                                        .addressLine2("Suite 100")
                                        .addressLine3(null)
                                        .addressLine4(null)
                                        .addressLine5(null)
                                        .countryCode("gb")
                                        .postCode("EC1N 2TD")
                                        .build())
                                .build()
                ))
                .searchResults(1)
                .totalResults(1)
                .build();
    }

    public static CdhSearchCompaniesResponse createCdhSearchCompaniesResponseInvalidPostalCode() {
        return CdhSearchCompaniesResponse.builder()
                .continuationToken("mockToken123")
                .results(Collections.singletonList(
                        Company.builder()
                                .companyAddress(CompanyAddress.builder()
                                        .companyName("Company A_M")
                                        .alternateCompanyName("Comp A")
                                        .addressLine1("123 Main St")
                                        .addressLine2("Suite 100")
                                        .addressLine3(null)
                                        .addressLine4(null)
                                        .addressLine5(null)
                                        .countryCode("gb")
                                        .postCode(null)
                                        .build())
                                .build()
                ))
                .searchResults(1)
                .totalResults(1)
                .build();
    }

    public static CompaniesDto buildCompanyDto() {
        CompanyAddressDto companyAddress = new CompanyAddressDto();
        companyAddress.setAddressLine1("123 Main St");
        companyAddress.setCountryCode("gb");
        companyAddress.setPostCode("EC1N 2TD");

        CompaniesDto companyDto = new CompaniesDto();
        companyDto.setCompanyAddress(companyAddress);
        companyDto.setCompanyName("Company A_M");
        companyDto.setAlternateCompanyName("alternateCompanyName");

        return companyDto;
    }
}
