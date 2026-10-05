package uk.co.whitbread.ohip.infrastructure.rest.controller.preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferences;
import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;
import uk.co.whitbread.ohip.domain.ports.primary.PreferenceInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.mapper.HotelPreferencesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.model.out.HotelPreferenceDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.model.out.HotelPreferencesResponseDto;

@ExtendWith(MockitoExtension.class)
class PreferenceControllerTest {

  @Mock
  private PreferenceInPort preferenceInPort;
  @Mock
  private HotelPreferencesResponseMapper hotelPreferencesMapper;
  @InjectMocks
  private PreferenceController preferenceController;



  @Test
  void getPreferencesForGroup_Ok() {
    //Arrange
    when(preferenceInPort.getPreferencesForGroup(anyString(), anyString()))
        .thenReturn(createHotelPreferencesDomain());
    when(hotelPreferencesMapper.toResponseDto(any()))
        .thenReturn(createHotelPreferencesDto());
    var responseDto = preferenceController.getPreferencesForGroup(
        "hotelId", "groupCode");
    assertNotNull(responseDto);
    assertEquals(2,responseDto.getHotelPreferences().size());
  }

  private HotelPreferencesResponse createHotelPreferencesDomain() {
    var firstPreference = HotelPreferences.builder()
        .hotelId("hotelId")
        .preferenceGroup("group")
        .description("description")
        .code("code")
        .orderSequence(2)
        .housekeeping(false)
        .build();

    var secondPreference = HotelPreferences.builder()
        .hotelId("hotelId")
        .preferenceGroup("secondGroup")
        .description("description")
        .code("secondCode")
        .orderSequence(2)
        .housekeeping(false)
        .build();

    return new HotelPreferencesResponse(List.of(firstPreference,secondPreference));
  }

  private HotelPreferencesResponseDto createHotelPreferencesDto() {
    var firstPreference = HotelPreferenceDto.builder()
        .hotelId("hotelId")
        .preferenceGroup("group")
        .description("description")
        .code("code")
        .orderSequence(2)
        .housekeeping(false)
        .build();

    var secondPreference = HotelPreferenceDto.builder()
        .hotelId("hotelId")
        .preferenceGroup("secondGroup")
        .description("description")
        .code("secondCode")
        .orderSequence(2)
        .housekeeping(false)
        .build();

    return new HotelPreferencesResponseDto(List.of(firstPreference,secondPreference));
  }

}