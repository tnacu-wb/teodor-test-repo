package uk.co.whitbread.company.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

class CompanyDetailsRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateCompanyDetailsRequest() {
        //Given
        CompanyDetailsRequest companyDetailsRequest = new CompanyDetailsRequest();

        //When
        List<String> validationMessages = validator.validate(companyDetailsRequest).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(2));
        assertThat(validationMessages, hasItem("companyId must not be null"));
        assertThat(validationMessages, hasItem("sessionId must not be empty"));
    }
}
