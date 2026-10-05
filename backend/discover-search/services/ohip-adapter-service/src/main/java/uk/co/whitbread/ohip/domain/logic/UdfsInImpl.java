package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.udfs.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.ohip.domain.ports.primary.UdfsInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.UdfsOutPort;

@Slf4j
@RequiredArgsConstructor
public class UdfsInImpl implements UdfsInPort {

  private final UdfsOutPort udfsOutPort;

  @Override
  public void updateUdfs(UpdateReservationUdfsRequest request) {
    udfsOutPort.updateUdfs(request.getHotelId(), request.getReservationIds(),
        request.getUdfs());
  }

}
