package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.out.*;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.ItemInventoryRequestDto;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ItemInventoryMapperImpl.class)
class ItemInventoryMapperTest {

  private static final String ECI_NAME_DESCRIPTION = "Early Check In";
  private static final String ECI_CODE = "ECI";
  private static final String LC2_NAME_DESCRIPTION = "Late Check Out 2pm";
  private static final String LC2_CODE = "LC2";
  private static final String START_DATE = "2024-10-28";
  private static final String END_DATE = "2024-10-30";
  private static final String HOTEL_ID = "MANOLD";

  @Autowired
  ItemInventoryMapper itemInventoryMapper;

  @Test
  void toDomainModel__ShouldReturnOK() {
    // Arrange
    ItemInventoryRequestDto itemInventoryRequestDto = ItemInventoryRequestDto.builder()
            .startDate(START_DATE).endDate(END_DATE).build();

    // Act
    var hotelInventoryRequest = itemInventoryMapper.toDomainModel(HOTEL_ID,
            itemInventoryRequestDto);

    // Assert
    assertThat(hotelInventoryRequest.getHotelId(), is(HOTEL_ID));
    assertThat(hotelInventoryRequest.getStartDate(), is(START_DATE));
    assertThat(hotelInventoryRequest.getEndDate(), is(END_DATE));

  }

  @Test
  void toDto__ShouldReturnOK() {
    // Arrange
    ItemInventoryResponse itemInventory = createItemInventoryResponse();

    // Act
    var inventoryResponseDto = itemInventoryMapper.toDto(itemInventory);

    // Assert
    assertThat(inventoryResponseDto.getItemsInventory(), hasSize(2));
    assertThat(inventoryResponseDto.getItemsInventory().get(0).getCode(), is(ECI_CODE));
    assertThat(inventoryResponseDto.getItemsInventory().get(0).getName(), is(ECI_NAME_DESCRIPTION));
    assertThat(inventoryResponseDto.getItemsInventory().get(0).getDescription(), is(ECI_NAME_DESCRIPTION));
    assertThat(inventoryResponseDto.getItemsInventory().get(0).getInventories().get(0).getAvailable(), is(10));
    assertThat(inventoryResponseDto.getItemsInventory().get(0).getInventories().get(0).getTotal(), is(10));
    assertThat(inventoryResponseDto.getItemsInventory().get(0).getInventories().get(0).getDate(), is(START_DATE));
    assertThat(inventoryResponseDto.getItemsInventory().get(1).getCode(), is(LC2_CODE));
    assertThat(inventoryResponseDto.getItemsInventory().get(1).getName(), is(LC2_NAME_DESCRIPTION));
    assertThat(inventoryResponseDto.getItemsInventory().get(1).getDescription(), is(LC2_NAME_DESCRIPTION));
    assertThat(inventoryResponseDto.getItemsInventory().get(1).getInventories().get(0).getAvailable(), is(9));
    assertThat(inventoryResponseDto.getItemsInventory().get(1).getInventories().get(0).getTotal(), is(10));
    assertThat(inventoryResponseDto.getItemsInventory().get(1).getInventories().get(0).getDate(), is(START_DATE));
  }

  private ItemInventoryResponse createItemInventoryResponse() {

    ItemInventory itemInventoryECI = ItemInventory.builder()
            .description(ECI_NAME_DESCRIPTION)
            .code(ECI_CODE)
            .name(ECI_NAME_DESCRIPTION)
            .inventories(Collections.singletonList(buildInventoryAvailability(10)))
        .build();

    ItemInventory itemInventoryLC2 = ItemInventory.builder()
            .description(LC2_NAME_DESCRIPTION)
            .code(LC2_CODE)
            .name(LC2_NAME_DESCRIPTION)
            .inventories(Collections.singletonList(buildInventoryAvailability(9)))
            .build();

    return ItemInventoryResponse.builder()
            .itemsInventory(Arrays.asList(itemInventoryECI, itemInventoryLC2)).build();

  }

  private static InventoryAvailability buildInventoryAvailability(Integer available) {
    return InventoryAvailability.builder()
            .date(START_DATE)
            .total(10)
            .available(available)
            .build();
  }
}
