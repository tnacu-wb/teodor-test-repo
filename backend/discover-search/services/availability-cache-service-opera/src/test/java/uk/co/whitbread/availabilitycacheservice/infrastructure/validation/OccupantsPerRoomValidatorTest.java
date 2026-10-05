package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;


public class OccupantsPerRoomValidatorTest {

  private OccupantsPerRoomValidator validatorToTest;

  private SearchCriteria criteria;

  private boolean isValid;

  @BeforeEach
  public void setUp() throws Exception {
    validatorToTest = new OccupantsPerRoomValidator();
    criteria = new SearchCriteria();
  }

  @AfterEach
  public void tearDown() throws Exception {
    //Nothing needs to be done here
  }

  @Test
  public void shouldReturnFalseUponZeroAdultsForSingleRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));

    //shouldReturnFalseUpon2AdultsForSingleRoom
    criteria.setAdults(new int[]{2});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));

    //shouldReturnFalseUpon1ChildForSingleRoom
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{1});
    criteria.setType(new String[]{RoomType.SB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon0AdultsForDoubleRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.DB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));

    //shouldReturnFalseUpon3AdultsForDoubleRoom
    criteria.setAdults(new int[]{3});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.DB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon0AdultsForAccessibleRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.DIS.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));

    //shouldReturnFalseUpon3AdultsForAccessibleRoom
    criteria.setAdults(new int[]{3});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.DIS.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon0AdultsForTwinRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.TWIN.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));

    //shouldReturnFalseUpon3AdultsForTwinRoom
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.TWIN.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon1ChildForDoubleRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{1});
    criteria.setType(new String[]{RoomType.DB.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon1ChildForAccessibleRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{1});
    criteria.setType(new String[]{RoomType.DIS.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon1ChildForTwinRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{1});
    criteria.setType(new String[]{RoomType.TWIN.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  //Family room test cases
  @Test
  public void shouldReturnFalseUponZeroAdultsForFamilyRoom() {
    criteria.setAdults(new int[]{0});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.FAM.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));

    //shouldReturnFalseUponZeroChildrenForFamilyRoom
    criteria.setAdults(new int[]{1});
    criteria.setChildren(new int[]{0});
    criteria.setType(new String[]{RoomType.FAM.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnFalseUpon3AdultsN3ChildrenForFamilyRoom() {
    criteria.setAdults(new int[]{3});
    criteria.setChildren(new int[]{3});
    criteria.setType(new String[]{RoomType.FAM.name()});
    criteria.setRooms(1);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldReturnNoViolation() {
    criteria.setAdults(new int[]{2, 1, 1, 2});
    criteria.setChildren(new int[]{0, 0, 0, 2});
    criteria.setType(new String[]{RoomType.DB.name(), RoomType.TWIN.name(), RoomType.SB.name(), RoomType.FAM.name()});
    criteria.setRooms(4);
    isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }
}