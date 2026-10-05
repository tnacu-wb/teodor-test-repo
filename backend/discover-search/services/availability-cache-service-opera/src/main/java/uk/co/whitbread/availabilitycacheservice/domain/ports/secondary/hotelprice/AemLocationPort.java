package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice;

import java.util.Collection;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.Location;

public interface AemLocationPort {

  Collection<Location> getAemLocations();
}
