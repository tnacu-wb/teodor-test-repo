package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.distribution;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionHotelCodeValidator;


public class DistributionHotelCodeValidatorTest {

  private static Validator validator;
  private DistributionHotelCodeValidator hotelCodeValidator;
  private DistributionSearchCriteria criteria;

  @BeforeEach
  public void setUp() {
    hotelCodeValidator = new DistributionHotelCodeValidator();
    criteria = buildDistributionSearchCriteria();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void successTestForHotelCodeValidator() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    assertThat(hotelCodeValidator.isValid(criteria, null)).isTrue();
  }

  @Test
  public void failureTestForEmptyListHotelCode() {
    criteria.setHotelCodes(Collections.emptyList());
    assertThat(hotelCodeValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForInvalidHotelCodePattern() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "abcde"));
    Set<ConstraintViolation<DistributionSearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(
        Collectors.toList());
    assertThat(violationMessages).contains("Invalid hotel codes in input");

    //failureTestForInvalidHotelCodePatternWithSpecialChars
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "abc$"));
    constraintViolations = validator.validate(criteria);
    violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    assertThat(violationMessages).contains("Invalid hotel codes in input");
  }

  @Test
  public void failureTestForInvalidHotelCodePatternWithNumbers() {
    criteria.setHotelCodes(Arrays.asList("PLYPTI", "abc123"));
    Set<ConstraintViolation<DistributionSearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("Invalid hotel codes in input");
  }

  @Test
  public void failureTestForHotelCodeMoreThan40Hotels() {

    List<String> hotelCodes = new ArrayList<>();
    for (int i = 0; i < 210; i++) {
      hotelCodes.add("PLYPTI");
    }
    criteria.setHotelCodes(hotelCodes);
    Set<ConstraintViolation<DistributionSearchCriteria>> constraintViolations = validator.validate(criteria);
    List<String> violationMessages = constraintViolations.stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());
    assertThat(violationMessages).contains("Hotel Codes List should not contain more than 200 hotel Codes");
  }

  private DistributionSearchCriteria buildDistributionSearchCriteria() {
    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI
    return DistributionSearchCriteria
        .builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .departure(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(new boolean[]{false, false, false, false})
        .rooms(1)
        .language("EN")
        .country("GB")
        .build();
  }

}
