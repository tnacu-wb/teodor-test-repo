package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.NotEmptyConditional;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.NotEmptyConditionalValidator;

@ExtendWith(MockitoExtension.class)
class NotEmptyConditionalValidatorTest {

  private NotEmptyConditionalValidator notEmptyConditionalValidator = new NotEmptyConditionalValidator();
  private GroupBookingRequestDto validRequestDto;

  @Mock
  ConstraintValidatorContext constraintValidatorContext;

  @Mock
  private NotEmptyConditional mockNotEmptyConditionalAnnotation;

  @Mock
  ConstraintViolationBuilder mockConstraintViolationBuilder;

  @BeforeEach
  public void setUp() {
    when(mockNotEmptyConditionalAnnotation.checkedField()).thenReturn("additionalInformation");
    when(mockNotEmptyConditionalAnnotation.condition()).thenReturn("isSchoolOrYouth");
    notEmptyConditionalValidator = new NotEmptyConditionalValidator();
    notEmptyConditionalValidator.initialize(mockNotEmptyConditionalAnnotation);

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
    boolean valid = notEmptyConditionalValidator.isValid(validRequestDto, null);

    //Assert
    assertThat(valid, is(true));
  }

  @Test
  void isValid__additionalInformationIsEmpty_BadRequest() {
    //Arrange
    validRequestDto.setAdditionalInformation("");
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(any(String.class))).thenReturn(
        mockConstraintViolationBuilder);

    //Act
    boolean valid = notEmptyConditionalValidator.isValid(validRequestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void isValid__additionalInformationIsNull_BadRequest() {
    //Arrange
    validRequestDto.setAdditionalInformation(null);
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(any(String.class))).thenReturn(
        mockConstraintViolationBuilder);

    //Act
    boolean valid = notEmptyConditionalValidator.isValid(validRequestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

}