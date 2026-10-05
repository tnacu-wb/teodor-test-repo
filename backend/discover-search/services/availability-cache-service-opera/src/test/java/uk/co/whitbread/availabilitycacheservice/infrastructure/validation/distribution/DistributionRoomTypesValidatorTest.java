package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.distribution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionOperaRoomTypesValidator;


public class DistributionRoomTypesValidatorTest {

  private DistributionOperaRoomTypesValidator distributionOperaRoomTypesValidator;

  @BeforeEach
  public void setup() {
    distributionOperaRoomTypesValidator = new DistributionOperaRoomTypesValidator();
  }

  @Test
  public void shouldReturnInValidIfDistributionPayloadIsNull() {
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnInValidIfRoomQtyIsNull() {
    final DistributionPayload distributionPayload =
        buildDistributionPayload(null, new String[1][1], 2);
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnInValidIfRoomTypesIsNull() {
    final DistributionPayload distributionPayload =
        buildDistributionPayload(new int[2], null, 2);
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnInValidIfQryLengthRoomTypesLengthIsNotEqual() {
    final DistributionPayload distributionPayload =
        buildDistributionPayload(new int[2], new String[1][1], 2);
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnInValidIfRoomQrySumIsNotEqualToRooms() {
    final DistributionPayload distributionPayload =
        buildDistributionPayload(new int[]{1, 0}, new String[1][1], 2);
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnValidIfRoomQrySumIsEqualToRoomsAndQtyTypeLengthSame() {
    final String[][] roomTypes = {{"DBLDBL", "ZIPDBL"}, {"ZIPSB", "SBSBDB"}};
    final DistributionPayload distributionPayload =
        buildDistributionPayload(
            new int[]{1, 1}, roomTypes, 2);
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    assertThat(isValid, is(true));
  }

  @Test
  public void shouldReturnInValidIfRoomTypeInCorrect() {
    final String[][] roomTypes = {{"12DBLDBL", "ZIPDBL"}, {"ZIPSB", "SBSBDB"}};
    final DistributionPayload distributionPayload =
        buildDistributionPayload(
            new int[]{1, 1}, roomTypes, 2);
    boolean isValid = distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    assertThat(isValid, is(false));
  }

  private DistributionPayload buildDistributionPayload(final int[] roomQty,
      final String[][] roomTypes, final int rooms) {
    return DistributionPayload.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .rooms(rooms)
        .roomQty(roomQty)
        .roomTypes(roomTypes)
        .build();
  }

}
