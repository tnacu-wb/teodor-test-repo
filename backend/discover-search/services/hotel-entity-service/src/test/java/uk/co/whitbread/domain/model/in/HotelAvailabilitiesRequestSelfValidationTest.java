package uk.co.whitbread.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Arrays;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.validation.ValidatorFactory;

class HotelAvailabilitiesRequestSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
          Validation.buildDefaultValidatorFactory()
                  .getValidator());

  @Test
  void constructor_emptyLocation_shouldSelfValidateAndThrow() {
    String expectedMessage = "location: must not be empty";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyLocationFormat_shouldSelfValidateAndThrow() {
    String expectedMessage = "locationFormat: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyLanguage_shouldSelfValidateAndThrow() {
    String expectedMessage = "language: must not be empty";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyCountry_shouldSelfValidateAndThrow() {
    String expectedMessage = "country: must not be empty";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyAdultsNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "adultsNumber: must not be empty";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyChildrenNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "childrenNumber: must not be empty";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 1))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyPage_shouldSelfValidateAndThrow() {
    String expectedMessage = "page: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyPageSize_shouldSelfValidateAndThrow() {
    String expectedMessage = "pageSize: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullBookingChannel_shouldSelfValidateAndThrow() {
    String expectedMessage = "oldWorldChannel: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullRadiusUnit_shouldSelfValidateAndThrow() {
    String expectedMessage = "radiusUnit: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullChannel_shouldSelfValidateAndThrow() {
    String expectedMessage = "channel: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullSubChannel_shouldSelfValidateAndThrow() {
    String expectedMessage = "subChannel: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }



  @Test
  void constructor_nullRadius_shouldSelfValidateAndThrow() {
    String expectedMessage = "radius: must not be null";

    checkErrorThrown(() -> HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow((Executable) this::generateRequestObject);
  }

  private HotelAvailabilitiesRequest generateRequestObject() {
    return HotelAvailabilitiesRequest.builder()
        .location("London")
        .locationFormat(LocationFormatEnum.PLACEID)
        .radiusUnit(RadiusUnitEnum.MILES)
        .radius(50)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-05")
        .language("en")
        .country("gb")
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .roomTypes(Arrays.asList("DB", "DB"))
        .oldWorldChannel(OldWorldChannelEnum.WEB)
        .channel("PI")
        .subChannel("WEB")
        .companyId(null)
        .page(0)
        .pageSize(10)
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
