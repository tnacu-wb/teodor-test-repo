package uk.co.whitbread.company.infrastructure.rest.client.company;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.COMPANY_ID;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getCdhMiReportRequestDto;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getCdhMiReportResponseDto;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getEmergencyReportTestData;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getEmergencyReportTestDataWhenGuestEmpty;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getEmergencyReportTestDatawithReserved;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getMIRequestFutureDate;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getMIRequestMoreThanOneYear;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getManagementInformationReports;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getManagementInformationRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import software.amazon.awssdk.services.s3.S3Client;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.ManagementInformationResponseDto;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.domain.model.out.MiReportResult;
import uk.co.whitbread.company.infrastructure.exceptions.DateRangeException;
import uk.co.whitbread.company.infrastructure.rest.client.aws.AmazonClient;
import uk.co.whitbread.company.infrastructure.rest.client.aws.properties.S3Properties;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhMiReportRequestMapper;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhMiReportResponseMapper;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.CdhAdapterClient;
import uk.co.whitbread.company.infrastructure.rest.client.company.exceptions.RecordsCountExceedException;
import uk.co.whitbread.company.infrastructure.rest.client.company.exceptions.RecordsNotFoundException;
import uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient;
import uk.co.whitbread.company.infrastructure.rest.client.properties.CompanyReportProperties;
import uk.co.whitbread.company.utils.TestUtils;

@ExtendWith(MockitoExtension.class)
class ReportOutPortImplTest {


  @Mock
  private CdhMiReportRequestMapper cdhMiReportRequestMapper;

  @Mock
  private CdhAdapterClient cdhAdapterClient;

  @Mock
  private CdhMiReportResponseMapper cdhMiReportResponseMapper;

  @Mock
  private CdhReportClient cdhReportClient;

  @Mock
  private CompanyReportProperties companyReportProperties;

  @Mock
  private AmazonClient amazonClient;

  @Mock
  private S3Properties s3properties;

  @Mock
  private S3Client s3Client;

  @InjectMocks
  private ReportOutPortImpl reportOutPortImpl;

  private static final String DATE_FORMAT_FILE = "yyyy-MM-dd_HH-mm-ss";
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  @Test
  void getHeaders__ShouldReturnOK() {

    //Arrange
    var managementInformationRequest = getManagementInformationRequest();

    //Act
    final HttpHeaders result = reportOutPortImpl.getHeaders(managementInformationRequest);

    //Assert
    assertThat(result.getContentType(), is(MediaType.MULTIPART_FORM_DATA));
    assertThat(result.getContentDisposition(), is(ContentDisposition.parse(
        "attachment; filename=\"MI_Report_01-01-2023_30-04-2023_WithQnA.xls\"")));

  }

  @Test
  void getManagementInformationReport__ShouldReturnOK() throws IOException {

    //Arrange
    var managementInformationRequest = getManagementInformationRequest();
    var cdhMiReportRequestDto = getCdhMiReportRequestDto();
    var cdhMiReportResponseDto = getCdhMiReportResponseDto();
    var managementInformationReports = getManagementInformationReports();
    ByteArrayOutputStream bos = getOutputStream();

    when(cdhMiReportRequestMapper.toDto(managementInformationRequest))
        .thenReturn(cdhMiReportRequestDto);
    when(cdhAdapterClient.getManagementInformation(COMPANY_ID,
        cdhMiReportRequestDto)).thenReturn(cdhMiReportResponseDto);
    when(cdhMiReportResponseMapper.toModel(cdhMiReportResponseDto))
        .thenReturn(managementInformationReports);
    when(cdhReportClient.retrieveSpreadsheet(any(), any(), any())).thenReturn(bos);
    when(companyReportProperties.getAllowedRecordCount()).thenReturn(100);
    when(companyReportProperties.getManagementInformationStatusItems()).thenReturn("NoShow,CheckedOut");

    //Act
    var result = reportOutPortImpl.getManagementInformationReport(
        managementInformationRequest);

    //Assert
    assertThat(result, notNullValue());
  }

  @Test
  void getManagementInformationReport__ShouldReturnException() throws IOException {

    //Arrange
    var managementInformationRequest = getManagementInformationRequest();
    var cdhMiReportRequestDto = getCdhMiReportRequestDto();
    var cdhMiReportResponseDto = getCdhMiReportResponseDto();

    when(cdhMiReportRequestMapper.toDto(managementInformationRequest))
        .thenReturn(cdhMiReportRequestDto);
    when(cdhAdapterClient.getManagementInformation(COMPANY_ID,
        cdhMiReportRequestDto)).thenReturn(cdhMiReportResponseDto);

    //Assert
    assertThrows(RecordsCountExceedException.class,
        () -> reportOutPortImpl.getManagementInformationReport(
            managementInformationRequest));
  }

  @Test
  void getManagementInformationReport__ShouldThrowException() {

    //Arrange
    var managementInformationRequest = getManagementInformationRequest();
    var cdhMiReportRequestDto = getCdhMiReportRequestDto();
    var cdhMiReportResponseDto = new ManagementInformationResponseDto();
    cdhMiReportResponseDto.setResults(null);

    when(cdhMiReportRequestMapper.toDto(managementInformationRequest))
        .thenReturn(cdhMiReportRequestDto);
    when(cdhAdapterClient.getManagementInformation(COMPANY_ID,
        cdhMiReportRequestDto)).thenReturn(cdhMiReportResponseDto);

    //Assert
    assertThrows(RecordsNotFoundException.class,
        () -> reportOutPortImpl.getManagementInformationReport(
            managementInformationRequest));
  }

  @Test
  void getManagementInformationReportUrl__ShouldReturnOK() throws IOException {

    //Arrange
    var managementInformationRequest = getManagementInformationRequest();

    when(amazonClient.checkFileAlreadyExists(
        "1568_MI_Report_01-01-2023_30-04-2023_WithQnA_en.xls","test-bucket")).thenReturn(false);
    when(amazonClient.createPresignedGetUrl(
        "1568_MI_Report_01-01-2023_30-04-2023_WithQnA_en.xls","test-bucket")).thenReturn(
        "https://test.com/MI_Report_01-01-2023_30-04-2023_WithQnA.xls");
    //Act
    var cdhMiReportRequestDto = getCdhMiReportRequestDto();
    var cdhMiReportResponseDto = getCdhMiReportResponseDto();
    var managementInformationReports = getManagementInformationReports();

    when(companyReportProperties.getAllowedRecordCount()).thenReturn(100);
    when(cdhMiReportRequestMapper.toDto(managementInformationRequest))
        .thenReturn(cdhMiReportRequestDto);
    when(cdhAdapterClient.getManagementInformation(COMPANY_ID,
        cdhMiReportRequestDto)).thenReturn(cdhMiReportResponseDto);
    when(cdhMiReportResponseMapper.toModel(cdhMiReportResponseDto))
        .thenReturn(managementInformationReports);
    when(s3properties.getBucket()).thenReturn(TestUtils.getBucket());
    when(companyReportProperties.getManagementInformationStatusItems()).thenReturn("NoShow,CheckedOut");
    final MiReportResult result = reportOutPortImpl.getManagementInformationReportUrl(
        managementInformationRequest);

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.downloadUrl(), is("https://test.com/MI_Report_01-01-2023_30-04-2023_WithQnA.xls"));
    assertThat(result.fileName(), is("MI_Report_01-01-2023_30-04-2023_WithQnA.xls"));
    assertThat(result.reportName(), is("Management Information Report"));
  }

  @Test
  void getManagementInformationReportUrl__ShouldReturnOKWhenFlag() {

    //Arrange
    var managementInformationRequest = getManagementInformationRequest();

    when(amazonClient.checkFileAlreadyExists(
        "1568_MI_Report_01-01-2023_30-04-2023_WithQnA_en.xls","test-bucket")).thenReturn(true);
    when(amazonClient.createPresignedGetUrl(
        "1568_MI_Report_01-01-2023_30-04-2023_WithQnA_en.xls","test-bucket")).thenReturn(
        "https://test.com/MI_Report_01-01-2023_30-04-2023_WithQnA.xls");
    //Act
    when(s3properties.getBucket()).thenReturn(TestUtils.getBucket());
    final MiReportResult result = reportOutPortImpl.getManagementInformationReportUrl(
        managementInformationRequest);

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.downloadUrl(), is("https://test.com/MI_Report_01-01-2023_30-04-2023_WithQnA.xls"));
    assertThat(result.fileName(), is("MI_Report_01-01-2023_30-04-2023_WithQnA.xls"));
    assertThat(result.reportName(), is("Management Information Report"));
  }

  @Test
  void getEmergencyReportUrl_ShouldReturnOK() throws IOException {

    //Arrange
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);

    String startDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
    String endDate = new SimpleDateFormat(DATE_FORMAT).format(c.getTime());
    String startDateTime = new SimpleDateFormat(DATE_FORMAT_FILE).format(new Date());
    String endDateTime = new SimpleDateFormat(DATE_FORMAT_FILE).format(c.getTime());
    String fileName = "Emergency_Report_" + startDateTime + "_" + endDateTime ;
    String fileNameEt = "Emergency_Report_" + startDate + "_" + endDate + ".xls";

    when(amazonClient.createPresignedGetUrl(
        "test_"+fileName+"_en.xls","test-bucket")).thenReturn(
        "https://test.com/"+fileName+".xls");
    var cdhEmergencyReportResponseDto = getEmergencyReportTestData();
    //Act
    when(s3properties.getBucket()).thenReturn(TestUtils.getBucket());
    when(cdhAdapterClient.getEmergencyReport(any(),any())).thenReturn(
        cdhEmergencyReportResponseDto);
    when(companyReportProperties.getEmergencyStatusItems()).thenReturn("Reserved","CheckedIn");
    final EmergencyReportResult result = reportOutPortImpl.getEmergencyReportUrl("test",
        "test", "test", "en");

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.downloadUrl(), is("https://test.com/"+fileName+".xls"));
    assertThat(result.fileName(), is(fileNameEt));
    assertThat(result.reportName(), is("Emergency Report"));
  }

  @Test
  void getEmergencyReportUrl_ShouldReturnOKWhenGuestDetailsEmpty() throws IOException {

    //Arrange
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);

    String startDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
    String endDate = new SimpleDateFormat(DATE_FORMAT).format(c.getTime());
    String startDateTime = new SimpleDateFormat(DATE_FORMAT_FILE).format(new Date());
    String endDateTime = new SimpleDateFormat(DATE_FORMAT_FILE).format(c.getTime());
    String fileName = "Emergency_Report_" + startDateTime + "_" + endDateTime ;
    String fileNameExt = "Emergency_Report_" + startDate + "_" + endDate + ".xls";

    when(amazonClient.createPresignedGetUrl(
        "test_"+fileName+"_de"+ ".xls","test-bucket")).thenReturn(
        "https://test.com/"+fileNameExt);

    var cdhEmergencyReportResponseDto = getEmergencyReportTestDataWhenGuestEmpty();
    //Act
    when(s3properties.getBucket()).thenReturn(TestUtils.getBucket());
    when(cdhAdapterClient.getEmergencyReport(any(),any())).thenReturn(
        cdhEmergencyReportResponseDto);
    when(companyReportProperties.getEmergencyStatusItems()).thenReturn("Reserved","CheckedIn");
    final EmergencyReportResult result = reportOutPortImpl.getEmergencyReportUrl("test",
        "test", "test", "de" );

    //Assert
    assertThat(result, notNullValue());
    assertThat(result.downloadUrl(), is("https://test.com/"+fileNameExt));
    assertThat(result.fileName(), is(fileNameExt));
    assertThat(result.reportName(), is("Emergency Report"));
  }

  @Test
  void getEmergencyReportUrl_NoRecordFound() throws IOException {

    //Arrange

    var cdhEmergencyReportResponseDto = getEmergencyReportTestDatawithReserved();
    //Act
    when(cdhAdapterClient.getEmergencyReport(any(),any())).thenReturn(
        cdhEmergencyReportResponseDto);
    when(companyReportProperties.getEmergencyStatusItems()).thenReturn("abc");

    //Assert
    assertThrows(RecordsNotFoundException.class,
        () -> reportOutPortImpl.getEmergencyReportUrl("test",
            "test", "test", "de" ));
  }

  @Test
  void validateDateRange_futureDate_shouldReturnThrowException() {
    var managementInformationRequest = getMIRequestFutureDate();
    assertThrows(DateRangeException.class,
        () -> reportOutPortImpl.validateDateRange(
            managementInformationRequest));
  }

  @Test
  void validateDateRange_moreThanOneYear_shouldReturnThrowException() {
    var managementInformationRequest = getMIRequestMoreThanOneYear();
    assertThrows(DateRangeException.class,
        () -> reportOutPortImpl.validateDateRange(
            managementInformationRequest));
  }

  private static ByteArrayOutputStream getOutputStream() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    String s = "Test Value";
    for (int i = 0; i < s.length(); ++i) {
      bos.write(s.charAt(i));
    }
    return bos;
  }


}