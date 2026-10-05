package uk.co.whitbread.digitalkey.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CharacterUdfOutPort;
import org.mockito.ArgumentCaptor;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterUdfInPortImplTest {

    @Test
    void testUpdateUdfc20_buildsRequestAndCallsOutPort() {
        // Arrange
        CharacterUdfOutPort outPort = mock(CharacterUdfOutPort.class);
        CharacterUdfInPortImpl inPort = new CharacterUdfInPortImpl(outPort);

        String reservationId = "RES123";
        String hotelId = "H100";
        String ciolStatus = "COMPLETED";

        ArgumentCaptor<UpdateUdfc20Request> captor = ArgumentCaptor.forClass(UpdateUdfc20Request.class);

        // Act
        inPort.updateUdfc20(reservationId, hotelId, ciolStatus);

        // Assert
        verify(outPort).updateUdfc20(captor.capture());
        UpdateUdfc20Request req = captor.getValue();

        assertNotNull(req);
        assertEquals(Set.of(reservationId), req.getReservationIds());
        assertEquals(hotelId, req.getHotelId());
        assertNotNull(req.getUdfs());
        assertEquals(1, req.getUdfs().size());
    }
}
