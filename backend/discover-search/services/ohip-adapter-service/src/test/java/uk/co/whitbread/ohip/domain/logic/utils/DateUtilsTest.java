package uk.co.whitbread.ohip.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
class DateUtilsTest {

  @Test
  void testSplitDateRangeInNonEqualsIntervals() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 3, 20), LocalDate.of(2024, 4, 11)),
        Pair.of(LocalDate.of(2024, 4, 12), LocalDate.of(2024, 5, 4)),
        Pair.of(LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 27)),
        Pair.of(LocalDate.of(2024, 5, 28), LocalDate.of(2024, 6, 19)),
        Pair.of(LocalDate.of(2024, 6, 20), LocalDate.of(2024, 7, 12))
    );
    //Act
    var actual = DateUtils.splitDateRange("20/03/2024", "12/07/2024", 23, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeInEqualsIntervals() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 3, 20), LocalDate.of(2024, 4, 11)),
        Pair.of(LocalDate.of(2024, 4, 12), LocalDate.of(2024, 5, 4)),
        Pair.of(LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 27)),
        Pair.of(LocalDate.of(2024, 5, 28), LocalDate.of(2024, 6, 19))
    );
    //Act
    var actual = DateUtils.splitDateRange("20/03/2024", "19/06/2024", 23, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeInIntervalsLastInterval() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 13)),
        Pair.of(LocalDate.of(2024, 4, 14), LocalDate.of(2024, 4, 15)),
        Pair.of(LocalDate.of(2024, 4, 16), LocalDate.of(2024, 4, 17)));

    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "17/04/2024", 3, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }


  @Test
  void testSplitDateRangeInIntervalByOneDay() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 19)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "19/04/2024", 1, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeInOneDayInterval() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 11)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "11/04/2024", 2, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeInIntervalByTwoDays() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 12)),
        Pair.of(LocalDate.of(2024, 4, 13), LocalDate.of(2024, 4, 14)),
        Pair.of(LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 15)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "15/04/2024", 2, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeInEqualsIntervalByTwoDays() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 12)),
        Pair.of(LocalDate.of(2024, 4, 13), LocalDate.of(2024, 4, 14)),
        Pair.of(LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 16)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "16/04/2024", 2, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeLastIntervalWithTwoDays() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 14)),
        Pair.of(LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 17)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "17/04/2024", 4, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeLastIntervalWithOneDay() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 14)),
        Pair.of(LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 16)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "16/04/2024", 5, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeLastIntervalWithLastDays() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 15)),
        Pair.of(LocalDate.of(2024, 4, 16), LocalDate.of(2024, 4, 20)),
        Pair.of(LocalDate.of(2024, 4, 21), LocalDate.of(2024, 4, 23)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "23/04/2024", 5, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateRangeOneInterval() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 11), LocalDate.of(2024, 4, 15)));
    //Act
    var actual = DateUtils.splitDateRange("11/04/2024", "15/04/2024", 50, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDate() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9)),
        Pair.of(LocalDate.of(2024, 4, 10), LocalDate.of(2024, 4, 14)),
        Pair.of(LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 19)),
        Pair.of(LocalDate.of(2024, 4, 20), LocalDate.of(2024, 4, 24)),
        Pair.of(LocalDate.of(2024, 4, 25), LocalDate.of(2024, 4, 29)),
        Pair.of(LocalDate.of(2024, 4, 30), LocalDate.of(2024, 5, 4)),
        Pair.of(LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 9)),
        Pair.of(LocalDate.of(2024, 5, 10), LocalDate.of(2024, 5, 13)),
        Pair.of(LocalDate.of(2024, 5, 14), LocalDate.of(2024, 5, 15)));
    //Act
    var actual = DateUtils.splitDateRange("05/04/2024", "15/05/2024", 5, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

  @Test
  void testSplitDateOneFullInterval() {
    // Arrange
    List<Pair<LocalDate, LocalDate>> expected = List.of(
        Pair.of(LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 22))
    );
    //Act
    var actual = DateUtils.splitDateRange("20/03/2024", "22/03/2024", 3, "dd/MM/yyyy");
    assertEquals(expected, actual);
  }

}
