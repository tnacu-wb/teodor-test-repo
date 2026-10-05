package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


class NotNullIfAnotherFieldHasValueValidatorTest {

  LocalValidatorFactoryBean localValidatorFactory;

  @BeforeEach
  void setup() {

    localValidatorFactory = new LocalValidatorFactoryBean();
    localValidatorFactory.setProviderClass(HibernateValidator.class);
    localValidatorFactory.afterPropertiesSet();
  }

  @Test
  void oneOfTheThreeFieldsIsNull() {
    Set<ConstraintViolation<DummyClass>> result = upcomingBooking(null, "Juliano", "40");
    assertThat("", result, hasSize(1));
    List<String> messages = getErrorMessages(result);
    assertThat("", messages,
        containsInAnyOrder("all or none fields must be populated: [date, name, age]"));
  }

  @Test
  void twoOfTheThreeFieldsAreNull() {
    Set<ConstraintViolation<DummyClass>> result = upcomingBooking(null, "", "40");
    assertThat("", result, hasSize(1));
    List<String> messages = getErrorMessages(result);
    assertThat("", messages,
        containsInAnyOrder("all or none fields must be populated: [date, name, age]"));
  }

  @Test
  void threeFieldsAreNull() {
    Set<ConstraintViolation<DummyClass>> result = upcomingBooking("", null, null);
    assertThat("", result, hasSize(0));
  }

  @Test
  void threeFieldsAreNotNull() {
    Set<ConstraintViolation<DummyClass>> result = upcomingBooking("2010-03-15", "Juliano", "40");
    assertThat("", result, hasSize(0));
  }

  private Set<ConstraintViolation<DummyClass>> upcomingBooking(String date, String name,
      String age) {
    var dc = new NotNullIfAnotherFieldHasValueValidatorTest.DummyClass(date, name, age);
    return localValidatorFactory.validate(dc);
  }

  private List<String> getErrorMessages(Set<ConstraintViolation<DummyClass>> result) {
    return result.stream().map(ConstraintViolation::getMessage).toList();
  }

  @NotNullIfAnotherFieldHasValue(values = {"date", "name", "age"})
  @AllArgsConstructor
  @Data
  public static class DummyClass {

    String date;
    String name;
    String age;
  }

}