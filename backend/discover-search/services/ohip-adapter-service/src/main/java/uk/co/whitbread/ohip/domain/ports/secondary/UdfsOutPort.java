package uk.co.whitbread.ohip.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.ohip.domain.model.udfs.in.CharacterUdf;

public interface UdfsOutPort {

  void updateUdfs(String hotelId, Set<String> reservationIds, List<CharacterUdf> udfs);
}
