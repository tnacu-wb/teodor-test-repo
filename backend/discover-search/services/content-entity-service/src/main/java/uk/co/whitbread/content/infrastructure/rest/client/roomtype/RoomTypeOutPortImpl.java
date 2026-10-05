package uk.co.whitbread.content.infrastructure.rest.client.roomtype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.domain.ports.secondary.RoomTypeOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AemHotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabItem;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.adapter.RoomTypeAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeDto;

@Slf4j
@RequiredArgsConstructor
public class RoomTypeOutPortImpl implements RoomTypeOutPort {

  private final RoomTypeAemClient roomTypeAemClient;
  private final RoomTypeRequestMapper roomTypeRequestMapper;
  private final RoomTypeMapper roomTypeMapper;
  private final AemClient aemClient;

  @Override
  public RoomType getRoomType(RoomTypeRequest roomTypeRequest) {
    log.debug("Entered getRoomType with country={}, language={}, brand={}, hotelId={}",
            roomTypeRequest.getCountry(), roomTypeRequest.getLanguage(),
            roomTypeRequest.getBrand(), roomTypeRequest.getHotelId());

    var request = roomTypeRequestMapper.toDto(roomTypeRequest);
    var roomTypeAem = roomTypeAemClient.getRoomType(request);
    if (request.getHotelId() != null && !request.getHotelId().isEmpty()) {
      var hotelInformation = aemClient.getSingleHotelInformation(
              request.getCountry(), request.getLanguage(), request.getHotelId());
      if (hotelInformation != null) {
        updateRoomImageAndDescription(roomTypeAem, hotelInformation);
      }
    }

    log.trace("Fetched room types from AEM: roomType={}", roomTypeAem);
    return roomTypeMapper.toDomainModel(roomTypeAem);
  }

  private void updateRoomImageAndDescription(RoomTypeDto roomType, AemHotelInformationDto hotelInformation) {
    List<TabItem> tabItems = hotelInformation.getHotelRoomConfiguration().getTabItems();
    roomType.getRoomTypes().forEach(room -> tabItems.stream()
            .filter(tab -> verifyRoomTypeCode(room.getRoomTypeCode(), tab.getRoomTypeCode()))
            .findFirst()
            .ifPresent(tab -> {
              if (tab.getFileReference() != null && !tab.getFileReference().isEmpty()) {
                room.setRoomImage(tab.getFileReference());
              }
              if (tab.getRateGridRoomDescription() != null && !tab.getRateGridRoomDescription().isEmpty()) {
                room.setRoomDescription(tab.getRateGridRoomDescription());
              }
            }));
  }

  private boolean verifyRoomTypeCode(String roomTypeCode, String hotelInfoRoomTypeCode) {
    if (roomTypeCode != null && hotelInfoRoomTypeCode != null
            && !roomTypeCode.isEmpty() && !hotelInfoRoomTypeCode.isEmpty()) {
      List<String> roomTypesCode = Arrays.asList(roomTypeCode.replace(" ", "").split(","));
      List<String> hotelInfoRoomTypes = Arrays.asList(hotelInfoRoomTypeCode.replace(" ", "").split(","));
      List<String> commonElements = new ArrayList<>(roomTypesCode);
      commonElements.retainAll(hotelInfoRoomTypes);
      return !commonElements.isEmpty();
    }
    return false;
  }
}
