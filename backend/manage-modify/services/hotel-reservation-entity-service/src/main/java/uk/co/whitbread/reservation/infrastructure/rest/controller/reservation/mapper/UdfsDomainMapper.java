package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.UDFC20;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.CharacterUdf;
import uk.co.whitbread.reservation.domain.model.in.CiolStatusEnum;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UdfsRequestDto;

@Mapper(componentModel = "spring")
public interface UdfsDomainMapper {

  @Mapping(target = "udfs", expression = "java(toUdfc20Model(udfsRequestDto.getCiolStatus()))")
  UpdateReservationUdfsRequest toDomainModel(UdfsRequestDto udfsRequestDto);

  default List<CharacterUdf> toUdfc20Model(CiolStatusEnum ciolStatusEnum) {
    return List.of(new CharacterUdf(UDFC20, ciolStatusEnum.name()));
  }

}