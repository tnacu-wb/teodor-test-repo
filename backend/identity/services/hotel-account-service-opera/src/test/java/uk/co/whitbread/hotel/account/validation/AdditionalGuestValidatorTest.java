package uk.co.whitbread.hotel.account.validation;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.account.model.ContactDetail;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionalGuestValidatorTest {
    
    private final AdditionalGuestValidator additionalGuestValidator = new AdditionalGuestValidator();

    @Test
    void shouldBeValidWhenCountryOfIssueIsNull() {

        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.getPassport().setCountryOfIssue(null);
        assertTrue(additionalGuestValidator.isValid(contactDetail, null));
    }

    @Test
    void shouldBeValidWhenCountryOfIssueISGreaterThan3Characters() {

        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.getPassport().setCountryOfIssue("United Kingdom");
        assertTrue(additionalGuestValidator.isValid(contactDetail, null));
    }

    @Test
    void shouldNotBeValidWhenCountryOfIssueLenghthIsLessThan3Characters() {

        ContactDetail contactDetail = random(ContactDetail.class);
        contactDetail.getPassport().setCountryOfIssue("UK");
        assertFalse(additionalGuestValidator.isValid(contactDetail, null));
    }
}
