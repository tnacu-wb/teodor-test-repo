package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;

public interface HotelMigrationStatusRefreshOutPort {
    void refreshOperaHotelMigrationStatus();
    HotelMigrationStatusEntity getHotelMigrationStatus(final String hotelCode);

}
