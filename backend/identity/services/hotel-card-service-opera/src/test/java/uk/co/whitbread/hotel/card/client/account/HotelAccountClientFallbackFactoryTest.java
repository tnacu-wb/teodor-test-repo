package uk.co.whitbread.hotel.card.client.account;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = HotelAccountClientFallbackFactory.class)
public class HotelAccountClientFallbackFactoryTest {

    @Autowired
    private HotelAccountClientFallbackFactory hotelAccountClientFallbackFactory;

    private Throwable mockThrowable;

    @BeforeEach
    public void setup() {
        mockThrowable = mock(Throwable.class);
    }

    @Test
    public void getHotelAccountClient_returnNull() {
        assertThat(hotelAccountClientFallbackFactory
                .create(mockThrowable)
                .getCustomer("customer-id", "session-id", false)).isNull();
    }
}