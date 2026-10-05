package uk.co.whitbread.employee.bulk.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static uk.co.whitbread.employee.bulk.converter.CdhBulkEmployeeConverter.SPREADSHEET_LIMIT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import uk.co.whitbread.employee.bulk.exception.BulkEmployeeUploadValidationException;
import uk.co.whitbread.employee.bulk.exception.ValidationError;
import uk.co.whitbread.employee.bulk.model.AccessLevel;
import uk.co.whitbread.employee.bulk.model.Country;
import uk.co.whitbread.employee.bulk.model.EmployeeWithRowNumber;
import uk.co.whitbread.employee.bulk.service.CountriesService;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;

@ExtendWith(MockitoExtension.class)
class CDHBulkEmployeeConverterTest {

  private static final String COUNTRY_CODE = "GB";
  private static final String COUNTRY_LEGEND = "United Kingdom (the)";
  static final String FILE_NAME = "upFile";
  static final String RESOURCES_PATH = "src/test/resources/__files/";

  @Mock
  private CountriesService mockCountriesService;

  @InjectMocks
  private CdhBulkEmployeeConverter cdhBulkEmployeeConverter;

  @BeforeEach
  void setup() {
    lenient().when(mockCountriesService.getCountriesByLegend())
        .thenReturn(Collections
            .singletonMap(COUNTRY_LEGEND, generateCountry(COUNTRY_CODE, COUNTRY_LEGEND)));
  }

  @Test
  void shouldConvertFromXLSFileToEmployeeAccountRequest() throws IOException {
    MockMultipartFile upFile = generateMultipartFileByPath(FILE_NAME,
        RESOURCES_PATH + "employees_with-data.xls");

    EmployeeAccountRequest emp = buildEmployeeAccountRequest();

    List<ValidationError> failedEmployees = new java.util.LinkedList<>();
    List<EmployeeWithRowNumber> exportData = cdhBulkEmployeeConverter
        .convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees);

    assertThat(exportData.getFirst().getEmployee())
        .isEqualTo(emp);
    assertThat(exportData.getFirst().getRowNumber()).isEqualTo(1);
    assertThat(failedEmployees).isEmpty();
  }

  @Test
  void shouldConvertFromXLSXToEmployeeAccountRequest() throws IOException {
    MockMultipartFile upFile = generateMultipartFileByPath(FILE_NAME,
        RESOURCES_PATH + "employees_with-data.xlsx");

    EmployeeAccountRequest emp = buildEmployeeAccountRequest();

    List<ValidationError> failedEmployees = new java.util.LinkedList<>();
    List<EmployeeWithRowNumber> exportData = cdhBulkEmployeeConverter
        .convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees);

    assertThat(exportData.getFirst().getEmployee())
        .isEqualTo(emp);
    assertThat(exportData.getFirst().getRowNumber()).isEqualTo(1);
    assertThat(failedEmployees).isEmpty();
  }

  @Test
  void testForInvalidCountryLegend() throws Exception {
    MockMultipartFile upFile = generateMultipartFileByPath(FILE_NAME,
        RESOURCES_PATH + "employees_invalid-country.xlsx");

    List<ValidationError> failedEmployees = new java.util.LinkedList<>();
    //Then
    assertThatThrownBy(
        () -> cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees))
        .isInstanceOf(BulkEmployeeUploadValidationException.class)
        .hasFieldOrPropertyWithValue("message", "Unrecognised country legend: INVALID");
  }

  @Test
  void testForInvalidAccessLevel() throws Exception {
    MockMultipartFile upFile = generateMultipartFileByPath(FILE_NAME,
        RESOURCES_PATH + "employees_invalid-access-level.xlsx");

    List<ValidationError> failedEmployees = new java.util.LinkedList<>();
    //Then
    assertThatThrownBy(
        () -> cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees))
        .isInstanceOf(BulkEmployeeUploadValidationException.class)
        .hasFieldOrPropertyWithValue("message", "Unrecognised access level: Invalid");
  }

  @Test
  void testForSpreadsheetExceedsTheLimit() throws Exception {
    MockMultipartFile upFile = generateMultipartFileByPath(FILE_NAME,
        RESOURCES_PATH + "employees_2001-entries.xlsx");

    List<ValidationError> failedEmployees = new java.util.LinkedList<>();
    //Then
    assertThatThrownBy(
        () -> cdhBulkEmployeeConverter.convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees))
        .isInstanceOf(BulkEmployeeUploadValidationException.class)
        .hasFieldOrPropertyWithValue("message",
            "The provided spreadsheet exceeds the limit of " + SPREADSHEET_LIMIT
                + " employees at a time");
  }

  @Test
  void testForEmployeeValidationFailures() throws Exception {
    MockMultipartFile upFile = generateMultipartFileByPath(FILE_NAME,
        RESOURCES_PATH + "employees_invalid-fields.xlsx");

    List<ValidationError> failedEmployees = new java.util.LinkedList<>();
    List<EmployeeWithRowNumber> result = cdhBulkEmployeeConverter
        .convertEmployeeSpreadsheetToEmployeeAccountRequest(upFile, failedEmployees);

    assertThat(result).hasSize(4);
    assertThat(failedEmployees)
        .isNotEmpty()
        .hasSize(9);
    assertThat(failedEmployees.get(0).getErrorCode()).isEqualTo("283");
    assertThat(failedEmployees.get(0).getErrorDescription()).isEqualTo("1");
    assertThat(failedEmployees.get(1).getErrorCode()).isEqualTo("289");
    assertThat(failedEmployees.get(1).getErrorDescription()).isEqualTo("1");
    assertThat(failedEmployees.get(2).getErrorCode()).isEqualTo("285");
    assertThat(failedEmployees.get(2).getErrorDescription()).isEqualTo("2");
    assertThat(failedEmployees.get(3).getErrorCode()).isEqualTo("285");
    assertThat(failedEmployees.get(3).getErrorDescription()).isEqualTo("3");
    assertThat(failedEmployees.get(4).getErrorCode()).isEqualTo("286");
    assertThat(failedEmployees.get(4).getErrorDescription()).isEqualTo("3");
    assertThat(failedEmployees.get(5).getErrorCode()).isEqualTo("288");
    assertThat(failedEmployees.get(5).getErrorDescription()).isEqualTo("3");
    assertThat(failedEmployees.get(6).getErrorCode()).isEqualTo("284");
    assertThat(failedEmployees.get(6).getErrorDescription()).isEqualTo("4");
    assertThat(failedEmployees.get(7).getErrorCode()).isEqualTo("287");
    assertThat(failedEmployees.get(7).getErrorDescription()).isEqualTo("4");
    assertThat(failedEmployees.get(8).getErrorCode()).isEqualTo("288");
    assertThat(failedEmployees.get(8).getErrorDescription()).isEqualTo("4");
  }

  private static EmployeeAccountRequest buildEmployeeAccountRequest() {
    BusinessAddress address = new BusinessAddress();
    address.setAddressLine1("120 Holburn");
    address.setAddressLine2("Holburn");
    address.setAddressLine3("London");
    address.setAddressLine4("Greater London");
    address.setAddressLine5("England");
    address.setPostCode("EC1N 2TD");
    address.setCountryCode("GB");

    return EmployeeAccountRequest.builder()
        .title("Miss")
        .firstName("Jane")
        .lastName("Long")
        .emailAddress("jlong3@gmail.com")
        .phoneNumber("07912345678")
        .mobileNumber("07912345678")
        .textConfirmation(true)
        .centralCardIdString("1")
        .accessLevel(AccessLevel.BOOKER.toString())
        .address(address).build();
  }

  private static Country generateCountry(String countryCode, String countryLegend) {
    return Country.builder()
        .countryCode(countryCode)
        .countryLegend(countryLegend)
        .build();
  }

  private static MockMultipartFile generateMultipartFileByPath(String name, String path)
      throws IOException {
    return new MockMultipartFile(name,
        IOUtils.toByteArray(Files.newInputStream(Paths.get(path))));
  }
}
