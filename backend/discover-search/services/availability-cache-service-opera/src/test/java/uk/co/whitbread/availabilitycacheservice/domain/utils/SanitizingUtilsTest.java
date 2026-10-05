package uk.co.whitbread.availabilitycacheservice.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.GqtSearchCriteria;

class SanitizingUtilsTest {

  @Test
  void sanitizeStrings() {
    assertEquals("test", SanitizingUtils.sanitize("test"));
    assertEquals("testtest", SanitizingUtils.sanitize("test\ntest"));
    assertEquals("testtest", SanitizingUtils.sanitize("test\rtest"));
    assertEquals("testtest", SanitizingUtils.sanitize("test\r\ntest"));
    assertEquals("", SanitizingUtils.sanitize(""));
  }

  @Test
  void sanitize_whenToStringThrowsException() {
    Object object = Mockito.mock(Object.class);
    Mockito.when(object.toString()).thenThrow(new RuntimeException(("Exception")));
    assertEquals("", SanitizingUtils.sanitize(object));
  }

  @Test
  void sanitizeObject() {
    GqtSearchCriteria gqtSearchCriteria = GqtSearchCriteria.builder().arrival("test").build();
    assertEquals(
        "GqtSearchCriteria{hotelCodes=null,arrival='test',departure='null',country='null',language='null'}",
        SanitizingUtils.sanitize(gqtSearchCriteria));
  }

  @Test
  void sanitizeList() {
    GqtSearchCriteria gqtSearchCriteria = GqtSearchCriteria.builder().arrival("test").build();
    GqtSearchCriteria gqtSearchCriteria1 = GqtSearchCriteria.builder().arrival("test1").build();
    List<GqtSearchCriteria> gqtSearchCriteriaList =
        List.of(gqtSearchCriteria, gqtSearchCriteria1);
    assertEquals(
        "GqtSearchCriteria{hotelCodes=null,arrival='test',departure='null',country='null',language='null'},GqtSearchCriteria{hotelCodes=null,arrival='test1',departure='null',country='null',language='null'}",
        SanitizingUtils.sanitize(gqtSearchCriteriaList));
  }

  @Test
  void testSanitizeRoomTypesForLog() {
    String [][] roomTypes = new String[][] {{"SB", "EXTSB", "SBDB", "123456"}};
    assertEquals("SB,EXTSB,SBDB,123456", SanitizingUtils.sanitizeRoomTypesForLog(roomTypes));
  }

  @Test
  void testSanitizeRoomTypesForLog_WhenRoomTypeIsNull() {
    assertEquals("", SanitizingUtils.sanitizeRoomTypesForLog(null));

  }
}