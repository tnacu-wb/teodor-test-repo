package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.ondemandrefreshservice.utils.MockDataReader.generateMockRsp;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OperaRestApiException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.inventory.opera.HotelInventoryClient;

@ExtendWith(MockitoExtension.class)
class HotelInventoryServiceTest {

  @Mock
  private HotelInventoryClient hotelInventoryClient;

  @InjectMocks
  private HotelInventoryService hotelInventoryOutPort;

  private static final String HOTEL_ID = "TKINPT";

  private static final LocalDate startDate = LocalDate.parse("2022-11-01");

  private static final LocalDate endDate = LocalDate.parse("2022-11-30");

  @Test
  void getHotelInventoryTest() {
    final HotelInventoryInput hotelInventoryWithAppKey = buildHotelInventoryInput();
    final HotelInventoryResponse hotelInventoryExpected = buildHotelInventoryResponse();
    Mockito.when(hotelInventoryClient.getHotelInventory(
        hotelInventoryWithAppKey.getHotelId(),
        hotelInventoryWithAppKey.getDateRangeStart(),
        hotelInventoryWithAppKey.getDateRangeEnd(),
        hotelInventoryWithAppKey.isDailyInventory(),
        hotelInventoryWithAppKey.getRoomCountRequested(),
        hotelInventoryWithAppKey.isHouseLevel()
    )).thenReturn(hotelInventoryExpected);
    final HotelInventoryInput hotelInventoryInput = buildHotelInventoryInput();
    final HotelInventoryResponse response = hotelInventoryOutPort.getHotelInventory(
        hotelInventoryInput);
    assertNotNull(response);
    assertEquals(hotelInventoryExpected.getHotelInventories().size(),
        response.getHotelInventories().size());
  }

  private HotelInventoryResponse buildHotelInventoryResponse() {
    return HotelInventoryResponse.builder()
        .hotelInventories(Collections.emptyList())
        .build();
  }

  private HotelInventoryInput buildHotelInventoryInput() {
    return HotelInventoryInput.builder()
        .houseLevel(false)
        .dailyInventory(true)
        .roomCountRequested(5)
        .dateRangeStart("2023-10-22")
        .dateRangeEnd("2023-10-25")
        .hotelId("TKINPT")
        .build();
  }

  @Test
  void getOperaHotelInventory_Success() throws IOException {

    final HotelInventoryInput hotelInventoryInput = HotelInventoryInput.builder()
        .houseLevel(false)
        .dailyInventory(true)
        .roomCountRequested(1)
        .dateRangeStart(startDate.toString())
        .dateRangeEnd(endDate.toString())
        .hotelId(HOTEL_ID)
        .build();

    final HotelInventoryResponse hotelInventoryRsp = generateMockRsp("/mock_data/inventory.json",
        HotelInventoryResponse.class);

    Mockito.when(hotelInventoryClient.getHotelInventory(
            hotelInventoryInput.getHotelId(),
            hotelInventoryInput.getDateRangeStart(),
            hotelInventoryInput.getDateRangeEnd(),
            hotelInventoryInput.isDailyInventory(),
            hotelInventoryInput.getRoomCountRequested(),
            hotelInventoryInput.isHouseLevel()
        ))
        .thenReturn(hotelInventoryRsp);
    final HotelInventory actualInventory = hotelInventoryOutPort.getOperaHotelInventory(HOTEL_ID, startDate, endDate);
    assertNotNull(actualInventory);
    final HotelInventory expectedInventory = hotelInventoryRsp.getHotelInventories().stream()
        .findFirst().orElseThrow(RuntimeException::new);
    assertEquals(expectedInventory.getRoomTypeInventories().size(),
        actualInventory.getRoomTypeInventories().size());
  }

  @Test
  void getOperaHotelInventory_Exception() throws IOException {

    final HotelInventoryInput hotelInventoryWithAppKey = HotelInventoryInput.builder()
        .houseLevel(false)
        .dailyInventory(true)
        .roomCountRequested(1)
        .dateRangeStart(startDate.toString())
        .dateRangeEnd(endDate.toString())
        .hotelId(HOTEL_ID)
        .build();

    final HotelInventoryResponse hotelInventoryRsp = generateMockRsp("/mock_data/inventory.json",
        HotelInventoryResponse.class);
    List<HotelInventory> hotelInventories = new ArrayList<>();
    hotelInventoryRsp.setHotelInventories(hotelInventories);
    Mockito.when(hotelInventoryClient.getHotelInventory(
            hotelInventoryWithAppKey.getHotelId(),
            hotelInventoryWithAppKey.getDateRangeStart(),
            hotelInventoryWithAppKey.getDateRangeEnd(),
            hotelInventoryWithAppKey.isDailyInventory(),
            hotelInventoryWithAppKey.getRoomCountRequested(),
            hotelInventoryWithAppKey.isHouseLevel()
        ))
        .thenReturn(hotelInventoryRsp);

    assertThrows(OperaRestApiException.class,
        () -> hotelInventoryOutPort.getOperaHotelInventory(HOTEL_ID, startDate, endDate));
  }
}
