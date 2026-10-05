package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestReservationDto;

@Mapper(componentModel = "spring")
public interface CreateBasketRequestMapper {
  CreateBasketRequest toDomainModel(CreateBasketRequestDto createBasketRequest);

  @Mapping(source = "userInfoDto.userId",                     target = "userId")
  @Mapping(source = "userInfoDto.idContext",                  target = "idContext")
  @Mapping(source = "reservationBasketInfoDto.migratedResNo", target = "migratedResNo")
  @Mapping(source = "reservationBasketInfoDto.originalBasketId", target = "originalBasketId")
  @Mapping(source = "reservationBasketInfoDto.linkAmendReservations", target = "linkAmendReservations")
  @Mapping(source = "basketStatus",                        target = "basketStatus")
  @Mapping(source = "paymentInfoDto.paymentOption",           target = "paymentOption")
  @Mapping(source = "paymentInfoDto.paymentId",               target = "paymentId")
  @Mapping(source = "channelInfoDto.channel",                 target = "channel")
  @Mapping(source = "channelInfoDto.subChannel",              target = "subChannel")
  @Mapping(source = "basketItemInfoDto.basketItems",          target = "basketItems")
  @Mapping(source = "basketItemInfoDto.basketItemTypes",      target = "basketItemTypes")
  CreateBasketRequest toDomainModel(CreateBasketRequestReservationDto dto);

}
