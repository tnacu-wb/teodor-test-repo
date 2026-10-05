package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;

@ExtendWith(SpringExtension.class)

public class SearchCriteriaForDatesTest {

  private static String[] TYPE = new String[]{RoomType.DB.name()};
  private Validator validator;
  private ValidatorFactory validatorFactory;
  private SearchCriteria searchCriteria;
  private int rooms;
  private int[] adults;
  private int[] children;

  @BeforeEach
  public void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
    searchCriteria = new SearchCriteria();
    searchCriteria.setCountry("GB");
    searchCriteria.setLanguage("en");
    rooms = 1;
    adults = new int[]{1};
    children = new int[]{0};
    searchCriteria.setRooms(rooms);
    searchCriteria.setAdults(adults);
    searchCriteria.setChildren(children);
    searchCriteria.setType(TYPE);
    searchCriteria.setSort(SortType.DISTANCE);

  }

  @AfterEach
  public void tearDown() {
    validatorFactory.close();
  }

  @Test
  public void arrivalDateTestWithViolationsForBlankAndNull() {

    searchCriteria.setArrival(null);
    searchCriteria.setDeparture(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(searchCriteria);

    List<String> violationMessages = violations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("arrival date may not be on or after departure date and earlier than today.")
        .contains("must be in correct date format")
        .contains("must not be blank");
  }

  @Test
  public void arrivalDateTestWithViolationsForDateFormat() {

    searchCriteria.setArrival("12-12-2025");
    searchCriteria.setDeparture(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(searchCriteria);

    List<String> violationMessages = violations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("must be in correct date format");
  }

  @Test
  public void departureDateTestWithViolationsForArrivalDepartureDateConstraints() {

    searchCriteria.setArrival(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    searchCriteria.setDeparture(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(searchCriteria);

    List<String> violationMessages = violations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains(
        "arrival date may not be on or after departure date and earlier than today.");
  }
}
