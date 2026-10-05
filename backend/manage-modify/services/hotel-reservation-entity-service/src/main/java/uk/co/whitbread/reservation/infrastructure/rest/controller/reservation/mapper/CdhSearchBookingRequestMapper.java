package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CdhSearchBookingsRequestDto;

@Mapper(componentModel = "spring")
public interface CdhSearchBookingRequestMapper {

  final Integer NO_OF_DAYS = 364;

  @Mapping(target = "arrivalDateFrom", expression = "java(toArrivalDateFromModel(cdhSearchBookingsRequestDto))")
  @Mapping(target = "arrivalDateTo", expression = "java(toArrivalDateToModel(cdhSearchBookingsRequestDto))")
  @Mapping(target = "cancellationDate", expression = "java(toCancellationDateModel(cdhSearchBookingsRequestDto))")
  @Mapping(target = "bookerPhone", expression = "java(toBookerPhoneModel(cdhSearchBookingsRequestDto))")
  CdhSearchBookingsRequest toModel(CdhSearchBookingsRequestDto cdhSearchBookingsRequestDto);

  default String toArrivalDateFromModel(CdhSearchBookingsRequestDto requestDto) {

    var modifiedDate = LocalDate.now().minusDays(NO_OF_DAYS);

    if (requestDto.getArrivalDateFrom() == null || requestDto.getArrivalDateFrom().equals("")) {
      requestDto.setArrivalDateFrom(String.valueOf(modifiedDate));
    }
    return requestDto.getArrivalDateFrom();
  }

  default String toArrivalDateToModel(CdhSearchBookingsRequestDto requestDto) {

    var modifiedDate = LocalDate.now().plusDays(NO_OF_DAYS);

    if (requestDto.getArrivalDateTo() == null || requestDto.getArrivalDateTo().equals("")) {
      requestDto.setArrivalDateTo(String.valueOf(modifiedDate));
    }
    return requestDto.getArrivalDateTo();
  }

  default String toCancellationDateModel(CdhSearchBookingsRequestDto requestDto) {

    if (requestDto.getCancellationDate() == null || requestDto.getCancellationDate().equals("")) {
      requestDto.setCancellationDate(null);
    }
    return requestDto.getCancellationDate();
  }

  default String toBookerPhoneModel(CdhSearchBookingsRequestDto requestDto) {

    if (!StringUtils.isBlank(requestDto.getBookerPhone())
        && Character.isWhitespace(requestDto.getBookerPhone().charAt(0))) {
      requestDto.setBookerPhone("+" + requestDto.getBookerPhone().trim());
    }
    return requestDto.getBookerPhone();
  }
}
