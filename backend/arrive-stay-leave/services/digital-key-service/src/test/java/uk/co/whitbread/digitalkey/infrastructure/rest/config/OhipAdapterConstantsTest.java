package uk.co.whitbread.digitalkey.infrastructure.rest.config;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.config.OhipAdapterConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OhipAdapterConstantsTest {

  @Test
  void testExternalReferenceIdConstant() {
    assertEquals("externalReferenceId", OhipAdapterConstants.EXTERNAL_REFERENCE_ID,
        "EXTERNAL_REFERENCE_ID should match expected value");
  }
}
