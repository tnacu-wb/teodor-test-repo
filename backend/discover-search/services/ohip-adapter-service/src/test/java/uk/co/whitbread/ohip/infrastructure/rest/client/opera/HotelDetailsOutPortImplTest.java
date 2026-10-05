package uk.co.whitbread.ohip.infrastructure.rest.client.opera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.model.opera.out.OperaHotelDetails;
import uk.co.whitbread.ohip.domain.model.opera.out.OperaHotelDetailsList;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.mapper.HotelStatusMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.properties.OhipHotelDetailsClient;

@ExtendWith(MockitoExtension.class)
class HotelDetailsOutPortImplTest {

  @Mock
  private OhipHotelDetailsClient ohipHotelDetailsClient;

  @Mock
  private HotelStatusMapper hotelStatusMapper;

  @InjectMocks
  private HotelDetailsOutPortImpl hotelDetailsOutPort;

  @Test
  void testGetHotelsMigrationStatus_AllHotelsFoundSuccessfully() {
    // Arrange
    // hotel with onSale FALSE
    var ediparDetails = List.of(
        OperaHotelDetails.builder()
            .hotelId("EDIPAR")
            .code("OPERA")
            .description("OPERA")
            .category("PMS")
            .sequence(2)
            .hotelDetailsValues(List.of())
            .build(),
        OperaHotelDetails.builder()
            .hotelId("EDIPAR")
            .code("FALSE")
            .description("FALSE")
            .category("ONSALE")
            .sequence(2)
            .hotelDetailsValues(List.of())
            .build(),
        OperaHotelDetails.builder()
            .hotelId("EDIPAR")
            .code("VIRGIN")
            .description("VIRGIN WIFI")
            .category("WIFI")
            .sequence(1)
            .hotelDetailsValues(List.of())
            .build()
    );
    // hotel with onSale TRUE
    var lonkinDetails = List.of(
        OperaHotelDetails.builder()
            .hotelId("LONKIN")
            .code("OPERA")
            .description("OPERA")
            .category("PMS")
            .sequence(2)
            .hotelDetailsValues(List.of())
            .build(),
        OperaHotelDetails.builder()
            .hotelId("LONKIN")
            .code("TRUE")
            .description("")
            .category("ONSALE")
            .sequence(2)
            .hotelDetailsValues(List.of())
            .build()
    );

    var operaHotelDetailsListEdipar = OperaHotelDetailsList.builder().hotelDetails(ediparDetails).build();
    var operaHotelDetailsListLonkin = OperaHotelDetailsList.builder().hotelDetails(lonkinDetails).build();

    when(ohipHotelDetailsClient.getHotelDetails("EDIPAR")).thenReturn(operaHotelDetailsListEdipar);
    when(ohipHotelDetailsClient.getHotelDetails("LONKIN")).thenReturn(operaHotelDetailsListLonkin);

    when(hotelStatusMapper.toDomainModel(any(OperaHotelDetails.class)))
        .thenAnswer(invocation -> {
          OperaHotelDetails details = invocation.getArgument(0);
          return HotelStatus.builder()
              .hotelId(details.getHotelId())
              .pmsSource(details.getCode())
              .onSale(details.isOnSale())
              .build();
        });

    // Act
    var response = hotelDetailsOutPort.getHotelsMigrationStatus(Set.of("LONKIN", "EDIPAR"));

    // Assert
    assertNotNull(response);
    assertEquals(2, response.size());
    
    var resultEdipar = response.stream().filter(r -> r.getHotelId().equals("EDIPAR")).findFirst().orElse(null);
    var resultLonkin = response.stream().filter(r -> r.getHotelId().equals("LONKIN")).findFirst().orElse(null);
    
    assertNotNull(resultEdipar);
    assertNotNull(resultLonkin);
    assertEquals("EDIPAR", resultEdipar.getHotelId());
    assertEquals("OPERA", resultEdipar.getPmsSource());
    assertFalse(resultEdipar.isOnSale());
    assertEquals("LONKIN", resultLonkin.getHotelId());
    assertEquals("OPERA", resultLonkin.getPmsSource());
    assertTrue(resultLonkin.isOnSale());
  }

  @Test
  void testGetHotelsMigrationStatus_HotelNotFound_ShouldDefaultToBart() {
    // Arrange
    var validHotelDetails = List.of(
        OperaHotelDetails.builder()
            .hotelId("LONKIN")
            .code("OPERA")
            .description("OPERA")
            .category("PMS")
            .sequence(2)
            .hotelDetailsValues(List.of())
            .build(),
        OperaHotelDetails.builder()
            .hotelId("LONKIN")
            .code("TRUE")
            .description("ONSALE")
            .category("ONSALE")
            .sequence(2)
            .hotelDetailsValues(List.of())
            .build()
    );

    var operaHotelDetailsListLonkin = OperaHotelDetailsList.builder().hotelDetails(validHotelDetails).build();

    when(ohipHotelDetailsClient.getHotelDetails("LONKIN")).thenReturn(operaHotelDetailsListLonkin);
    
    when(ohipHotelDetailsClient.getHotelDetails("NONEXISTENT"))
        .thenThrow(new RuntimeException("403 Forbidden - Hotel not found"));

    when(hotelStatusMapper.toDomainModel(any(OperaHotelDetails.class)))
        .thenAnswer(invocation -> {
          OperaHotelDetails details = invocation.getArgument(0);
          return HotelStatus.builder()
              .hotelId(details.getHotelId())
              .pmsSource(details.getCode())
              .onSale(details.isOnSale())
              .build();
        });

    // Act
    var response = hotelDetailsOutPort.getHotelsMigrationStatus(Set.of("LONKIN", "NONEXISTENT"));

    // Assert
    assertNotNull(response);
    assertEquals(2, response.size());
    
    var validHotel = response.stream().filter(r -> r.getHotelId().equals("LONKIN")).findFirst().orElse(null);
    var errorHotel = response.stream().filter(r -> r.getHotelId().equals("NONEXISTENT")).findFirst().orElse(null);
    
    assertNotNull(validHotel);
    assertNotNull(errorHotel);
    assertEquals("LONKIN", validHotel.getHotelId());
    assertEquals("OPERA", validHotel.getPmsSource());
    assertTrue(validHotel.isOnSale());
    assertEquals("NONEXISTENT", errorHotel.getHotelId());
    assertEquals("BART", errorHotel.getPmsSource());
    assertTrue(errorHotel.isOnSale());
  }
}
