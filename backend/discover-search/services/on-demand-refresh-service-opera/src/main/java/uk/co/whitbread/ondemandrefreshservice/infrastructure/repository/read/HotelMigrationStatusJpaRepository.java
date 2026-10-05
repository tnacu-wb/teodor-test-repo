package uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;

@Repository
public interface HotelMigrationStatusJpaRepository extends
    JpaRepository<HotelMigrationStatusEntity, String> {
  Set<HotelMigrationStatusEntity> findByPmsSource(final String pmsSource);

  @Query(value =
      "SELECT * FROM avail_cache.hotel_migration_status t WHERE t.pms_source = ?1 OR t.on_sale = ?2;",
      nativeQuery = true)
  Set<HotelMigrationStatusEntity> findByPmsSourceOrOnSale(final String pmsSource, final boolean onSale);

  @Query(value =
      "select distinct hotel_code from avail_cache.hotel_migration_status hms where pms_source = 'OPERA' and on_sale = false;",
      nativeQuery = true)
  Set<String> getOperaHotelCodes();
}
