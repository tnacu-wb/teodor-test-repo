package uk.co.whitbread.wallet.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.wallet.domain.exception.WalletCreationException;
import uk.co.whitbread.wallet.domain.utils.TemplateUtils;

class TemplateUtilsTest {

  @SneakyThrows
  @ParameterizedTest
  @ValueSource(strings = {"PI", "BB", "HUB", "ZIP", "PIGE"})
  void testGetPkPassTemplate_success(String input) {
    var result = TemplateUtils.getPkPassTemplate("template", input);
    assertNotNull(result);
    assertEquals(8, result.getAllFiles().size());
  }

  @SneakyThrows
  @ParameterizedTest
  @ValueSource(strings = {"PI", "BB", "HUB", "ZIP", "PIGE", ""})
  void testGetPkPassTemplateWrongDir_exception(String input) {
    assertThrowsExactly(WalletCreationException.class,
        () -> TemplateUtils.getPkPassTemplate("tmp", input));
  }

  @ParameterizedTest
  @ValueSource(strings = {"12:00", "15:00"})
  void testGetCheckInPMTime(String input) {
    var resultCheckIn = TemplateUtils.getTime(LocalDate.now(), input);
    assertTrue(resultCheckIn.toLowerCase().endsWith("pm"));
  }

  @Test
  void testGetCheckInAMTime() {
    var resultCheckIn = TemplateUtils.getTime(LocalDate.now(), "11:00");
    assertTrue(resultCheckIn.toLowerCase().endsWith("am"));
  }
}