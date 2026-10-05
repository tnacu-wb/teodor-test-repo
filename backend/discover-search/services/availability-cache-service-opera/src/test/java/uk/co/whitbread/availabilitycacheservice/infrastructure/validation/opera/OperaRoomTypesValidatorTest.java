package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

@ExtendWith(MockitoExtension.class)

class OperaRoomTypesValidatorTest {

  @InjectMocks
  private OperaRoomTypesValidator target;

  private OperaHotelsSearchCriteria operaHotelsSearchCriteria;

  @BeforeEach
  void setup() {
    operaHotelsSearchCriteria = buildOperaHotelsSearchCriteria(new String[][]
        {{"SB", "EXTSB", "SBDB"}});
  }

  @Test
  void isValidRoomTypesTest() {
    assertTrue(target.isValidRoomTypes(operaHotelsSearchCriteria));
  }

  @Test
  void isValidRoomTypesTestForInvalidRoomTypes() {
    operaHotelsSearchCriteria.setRoomTypes(new String[][]
        {{"SB", "EXTSB", "SBDB", "123456"}});
    assertFalse(target.isValidRoomTypes(operaHotelsSearchCriteria));
  }

  @Test
  void isValidRoomTypesTestWithNullRoomTypes() {
    operaHotelsSearchCriteria.setRoomTypes(null);
    assertFalse(target.isValidRoomTypes(operaHotelsSearchCriteria));
  }

  @Test
  void isValidRoomTypesTestWithNullRoomQty() {
    operaHotelsSearchCriteria.setRoomQty(null);
    assertFalse(target.isValidRoomTypes(operaHotelsSearchCriteria));
  }

  @Test
  void isValidRoomTypesTestWithNullCriteria() {
    assertFalse(target.isValidRoomTypes(null));
  }

  private OperaHotelsSearchCriteria buildOperaHotelsSearchCriteria(
      String[][] roomTypes) {
    return OperaHotelsSearchCriteria.builder()
        .hotelCodes(Arrays.asList("LONSLA,LONBLA,OXFORD"))
        .arrival(LocalDate.parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString())
        .departure(LocalDate.parse(LocalDate.now().plusDays(2).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString())
        .cot(new boolean[]{false})
        .language("en")
        .country("GB")
        .adults(new int[]{2})
        .children(new int[]{0})
        .rooms(1)
        .roomQty(new int[]{1})
        .roomTypes(roomTypes)
        .build();
  }

}
