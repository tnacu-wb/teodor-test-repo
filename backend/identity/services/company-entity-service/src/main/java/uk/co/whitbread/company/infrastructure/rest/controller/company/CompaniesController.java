package uk.co.whitbread.company.infrastructure.rest.controller.company;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.company.domain.ports.primary.CdhCompanyPort;
import uk.co.whitbread.company.domain.ports.primary.CompaniesProfilePort;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.CompaniesMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompaniesResponseDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompanyResponseDto;


@RequestMapping("/v1/companies")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CompaniesController implements CompaniesControllerApi {

  private final CompaniesProfilePort companiesProfileService;
  private final CdhCompanyPort companyPort;
  private final CompaniesMapper companiesMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public CompaniesResponseDto getCompaniesFromCdh(
      @ParameterObject @Valid CompaniesRequestDto companiesRequestDto
  ) {
    var companiesSearchRequest = companiesMapper.toCompaniesSearchRequestModel(companiesRequestDto);
    var companiesSearchResponse = this.companyPort.getCompaniesFromCdh(companiesSearchRequest);
    return companiesMapper.toCompaniesProfileDto(companiesSearchResponse);
  }

  @GetMapping(value = "{corporateId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanyResponseDto getCompany(@PathVariable @NotNull String corporateId,
      @RequestParam(required = false) Boolean excludeNegotiatedRates) {
    boolean isNegotiatedRatesExcluded = false;
    if (excludeNegotiatedRates != null && Boolean.TRUE.equals(excludeNegotiatedRates)) {
      isNegotiatedRatesExcluded = true;
    }
    var companyResponse = this.companiesProfileService.getCompanyProfile(corporateId, isNegotiatedRatesExcluded);
    return companiesMapper.toCompanyResponseDto(companyResponse);
  }

  @GetMapping(value = "/id/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanyResponseDto getCompanyById(@PathVariable @NotNull String id) {
    var companyResponse = this.companiesProfileService.getCompanyWithNegotiatedRatesById(id);
    return companiesMapper.toCompanyResponseDto(companyResponse);
  }

  @GetMapping(value = "/opera-id/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanyResponseDto getCompanyByOperaId(@PathVariable @NotNull String id) {
    var companyResponse = companiesProfileService.getCompanyByOperaId(id);
    return companiesMapper.toCompanyResponseDto(companyResponse);
  }

  @GetMapping(value = "/profile",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CompaniesResponseDto getCompaniesProfile(
      @ParameterObject @Validated CompaniesProfileRequestDto requestDto) {
    var companiesProfileRequest = companiesMapper.toCompaniesProfileRequestModel(requestDto);
    var companiesResponse = this.companiesProfileService.getCompaniesProfile(companiesProfileRequest);
    return companiesMapper.toCompaniesProfileDto(companiesResponse);
  }

}