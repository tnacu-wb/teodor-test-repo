package uk.co.whitbread.wallet.utils;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import uk.co.whitbread.wallet.domain.properties.WalletProperties.PassStyle;


import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class TestUtils {

  public static PassStyle pi() {
    PassStyle ps = new PassStyle();
    ps.setForegroundColor("fpi");
    ps.setBackgroundColor("bpi");
    ps.setLabelColor("lpi");
    return ps;
  }

  public static PassStyle bb() {
    PassStyle ps = new PassStyle();
    ps.setForegroundColor("fbb");
    ps.setBackgroundColor("bbb");
    ps.setLabelColor("lbb");
    return ps;
  }

  public static PassStyle hub() {
    PassStyle ps = new PassStyle();
    ps.setForegroundColor("fh");
    ps.setBackgroundColor("bh");
    ps.setLabelColor("lh");
    return ps;
  }

  public static PassStyle zip() {
    PassStyle ps = new PassStyle();
    ps.setForegroundColor("fz");
    ps.setBackgroundColor("bz");
    ps.setLabelColor("lz");
    return ps;
  }

  public static void checkErrorThrown(Executable executable, String[] errors) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    Arrays.stream(errors)
        .forEach(error -> Assertions.assertTrue(thrownException.getMessage().contains(error)));
  }

}
