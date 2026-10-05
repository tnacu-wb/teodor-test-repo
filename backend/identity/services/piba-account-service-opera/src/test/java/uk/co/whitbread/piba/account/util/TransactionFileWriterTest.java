package uk.co.whitbread.piba.account.util;

import com.opencsv.CSVWriter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.model.Currency;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransaction;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransactionDetail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionFileWriterTest {
    private static final LocalDateTime TRANSACTION_DATE = LocalDateTime.now();
    private static final String[] EXPECTED_FILE_HEADERS =
            {"Date/time", "Guest Name", "Card no.", "Transaction location",
                    "Description", "Purchase order no.", "Customers own ref.", "Gross value", "Net value", "Tax value"};


    @Mock
    private CSVWriter csvWriter;

    @Captor
    private ArgumentCaptor<String[]> argumentCaptorForFileHeader;

    private TransactionFileWriter transactionFileWriter = new TransactionFileWriter();


    @Test
    void testPopulateCsv_forNullList() throws IOException {

        // WHEN
        transactionFileWriter.populateCsv(csvWriter, null);

        // THEN
        verify(csvWriter).writeNext(argumentCaptorForFileHeader.capture());
        List<String[]> fileRecords = argumentCaptorForFileHeader.getAllValues();
        assertEquals(1, fileRecords.size());
        assertArrayEquals(EXPECTED_FILE_HEADERS, fileRecords.get(0));
    }

    @Test
    void testPopulateCsv_forListOfTransactions() throws IOException {
        List<CustomerAccountCardTransactionDetail> lineItems = new ArrayList<>();
        lineItems.add(createLineItem("Mr First User","Daily Record - Saturday Premier Breakfast"));
        lineItems.add(createLineItem("Mr Second User","Food and Drinks"));

        CustomerAccountCardTransaction transaction1 = createTransaction(143.60,  137.00,6.60, lineItems);
        CustomerAccountCardTransaction transaction2 = createTransaction(301.10,287.00,  14.10, lineItems);
        CustomerAccountCardTransaction transaction3 = createTransaction(281.30,267.00,  14.30, lineItems);
        // WHEN
        transactionFileWriter.populateCsv(csvWriter, asList(transaction1, transaction2, transaction3));

        // THEN
        verify(csvWriter, times(4)).writeNext(argumentCaptorForFileHeader.capture());
        List<String[]> fileRecords = argumentCaptorForFileHeader.getAllValues();
        assertEquals(4, fileRecords.size());

        assertArrayEquals(EXPECTED_FILE_HEADERS, fileRecords.get(0));

        verifyTransactionRecord(transaction1, fileRecords.get(1));
        verifyTransactionRecord(transaction2, fileRecords.get(2));
        verifyTransactionRecord(transaction3, fileRecords.get(3));
    }

    @Test
    void testPopulateExcel() throws Exception {
        // Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        CustomerAccountCardTransaction transaction =
            getCustomerAccountCardTransaction(List.of(getCustomerAccountCardTransactionDetail("Room Service")));

        // When
        transactionFileWriter.populateExcel(outputStream, new ArrayList<>(Arrays.asList(transaction)), startDate, endDate);

        // Then
        try (Workbook workbook = new HSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Verify header row
            assertEquals("Date Range", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("01.01.2023", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("31.12.2023", sheet.getRow(0).getCell(3).getStringCellValue());

            // Verify data row
            assertEquals("15/06/2023", sheet.getRow(2).getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("123", sheet.getRow(2).getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("14.06.2023", sheet.getRow(2).getCell(2, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("John Doe", sheet.getRow(2).getCell(3, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("London", sheet.getRow(2).getCell(4, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("12345678****3456", sheet.getRow(2).getCell(5, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("Visa", sheet.getRow(2).getCell(6, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("PO123", sheet.getRow(2).getCell(7, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("", sheet.getRow(2).getCell(8, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("SO123", sheet.getRow(2).getCell(9, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("Room Service", sheet.getRow(2).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals(2, sheet.getRow(2).getCell(11, CREATE_NULL_AS_BLANK).getNumericCellValue());
            assertEquals(1, sheet.getRow(2).getCell(12, CREATE_NULL_AS_BLANK).getNumericCellValue());
            assertEquals(100, sheet.getRow(2).getCell(13, CREATE_NULL_AS_BLANK).getNumericCellValue());
            assertEquals(20, sheet.getRow(2).getCell(14, CREATE_NULL_AS_BLANK).getNumericCellValue());
            assertEquals(120, sheet.getRow(2).getCell(15, CREATE_NULL_AS_BLANK).getNumericCellValue());
            assertEquals("GBP", sheet.getRow(2).getCell(16, CREATE_NULL_AS_BLANK).getStringCellValue());
        }
    }

    @Test
    void testCreateInformationProperties() {
        // Given
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            // When
            workbook.createInformationProperties();

            // Then
            assertNotNull(workbook.getSummaryInformation(), "SummaryInformation should not be null");
            assertNotNull(workbook.getDocumentSummaryInformation(), "DocumentSummaryInformation should not be null");
        } catch (IOException e) {
            fail("Exception occurred while creating HSSFWorkbook: " + e.getMessage());
        }
    }

    @Test
    void testPopulateExcel_transactionsSortedByCardAndTransactionDate() throws Exception {
        // Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        CustomerAccountCardTransaction shouldBeMiddle =
            getCustomerAccountCardTransaction(List.of(getCustomerAccountCardTransactionDetail("Should be middle")));
        CustomerAccountCardTransaction shouldBeLast =
            getCustomerAccountCardTransaction(List.of(getCustomerAccountCardTransactionDetail("Should be last")));
        shouldBeLast.setPan("87654321****1234");
        CustomerAccountCardTransaction shouldBeFirst =
            getCustomerAccountCardTransaction(List.of(getCustomerAccountCardTransactionDetail("Should be first")));
        shouldBeFirst.setTransactionDate(shouldBeMiddle.getTransactionDate().minusDays(1));

        // When
        transactionFileWriter.populateExcel(outputStream, new ArrayList<>(Arrays.asList(shouldBeMiddle, shouldBeLast, shouldBeFirst)), startDate, endDate);

        // Then
        try (Workbook workbook = new HSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Assert the number of populated rows
            int expectedRows = 5; // 2 header rows + transaction rows
            assertEquals(expectedRows, sheet.getLastRowNum() + 1);

            assertEquals("Should be first", sheet.getRow(2).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("Should be middle", sheet.getRow(3).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("Should be last", sheet.getRow(4).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
        }
    }

    @Test
    void testPopulateExcel_oneRowPerLineItemAndOneRowForZeroLineItems() throws Exception {
        // Given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        CustomerAccountCardTransaction transactionWithThreeLineItems =
            getCustomerAccountCardTransaction(List.of(
                getCustomerAccountCardTransactionDetail("Line item 1"),
                getCustomerAccountCardTransactionDetail("Line item 2"),
                getCustomerAccountCardTransactionDetail("Line item 3")
            ));

        CustomerAccountCardTransaction transactionNoLineItems =
            getCustomerAccountCardTransaction(List.of());

        // When
        transactionFileWriter.populateExcel(outputStream, new ArrayList<>(Arrays.asList(transactionWithThreeLineItems, transactionNoLineItems)), startDate, endDate);

        // Then
        try (Workbook workbook = new HSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            Sheet sheet = workbook.getSheetAt(0);

            // Assert the number of populated rows
            int expectedRows = 6; // 2 header rows + transaction rows
            assertEquals(expectedRows, sheet.getLastRowNum() + 1);

            assertEquals("Line item 1", sheet.getRow(2).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("Line item 2", sheet.getRow(3).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("Line item 3", sheet.getRow(4).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
            assertEquals("", sheet.getRow(5).getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());
        }
    }

    @NotNull
    private static CustomerAccountCardTransaction getCustomerAccountCardTransaction(List<CustomerAccountCardTransactionDetail> lineItems) {
        CustomerAccountCardTransaction transaction = new CustomerAccountCardTransaction();
        transaction.setInvoiceDate(LocalDate.of(2023, 6, 15));
        transaction.setInvoiceNo("123");
        transaction.setTransactionDate(LocalDateTime.of(2023, 6, 14,2,2,2));
        transaction.setLineItems(lineItems);
        transaction.setLocation("London");
        transaction.setPan("12345678****3456");
        transaction.setCardName("Visa");
        transaction.setPurchaseOrderReference("PO123");
        transaction.setCustomerOwnRef(null);
        transaction.setSalesOrderNumber("SO123");
        return transaction;
    }

    @NotNull
    private static CustomerAccountCardTransactionDetail getCustomerAccountCardTransactionDetail(String description) {
        CustomerAccountCardTransactionDetail detail = new CustomerAccountCardTransactionDetail();
        detail.setGuestName("John Doe");
        detail.setDescription(description);
        detail.setQuantity(2);
        detail.setInvoiceLineItem(1);
        detail.setNetAmount(new Currency(BigDecimal.valueOf(100), "826", "£"));
        detail.setTaxAmount(new Currency(BigDecimal.valueOf(20), "826", "£"));
        detail.setGrossAmount(new Currency(BigDecimal.valueOf(120), "826", "£"));

        return detail;
    }

    private CustomerAccountCardTransactionDetail createLineItem(String guestName, String description) {
        CustomerAccountCardTransactionDetail transactionDetail  = new CustomerAccountCardTransactionDetail();
        transactionDetail.setGuestName(guestName);
        transactionDetail.setDescription(description);
        return transactionDetail;
    }

    private void verifyTransactionRecord(final CustomerAccountCardTransaction transaction, final String[] recordFields) {
        String currencySymbol  = transaction.getGrossAmount().getCurrencySymbol();

        assertEquals(transaction.getTransactionDate().toString(), recordFields[0]);
        assertEquals("Mr First User, Mr Second User", recordFields[1]);
        assertEquals(transaction.getPan(), recordFields[2]);
        assertEquals(transaction.getLocation(), recordFields[3]);
        assertEquals("Daily Record - Saturday Premier Breakfast, Food and Drinks", recordFields[4]);
        assertEquals(transaction.getPurchaseOrderReference(), recordFields[5]);
        assertEquals(transaction.getCustomerOwnRef(), recordFields[6]);
        assertEquals(currencySymbol + transaction.getGrossAmount().getAmount().toString(), recordFields[7]);
        assertEquals(currencySymbol + transaction.getNetAmount().getAmount().toString(), recordFields[8]);
        assertEquals(currencySymbol + transaction.getTaxAmount().getAmount().toString(), recordFields[9]);
    }

    private CustomerAccountCardTransaction createTransaction(double grossAmount, double netAmount, double taxAmount,
                                                             List<CustomerAccountCardTransactionDetail> lineItems) {
        CustomerAccountCardTransaction transaction = new CustomerAccountCardTransaction();
        transaction.setTransactionDate(TRANSACTION_DATE);
        transaction.setPan("30895001*******0137");
        transaction.setLocation("Jersey St Helier (Charing Cros");
        transaction.setPurchaseOrderReference("PRef-10");
        transaction.setCustomerOwnRef("OWNREF-10");
        transaction.setGrossAmount(new Currency(BigDecimal.valueOf(grossAmount), "GBP", "£"));
        transaction.setNetAmount(new Currency(BigDecimal.valueOf(netAmount), "GBP", "£"));
        transaction.setTaxAmount(new Currency(BigDecimal.valueOf(taxAmount), "GBP", "£"));
        transaction.setLineItems(lineItems);
        return transaction;
    }
}