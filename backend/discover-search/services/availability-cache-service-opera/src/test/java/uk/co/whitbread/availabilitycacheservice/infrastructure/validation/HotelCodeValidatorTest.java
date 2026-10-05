package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;


import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;


public class HotelCodeValidatorTest {

  private static Validator validator;
  private HotelCodeValidator hotelCodeValidator;
  private SearchCriteria criteria;

  @BeforeEach
  public void setUp() {
    hotelCodeValidator = new HotelCodeValidator();
    criteria = buildSearchCriteria();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void successTestForHotelCodeValidatorForSortTypeDistance() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setPage(1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isTrue();
  }

  @Test
  public void successTestForHotelCodeValidatorForSortTypePrice() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(SortType.PRICE);
    criteria.setPage(1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isTrue();

    criteria.setHotelCodes(Collections.emptyList());
    criteria.setPage(1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isFalse();

    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setPage(-1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForEmptyListHotelCode() {
    criteria.setHotelCodes(Collections.emptyList());
    criteria.setPage(1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForPageSizeLessThanZero() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setPage(-1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForInvalidHotelCodePattern() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "abcde"));
    criteria.setPage(1);
    criteria.setSize(40);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("Invalid hotel codes in input");

    criteria.setHotelCodes(Arrays.asList("PLYPTI", "abc$"));
    criteria.setPage(1);
    criteria.setSize(40);
    constraintViolations = validator.validate(criteria);
    violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(violationMessages).contains("Invalid hotel codes in input");

    criteria.setHotelCodes(Arrays.asList("PLYPTI", "abc123"));
    criteria.setPage(1);
    criteria.setSize(40);
    constraintViolations = validator.validate(criteria);
    violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(violationMessages).contains("Invalid hotel codes in input");
  }

  @Test
  public void failureTestForHotelCodeForPageForSortTypeDistance() {
    criteria.setHotelCodes(
        Arrays.asList("PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI",
            "PLYPTI",
            "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI"));
    criteria.setPage(2);
    criteria.setSize(10);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "Hotel Codes List should not contain more than 10 hotel Codes when Page Value is greater than 1 for Sort Type is DISTANCE");

  }

  @Test
  public void failureTestForHotelCodeForPageAsOneForSortTypeDistance() {
    criteria.setHotelCodes(
        Arrays.asList("PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI",
            "PLYPTI",
            "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI",
            "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI",
            "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI",
            "PLYPTI", "PLYPTI", "PLYPTI", "PLYPTI"));
    criteria.setPage(1);
    criteria.setSize(40);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "Hotel Codes List should not contain more than 40 hotel Codes when page is 1 for Sort Type is DISTANCE");
  }

  @Test
  public void failureTestForHotelCodeForSortTypePrice() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(SortType.PRICE);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("page/size cannot be empty or zero when sort = PRICE");

  }

  @Test
  public void failureTestForHotelCodeForInvalidSortType() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(null);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("Invalid Sort Type. Sort Type should be either DISTANCE or PRICE.");

  }

  @Test
  public void shouldReturnTrueForPage1Size40withSortPrice() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(SortType.PRICE);
    criteria.setPage(1);
    criteria.setSize(40);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isTrue();
  }

  @Test
  public void shouldReturnTrueForPage5Size10withSortPrice() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(SortType.PRICE);
    criteria.setPage(5);
    criteria.setSize(10);
    assertThat(hotelCodeValidator.isValid(criteria, null)).isTrue();
  }

  @Test
  public void shouldReturnValidationErrorForPage1Size15withSortPrice() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(SortType.PRICE);
    criteria.setPage(1);
    criteria.setSize(15);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("size should be equal to 40 for page = 1, sort = PRICE");
  }

  @Test
  public void shouldReturnValidationErrorForPage5Size40withSortPrice() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    criteria.setSort(SortType.PRICE);
    criteria.setPage(5);
    criteria.setSize(40);
    Set<ConstraintViolation<SearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("size should be equal to 10 for page > 1, sort = PRICE");
  }

  private SearchCriteria buildSearchCriteria() {
    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .departure(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(false)
        .rooms(1)
        .type(
            new String[]{uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType.SB.name()})
        .language("en")
        .country("GB")
        .sort(SortType.DISTANCE)
        .build();
  }
}
