package uk.co.whitbread.ohip.infrastructure.rest.controller.opera;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.ports.primary.HotelDetailsInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestRetryException;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.MultiHotelProperties;
import uk.co.whitbread.ohip.infrastructure.rest.controller.opera.mapper.HotelStatusDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.opera.model.out.HotelStatusDto;

@ExtendWith(MockitoExtension.class)
class OperaAdapterControllerTest {

  @InjectMocks
  OperaAdapterController operaAdapterController;

  @Mock
  private HotelDetailsInPort hotelDetailsInPort;

  @Mock
  private HotelStatusDtoMapper hotelStatusDtoMapper;

  @Mock
  private MultiHotelProperties multiHotelProperties;

  @Test
  void getOnSaleFlagFromOpera_ShouldReturnOk() {
    // Arrange
    var hotelIds = List.of("FRAMTI", "LONKIN");
    var hotelIdSet = Set.copyOf(hotelIds);
    var hotelStatusList = List.of(getHotelStatus(hotelIds.get(0)), getHotelStatus(hotelIds.get(1)));

    Mockito.when(multiHotelProperties.getNoOfAllowedHotels()).thenReturn(10);
    Mockito.when(hotelDetailsInPort.getHotelsMigrationStatus(hotelIdSet))
        .thenReturn(hotelStatusList);
    Mockito.when(hotelStatusDtoMapper.toDto(hotelStatusList.get(0)))
        .thenReturn(getHotelStatusDto(hotelIds.get(0)));
    Mockito.when(hotelStatusDtoMapper.toDto(hotelStatusList.get(1)))
        .thenReturn(getHotelStatusDto(hotelIds.get(1)));

    // Act
    var result = operaAdapterController.getOnSaleFlagFromOpera(hotelIdSet);

    // Assert
    Assertions.assertNotNull(result);
  }

  @Test
  void getOnSaleFlagFromOpera_WhenHotelIdHasInvalidCharacters_ShouldThrowOhipBadRequestRetryException() {
    // Arrange
    var invalidHotelIds = Set.of("HOTEL@123", "HOTEL#456", "HOTEL%789");

    // Act & Assert
    var exception = Assertions.assertThrows(OhipBadRequestRetryException.class, () -> {
      operaAdapterController.getOnSaleFlagFromOpera(invalidHotelIds);
    });

    Assertions.assertNotNull(exception.getMessage());
    Assertions.assertTrue(exception.getMessage().contains("Invalid HotelId format"));
  }

  @Test
  void getOnSaleFlagFromOpera_WhenTooManyHotels_ShouldThrowOhipBadRequestException() {
    // Arrange
    var tooManyHotelIds = Set.of("LONKIN", "FRAMTI", "EDIPAR", "MANOLD", "BIRHAM", "GLASGO");
    
    Mockito.when(multiHotelProperties.getNoOfAllowedHotels()).thenReturn(3);

    // Act & Assert
    var exception = Assertions.assertThrows(OhipBadRequestException.class, () -> {
      operaAdapterController.getOnSaleFlagFromOpera(tooManyHotelIds);
    });

    Assertions.assertNotNull(exception.getMessage());
  }

  @Test
  void getOnSaleFlagFromOpera_WhenValidationFails_ShouldThrowOhipBadRequestException() {
    // Arrange - Use hotel IDs that pass regex but fail HotelIdValidator
    var invalidHotelIds = Set.of("VERYLONGHOTELID123456789", "ABC", "XYZ123");
    
    Mockito.when(multiHotelProperties.getNoOfAllowedHotels()).thenReturn(10);

    // Act & Assert
    var exception = Assertions.assertThrows(OhipBadRequestException.class, () -> {
      operaAdapterController.getOnSaleFlagFromOpera(invalidHotelIds);
    });

    Assertions.assertNotNull(exception.getMessage());
  }

  private HotelStatusDto getHotelStatusDto(String hotelId) {
    return HotelStatusDto.builder()
        .hotelId(hotelId)
        .onSale(true)
        .build();
  }

  private HotelStatus getHotelStatus(String hotelId) {
    return HotelStatus.builder()
        .hotelId(hotelId)
        .pmsSource("OPERA")
        .onSale(true)
        .build();
  }

}
