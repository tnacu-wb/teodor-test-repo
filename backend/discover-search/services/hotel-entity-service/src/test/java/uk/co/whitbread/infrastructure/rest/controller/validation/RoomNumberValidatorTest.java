package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.RoomNumberValidator;

@ExtendWith(MockitoExtension.class)
class RoomNumberValidatorTest {

  private final RoomNumberValidator roomNumberValidator = new RoomNumberValidator();
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
    boolean valid = roomNumberValidator.isValid(validRequestDto, null);

    //Assert
    assertThat(valid, is(true));
  }

  @Test
  void isValid__roomNumberBelowMinimum_BadRequest() {
    //Arrange
    validRequestDto.setDoubleOccupancy(0);

    //Act
    boolean valid = roomNumberValidator.isValid(validRequestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

}