package uk.co.whitbread.ocd.infrastructure.rest.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.in.TaxRequestDto;

public class ArrivalDepartureDateValidatorTest {

  private ArrivalDepartureDateValidator arrivalDepartureDateValidator;

  private TaxRequestDto taxRequestDto;
  private LocalDate today;
  private DateTimeFormatter dtf;

  @BeforeEach
  public void setUp() {
    arrivalDepartureDateValidator = new ArrivalDepartureDateValidator();
    taxRequestDto = new TaxRequestDto();
    dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    today = LocalDate.now();
  }

  @Test
  void successfulTestForArrivalDepartureDate(){
    taxRequestDto.setArrivalDate(today.format(dtf));
    taxRequestDto.setDepartureDate(today.plusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isTrue();
  }

  @Test
  void failureTestForNullSearchCriteria(){
    taxRequestDto=null;
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForNullArrivalDate(){
    taxRequestDto.setArrivalDate(null);
    taxRequestDto.setDepartureDate(today.format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();

  }

  @Test
  void failureTestForNullDepartureDate(){
    taxRequestDto.setArrivalDate(today.format(dtf));
    taxRequestDto.setDepartureDate(null);
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForNullArrivalAndDepartureDateSearchCriteria(){
    taxRequestDto.setArrivalDate(null);
    taxRequestDto.setDepartureDate(null);
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForArrivalBeforeTodaySearchCriteria(){
    taxRequestDto.setArrivalDate(today.minusDays(1).format(dtf));
    taxRequestDto.setDepartureDate(null);
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForBothStartNEndInPast(){
    taxRequestDto.setArrivalDate(today.minusDays(2).format(dtf));
    taxRequestDto.setDepartureDate(today.minusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForArrivalAndDepartureEqualsSearchCriteria(){
    taxRequestDto.setArrivalDate(today.format(dtf));
    taxRequestDto.setDepartureDate(today.format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForArrivalAfterDepartureSearchCriteria(){
    taxRequestDto.setArrivalDate(today.plusDays(3).format(dtf));
    taxRequestDto.setDepartureDate(today.plusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForArrivalWithInvalidDateSearchCriteria(){
    taxRequestDto.setArrivalDate("invalid");
    taxRequestDto.setDepartureDate(today.plusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForStartAndEndDateInInvalidFormats(){
    taxRequestDto.setArrivalDate("22-09-2032");
    taxRequestDto.setDepartureDate("23-09-2032");
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void failureTestForDepartureWithInvalidDateSearchCriteria(){
    taxRequestDto.setArrivalDate(today.plusDays(1).format(dtf));
    taxRequestDto.setDepartureDate("invalid");
    assertThat(arrivalDepartureDateValidator.isValid(taxRequestDto,null)).isFalse();
  }

  @Test
  void testIsValid_TaxRequestDto_Success() {
    final LocalDate now = LocalDate.now();
    final TaxRequestDto taxRequestDto = mockTaxRequest(now, now.plusDays(1));
    assertThat(arrivalDepartureDateValidator
        .isValid(taxRequestDto, null)).isTrue();
  }

  @Test
  void testIsValid_TaxRequestDto_Failure() {
    final LocalDate now = LocalDate.now();
    final TaxRequestDto taxRequestDto = mockTaxRequest(now, now.minusDays(2));
    assertThat(arrivalDepartureDateValidator
        .isValid(taxRequestDto, null)).isFalse();
  }

  private TaxRequestDto mockTaxRequest(final LocalDate arrivalDate, final LocalDate departureDate) {
    TaxRequestDto taxRequestDto = new TaxRequestDto();
    taxRequestDto.setAdults(1);
    taxRequestDto.setRoomType("FLEXRATE");
    taxRequestDto.setRoomType("DOUBLE");
    taxRequestDto.setArrivalDate(arrivalDate.format(dtf));
    taxRequestDto.setDepartureDate(departureDate.format(dtf));
    return taxRequestDto;
  }
}
