package uk.co.whitbread.ohip.infrastructure.rest.controller.udfs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.udfs.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.ohip.domain.ports.primary.UdfsInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.mapper.UdfsDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.model.in.UdfsRequestDto;

@ExtendWith(MockitoExtension.class)
class UdfsControllerTest {

  @InjectMocks
  UdfsController udfsController;
  @Mock
  private UdfsDomainMapper udfsMapper;
  @Mock
  private UdfsInPort udfsInPort;

  @Test
  void updateUdfs__ShouldReturnOk() {
    // Arrange
    doNothing().when(udfsInPort).updateUdfs(any());
    when(udfsMapper.toDomainModel(any())).thenReturn(
        UpdateReservationUdfsRequest.builder().build());
    var udfRequestDto = UdfsRequestDto.builder().build();
    //act
    udfsController.updateCharacterUdfs(udfRequestDto);
    // Assert
    verify(udfsInPort).updateUdfs(any());
  }

}
