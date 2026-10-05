package uk.co.whitbread.contentservice.roomtypes.client.feign;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassificationsResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AEMFeignClientRatesHystrixFallbackFactoryTest {
    private final AEMFeignClientRatesHystrixFallbackFactory target =
            new AEMFeignClientRatesHystrixFallbackFactory();

    private Throwable mockThrowable;

    @BeforeEach
    public void setup() {
        mockThrowable = mock(Throwable.class);
    }

    @Test
    public void shouldBeNullSafe() {
        final AEMFeignClientRates aemFeignClientRates = target.create(mockThrowable);

        final AEMRateClassificationsResponse rateClassifications = aemFeignClientRates.getRateClassifications(null, null,
                null, null);

        assertThat(rateClassifications.getItems(), Matchers.nullValue());
    }

    @Test
    public void shouldBeEmptyFieldSafe() {
        final AEMFeignClientRates aemFeignClientRates = target.create(mockThrowable);

        final AEMRateClassificationsResponse rateClassifications = aemFeignClientRates.getRateClassifications("", "",
                "","");

        assertThat(rateClassifications.getItems(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnEmptyObject() {
        final AEMFeignClientRates aemFeignClient = target.create(mockThrowable);

        final AEMRateClassificationsResponse rateClassifications = aemFeignClient.getRateClassifications("gb", "en",
                "someBrand","resource");

        assertThat(rateClassifications.getItems(), Matchers.nullValue());
    }
}
