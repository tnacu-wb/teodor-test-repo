package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class UpsellItemTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateUppselItem() {
        //Given
        UpsellItem upsellItem = new UpsellItem();

        //When
        List<String> validationMessages = validator.validate(upsellItem).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(4));
        assertThat(validationMessages, hasItem("roomNumber must not be empty"));
        assertThat(validationMessages, hasItem("postingDate must not be null"));
        assertThat(validationMessages, hasItem("quantity must not be null"));
        assertThat(validationMessages, hasItem("code must not be empty"));
    }
}