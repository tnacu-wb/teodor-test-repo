package uk.co.whitbread.infrastructure.rest.client.hotel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.logic.EventCodes;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferences;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.infrastructure.rest.client.hotel.mapper.RoomTypesInfoMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelInfoOutPortImpl implements HotelInfoOutPort {

  private final OhipClient ohipClient;
  private final HotelInfoMapper hotelInfoMapper;
  private final RoomTypesInfoMapper roomTypesInfoMapper;
  private final ContentServiceOutPort contentServiceOutPort;
  private static final String CATEGORY = "main";
  private static final String LABEL = "booking.confirmation.specialOccasion.list";
  private static final Map<String, String> LANGUAGE_MAP = Map.of("en", "gb", "de", "de");

  @Override
  public HotelInfo getHotelInfo(String hotelId) {
    return hotelInfoMapper.toDomainModel(ohipClient.getHotelInfo(hotelId));
  }

  @Override
  public Map<String, RoomTypesInfo> getRoomTypesInfoByHotelIds(List<String> hotelIds) {
    return hotelIds.parallelStream()
        .map(ohipClient::getRoomTypesInfo)
        .map(roomTypesInfoMapper::toDomainModel)
        .collect(Collectors.toMap(RoomTypesInfo::getHotelId, Function.identity()));

  }

  @Override
  public HotelPreferencesResponse getHotelPreferences(String hotelId, String groupCode,
      String language) {

    var ohipResponse = ohipClient.getPreferencesForGroup(hotelId, groupCode);
    var labels = contentServiceOutPort
        .getPreferencesLabels(LANGUAGE_MAP.get(language), language, CATEGORY, LABEL).get(LABEL);
    var responseModel = hotelInfoMapper.toDomainModel(ohipResponse);
    return processLabels(responseModel, labels);
  }

  private HotelPreferencesResponse processLabels(
      HotelPreferencesResponse hotelPreferencesResponse,
      String labels) {

    var labelList = Arrays.stream(labels.split(",")).toList();
    for (HotelPreferences hotelPreferences : hotelPreferencesResponse.getHotelPreferences()) {

      if (EventCodes.isValidEventCode(hotelPreferences.getCode())) {
        var codePosition = EventCodes.valueOf(hotelPreferences.getCode()).ordinal();

        if (isValidLabelsIndex(labelList.size(), codePosition)) {
          hotelPreferences.setLabel(labelList.get(codePosition));
        }
      }
    }
    return hotelPreferencesResponse;

  }

  private boolean isValidLabelsIndex(int size, int index) {
    return index >= 0 && index < size;
  }

}
