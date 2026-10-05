package uk.co.whitbread.ohip.infrastructure.rest.client.changelog.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLog;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLogListType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLogType;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogListType;
import uk.co.whitbread.ohip.domain.model.changelog.out.ChangeLogResponse;

@Mapper(componentModel = "spring")
public interface ChangeLogResponseOhipMapper {

  ChangeLogResponse toDomainModel(ActivityLog activityLog);

}
