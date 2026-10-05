package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelData;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LabelDataDto;

@Mapper(componentModel = "spring")
public interface LabelDataResponseMapper {

  default List<LabelDataDto> toDto(List<LabelData> labelDataResponse) {
    if (labelDataResponse == null) {
      return new ArrayList<>();
    }
    return labelDataResponse.stream()
        .map(response -> LabelDataDto.builder()
            .value(response.getValue())
            .key(response.getKey())
            .build())
        .toList();
  }

}
