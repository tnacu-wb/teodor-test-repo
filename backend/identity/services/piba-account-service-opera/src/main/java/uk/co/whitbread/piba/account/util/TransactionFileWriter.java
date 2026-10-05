
package uk.co.whitbread.piba.account.util;

import com.opencsv.CSVWriter;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.exception.TransactionListXlsException;
import uk.co.whitbread.piba.account.model.Currency;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransaction;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransactionDetail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Component
public class TransactionFileWriter {
    String[] csvHeaderFields = new String[] {"Date/time", "Guest Name", "Card no.", "Transaction location",
            "Description", "Purchase order no.", "Customers own ref.", "Gross value", "Net value", "Tax value"};
    String[] xlsHeaderFields = {
        "Invoice Date", "Invoice No", "Transaction Date", "Guest Name", "Location",
        "Card Number", "Card Name", "Purchase Order Number", "Customer Own Reference",
        "Reservation Number", "Product", "Quantity", "Line Item Number", "Net Value",
        "VAT", "Gross Value", "Currency Code"
    };
    private final DateTimeFormatter invoiceDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter transactionDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private void writeTransactionRecord(final CSVWriter csvWriter, final CustomerAccountCardTransaction transaction) {
        String currencySymbol = transaction.getGrossAmount().getCurrencySymbol();

        List<String> csvRowFieldList = new ArrayList<>();
        String name = transaction.getLineItems().stream()
                .map(CustomerAccountCardTransactionDetail::getGuestName)
                .collect(Collectors.joining(", "));
        String description = transaction.getLineItems().stream()
                .map(CustomerAccountCardTransactionDetail::getDescription)
                .collect(Collectors.joining(", "));

        csvRowFieldList.add(transaction.getTransactionDate().toString());
        csvRowFieldList.add(name);
        csvRowFieldList.add(transaction.getPan());
        csvRowFieldList.add(transaction.getLocation());
        csvRowFieldList.add(description);
        csvRowFieldList.add(transaction.getPurchaseOrderReference());
        csvRowFieldList.add(transaction.getCustomerOwnRef());
        csvRowFieldList.add(currencySymbol + transaction.getGrossAmount().getAmount().toString());
        csvRowFieldList.add(currencySymbol + transaction.getNetAmount().getAmount().toString());
        csvRowFieldList.add(currencySymbol + transaction.getTaxAmount().getAmount().toString());
        csvWriter.writeNext(csvRowFieldList.toArray(new String[0]));
    }

    public void populateCsv(final CSVWriter csvWriter,
                            final List<CustomerAccountCardTransaction> transactions) throws IOException {
        csvWriter.writeNext(csvHeaderFields);
        emptyIfNull(transactions).forEach(transaction -> writeTransactionRecord(csvWriter, transaction));

        csvWriter.flush();
        csvWriter.close();
    }

    public void populateExcel(ByteArrayOutputStream transactionListOutputStream,
                              List<CustomerAccountCardTransaction> allTransactions,
                              LocalDate startDate, LocalDate endDate) {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            workbook.createInformationProperties();
            Sheet sheet = workbook.createSheet();
            createFirstHeaderRow(workbook, sheet, startDate, endDate);
            createSecondHeaderRow(workbook, sheet);

            // Sort transactions by card number and transaction date
            Optional.ofNullable(allTransactions)
                .ifPresent(transactions -> transactions.sort(
                    Comparator.comparing(CustomerAccountCardTransaction::getPan,
                            Comparator.nullsFirst(String::compareTo))
                        .thenComparing(CustomerAccountCardTransaction::getTransactionDate,
                            Comparator.nullsFirst(LocalDateTime::compareTo))
                ));

            // Populate data rows
            AtomicInteger rowIndex = new AtomicInteger(2);
            CellStyle numberCellStyle = createNumberCellStyle(workbook);
            Optional.ofNullable(allTransactions).ifPresent(transactions ->
                transactions.forEach(transaction -> {
                  if (transaction.getLineItems() != null && !transaction.getLineItems().isEmpty()) {
                    transaction.getLineItems()
                        .forEach(lineItem ->
                            buildRow(sheet.createRow(rowIndex.getAndIncrement()),
                                transaction, lineItem, numberCellStyle));
                  } else {
                    buildRow(sheet.createRow(rowIndex.getAndIncrement()), transaction, null, numberCellStyle);
                  }
                })
            );

            // Adjust column widths to fit the content
            for (int i = 0; i < xlsHeaderFields.length; i++) {
              sheet.autoSizeColumn(i);
            }
            workbook.write(transactionListOutputStream);
        } catch (IOException e) {
          throw new TransactionListXlsException("Failed to write transaction list to Excel file: " + e);
        }
    }

  private void buildRow(Row row, CustomerAccountCardTransaction transaction, CustomerAccountCardTransactionDetail item,
                        CellStyle numberCellStyle) {
    row.createCell(0).setCellValue(Optional.ofNullable(transaction.getInvoiceDate())
        .map(date -> date.format(invoiceDateFormatter)).orElse(""));
    row.createCell(1).setCellValue(transaction.getInvoiceNo());
    row.createCell(2).setCellValue(Optional.ofNullable(transaction.getTransactionDate())
        .map(date -> date.format(transactionDateFormatter)).orElse(""));
    row.createCell(4).setCellValue(transaction.getLocation());
    row.createCell(5).setCellValue(transaction.getPan());
    row.createCell(6).setCellValue(transaction.getCardName());
    row.createCell(7).setCellValue(transaction.getPurchaseOrderReference());
    row.createCell(8).setCellValue(transaction.getCustomerOwnRef());
    row.createCell(9).setCellValue(transaction.getSalesOrderNumber());
    if (item != null) {
      row.createCell(3).setCellValue(item.getGuestName());
      row.createCell(10).setCellValue(item.getDescription());
      row.createCell(11).setCellValue(item.getQuantity());
      row.createCell(12).setCellValue(item.getInvoiceLineItem());
      addFormattedNumber(row, 13, item.getNetAmount(), numberCellStyle);
      addFormattedNumber(row, 14, item.getTaxAmount(), numberCellStyle);
      addFormattedNumber(row, 15, item.getGrossAmount(), numberCellStyle);
      row.createCell(16).setCellValue(toCurrencyAbbreviation(item.getGrossAmount()));
    }
  }

  private void createSecondHeaderRow(Workbook workbook, Sheet sheet) {
    var secondHeaderFont = workbook.createFont();
    secondHeaderFont.setBold(true);
    var secondHeaderStyle = workbook.createCellStyle();
    secondHeaderStyle.setFont(secondHeaderFont);

    Row secondHeaderRow = sheet.createRow(1);
    for (int i = 0; i < xlsHeaderFields.length; i++) {
      var cell = secondHeaderRow.createCell(i);
      cell.setCellValue(xlsHeaderFields[i]);
      cell.setCellStyle(secondHeaderStyle);
    }
  }

  private void createFirstHeaderRow(Workbook workbook, Sheet sheet, LocalDate startDate, LocalDate endDate) {
    var firstHeaderFont = workbook.createFont();
    firstHeaderFont.setFontHeightInPoints((short) 13);
    firstHeaderFont.setBold(true);
    var firstHeaderStyle = workbook.createCellStyle();
    firstHeaderStyle.setFont(firstHeaderFont);

    Row firstHeaderRow = sheet.createRow(0);
    firstHeaderRow.createCell(0).setCellValue("Date Range");
    firstHeaderRow.createCell(1).setCellValue(Optional.ofNullable(startDate)
        .map(date -> date.format(transactionDateFormatter)).orElse(""));
    firstHeaderRow.createCell(3).setCellValue(Optional.ofNullable(endDate)
        .map(date -> date.format(transactionDateFormatter)).orElse(""));
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 2));
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));
    for (int i = 0; i <= 4; i++) {
      if (firstHeaderRow.getCell(i) != null) {
        firstHeaderRow.getCell(i).setCellStyle(firstHeaderStyle);
      }
    }
  }

  private CellStyle createNumberCellStyle(Workbook workbook) {
      CellStyle style = workbook.createCellStyle();
      DataFormat dataFormat = workbook.createDataFormat();
      style.setDataFormat(dataFormat.getFormat(AppConstants.NUMBER_FORMAT));
      return style;
    }

    private void addFormattedNumber(Row row, int cellNumber, Currency grossAmount, CellStyle numberCellStyle) {
      var cell = row.createCell(cellNumber);
      if (grossAmount == null || grossAmount.getAmount() == null) {
        cell.setCellValue("");
        return;
      }

      cell.setCellValue(grossAmount.getAmount().doubleValue());
      cell.setCellStyle(numberCellStyle);
    }

    private String toCurrencyAbbreviation(Currency currency) {
      if (currency == null || currency.getCurrencyCode() == null) {
        return Strings.EMPTY;
      }
      if (AppConstants.CURRENCY_CODE_GBP.equals(currency.getCurrencyCode())) {
        return AppConstants.POUND;
      } else if (AppConstants.CURRENCY_CODE_EUR.equals(currency.getCurrencyCode())) {
        return AppConstants.EURO;
      }
      return currency.getCurrencyCode();
    }

}

