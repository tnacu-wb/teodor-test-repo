package uk.co.whitbread.wallet.utils.passkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.wallet.utils.TestUtils.bb;
import static uk.co.whitbread.wallet.utils.TestUtils.hub;
import static uk.co.whitbread.wallet.utils.TestUtils.pi;
import static uk.co.whitbread.wallet.utils.TestUtils.zip;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;
import uk.co.whitbread.wallet.domain.utils.passkit.BBType;
import uk.co.whitbread.wallet.domain.utils.passkit.HUBType;
import uk.co.whitbread.wallet.domain.utils.passkit.PIGEType;
import uk.co.whitbread.wallet.domain.utils.passkit.PIType;
import uk.co.whitbread.wallet.domain.utils.passkit.Type;
import uk.co.whitbread.wallet.domain.utils.passkit.TypeFactory;
import uk.co.whitbread.wallet.domain.utils.passkit.ZIPType;

@ExtendWith(MockitoExtension.class)
class TypeTest {

  private static WalletProperties walletProperties = new WalletProperties();

  @BeforeEach
  void setup() {
    initWalletProperties();
    assertEquals(pi(), walletProperties.getPi());
    assertEquals(bb(), walletProperties.getBb());
    assertEquals(hub(), walletProperties.getHub());
    assertEquals(zip(), walletProperties.getZip());
  }

  @ParameterizedTest
  @MethodSource
  void testGetPkPassTemplateByPI_success(TypeWithHotel input) {
    assertNotNull(input);
    Type argument = input.getType();
    boolean isDeHotel = input.isDeHotel();
    assertNotNull(argument);
    var result = TypeFactory.create(walletProperties, "PI", argument.getName(), isDeHotel);

    assertNotNull(result);
    if ("PI".equals(argument.getName()) || "PIGE".equals(argument.getName()) || "BB".equals(
        argument.getName())) {
      if (isDeHotel) {
        assertEquals("PIGE", result.getName());
      } else {
        assertEquals("PI", result.getName());
      }
      assertEquals(result.getBackgroundColor(), pi().getBackgroundColor());
      assertEquals(result.getForegroundColor(), pi().getForegroundColor());
      assertEquals(result.getLabelColor(), pi().getLabelColor());
    } else {
      assertEquals(result.getName(), argument.getName());
      assertEquals(result.getBackgroundColor(), argument.getBackgroundColor());
      assertEquals(result.getForegroundColor(), argument.getForegroundColor());
      assertEquals(result.getLabelColor(), argument.getLabelColor());
    }
  }

  @ParameterizedTest
  @MethodSource
  void testGetPkPassTemplateByBB_success(TypeWithHotel input) {
    assertNotNull(input);
    Type argument = input.getType();
    boolean isDeHotel = input.isDeHotel();
    var result = TypeFactory.create(walletProperties, "BB", argument.getName(), isDeHotel);
    assertNotNull(result);
    assertEquals("BB", result.getName());
      assertEquals(result.getBackgroundColor(), bb().getBackgroundColor());
      assertEquals(result.getForegroundColor(), bb().getForegroundColor());
      assertEquals(result.getLabelColor(), bb().getLabelColor());
    }

  static Stream<TypeWithHotel> testGetPkPassTemplateByPI_success() {
    return Stream.of(
        new TypeWithHotel(new PIType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new PIType(walletProperties), Boolean.FALSE),
        new TypeWithHotel(new HUBType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new HUBType(walletProperties), Boolean.FALSE),
        new TypeWithHotel(new ZIPType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new ZIPType(walletProperties), Boolean.FALSE),
        new TypeWithHotel(new BBType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new BBType(walletProperties), Boolean.FALSE),
         new TypeWithHotel(new PIGEType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new PIGEType(walletProperties), Boolean.FALSE));
  }

  static Stream<TypeWithHotel> testGetPkPassTemplateByBB_success() {
    return Stream.of(
        new TypeWithHotel(new PIType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new PIType(walletProperties), Boolean.FALSE),
        new TypeWithHotel(new HUBType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new HUBType(walletProperties), Boolean.FALSE),
        new TypeWithHotel(new ZIPType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new ZIPType(walletProperties), Boolean.FALSE),
        new TypeWithHotel(new BBType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new BBType(walletProperties), Boolean.FALSE),
          new TypeWithHotel(new PIGEType(walletProperties), Boolean.TRUE),
        new TypeWithHotel(new PIGEType(walletProperties), Boolean.FALSE));
  }

  private void initWalletProperties() {
    walletProperties.setPassJson("pass.json");
    walletProperties.setTemplateDir("template");
    walletProperties.setPi(pi());
    walletProperties.setBb(bb());
    walletProperties.setHub(hub());
    walletProperties.setZip(zip());
  }

  @Data
  @AllArgsConstructor
  private static class TypeWithHotel<T extends Type> {

    private T type;
    private boolean deHotel;
  }
}