package uk.co.whitbread.piba.account.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsCriteria;


public class TransactionsCriteriaValidator implements ConstraintValidator<TransactionsCriteriaConstraint, CustomerAccountTransactionsCriteria> {
    @Override
    public void initialize(TransactionsCriteriaConstraint constraintAnnotation) {
        //Nothing needs to be done here
    }

    @Override
    public boolean isValid(CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria, ConstraintValidatorContext constraintValidatorContext) {
        return (customerAccountTransactionsCriteria == null
                || ((customerAccountTransactionsCriteria.getDateSearch() != null || customerAccountTransactionsCriteria.getInvoiceNumberSearch() != null)
                && !(customerAccountTransactionsCriteria.getDateSearch() != null && customerAccountTransactionsCriteria.getInvoiceNumberSearch() != null)));
    }
}
