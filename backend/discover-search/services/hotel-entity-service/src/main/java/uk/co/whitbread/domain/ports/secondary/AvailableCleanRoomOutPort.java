package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.packages.in.PackagesRequest;

public interface AvailableCleanRoomOutPort {
  long availableCleanRooms(PackagesRequest packagesRequest);
}
