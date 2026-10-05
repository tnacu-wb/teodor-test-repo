package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)

class OperaMaxNightsValidatorTest {

  @InjectMocks
  private OperaMaxNightsValidator maxNightsValidator;

  @BeforeEach
  void setup() {

    ReflectionTestUtils.setField(maxNightsValidator, "defaultMaxNumberOfNights", 9);
  }

  @Test
  void MaxNightsNotExceeds() {
    String arrival = LocalDate.parse(LocalDate.now().plusDays(5).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    String departure = LocalDate.parse(LocalDate.now().plusDays(10).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    assertThat(maxNightsValidator.validate(arrival, departure)).isTrue();
  }

  @Test
  void maxNightExceeded() {
    String arrival = LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    String departure = LocalDate.parse(LocalDate.now().plusDays(20).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    assertThat(maxNightsValidator.validate(arrival, departure)).isFalse();
  }

  @Test
  void numNightsEqualsToMax() {
    String arrival = LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    String departure = LocalDate.parse(LocalDate.now().plusDays(10).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    assertThat(maxNightsValidator.validate(arrival, departure)).isTrue();
  }

  @Test
  void numNightsDateFormatException() {
    String arrival = LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    String departure = "abc";
    assertThat(maxNightsValidator.validate(arrival, departure)).isFalse();
  }

  @Test
  void numNightsWithNull() {
    String arrival = LocalDate.parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
        .toString();
    assertThat(maxNightsValidator.validate(arrival, null)).isFalse();
  }

}
