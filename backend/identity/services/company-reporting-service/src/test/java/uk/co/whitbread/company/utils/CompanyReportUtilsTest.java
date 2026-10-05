package uk.co.whitbread.company.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.ManagementInformationResponseDto;
import uk.co.whitbread.company.domain.model.in.EmergencyReportRequest;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.domain.model.out.ManagementInformationReports;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.model.in.CdhMiReportRequestDto;

public class CompanyReportUtilsTest {

  public static final String FROM_DATE = "2023-01-01";
  public static final String FUTURE_DATE = "2039-01-01";
  public static final String TO_DATE = "2023-04-30";
  public static final String TO_DATE_2 = "2024-03-30";
  public static final String COMPANY_ID = "1568";
  public static final String ACCESS_CONTEXT = "Reporting Microservice";
  public static final String ACCESSED_BY = "reporting@whitbread.com";
  public static final String EN = "en";

  private static final ObjectMapper mapper = new ObjectMapper();

  public static ManagementInformationResponseDto getCdhMiReportResponseDto() throws IOException {

    return mapper.readValue(
        CompanyReportUtilsTest.class.getClassLoader()
            .getResource("__files/cdh_mi_report_response.json"),
        ManagementInformationResponseDto.class);
  }

  public static ManagementInformationReports getManagementInformationReports() throws IOException {
    return mapper.readValue(
        CompanyReportUtilsTest.class.getClassLoader()
            .getResource("__files/cdh_mi_report_response.json"),
        ManagementInformationReports.class);
  }

  public static EmergencyReportResponseDto getEmergencyReportTestData() throws IOException {

    return mapper.readValue(
        CompanyReportUtilsTest.class.getClassLoader()
            .getResource("__files/cdh_emergency_report_response.json"),
        EmergencyReportResponseDto.class);
  }
  public static EmergencyReportResponseDto getEmergencyReportTestDataWhenGuestEmpty() throws IOException {

    return mapper.readValue(
        CompanyReportUtilsTest.class.getClassLoader()
            .getResource("__files/cdh_emergency_report_response_emptyGuest.json"),
        EmergencyReportResponseDto.class);
  }

  public static EmergencyReportResponseDto getEmergencyReportTestDatawithReserved() throws IOException {

    return mapper.readValue(
        CompanyReportUtilsTest.class.getClassLoader()
            .getResource("__files/cdh_emergency_report_response_abc.json"),
        EmergencyReportResponseDto.class);
  }

  public static ManagementInformationRequest getManagementInformationRequest() {
    return new ManagementInformationRequest(FROM_DATE, TO_DATE, true,
        COMPANY_ID, ACCESS_CONTEXT, ACCESSED_BY, EN);
  }

  public static ManagementInformationRequest getMIRequestFutureDate() {
    return new ManagementInformationRequest(FUTURE_DATE, TO_DATE, true,
        COMPANY_ID, ACCESS_CONTEXT, ACCESSED_BY, EN);
  }

  public static ManagementInformationRequest getMIRequestMoreThanOneYear() {
    return new ManagementInformationRequest(FROM_DATE, TO_DATE_2, true,
        COMPANY_ID, ACCESS_CONTEXT, ACCESSED_BY, EN);
  }

  public static CdhMiReportRequestDto getCdhMiReportRequestDto() {
    return new CdhMiReportRequestDto(FROM_DATE, TO_DATE, ACCESS_CONTEXT, ACCESSED_BY);
  }

  public static EmergencyReportRequest getCdhEmergencyReportRequest() {
    return new EmergencyReportRequest("2024-04-30", "2024-05-02",
        "test", "test", "test");
  }

}
