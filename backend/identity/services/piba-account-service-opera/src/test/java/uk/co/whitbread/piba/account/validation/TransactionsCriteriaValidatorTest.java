package uk.co.whitbread.piba.account.validation;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByDateCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByInvoiceNumberCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsCriteria;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TransactionsCriteriaValidatorTest {
    private final TransactionsCriteriaValidator objectUnderTest = new TransactionsCriteriaValidator();

    CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria = new CustomerAccountTransactionsCriteria();

    @Test
    void testTransactionCriteriaIsValidWhenInvoiceNumberSearchIsNotNull() {
        CustomerAccountTransactionsByInvoiceNumberCriteria invoiceNumberSearch = new CustomerAccountTransactionsByInvoiceNumberCriteria();
        invoiceNumberSearch.setInvoiceNumber(5);
        customerAccountTransactionsCriteria.setInvoiceNumberSearch(invoiceNumberSearch);

        assertTrue(objectUnderTest.isValid(customerAccountTransactionsCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsValidWhenDateSearchIsNotNull() {
        CustomerAccountTransactionsByDateCriteria dateSearch = new CustomerAccountTransactionsByDateCriteria();
        dateSearch.setDateFrom(LocalDate.now());
        dateSearch.setDateTo(LocalDate.now());
        customerAccountTransactionsCriteria.setDateSearch(dateSearch);

        assertTrue(objectUnderTest.isValid(customerAccountTransactionsCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsNotValidWhenDateSearchAndInvoiceNumberSearchIsNotNull() {
        CustomerAccountTransactionsByDateCriteria dateSearch = new CustomerAccountTransactionsByDateCriteria();
        dateSearch.setDateFrom(LocalDate.now());
        dateSearch.setDateTo(LocalDate.now());
        customerAccountTransactionsCriteria.setDateSearch(dateSearch);
        CustomerAccountTransactionsByInvoiceNumberCriteria invoiceNumberSearch = new CustomerAccountTransactionsByInvoiceNumberCriteria();
        invoiceNumberSearch.setInvoiceNumber(5);
        customerAccountTransactionsCriteria.setInvoiceNumberSearch(invoiceNumberSearch);

        assertFalse(objectUnderTest.isValid(customerAccountTransactionsCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsNotValidWhenDateSearchAndInvoiceNumberSearchIsNull() {
        assertFalse(objectUnderTest.isValid(customerAccountTransactionsCriteria, null));
    }
}
