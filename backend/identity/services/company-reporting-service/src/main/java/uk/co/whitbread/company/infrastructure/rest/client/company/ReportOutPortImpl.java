package uk.co.whitbread.company.infrastructure.rest.client.company;

import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.EMERGENCY_REPORT_COLUMNS_DE;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.EMERGENCY_REPORT_COLUMNS_EN;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.MANAGEMENT_INFORMATION_REPORT_COLUMNS_DE;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.MANAGEMENT_INFORMATION_REPORT_COLUMNS_EN;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.generateDatedFileName;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.generateNoRecordsFoundException;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.ReportUtils.getValuesForManagementInformationReport;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyResultsDto;
import uk.co.whitbread.company.domain.model.in.EmergencyReportRequest;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.domain.model.out.Booking;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.domain.model.out.MiReportResult;
import uk.co.whitbread.company.domain.ports.secondary.ReportOutPort;
import uk.co.whitbread.company.infrastructure.exceptions.DateRangeException;
import uk.co.whitbread.company.infrastructure.rest.client.aws.AmazonClient;
import uk.co.whitbread.company.infrastructure.rest.client.aws.properties.S3Properties;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhMiReportRequestMapper;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhMiReportResponseMapper;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.CdhAdapterClient;
import uk.co.whitbread.company.infrastructure.rest.client.company.exceptions.RecordsCountExceedException;
import uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient;
import uk.co.whitbread.company.infrastructure.rest.client.company.utils.ReportUtils;
import uk.co.whitbread.company.infrastructure.rest.client.properties.CompanyReportProperties;

@Slf4j
@RequiredArgsConstructor
public class ReportOutPortImpl implements ReportOutPort {

  private final CdhReportClient reportClient;
  private final CdhAdapterClient cdhAdapterClient;
  private final CdhMiReportRequestMapper cdhMiReportRequestMapper;
  private final CdhMiReportResponseMapper cdhMiReportResponseMapper;
  private final CompanyReportProperties companyReportProperties;
  private final AmazonClient amazonClient;
  private final S3Properties s3Properties;

  private static final String MI_REPORT_NAME = "Management Information Report";

  private static final String EMERGENCY_REPORT_NAME = "Emergency Report";
  private static final String EMERGENCY_REPORT = "Emergency_Report_";
  private static final String ATTACHMENT_FILENAME = "attachment; filename=";
  private static final int DATE_RANGE = 365;

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String DATE_FORMAT_FILE = "yyyy-MM-dd_HH-mm-ss";
  private static final String DE = "de";
  private static final String EN = "en";

  private static final String EXT_XLS = ".xls";
  private static final String UNDERSCORE = "_";

  @Override
  public byte[] getManagementInformationReport(
      ManagementInformationRequest managementInformationRequest) {

    ByteArrayOutputStream byteArrayOutputStream = getManagementInformationReportForUri(
        managementInformationRequest);

    return byteArrayOutputStream.toByteArray();
  }

  private ByteArrayOutputStream getManagementInformationReportForUri(
      ManagementInformationRequest managementInformationRequest) {

    List<String> managementInformationReportColumns =
        managementInformationRequest.language().equalsIgnoreCase(DE) ? new ArrayList<>(
            Arrays.asList(
                MANAGEMENT_INFORMATION_REPORT_COLUMNS_DE)) : new ArrayList<>(Arrays.asList(
            MANAGEMENT_INFORMATION_REPORT_COLUMNS_EN));

    var cdhMiReportRequestDto = cdhMiReportRequestMapper
        .toDto(managementInformationRequest);
    var cdhResponse = cdhAdapterClient
        .getManagementInformation(managementInformationRequest.companyId(),
            cdhMiReportRequestDto);
    if (null == cdhResponse.getResults()) {
      throw generateNoRecordsFoundException(managementInformationRequest.companyId(),
          managementInformationRequest);

    } else {
      log.debug("Number of records allowed in excel report: {}",
          companyReportProperties.getAllowedRecordCount());
      if (cdhResponse.getResults().size() > companyReportProperties.getAllowedRecordCount()) {
        throw new RecordsCountExceedException("The number of records is greater than expected.");
      }
    }

    var responseModel = cdhMiReportResponseMapper.toModel(cdhResponse);
    List<Booking> allBookings = responseModel.results();

    List<List<String>> managementInformationReport = getValuesForManagementInformationReport(
        allBookings, managementInformationRequest, managementInformationReportColumns,
        Arrays.asList(companyReportProperties.getManagementInformationStatusItems().split(",")));

    return reportClient.retrieveSpreadsheet(
        managementInformationReport, managementInformationReportColumns.toArray(String[]::new),
        managementInformationRequest);
  }

  @Override
  public HttpHeaders getHeaders(ManagementInformationRequest managementInformationRequest) {
    HttpHeaders headers = new HttpHeaders();
    String datedFilename = generateDatedFileName(managementInformationRequest);
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.setContentDisposition(
        ContentDisposition.parse(ATTACHMENT_FILENAME + datedFilename + EXT_XLS));
    return headers;
  }


  @Override
  public MiReportResult getManagementInformationReportUrl(ManagementInformationRequest request) {

    String filename = generateDatedFileName(request);
    String awsFileName =
        request.companyId() + UNDERSCORE + filename + UNDERSCORE + request.language() + EXT_XLS;
    filename = filename + EXT_XLS;
    log.info("File name created: {} - aws fileName: {} ", filename, awsFileName);
    boolean flag = amazonClient.checkFileAlreadyExists(awsFileName,
        s3Properties.getBucket().getMiReportName());
    log.info("File already exists: {}", flag);
    if (!flag) {

      ByteArrayOutputStream byteArrayOutputStream = getManagementInformationReportForUri(request);
      amazonClient.uploadFileToS3bucket(filename, awsFileName,
          s3Properties.getBucket().getMiReportName(),
          byteArrayOutputStream);
      log.info("File uploaded to S3 bucket: {}", awsFileName);
    }

    String preSignedUrl = amazonClient.createPresignedGetUrl(awsFileName,
        s3Properties.getBucket().getMiReportName());
    log.debug("Pre-signed URL generated: {}", preSignedUrl);

    return new MiReportResult(MI_REPORT_NAME, filename, preSignedUrl);
  }

  @Override
  public void validateDateRange(ManagementInformationRequest request) {

    Predicate<ManagementInformationRequest> isNotWithinOneYear = s ->
        DAYS.between(LocalDate.parse(s.fromDate()), LocalDate.parse(s.toDate())) > DATE_RANGE;
    Predicate<ManagementInformationRequest> isFutureFromDate = r -> LocalDate.parse(r.fromDate())
        .isAfter(LocalDate.now());
    Predicate<ManagementInformationRequest> isFutureToDate = r -> LocalDate.parse(r.toDate())
        .isAfter(LocalDate.now());

    if (isNotWithinOneYear.or(isFutureFromDate).or(isFutureToDate).test(request)) {
      throw new DateRangeException(
          "Dates cannot be a future date / Date range shouldn't be more than 1 year.");
    }
  }

  @Override
  public EmergencyReportResult getEmergencyReportUrl(String companyId, String accessContext,
      String accessedBy, String language) {

    EmergencyReportRequest request = getEmergencyReportRequest(
        companyId, accessContext, accessedBy);

    String filename = prepareS3FileName();
    String awsFileName =
        request.companyId() + UNDERSCORE + generateDatedTimeFileName() + UNDERSCORE + language
            + EXT_XLS;
    filename = filename + EXT_XLS;
    log.info("File name created: {} - aws fileName: {} ", filename, awsFileName);

    ByteArrayOutputStream byteArrayOutputStream = getEmergencyReportForS3Uri(request, language);
    amazonClient.uploadFileToS3bucket(filename, awsFileName,
        s3Properties.getBucket().getEmergencyReportName(), byteArrayOutputStream);
    log.info("File uploaded to S3 bucket: {}", awsFileName);
    String preSignedUrl = amazonClient.createPresignedGetUrl(awsFileName,
        s3Properties.getBucket().getEmergencyReportName());
    log.debug("Pre-signed URL generated: {}", preSignedUrl);

    return new EmergencyReportResult(EMERGENCY_REPORT_NAME, filename, preSignedUrl);
  }

  private static EmergencyReportRequest getEmergencyReportRequest(String companyId,
      String accessContext, String accessedBy) {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);

    return EmergencyReportRequest.builder()
        .fromDate(new SimpleDateFormat(DATE_FORMAT).format(new Date()))
        .toDate(new SimpleDateFormat(DATE_FORMAT).format(c.getTime()))
        .companyId(companyId)
        .accessContext(accessContext)
        .accessedBy(accessedBy)
        .build();
  }

  private static String prepareS3FileName() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);

    return EMERGENCY_REPORT + new SimpleDateFormat(DATE_FORMAT).format(new Date())
        + UNDERSCORE + new SimpleDateFormat(DATE_FORMAT).format(c.getTime());
  }

  private static String generateDatedTimeFileName() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);

    return EMERGENCY_REPORT + new SimpleDateFormat(DATE_FORMAT_FILE).format(new Date())
        + UNDERSCORE + new SimpleDateFormat(DATE_FORMAT_FILE).format(c.getTime());
  }

  private ByteArrayOutputStream getEmergencyReportForS3Uri(EmergencyReportRequest request,
      String language) {

    List<String> emergencyReportColumns = language.equalsIgnoreCase(DE)
        ? new ArrayList<>(Arrays.asList(
        EMERGENCY_REPORT_COLUMNS_DE)) : new ArrayList<>(Arrays.asList(
        EMERGENCY_REPORT_COLUMNS_EN));

    var cdhResponse = cdhAdapterClient
        .getEmergencyReport(request.companyId(),
            request);
    if (null == cdhResponse.getResults()) {
      throw generateNoRecordsFoundException(request.companyId(),
          request);
    }

    List<EmergencyResultsDto> emergencyResultsDtos = cdhResponse.getResults();

    List<List<String>> emergencyReport = ReportUtils.getValuesForEmergencyReport(
        emergencyResultsDtos,
        Arrays.asList(companyReportProperties.getEmergencyStatusItems().split(",")),
        request);

    return reportClient.retrieveEmergencySpreadsheet(
        emergencyReport, emergencyReportColumns.toArray(String[]::new),
        request);
  }


}
