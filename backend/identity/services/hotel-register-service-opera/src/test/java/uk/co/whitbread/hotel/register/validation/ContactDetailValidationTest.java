package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.ContactDetail;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ContactDetailValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldReturnAListOfErrorsWhenAnEmptyTitleIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.setTitle(null);

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    public void shouldReturnAListOfErrorsWhenANullAddressIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.setAddress(null);

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    public void shouldReturnANonEmptyListWhenAnInvalidEmailValueIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
        assertThat(validationMessages, hasItem("email must be a well-formed email address"));
    }

    @Test
    public void shouldReturnANonEmptyListWhenAnInvalidTelephoneAndMobilePhoneValueIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, hasItem("telephone not a well-formed telephone/mobile number"));
        assertThat(validationMessages, hasItem("mobile not a well-formed telephone/mobile number"));
    }

    @Test
    public void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.setEmail("jdoe@example.com");
        contactDetail.setTelephone("+420 123 456 789");
        contactDetail.setMobile("444-555-1234");

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }
}
