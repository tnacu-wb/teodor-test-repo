package uk.co.whitbread.company.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

class CheckCompanyRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateLinkCriteria() {
        //Given
        CheckCompanyRequest searchCompanyRequest = new CheckCompanyRequest();

        //When
        List<String> validationMessages = validator.validate(searchCompanyRequest).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(2));
        assertThat(validationMessages, hasItem("companyName must not be empty"));
        assertThat(validationMessages, hasItem("address must not be null"));
    }
}
