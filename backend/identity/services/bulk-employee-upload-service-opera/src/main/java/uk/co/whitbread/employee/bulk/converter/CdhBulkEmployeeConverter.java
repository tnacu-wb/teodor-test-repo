package uk.co.whitbread.employee.bulk.converter;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.co.whitbread.employee.bulk.ErrorCode;
import uk.co.whitbread.employee.bulk.exception.BulkEmployeeUploadValidationException;
import uk.co.whitbread.employee.bulk.exception.ValidationError;
import uk.co.whitbread.employee.bulk.model.AccessLevel;
import uk.co.whitbread.employee.bulk.model.Country;
import uk.co.whitbread.employee.bulk.model.EmployeeSpreadsheet;
import uk.co.whitbread.employee.bulk.model.EmployeeWithRowNumber;
import uk.co.whitbread.employee.bulk.model.SpreadsheetAccessLevel;
import uk.co.whitbread.employee.bulk.service.CountriesService;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;

@Component
@RequiredArgsConstructor
public class CdhBulkEmployeeConverter {

  protected static final int SPREADSHEET_LIMIT = 2000;

  private final CountriesService countriesService;

  public List<EmployeeWithRowNumber> convertEmployeeSpreadsheetToEmployeeAccountRequest(
      MultipartFile multipartFile, List<ValidationError> failedEmployees) throws IOException {

    final List<EmployeeSpreadsheet> employeeSummaryList = getEmployeeSpreadsheets(
        multipartFile.getInputStream(), multipartFile.getOriginalFilename());

    if (employeeSummaryList.size() > SPREADSHEET_LIMIT) {
      throw new BulkEmployeeUploadValidationException(
          "The provided spreadsheet exceeds the limit of " + SPREADSHEET_LIMIT
              + " employees at a time");
    }

    return IntStream.range(0, employeeSummaryList.size())
        .mapToObj(i -> {
          validateEmployee(employeeSummaryList.get(i), failedEmployees, i + 1);
          return new EmployeeWithRowNumber(i + 1, toEmployeeAccountRequest(employeeSummaryList.get(i)));
        })
        .collect(Collectors.toList());
  }

  private List<EmployeeSpreadsheet> getEmployeeSpreadsheets(InputStream inputStream,
      String fileName) {
    final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
        .headerStart(1)
        .build();
    return Poiji.fromExcel(inputStream,
        getPoijiExcelFileType(fileName), EmployeeSpreadsheet.class, options);
  }


  private PoijiExcelType getPoijiExcelFileType(String fileName) {
    return FilenameUtils.getExtension(fileName).equalsIgnoreCase("xls") ? PoijiExcelType.XLS
        : PoijiExcelType.XLSX;
  }

  private EmployeeAccountRequest toEmployeeAccountRequest(EmployeeSpreadsheet spreadsheet) {
    return EmployeeAccountRequest.builder()
        .accessLevel(convertAccessLevel(spreadsheet.getAccessLevel()))
        .title(amendValidProperty(spreadsheet.getTitle()))
        .firstName(amendValidProperty(spreadsheet.getFirstName()))
        .lastName(amendValidProperty(spreadsheet.getLastName()))
        .textConfirmation(convertTextConfirmation(spreadsheet.getTextConfirmation()))
        .centralCardIdString(amendValidProperty(spreadsheet.getCentralCardId()))
        .emailAddress(amendValidProperty(spreadsheet.getEmailAddress()))
        .phoneNumber(amendValidProperty(spreadsheet.getPhoneNumber()))
        .mobileNumber(amendValidProperty(spreadsheet.getMobileNumber()))
        .address(toBusinessAddress(spreadsheet))
        .build();
  }

  private BusinessAddress toBusinessAddress(EmployeeSpreadsheet spreadsheet) {
    BusinessAddress address = new BusinessAddress();
    address.setAddressLine1(amendValidProperty(spreadsheet.getAddressLine1()));
    address.setAddressLine2(amendValidProperty(spreadsheet.getAddressLine2()));
    address.setAddressLine3(amendValidProperty(spreadsheet.getAddressLine3()));
    address.setAddressLine4(amendValidProperty(spreadsheet.getAddressLine4()));
    address.setAddressLine5(amendValidProperty(spreadsheet.getAddressLine5()));
    address.setPostCode(amendValidProperty(spreadsheet.getPostcode()));
    address.setCountryCode(
        amendValidProperty(getCountryCodeByCountryLegend(spreadsheet.getCountry())));
    return address;
  }

  private String convertAccessLevel(String spreadsheetAccessLevel) {
    if ((spreadsheetAccessLevel == null) || spreadsheetAccessLevel.isEmpty()) {
      return StringUtils.SPACE;
    }
    if (spreadsheetAccessLevel.equals(SpreadsheetAccessLevel.GUEST.getAccessLevel())) {
      return AccessLevel.STAYER.toString();
    } else if (spreadsheetAccessLevel.equals(SpreadsheetAccessLevel.SELF_BOOKER.getAccessLevel())) {
      return AccessLevel.SELF.toString();
    } else if (spreadsheetAccessLevel.equals(SpreadsheetAccessLevel.BOOKER.getAccessLevel())) {
      return AccessLevel.BOOKER.toString();
    } else if (spreadsheetAccessLevel
        .equals(SpreadsheetAccessLevel.TRAVEL_MANAGER.getAccessLevel())) {
      return AccessLevel.SUPER.toString();
    } else {
      throw new BulkEmployeeUploadValidationException(
          "Unrecognised access level: " + spreadsheetAccessLevel);
    }
  }

  private String amendValidProperty(String prop) {
    return prop != null ? prop : "";
  }

  private Boolean convertTextConfirmation(String textConfirmation) {
    if ((textConfirmation == null) || textConfirmation.isEmpty()) {
      return false;
    }
    return textConfirmation.equalsIgnoreCase("YES");
  }

  private String getCountryCodeByCountryLegend(String countryLegend) {
    if ((countryLegend == null) || countryLegend.isEmpty()) {
      return "";
    }
    Map<String, Country> countriesByLegend = countriesService.getCountriesByLegend();
    if (!countriesByLegend.containsKey(countryLegend)) {
      throw new BulkEmployeeUploadValidationException(
          "Unrecognised country legend: " + countryLegend);
    }
    return countriesByLegend.get(countryLegend).getCountryCode();
  }

  private boolean validateEmployee(EmployeeSpreadsheet employee, List<ValidationError> failedEmployees,
      int entryIndex) {
    boolean hasErrors = false;


    if (StringUtils.isBlank(employee.getEmailAddress())) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.EMAIL_IS_EMPTY.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    } else if (!employee.getEmailAddress().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.EMAIL_IS_INVALID.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    }
    if (StringUtils.isBlank(employee.getTitle())) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.TITLE_EMPTY.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    }
    if (StringUtils.isBlank(employee.getFirstName())) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.FIRST_NAME_EMPTY.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    }
    if (StringUtils.isBlank(employee.getLastName())) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.LAST_NAME_EMPTY.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    }
    if (StringUtils.isBlank(employee.getTextConfirmation())) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.TEXT_CONFIRMATION_EMPTY.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    }
    if (StringUtils.isBlank(employee.getAccessLevel())) {
      failedEmployees.add(
          new ValidationError(String.valueOf(ErrorCode.ACCESS_LEVEL_EMPTY.getCode()),
              String.valueOf(entryIndex)));
      hasErrors = true;
    }
    return hasErrors;
  }
}
