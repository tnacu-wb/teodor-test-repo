package uk.co.whitbread.wallet.domain.model.in;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.wallet.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.wallet.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

 class WalletRequestTest {

    private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
            Validation.buildDefaultValidatorFactory().getValidator()
    );

    @Test
    void verifyFields() {
        assertDoesNotThrow(() -> WalletRequest.builder()
                .reservationNumber("AKU5411146")
                .arrivalDate("2024-10-01")
                .lastName("Test")
                .language("en")
                .country("gb")
                .channel("PI")
                .build());
    }

    @Test
    void verifyErrorMessageWhenReservationNumberFieldIsNotSet() {
        String[] errors = {"reservationNumber: must not be null"};
        TestUtils.checkErrorThrown(() ->
                WalletRequest.builder()
                .arrivalDate("2024-10-01")
                .lastName("Test")
                .language("en")
                .country("gb")
                .channel("PI")
                .build(), errors);
    }

    @Test
    void verifyErrorMessageWhenLastNameFieldIsNotSet() {
        String[] errors = {"lastName: must not be null"};
        TestUtils.checkErrorThrown(() ->
                WalletRequest.builder()
                        .reservationNumber("AKU5411146")
                        .arrivalDate("2024-10-01")
                        .language("en")
                        .country("gb")
                        .channel("PI")
                        .build(), errors);
    }

    @Test
    void verifyErrorMessageWhenArrivalDateFieldIsNotSet() {
        String[] errors = {"arrivalDate: must not be null"};
        TestUtils.checkErrorThrown(() ->
                WalletRequest.builder()
                        .reservationNumber("AKU5411146")
                        .lastName("Test")
                        .language("en")
                        .country("gb")
                        .channel("PI")
                        .build(), errors);
    }
}
