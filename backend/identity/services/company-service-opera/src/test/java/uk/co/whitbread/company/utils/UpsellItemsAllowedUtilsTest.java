package uk.co.whitbread.company.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.properties.UpsellItemAllowancesProperties;


@ExtendWith(MockitoExtension.class)
class UpsellItemsAllowedUtilsTest {

  private UpsellItemsAllowedUtils upsellItemsAllowedUtils;

  @Mock
  UpsellItemAllowancesProperties upsellItemAllowancesProperties;

  @BeforeEach
  void setUp() {
    upsellItemsAllowedUtils = new UpsellItemsAllowedUtils(upsellItemAllowancesProperties);
  }

  @Test
  void calculateUpsellItemsAllowed() {

    var extrasCodes = List.of("1", "2", "3", "4", "5");
    when(upsellItemAllowancesProperties.getPremierBreakfast()).thenReturn("1,2");
    when(upsellItemAllowancesProperties.getContinentalBreakfast()).thenReturn("3,4");
    when(upsellItemAllowancesProperties.getMealDeal()).thenReturn("5,6");
    when(upsellItemAllowancesProperties.getWiFi()).thenReturn("7,8");
    when(upsellItemAllowancesProperties.getHubBreakfast()).thenReturn("9,10");

    var result = upsellItemsAllowedUtils.calculateUpsellItemsAllowed(extrasCodes);

    assertNotNull(result);
    assertEquals(List.of("1", "2", "3", "4", "5", "6", "9", "10", "7", "8"), result);
  }

  @Test
  void calculateUpsellItemsAllowed_WithWrongExtrasCodes() {
    var extrasCodes = List.of("6", "7");

    var result = upsellItemsAllowedUtils.calculateUpsellItemsAllowed(extrasCodes);

    assertNotNull(result);
    assertEquals(new ArrayList<>(), result);
  }
}
