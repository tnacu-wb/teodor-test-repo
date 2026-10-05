package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import java.math.BigDecimal;

public interface OcdAdapterOutPort {

  BigDecimal getAmountAfterTax(String hotelId, String arrivalDate, String departureDate,
      Integer adults, String ratePlanCode, String roomType);
}
