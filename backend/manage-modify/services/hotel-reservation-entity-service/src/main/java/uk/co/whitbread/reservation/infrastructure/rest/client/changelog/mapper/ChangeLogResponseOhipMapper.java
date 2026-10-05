package uk.co.whitbread.reservation.infrastructure.rest.client.changelog.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeLogResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeLogTypeDto;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogResponse;
import uk.co.whitbread.reservation.domain.model.out.ChangeLogType;

@Mapper(componentModel = "spring")
public interface ChangeLogResponseOhipMapper {

  ChangeLogResponse toModel(ChangeLogResponseDto changeLogResponseDto);

}
