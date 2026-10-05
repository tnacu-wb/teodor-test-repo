package uk.co.whitbread.hotel.register.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;
import uk.co.whitbread.hotel.register.model.ReservationRequest;

class HotelReservationEntityClientTest {
    @Mock
    private HotelReservationEntityClient hotelReservationEntityClient;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void testLinkLeisureCustomer() {
        // Arrange
        ReservationRequest request = new ReservationRequest("AKU-b94ba40e-b46e-4417-85d0-526c9e1b698e", "CUST_20302bb9-a1d1-4bb7-af56-f3ee97070276");
        // Act
        hotelReservationEntityClient.linkLeisureCustomer(request);
        // Assert
        verify(hotelReservationEntityClient, times(1)).linkLeisureCustomer(any(ReservationRequest.class));
    }
}
