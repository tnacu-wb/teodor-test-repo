package uk.co.whitbread.company.infrastructure.rest.client.company.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import uk.co.whitbread.company.domain.model.in.EmergencyReportRequest;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.infrastructure.rest.client.company.exceptions.GenerateReportException;
import uk.co.whitbread.company.infrastructure.rest.client.company.exceptions.RecordsNotFoundException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdhReportClient {

  public static final String[] MANAGEMENT_INFORMATION_REPORT_COLUMNS_EN = {"Booking Reference",
      "Booking Status",
      "Total Cost (inc VAT)", "Total Cost (exc VAT)", "Room Cost (inc VAT)", "Room Cost (exc VAT)",
      "Additional Costs (inc VAT)",
      "Additional Costs (exc VAT)", "Number of Rooms", "Number of Nights", "Number of Adults",
      "Number of Children", "Date Of Booking", "Check In Date", "Check Out Date",
      "Lead Time (Days)", "Rate Type", "Company", "Card Type", "Masked Card No.", "Hotel",
      "Booker First Name",
      "Booker Last Name", "Lead Guest First Name", "Lead Guest Last Name", "Booker Email Address",
      "Booker Phone Number", "Guest Email Address", "Guest Phone Number",
      "Purchase Order Answer", "Customer Reference Answer"};

  public static final String[] MANAGEMENT_INFORMATION_REPORT_COLUMNS_DE = {"Buchungsnummer", "Buchungsstatus",
      "Gesamtpreis (inkl. MwSt.)", "Gesamtpreis (exkl. MwSt.)", "Zimmerpreis (inkl. MwSt.)",
      "Zimmerpreis (exkl. MwSt.)", "Zusätzliche Kosten (inkl. MwSt.)",
      "Zusätzliche Kosten (exkl. MwSt.)", "Zimmer", "Nächte", "Erwachsene",
      "Kinder", "Buchungsdatum", "Anreisedatum", "Abreisedatum",
      "Vorlaufzeit (Tage)", "Tarifart", "Unternehmen", "Kartentyp", "Maskierte Kartennummer", "Hotel",
      "Vorname des Buchenden",
      "Nachname des Buchenden", "Vorname des Hauptgastes", "Nachname des Hauptgastes", "E-Mail-Adresse des Buchenden",
      "Telefonnummer des Buchenden", "E-Mail-Adresse des Gastes", "Telefonnummer des Gastes",
      "Purchase Order Answer", "Customer Reference Answer"};

  public static final String[] EMERGENCY_REPORT_COLUMNS_EN = {
      "Booking ID", "Hotel Area", "Hotel Name", "Hotel Postcode", "Hotel Phone Number",
      "Arrival Date", "Departure Date", "Number of Adults", "Number of Children",
      "Guest One First Name", "Guest One Surname",
      "Guest Email", "Guest Phone Number", "Booker Email", "Booker Phone Number", "Booker Mobile",
      "Guest Status"
  };

  public static final String[] EMERGENCY_REPORT_COLUMNS_DE = {
      "Buchungsreferenz", "Hotelregion", "Hotelname", "Postleitzahl des Hotels",
      "Telefonnummer des Hotels", "Anreisedatum", "Abreisedatum", "Anzahl Erwachsene",
      "Anzahl Kinder", "Gast 1 Vorname", "Gast 1 Nachname",
      "E-Mail-Adresse des Gastes", "Telefonnummer des Gastes", "E-Mail-Adresse des Buchenden",
      "Telefonnummer des Buchenden", "Mobilnummer des Buchenden", "Gaststatus"
  };

  private static final String FAILED_TO_WRITE_TO_REPORT_FILE = "Failed to write to report file: ";
  public static final String DATE_FIELD_FORMAT = "dd-MM-yyyy";
  private static final String MI_REPORT = "MI_Report_";

  private static final String EMERGENCY_REPORT = "Emergency_Report_";
  private static final String WITH_QNA = "_WithQnA";
  private static final String WITHOUT_QNA = "_WithoutQnA";
  private static final String UNDERSCORE = "_";
  private static final String NO_RECORDS_FOUND = "No records found between for company ";
  private static final String WITH_REQUIREMENTS = " with the following requirements: ";

  public ByteArrayOutputStream retrieveSpreadsheet(List<List<String>> reports, String[] columns,
      ManagementInformationRequest request) {
    ByteArrayOutputStream byteArrayOutputStream;
    try {
      byteArrayOutputStream = populateSpreadSheet(reports, columns, request);
      return byteArrayOutputStream;
    } catch (IOException ioe) {
      throw new GenerateReportException(FAILED_TO_WRITE_TO_REPORT_FILE + ioe);
    }
  }

  public ByteArrayOutputStream retrieveEmergencySpreadsheet(List<List<String>> reports,
      String[] columns,
      EmergencyReportRequest request) {
    ByteArrayOutputStream byteArrayOutputStream;
    try {
      byteArrayOutputStream = populateSpreadSheet(reports, columns, request);
      return byteArrayOutputStream;
    } catch (IOException ioe) {
      throw new GenerateReportException(FAILED_TO_WRITE_TO_REPORT_FILE + ioe);
    }
  }

  private ByteArrayOutputStream populateSpreadSheet(List<List<String>> reports, String[] columns,
      ManagementInformationRequest request) throws IOException {
    int rowCount = 0;
    int headerCellCount = 0;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (Workbook workbook = new HSSFWorkbook()) {
      Sheet sheet = workbook.createSheet(generateDatedFileName(request));
      Row row = sheet.createRow(rowCount++);
      for (String column : columns) {
        row.createCell(headerCellCount++).setCellValue(column);
      }
      for (int column = 0; column < columns.length; column++) {
        row.createCell(column).setCellValue(columns[column]);
      }
      for (List<String> report : reports) {
        writeReportRecord(sheet, report, rowCount++);
      }
      workbook.write(byteArrayOutputStream);
      byteArrayOutputStream.close();
    }
    return byteArrayOutputStream;
  }

  private ByteArrayOutputStream populateSpreadSheet(List<List<String>> reports, String[] columns,
      EmergencyReportRequest request) throws IOException {
    int rowCount = 0;
    int headerCellCount = 0;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (Workbook workbook = new HSSFWorkbook()) {
      Sheet sheet = workbook.createSheet(generateDatedFileNameForEmergency(request));
      Row row = sheet.createRow(rowCount++);
      for (String column : columns) {
        row.createCell(headerCellCount++).setCellValue(column);
      }
      for (int column = 0; column < columns.length; column++) {
        row.createCell(column).setCellValue(columns[column]);
      }
      for (List<String> report : reports) {
        writeReportRecord(sheet, report, rowCount++);
      }
      workbook.write(byteArrayOutputStream);
      byteArrayOutputStream.close();
    }
    return byteArrayOutputStream;
  }

  private void writeReportRecord(final Sheet sheet, final List<String> report, int rowCount) {
    int i = 0;
    Row row = sheet.createRow(rowCount);

    for (String item : report) {
      row.createCell(i++).setCellValue(item);
    }
  }

  public static String generateDatedFileName(final ManagementInformationRequest request) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FIELD_FORMAT);
    LocalDate fromDate = LocalDate.parse(request.fromDate(), DateTimeFormatter.ISO_DATE);
    LocalDate toDate = LocalDate.parse(request.toDate(), DateTimeFormatter.ISO_DATE);
    return Boolean.TRUE.equals(request.showQnAcolumns()) ? MI_REPORT + fromDate.format(formatter)
        + UNDERSCORE + toDate.format(formatter) + WITH_QNA :
        MI_REPORT + fromDate.format(formatter) + UNDERSCORE + toDate.format(formatter)
            + WITHOUT_QNA;
  }

  public static String generateDatedFileNameForEmergency(final EmergencyReportRequest request) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FIELD_FORMAT);
    LocalDate fromDate = LocalDate.parse(request.fromDate(), DateTimeFormatter.ISO_DATE);
    LocalDate toDate = LocalDate.parse(request.toDate(), DateTimeFormatter.ISO_DATE);
    return EMERGENCY_REPORT + fromDate.format(formatter) + UNDERSCORE + toDate.format(formatter)
        + ".xls";
  }

  public static RecordsNotFoundException generateNoRecordsFoundException(String companyId,
      Object errorMessageRequirements) {
    String noReportsMessage = NO_RECORDS_FOUND + companyId;

    if (errorMessageRequirements != null) {
      noReportsMessage += WITH_REQUIREMENTS + errorMessageRequirements;
    }

    log.info(noReportsMessage);
    return new RecordsNotFoundException(noReportsMessage);
  }

}
