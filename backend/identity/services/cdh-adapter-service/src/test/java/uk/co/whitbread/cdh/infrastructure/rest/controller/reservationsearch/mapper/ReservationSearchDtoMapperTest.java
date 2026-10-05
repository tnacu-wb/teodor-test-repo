package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.model.booking.out.Results;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class ReservationSearchDtoMapperTest {

  private ReservationSearchDtoMapper mapper;

  @BeforeEach
  void init(){
     mapper = Mappers.getMapper(ReservationSearchDtoMapper.class);
  }

  @Test
  void shouldMapReservationSearchModelToDto() {

    var modelData = ReservationSearch.builder()
            .totalResults(5)
            .searchResults(1)
            .results(Collections.singletonList(Results.builder().accountType("testAccountType").bookingType("testType").build()))
            .build();

    var result = mapper.toDto(modelData);

    assertNotNull(result);
    assertEquals(modelData.getTotalResults(), result.getTotalResults());
    assertEquals(modelData.getSearchResults(), result.getSearchResults());
  }
}
