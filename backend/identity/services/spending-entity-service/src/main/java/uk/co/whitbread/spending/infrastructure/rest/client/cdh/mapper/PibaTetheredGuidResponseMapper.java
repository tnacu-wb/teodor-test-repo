package uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TetheredGuidResponse;

@Mapper(componentModel = "spring")
public interface PibaTetheredGuidResponseMapper {

  List<TetheredGuidResponse> toDto(List<PibaTetheredGuidResponse> companySpendingResponse);
}
