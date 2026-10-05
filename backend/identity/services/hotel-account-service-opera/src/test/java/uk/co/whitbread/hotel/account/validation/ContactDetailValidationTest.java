package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.model.ContactDetail;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ContactDetailValidationTest {

    @Autowired
    private Validator validator;

    private static final String EMAIL_ADDRESS = "test@example.com";
    private static final String LANDLINE = "+44777777777";
    private static final String MOBILE = "+44777777777";

    @Test
    void shouldReturnAListOfErrorsWhenAnEmptyTitleIsProvided() {
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
    void shouldReturnAListOfErrorsWhenANullAddressIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    void shouldReturnANonEmptyListWhenAnInvalidEmailValueIsProvided() {
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
    void shouldReturnANonEmptyListWhenAnInvalidTelephoneAndMobilePhoneValueIsProvided() {
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
    void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.setEmail("jdoe@example.com");
        contactDetail.setTelephone("+420 123 456 789");
        contactDetail.setMobile("444-555-1234");
        contactDetail.setNationality("GB");
        contactDetail.getAddress().setCountryCode("GB");

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }

    @Test
    void shouldReturnANonEmptyListWhenNoPostcodeProvidedForCountryCodeGB() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.getAddress().setPostCode(null);
        contactDetail.getAddress().setCountryCode("GB");

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    void shouldReturnANonEmptyListWhenNoPostcodeProvidedForCountryCodeDE() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.getAddress().setPostCode(null);
        contactDetail.getAddress().setCountryCode("DE");

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    void shouldReturnANonEmptyListWhenCountryCodeNotProvided() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.getAddress().setCountryCode(null);

        //When
        List<String> validationMessages = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    void shouldAcceptEmptyStringForNationality() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.setNationality("");
        contactDetail.setTelephone(LANDLINE);
        contactDetail.setMobile(MOBILE);
        contactDetail.setEmail(EMAIL_ADDRESS);
        contactDetail.getAddress().setCountryCode("GB");

        //When
        List<String> validationMessage = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());
        //Then
        assertThat(validationMessage, is(empty()));
    }

    @Test
    void shouldReturnErrorMessageAboutTheMaximumStringSizeForNationality() {
        //Given
        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.setNationality("USSA");

        //When
        List<String> validationMessage = validator.validate(contactDetail)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .toList();
        //Then
        assertThat(validationMessage, is(not(empty())));
        assertThat(validationMessage, hasItem("nationality size must be between 0 and 3"));
    }
}