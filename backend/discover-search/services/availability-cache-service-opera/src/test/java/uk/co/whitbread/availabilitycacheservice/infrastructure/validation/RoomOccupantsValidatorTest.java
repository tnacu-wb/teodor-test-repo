package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;


public class RoomOccupantsValidatorTest {

  private RoomOccupantsValidator validatorToTest;

  private SearchCriteria criteria;

  private boolean isValid;

  @BeforeEach
  public void setUp() throws Exception {
    validatorToTest = new RoomOccupantsValidator();
    criteria = new SearchCriteria();
  }

  @Test
  public void testSuccessfulValidationUsingRoomOccupantsValidator() {
    criteria.setAdults(new int[]{1});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(true));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsNullCheckForNullInput() {
    criteria = null;
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsNullCheckForNullAdultsInput() {
    criteria.setAdults(null);
    criteria.setChildren(new int[0]);
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsNullCheckForNullChildrenInput() {
    criteria.setAdults(new int[]{1});
    criteria.setChildren(null);
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsNullCheckForNullRoomTypesInput() {
    criteria.setAdults(new int[]{1});
    criteria.setChildren(new int[0]);
    criteria.setType(null);
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsValidLengthCheckForAdultsInput() {
    criteria.setAdults(new int[]{1, 2});
    criteria.setChildren(new int[0]);
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsValidLengthCheckForChildrenInput() {
    criteria.setAdults(new int[]{1});
    criteria.setChildren(new int[]{1, 2});
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseOnFailureOfIsValidLengthCheckForRoomTypeInput() {
    criteria.setAdults(new int[]{1});
    criteria.setChildren(new int[]{1});
    criteria.setType(new String[]{RoomType.SB.name(), RoomType.DB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUponInvalidRoomTypeInput() {
    criteria.setAdults(new int[]{1, 1, 1});
    criteria.setChildren(new int[]{1, 0, 0});
    criteria.setType(new String[]{RoomType.DB.name(), RoomType.SB.name(), "ACC"});
    criteria.setRooms(3);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }
}