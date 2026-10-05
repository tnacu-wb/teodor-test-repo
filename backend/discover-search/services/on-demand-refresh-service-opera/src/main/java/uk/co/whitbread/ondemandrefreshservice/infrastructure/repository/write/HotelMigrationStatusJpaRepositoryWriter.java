package uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;

@Repository
public interface HotelMigrationStatusJpaRepositoryWriter extends
    JpaRepository<HotelMigrationStatusEntity, String> {


}
