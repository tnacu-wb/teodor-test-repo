package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.RoomEntity;

@Repository
public interface RoomJpaRepository extends JpaRepository<RoomEntity, String> {

}
