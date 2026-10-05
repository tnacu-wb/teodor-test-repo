package uk.co.whitbread.ohip.infrastructure.rest.controller.availability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilityByIdsResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilityByIdsSearchCriteriaDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilitySearchCriteriaDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.RestrictionsByDateRangeMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestV3Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RestrictionsByDateRangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.RestrictionsByDateRangeDto;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelAvailabilityControllerTest {

  @Mock
  private HotelAvailabilityInPort hotelAvailabilityPort;

  @Mock
  private RestrictionsByDateRangeMapper restrictionsByDateRangeMapper;

  @InjectMocks
  private HotelAvailabilityController controller;

  @Mock
  AvailabilityByIdsSearchCriteriaDomainMapper availabilityByIdsSearchCriteriaMapper;

  @Mock
  AvailabilityByIdsResponseDtoMapper availabilityByIdsResponseMapper;

  @Test
  void testGetMultiHotelRestrictionsByDateRange_success() {
    // Arrange
    var hotelIds = List.of("hotel1", "hotel2");
    var requestDto = new RestrictionsByDateRangeRequestDto("2022-03-01", "2022-03-02");
    var domainModel = RestrictionsByDateRangeSearchCriteria.builder().build();
    var resultDomain = new RestrictionsByDateRangeResult();
    var resultDto = new RestrictionsByDateRangeDto();

    when(restrictionsByDateRangeMapper.toDomainModel(anyString(), any()))
        .thenReturn(domainModel);
    when(hotelAvailabilityPort.getMultiHotelRestrictionsByDateRange(any()))
        .thenReturn(List.of(resultDomain, resultDomain));
    when(restrictionsByDateRangeMapper.toDto(any()))
        .thenReturn(resultDto);

    // Act
    List<RestrictionsByDateRangeDto> results =
        controller.getMultiHotelRestrictionsByDateRange(hotelIds, requestDto);

    // Assert
    assertNotNull(results);
    assertEquals(2, results.size());
    assertEquals(resultDto, results.get(0));
    assertEquals(resultDto, results.get(1));

    // Verify
    verify(restrictionsByDateRangeMapper, times(2))
        .toDomainModel(anyString(), any());
    verify(hotelAvailabilityPort, times(1))
        .getMultiHotelRestrictionsByDateRange(any());
    verify(restrictionsByDateRangeMapper, times(2))
        .toDto(any());
  }

  @Test
  void testGetMultiHotelRestrictionsByDateRange_throwsInternalServerError() {
    // Arrange
    var hotelIds = List.of("hotel1");
    var requestDto = new RestrictionsByDateRangeRequestDto();

    when(hotelAvailabilityPort.getMultiHotelRestrictionsByDateRange(any()))
        .thenThrow(new RuntimeException("Unexpected error"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        controller.getMultiHotelRestrictionsByDateRange(hotelIds, requestDto));

    assertEquals("Unexpected error", exception.getMessage());
  }

  @Test
  void getHotelAvailabilityByIdsV3ShouldReturnDto() {
    var req = AvailabilityByIdsRequestV3Dto.builder().build();
    var availabilityByIdsSearchCriteriaV2 = AvailabilityByIdsSearchCriteriaV2.builder().build();
    var availabilityByIdsResponseV2Dto = AvailabilityByIdsResponseV2Dto.builder().build();
    var availabilityByIdsResultV2= AvailabilityByIdsResultV2.builder().build();

    when(availabilityByIdsSearchCriteriaMapper.toV3DomainModel(req)).thenReturn(availabilityByIdsSearchCriteriaV2);
    when(hotelAvailabilityPort.getHotelAvailabilityByIdsV2(availabilityByIdsSearchCriteriaV2)).thenReturn(availabilityByIdsResultV2);
    when(availabilityByIdsResponseMapper.toV2Dto(availabilityByIdsResultV2)).thenReturn(availabilityByIdsResponseV2Dto);

    var result = controller.getHotelAvailabilityByIdsV3(req);

    assertNotNull(result);
  }

  @Test
  void getHotelAvailabilityByIdsV2ShouldReturnDto() {
    var req = AvailabilityByIdsRequestV2Dto.builder().build();
    var availabilityByIdsSearchCriteriaV2 = AvailabilityByIdsSearchCriteriaV2.builder().build();
    var availabilityByIdsResponseV2Dto = AvailabilityByIdsResponseV2Dto.builder().build();
    var availabilityByIdsResultV2= AvailabilityByIdsResultV2.builder().build();

    when(availabilityByIdsSearchCriteriaMapper.toV2DomainModel(req)).thenReturn(availabilityByIdsSearchCriteriaV2);
    when(hotelAvailabilityPort.getHotelAvailabilityByIdsV2(availabilityByIdsSearchCriteriaV2)).thenReturn(availabilityByIdsResultV2);
    when(availabilityByIdsResponseMapper.toV2Dto(availabilityByIdsResultV2)).thenReturn(availabilityByIdsResponseV2Dto);

    var result = controller.getHotelAvailabilityByIdsV2(req);

    // Assert
    assertNotNull(result);
  }

}
