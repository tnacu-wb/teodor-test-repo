package uk.co.whitbread.contentservice.roomtypes.client.feign;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomTypesResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AEMFeignClientHystrixFallbackFactoryTest {
    private final AEMFeignClientHystrixFallbackFactory target =
            new AEMFeignClientHystrixFallbackFactory();

    private Throwable mockThrowable;

    @BeforeEach
    public void setup() {
        mockThrowable = mock(Throwable.class);
    }

    @Test
    public void shouldBeNullSafe() {
        final AEMFeignClient aemFeignClient = target.create(mockThrowable);

        final AEMRoomTypesResponse roomTypes = aemFeignClient.getRoomTypes(null, null,
                null, null);

        assertThat(roomTypes.getItems(), Matchers.nullValue());
    }

    @Test
    public void shouldBeEmptyFieldSafe() {
        final AEMFeignClient aemFeignClient = target.create(mockThrowable);

        final AEMRoomTypesResponse roomTypes = aemFeignClient.getRoomTypes("", "",
                "","");

        assertThat(roomTypes.getItems(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnEmptyObject() {
        final AEMFeignClient aemFeignClient = target.create(mockThrowable);

        final AEMRoomTypesResponse roomTypes = aemFeignClient.getRoomTypes("gb", "en",
                "someBrand","resource");

        assertThat(roomTypes.getItems(), Matchers.nullValue());
    }
}