package uk.co.whitbread.avail.business.events.domain.ports.secondary;

import java.math.BigDecimal;

public interface OcdAdapterOutPort {

  BigDecimal getAmountAfterTax(String hotelId, String arrivalDate, String departureDate,
      Integer adults, String ratePlanCode, String roomType);
}
