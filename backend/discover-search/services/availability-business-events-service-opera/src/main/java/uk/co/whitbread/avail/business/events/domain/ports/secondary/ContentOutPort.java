package uk.co.whitbread.avail.business.events.domain.ports.secondary;

import java.util.List;

public interface ContentOutPort {

  List<String> getHotelsWithCityTax();
}
