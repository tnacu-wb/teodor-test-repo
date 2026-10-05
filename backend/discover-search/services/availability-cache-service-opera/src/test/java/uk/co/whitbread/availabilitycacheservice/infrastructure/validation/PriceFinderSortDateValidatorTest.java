package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SortingOption;

class PriceFinderSortDateValidatorTest {
    private PriceFinderSortDateValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PriceFinderSortDateValidator();
        validator.initialize(null);
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testIsValid_NullSearchCriteria() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testIsValid_SortByNotPrice() {
        PriceFinderLocationSearchCriteria criteria = new PriceFinderLocationSearchCriteria();
        criteria.setSortBy(SortingOption.DISTANCE);
        assertTrue(validator.isValid(criteria, context));
    }

    @Test
    void testIsValid_SortDateBlank() {
        PriceFinderLocationSearchCriteria criteria = new PriceFinderLocationSearchCriteria();
        criteria.setSortBy(SortingOption.PRICE);
        criteria.setSortDate("");
        assertTrue(validator.isValid(criteria, context));
    }

    @Test
    void testIsValidSortDate_Valid() {
        PriceFinderLocationSearchCriteria criteria = new PriceFinderLocationSearchCriteria();
        criteria.setArrival("2025-08-10");
        criteria.setDaysRange(3);
        criteria.setSortDate("2025-08-11");
        assertTrue(validator.isValidSortDate(criteria));
    }

    @ParameterizedTest
    @CsvSource(value = {"2025-08-10, 2025-08-09", "2025-08-10, 2025-08-14", "invalid-date, 2025-08-11"})
    void testIsValidSortDate(String arrivalDate, String sortDate) {
        PriceFinderLocationSearchCriteria criteria = new PriceFinderLocationSearchCriteria();
        criteria.setArrival(arrivalDate);
        criteria.setDaysRange(3);
        criteria.setSortDate(sortDate);
        assertFalse(validator.isValidSortDate(criteria));
    }
}
