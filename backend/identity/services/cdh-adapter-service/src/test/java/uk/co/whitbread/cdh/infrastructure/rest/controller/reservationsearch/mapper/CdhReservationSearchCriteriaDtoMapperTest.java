package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in.CdhReservationSearchCriteriaDto;

import static org.junit.Assert.assertEquals;

@Slf4j
class CdhReservationSearchCriteriaDtoMapperTest{

  private CdhReservationSearchCriteriaDtoMapper mapper;

  @BeforeEach
  void init(){
        mapper = Mappers.getMapper(CdhReservationSearchCriteriaDtoMapper.class);
  }

  @Test
  void shouldMapCdhReservationSearchCrirteriaDtoToModel(){
    var dtoData= CdhReservationSearchCriteriaDto.builder()
          .bookingReference("testRef")
          .lastName("testname")
          .hotelCode("EWMTI")
          .hotelName("testHotel")
          .build();

          var result=mapper.toModel(dtoData);

          assertEquals(dtoData.getLastName(),result.getLastName());
          assertEquals(dtoData.getBookingReference(),result.getBookingReference());
          assertEquals(dtoData.getHotelCode(),result.getHotelCode());
          assertEquals(dtoData.getHotelName(),result.getHotelName());
  }
}
