package uk.co.whitbread.digitalkey.infrastructure.rest.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.CharacterUdfOutPortImpl;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;

@ExtendWith(MockitoExtension.class)
class CharacterUdfOutPortImplTest {

    @Test
    void testUpdateUdfc20DelegatesToClient() {
        // Arrange
        OhipAdapterClient mockClient = Mockito.mock(OhipAdapterClient.class);
        CharacterUdfOutPortImpl service = new CharacterUdfOutPortImpl(mockClient);

        UpdateUdfc20Request request = new UpdateUdfc20Request();

        // Act
        service.updateUdfc20(request);

        // Assert
        Mockito.verify(mockClient, Mockito.times(1)).updateUdfc20(request);
    }

}