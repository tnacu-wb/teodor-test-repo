package uk.co.whitbread.cdh.infrastructure.rest.controller.account;

import static uk.co.whitbread.cdh.infrastructure.util.Utils.sanitizeInputString;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.cdh.domain.ports.primary.CompanyInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.CompanyResponseMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.CompanySearchCriteriaRequestMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.CompanySearchResponseMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.AccessRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.CompanySearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanyDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySearchResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySuppressRatesDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/cdh")
public class CompanyController implements  CompanyControllerApiDocumentation {

  private final CompanySearchCriteriaRequestMapper companySearchCriteriaRequestMapper;
  private final CompanySearchResponseMapper companySearchResponseMapper;
  private final CompanyResponseMapper companyResponseMapper;
  private final CompanyInPort companyInPort;

  @GetMapping(value = "/account/company/{companyAccountId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanyDto getCompany(
      @PathVariable String companyAccountId,
      @ParameterObject @Valid AccessRequestDto accessRequestDto) {
    log.info("Request to get company with id: {}", sanitizeInputString(companyAccountId));
    final var company = companyInPort.getCompany(companyAccountId,
        accessRequestDto.getAccessContext(), accessRequestDto.getAccessedBy());
    return companyResponseMapper.toDto(company);
  }

  @GetMapping(value = "/account/company/{companyAccountId}/suppress-rates", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanySuppressRatesDto getCompanySuppressRates(
      @PathVariable String companyAccountId,
      @ParameterObject @Valid AccessRequestDto accessRequestDto) {
    log.info("Request to get suppress rates for company with id: {}", sanitizeInputString(companyAccountId));
    final var company = companyInPort.getCompany(companyAccountId,
        accessRequestDto.getAccessContext(), accessRequestDto.getAccessedBy());
    return new CompanySuppressRatesDto(companyResponseMapper.toDto(company).getSuppressRates());
  }

  @PostMapping(value = "/account/companies", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanySearchResponseDto getCompanies(
      @Valid @RequestBody CompanySearchCriteriaDto companySearchCriteriaDto) {
    log.info("Request to search companies");
    final var companySearchCriteria = companySearchCriteriaRequestMapper.toModel(
                companySearchCriteriaDto);

    final var companySearchResponse = companyInPort.getCompanies(companySearchCriteria);

    return companySearchResponseMapper.toDto(companySearchResponse);
  }
}
