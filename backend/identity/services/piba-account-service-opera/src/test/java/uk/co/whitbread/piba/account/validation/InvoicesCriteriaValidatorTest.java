package uk.co.whitbread.piba.account.validation;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceCriteria;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class InvoicesCriteriaValidatorTest {
    private final InvoicesCriteriaValidator objectUnderTest = new InvoicesCriteriaValidator();

    @Test
    void testInvoicesCriteriaIsValid() {
        CustomerAccountInvoiceCriteria invoiceCriteria = new CustomerAccountInvoiceCriteria();
        invoiceCriteria.setDateFrom(LocalDate.now().minusMonths(3));
        invoiceCriteria.setDateTo(LocalDate.now());
        assertTrue(objectUnderTest.isValid(invoiceCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsValidWithSameDates() {
        CustomerAccountInvoiceCriteria invoiceCriteria = new CustomerAccountInvoiceCriteria();
        invoiceCriteria.setDateFrom(LocalDate.now());
        invoiceCriteria.setDateTo(LocalDate.now());

        assertTrue(objectUnderTest.isValid(invoiceCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsInValidWhenDateFromIsBeforeDateTo() {
        CustomerAccountInvoiceCriteria invoiceCriteria = new CustomerAccountInvoiceCriteria();
        invoiceCriteria.setDateFrom(LocalDate.now());
        invoiceCriteria.setDateTo(LocalDate.now().minusMonths(1));

        assertFalse(objectUnderTest.isValid(invoiceCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsInValidWhenDateSearchIsNull() {
        assertTrue(objectUnderTest.isValid(null, null));
    }

    @Test
    void testTransactionCriteriaIsInValidWhenDateFromIsNull() {
        CustomerAccountInvoiceCriteria invoiceCriteria = new CustomerAccountInvoiceCriteria();
        invoiceCriteria.setDateFrom(null);
        invoiceCriteria.setDateTo(LocalDate.now());

        assertFalse(objectUnderTest.isValid(invoiceCriteria, null));
    }

    @Test
    void testTransactionCriteriaIsInValidWhenDateToIsNull() {
        CustomerAccountInvoiceCriteria invoiceCriteria = new CustomerAccountInvoiceCriteria();
        invoiceCriteria.setDateFrom(LocalDate.now().minusMonths(1));
        invoiceCriteria.setDateTo(null);

        assertFalse(objectUnderTest.isValid(invoiceCriteria, null));
    }
}
