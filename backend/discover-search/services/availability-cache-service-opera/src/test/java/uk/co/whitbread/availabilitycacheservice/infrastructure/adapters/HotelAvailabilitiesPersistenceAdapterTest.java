package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

class HotelAvailabilitiesPersistenceAdapterTest {

  @Mock
  private HotelJpaRepository hotelJpaRepository;

  @Mock
  private HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPostProcessor;

  @InjectMocks
  private HotelAvailabilitiesPersistenceAdapter hotelAvailabilitiesPersistenceAdapter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getHotelsByCodeAndAvailDateBetween_shouldReturnResultsForValidInput() {
    // Arrange
    SearchCriteria searchCriteria = new SearchCriteria();
    searchCriteria.setArrival("2023-01-01");
    searchCriteria.setDeparture("2023-01-10");
    searchCriteria.setHotelCodes(Arrays.asList("HOTEL1", "HOTEL2"));
    searchCriteria.setType(new String[]{"DOUBLE", "SINGLE"});

    List<HotelAvailabilitiesResultSet> resultSet = Arrays.asList(new HotelAvailabilitiesResultSet());
    List<Hotel> expectedHotels = Arrays.asList(new Hotel());

    when(hotelJpaRepository.findByHotelCodesAndMultiRoomType(
        anyList(), any(LocalDate.class), any(LocalDate.class), anySet()))
        .thenReturn(resultSet);
    when(hotelAvailabilitiesPostProcessor.performPostProcessHotelAvailabilities(searchCriteria, resultSet))
        .thenReturn(expectedHotels);

    // Act
    List<Hotel> hotels = hotelAvailabilitiesPersistenceAdapter.getHotelsByCodeAndAvailDateBetween(searchCriteria);

    // Assert
    assertEquals(expectedHotels, hotels);
    verify(hotelJpaRepository, times(1)).findByHotelCodesAndMultiRoomType(
        anyList(), any(LocalDate.class), any(LocalDate.class), anySet());
    verify(hotelAvailabilitiesPostProcessor, times(1))
        .performPostProcessHotelAvailabilities(searchCriteria, resultSet);
  }

  @Test
  void getHotelsByCodeAndAvailDateBetween_shouldThrowExceptionForInvalidDateFormat() {
    // Arrange
    SearchCriteria searchCriteria = new SearchCriteria();
    searchCriteria.setArrival("invalid-date");
    searchCriteria.setDeparture("2023-01-10");
    searchCriteria.setHotelCodes(Arrays.asList("HOTEL1"));
    searchCriteria.setType(new String[]{"DOUBLE"});

    // Act & Assert
    assertThrows(Exception.class, () ->
        hotelAvailabilitiesPersistenceAdapter.getHotelsByCodeAndAvailDateBetween(searchCriteria)
    );
    verifyNoInteractions(hotelJpaRepository);
    verifyNoInteractions(hotelAvailabilitiesPostProcessor);
  }
}
