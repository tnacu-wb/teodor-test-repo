package uk.co.whitbread.payapp.infrastructure.rest.client.cdh.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;

@Mapper(componentModel = "spring")
public interface PibaTetheredGuidResponseMapper {

  List<TetheredGuidResponse> toDto(List<PibaTetheredGuidResponse> companySpendingResponse);
}
