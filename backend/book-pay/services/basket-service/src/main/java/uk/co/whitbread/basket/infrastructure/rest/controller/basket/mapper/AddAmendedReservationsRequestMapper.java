package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.AddAmendedReservationsRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddAmendedReservationsRequestDto;

@Mapper(componentModel = "spring")
public interface AddAmendedReservationsRequestMapper {

  AddAmendedReservationsRequest toDomainModel(AddAmendedReservationsRequestDto addAmendedReservationsRequestDto);

}
