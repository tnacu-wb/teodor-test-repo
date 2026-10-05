package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.MealPackagesValidator;

@ExtendWith(MockitoExtension.class)
class MealPackagesValidatorTest {

  private final MealPackagesValidator mealPackagesValidator = new MealPackagesValidator();
  private GroupBookingRequestDto validRequestDto;

  @Mock
  ConstraintValidatorContext constraintValidatorContext;

  @BeforeEach
  public void setUp() {
    validRequestDto = GroupBookingRequestDto.builder()
        .title("Mr")
        .firstName("John")
        .lastName("Doe")
        .emailAddress("john.doe@example.com")
        .phoneNumber("+1234567890")
        .bookerType("Individual")
        .purposeOfStay("Business")
        .isSchoolOrYouth(true)
        .reasonForVisit("Conference")
        .reasonForVisitOther("N/A")
        .companyName("Industrial co")
        .isPackageTypeBf(true)
        .isPackageTypeMealDeal(false)
        .hotelName("London Euston")
        .hotelBrand("PI")
        .arrivalDate("2024-07-01")
        .departureDate("2024-07-05")
        .isTravellingWithChild(true)
        .isAccessibleRoom(false)
        .singleOccupancy(1)
        .doubleOccupancy(10)
        .twinRooms(1)
        .familyOf21A1C(0)
        .familyOf32A1C(1)
        .familyOf31A2C(1)
        .familyOf42A2C(2)
        .accessibleSingle(0)
        .accessibleDouble(1)
        .accessibleTwin(0)
        .additionalInformation("Would appreciate quiet rooms")
        .build();
  }

  @Test
  void isValid__ShouldReturnOK() {
    //Arrange
    //Act
    boolean valid = mealPackagesValidator.isValid(validRequestDto, null);

    //Assert
    assertThat(valid, is(true));
  }

  @Test
  void isValid__noMealsSelected_BadRequest() {
    //Arrange
    validRequestDto.setIsPackageTypeBf(false);
    validRequestDto.setIsPackageTypeMealDeal(false);

    //Act
    boolean valid = mealPackagesValidator.isValid(validRequestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void isValid__moreThanOneMealSelected_BadRequest() {
    //Arrange
    validRequestDto.setIsPackageTypeBf(true);
    validRequestDto.setIsPackageTypeMealDeal(true);

    //Act
    boolean valid = mealPackagesValidator.isValid(validRequestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @ParameterizedTest
  @CsvSource({
      "false, false, true",
      "true, true, false",
      "true, false, true",
  })
  void isValid__hotelBrandPID__variousMealPackageCombinations_ShouldReturnExpected(
      boolean isPackageTypeBf, boolean isPackageTypeMealDeal, boolean expectedValid) {

    //Arrange
    validRequestDto.setHotelBrand("PID");
    validRequestDto.setIsPackageTypeBf(isPackageTypeBf);
    validRequestDto.setIsPackageTypeMealDeal(isPackageTypeMealDeal);

    //Act
    boolean valid = mealPackagesValidator.isValid(validRequestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(expectedValid));
  }

}