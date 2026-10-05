package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.distribution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionOccupantsPerRoomValidator;


public class DistributionOccupantsPerRoomValidatorTest {

  private DistributionOccupantsPerRoomValidator validatorToTest;

  private DistributionSearchCriteria criteria;

  @BeforeEach
  public void setUp() throws Exception {
    validatorToTest = new DistributionOccupantsPerRoomValidator();
  }

  @Test
  public void shouldBeInvalidWhenSearchCriteriaIsNull() {
    criteria = null;
    final boolean isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldBeInvalidWhenAdultsIsNull() {
    criteria = buildDistributionSearchCriteria(null, new int[1], 2);
    final boolean isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldBeInvalidWhenChildrenIsNull() {
    criteria = buildDistributionSearchCriteria(new int[1], null, 2);
    final boolean isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldBeInvalidWhenChildrenLengthIsNotEqualToRoom() {
    criteria = buildDistributionSearchCriteria(new int[2], new int[1], 2);
    final boolean isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldBeInvalidWhenAdultsLengthIsNotEqualToRoom() {
    criteria = buildDistributionSearchCriteria(new int[1], new int[2], 2);
    final boolean isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(false));
  }

  @Test
  public void shouldBeValidWhenAdultsAndChildrenLengthIsEqualToRoom() {
    criteria = buildDistributionSearchCriteria(new int[2], new int[2], 2);
    final boolean isValid = validatorToTest.isValid(criteria, null);
    assertThat(isValid, is(true));
  }

  private DistributionSearchCriteria buildDistributionSearchCriteria(final int[] adults,
      final int[] children, final int rooms) {
    return DistributionSearchCriteria.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(LocalDate.now().toString())
        .departure(LocalDate.now().plusDays(2).toString())
        .adults(adults)
        .children(children)
        .rooms(rooms)
        .build();
  }
}