package uk.co.whitbread.promo.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;

class PromoBatchRequestSelfValidationTest {

    @Test
    void validateSelf_shouldNotThrow_whenNoConstraintsAreDefined() {
        PromoBatchRequest request = PromoBatchRequest.builder().build();

        Assertions.assertDoesNotThrow(request::validateSelf);
    }
}
