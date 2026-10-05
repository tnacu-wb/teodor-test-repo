package uk.co.whitbread.piba.account.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceCriteria;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class InvoicesCriteriaValidator implements ConstraintValidator<InvoicesCriteriaConstraint, CustomerAccountInvoiceCriteria> {
    @Override
    public void initialize(InvoicesCriteriaConstraint constraintAnnotation) {
    //Nothing needs to be done here
    }

    @Override
    public boolean isValid(CustomerAccountInvoiceCriteria customerAccountInvoiceCriteria, ConstraintValidatorContext constraintValidatorContext) {
        Optional<CustomerAccountInvoiceCriteria> optInvoiceCriteria = ofNullable(customerAccountInvoiceCriteria);
        return optInvoiceCriteria.filter(t -> nonNull(t.getDateFrom()))
                .filter(t -> nonNull(t.getDateTo()))
                .filter(t -> !t.getDateFrom().isAfter(t.getDateTo()))
                .map(t -> true).orElseGet(optInvoiceCriteria::isEmpty);
    }
}
