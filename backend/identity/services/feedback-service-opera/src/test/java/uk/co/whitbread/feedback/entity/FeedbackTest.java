package uk.co.whitbread.feedback.entity;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.feedback.model.Feedback;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

class FeedbackTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateGuest() {
        //Given
        Feedback feedback = new Feedback();

        //When
        List<String> validationMessages = validator.validate(feedback).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(12));
        assertThat(validationMessages, hasItem("title must not be blank"));
        assertThat(validationMessages, hasItem("whb_summary must not be blank"));
        assertThat(validationMessages, hasItem("whb_wfcontactnumber must not be blank"));
        assertThat(validationMessages, hasItem("whb_wffirstname must not be blank"));
        assertThat(validationMessages, hasItem("whb_wfreasonforfeedback must not be blank"));
        assertThat(validationMessages, hasItem("whb_wfemailaddress must not be blank"));
        assertThat(validationMessages, hasItem("whb_wflastname must not be blank"));
        assertThat(validationMessages, hasItem("whb_wfpostcode must not be blank"));
        assertThat(validationMessages, hasItem("whb_reasonforcontact must not be null"));
        assertThat(validationMessages, hasItem("whb_wffeedbacktype must not be null"));
        assertThat(validationMessages, hasItem("whb_contacttype must not be null"));
        assertThat(validationMessages, hasItem("whb_wfididntbookthroughpremierinncom must not be null"));
    }

}
