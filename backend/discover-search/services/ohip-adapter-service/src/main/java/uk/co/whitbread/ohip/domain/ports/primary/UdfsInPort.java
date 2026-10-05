package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.udfs.in.UpdateReservationUdfsRequest;

public interface UdfsInPort {

  void updateUdfs(UpdateReservationUdfsRequest request);
}
