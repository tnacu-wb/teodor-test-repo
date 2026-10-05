package uk.co.whitbread.contentservice.roomtypes.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.RoomType;
import uk.co.whitbread.contentservice.roomtypes.model.RoomTypesResponse;
import uk.co.whitbread.contentservice.roomtypes.service.RoomTypesService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RoomTypesControllerTest {

    @InjectMocks
    private RoomTypesController controller;

    @Mock
    private RoomTypesService roomTypesService;

    @Test
    public void getRoomTypesSuccessResponse() {

        RoomType roomType_SB = RoomType.builder()
                .roomTypeCode("SB")
                .roomCategory("Standard")
                .roomLabel("Standard Room")
                .roomDescription("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.")
                .roomInfoLabel("Room Information")
                .roomInfo("This hotel is being refurbished, we're sorry for any inconvenience.")
                .roomImage(null)
                .gridImage(null)
                .facilities(null)
                .build();

        RoomType roomType_PB = RoomType.builder()
                .roomTypeCode("PB")
                .roomCategory("Standard")
                .roomLabel("Business Room")
                .roomDescription(null)
                .roomInfoLabel(null)
                .roomInfo(null)
                .roomImage(null)
                .gridImage(null)
                .facilities(null)
                .build();

        RoomTypesResponse roomTypesResponse = new RoomTypesResponse();
        List<RoomType> roomTypesList = new ArrayList<>();
        roomTypesList.add(roomType_SB);
        roomTypesList.add(roomType_PB);
        roomTypesResponse.setRoomTypes(roomTypesList);
        when(roomTypesService.getAllRoomTypes("gb", "en", "pi")).thenReturn(roomTypesResponse);


        final RoomTypesResponse roomTypes = controller.getRoomTypes(CountryCode.gb, LanguageCode.en, BrandCode.pi);
        assertThat(roomTypes.getRoomTypes().get(0).getRoomTypeCode().equalsIgnoreCase("SB")).isTrue();
        assertThat(roomTypes.getRoomTypes().get(1).getRoomTypeCode().equalsIgnoreCase("PB")).isTrue();
    }

    @Test
    public void getRoomTypesForSpecificCodeSuccessResponse() {

        RoomType roomType_SB = RoomType.builder()
                .roomTypeCode("SB")
                .roomCategory("Standard")
                .roomLabel("Standard Room")
                .roomDescription("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.")
                .roomInfoLabel("Room Information")
                .roomInfo("This hotel is being refurbished, we're sorry for any inconvenience.")
                .roomImage(null)
                .gridImage(null)
                .facilities(null)
                .build();

        RoomTypesResponse roomTypesResponse = new RoomTypesResponse();
        List<RoomType> roomTypesList = new ArrayList<>();
        roomTypesList.add(roomType_SB);
        roomTypesResponse.setRoomTypes(roomTypesList);
        when(roomTypesService.getRoomTypesForCode("gb", "en", "pi", "sb"))
                .thenReturn(roomTypesResponse);

        final RoomTypesResponse roomTypes = controller.getRoomTypesForCode(CountryCode.gb, LanguageCode.en, BrandCode.pi, "sb");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomTypeCode()).isEqualTo("SB");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomCategory()).isEqualTo("Standard");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomLabel()).isEqualTo("Standard Room");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomDescription()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomInfoLabel()).isEqualTo("Room Information");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomInfo()).isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(0).getGridImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(0).getFacilities()).isNull();
    }

    @Test
    public void getNotAvailableRoomTypes() {

        RoomTypesResponse roomTypesResponse = new RoomTypesResponse();
        List<RoomType> roomTypesList = new ArrayList<>();
        roomTypesResponse.setRoomTypes(roomTypesList);
        when(roomTypesService.getRoomTypesForCode("gb", "en", "pi", "rb"))
                .thenReturn(roomTypesResponse);

        final RoomTypesResponse roomTypes = controller.getRoomTypesForCode(CountryCode.gb, LanguageCode.en, BrandCode.pi, "rb");
        assertThat(roomTypes.getRoomTypes().isEmpty()).isTrue();
    }
}
