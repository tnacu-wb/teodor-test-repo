package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import java.time.LocalDate;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryResponse;

public interface HotelInventoryOutPort {

  HotelInventoryResponse getHotelInventory(final HotelInventoryInput hotelInventoryInput);

  HotelInventory getOperaHotelInventory(final String hotelCode, final LocalDate startDate, final LocalDate endDate);
}
