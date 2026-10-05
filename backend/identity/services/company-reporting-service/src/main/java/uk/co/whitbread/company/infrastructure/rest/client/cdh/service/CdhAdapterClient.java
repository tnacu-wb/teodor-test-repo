package uk.co.whitbread.company.infrastructure.rest.client.cdh.service;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.ManagementInformationResponseDto;
import uk.co.whitbread.company.domain.model.in.EmergencyReportRequest;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.exception.CdhRestClientErrorHandler;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.model.in.CdhMiReportRequestDto;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhAdapterClient {

  private final CdhAdapterProperties cdhAdapterProperties;
  private final RestClient cdhAdapterRestClient;
  private static final String FROM_DATE = "fromDate";
  private static final String TO_DATE = "toDate";
  private static final String ACCESS_CONTEXT = "accessContext";
  private static final String ACCESSED_BY = "accessedBy";

  private static final String DATE_FORMAT = "yyyy-MM-dd";


  public ManagementInformationResponseDto getManagementInformation(String companyId,
      CdhMiReportRequestDto cdhMiReportRequestDto) {
    MultiValueMap<String, String> queryParams = getQueryParamsForCdhAdapter(
        cdhMiReportRequestDto);
    CdhRestClientErrorHandler cdhRestClientErrorHandler = new CdhRestClientErrorHandler();
    return cdhAdapterRestClient.get()
        .uri(uriBuilder -> uriBuilder.path(cdhAdapterProperties.getCompanyMiReportEndpoint())
            .queryParams(queryParams)
            .build(companyId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, cdhRestClientErrorHandler::handle)
        .body(ManagementInformationResponseDto.class);
  }

  private static MultiValueMap<String, String> getQueryParamsForCdhAdapter(
      CdhMiReportRequestDto cdhMiReportRequestDto) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(FROM_DATE, cdhMiReportRequestDto.fromDate());
    queryParams.add(TO_DATE, cdhMiReportRequestDto.toDate());
    queryParams.add(ACCESS_CONTEXT, cdhMiReportRequestDto.accessContext());
    queryParams.add(ACCESSED_BY, cdhMiReportRequestDto.accessedBy());
    return queryParams;
  }

  public EmergencyReportResponseDto getEmergencyReport(String companyId,
      EmergencyReportRequest request) {
    MultiValueMap<String, String> queryParams = getQueryParamsForCdhEmergencyAdapter(
        request);
    CdhRestClientErrorHandler cdhRestClientErrorHandler = new CdhRestClientErrorHandler();
    return cdhAdapterRestClient.get()
        .uri(uriBuilder -> uriBuilder.path(cdhAdapterProperties.getEmergencyReportEndpoint())
            .queryParams(queryParams)
            .build(companyId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, cdhRestClientErrorHandler::handle)
        .body(EmergencyReportResponseDto.class);
  }

  private static MultiValueMap<String, String> getQueryParamsForCdhEmergencyAdapter(
      EmergencyReportRequest request) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);
    queryParams.add(FROM_DATE, new SimpleDateFormat(DATE_FORMAT).format(new Date()));
    queryParams.add(TO_DATE, new SimpleDateFormat(DATE_FORMAT).format(c.getTime()));
    queryParams.add(ACCESS_CONTEXT, request.accessContext());
    queryParams.add(ACCESSED_BY, request.accessedBy());
    return queryParams;
  }
}
