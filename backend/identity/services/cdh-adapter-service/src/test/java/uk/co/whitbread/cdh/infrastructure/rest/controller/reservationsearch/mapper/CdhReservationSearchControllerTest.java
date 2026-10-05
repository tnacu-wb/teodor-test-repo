package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.ports.primary.CdhReservationSearchInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.CdhReservationSearchController;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.CdhReservationSearchDto;

@ExtendWith(MockitoExtension.class)
class CdhReservationSearchControllerTest {

  @InjectMocks
  CdhReservationSearchController cdhReservationSearchController;

  @Mock
  CdhReservationSearchCriteriaDtoMapper cdhReservationSearchCriteriaDtoMapper;

  @Mock
  CdhReservationSearchInPort cdhReservationSearchInPort;

  @Mock
  ReservationSearchDtoMapper reservationSearchDtoMapper;

  @Test
  void test_getReservationById() {
    String reservationId = "testId";
    CdhReservationSearchDto response = CdhReservationSearchDto.builder().totalSize(50).build();

    ReservationSearch reservationSearch = ReservationSearch.builder().totalSize(50).build();

    when(cdhReservationSearchInPort.getReservationById(reservationId)).thenReturn(reservationSearch);
    when(reservationSearchDtoMapper.toDto(reservationSearch)).thenReturn(response);

    CdhReservationSearchDto cdhReservationSearchDto = cdhReservationSearchController.getReservationById(reservationId);

    assertThat(cdhReservationSearchDto.getTotals()).isEqualTo(response.getTotals());
  }

  @Test
  void test_getReservationSearch() {
    CdhReservationSearchCriteriaDto criteriaDto = CdhReservationSearchCriteriaDto.builder()
        .hotelCode("EWMTI")
        .bookingReference("testRef")
        .lastName("TestLastName")
        .build();

    ReservationSearchCriteria domainCriteria = ReservationSearchCriteria.builder()
        .hotelCode("EWMTI")
        .bookingReference("testRef")
        .lastName("TestLastName")
        .build();

    ReservationSearch reservationSearch = ReservationSearch.builder()
        .totalSize(100)
        .build();

    CdhReservationSearchDto expectedResponse = CdhReservationSearchDto.builder()
        .totalSize(100)
        .build();

    when(cdhReservationSearchCriteriaDtoMapper.toModel(criteriaDto)).thenReturn(domainCriteria);
    when(cdhReservationSearchInPort.getReservationSearch(domainCriteria)).thenReturn(reservationSearch);
    when(reservationSearchDtoMapper.toDto(reservationSearch)).thenReturn(expectedResponse);

    CdhReservationSearchDto result = cdhReservationSearchController.getReservationSearch(criteriaDto);

    assertThat(result).isNotNull();
    assertThat(result.getTotalSize()).isEqualTo(100);
    assertThat(result).isEqualTo(expectedResponse);
  }




}
