package uk.co.whitbread.ohip.domain.logic;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.ohip.domain.model.udfs.in.CharacterUdf;
import uk.co.whitbread.ohip.domain.model.udfs.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.ohip.domain.ports.secondary.UdfsOutPort;

@ExtendWith(MockitoExtension.class)
class UdfsInPortImplTest {

  @InjectMocks
  private UdfsInImpl udfsInPort;

  @Mock
  private UdfsOutPort udfsOutPort;

  @ParameterizedTest
  @MethodSource("provideTestData")
  void updateUdfs__ShouldReturnOK(UpdateReservationUdfsRequest req, String hotelId,
      Set<String> reservationIds,
      List<CharacterUdf> udfs) {
    // Arrange
    doNothing().when(udfsOutPort).updateUdfs(hotelId, reservationIds, udfs);

    // Act
    udfsInPort.updateUdfs(req);

    // Assert
    verify(udfsOutPort).updateUdfs(hotelId, reservationIds, udfs);
    verifyNoMoreInteractions(udfsOutPort);
  }

  private static Stream<Arguments> provideTestData() {
    return Stream.of(

        Arguments.of(UpdateReservationUdfsRequest.builder().hotelId("Hotel1")
                .reservationIds(Set.of("Res1", "Res2"))
                .udfs(List.of(new CharacterUdf("UDF1", "Value1"))).build(),
            "Hotel1", Set.of("Res1", "Res2"), List.of(new CharacterUdf("UDF1", "Value1"))),

        Arguments.of(UpdateReservationUdfsRequest.builder().build(), null, null, null)
    );
  }
}