package uk.co.whitbread.contentservice.roomtypes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.RoomType;
import uk.co.whitbread.contentservice.roomtypes.model.RoomTypesResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.Facilities;
import uk.co.whitbread.contentservice.roomtypes.model.aem.GridImage;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomCategory;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomImage;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomInfo;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomInfoLabel;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomLabel;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomTypeCode;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMRoomTypesService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

@ExtendWith(MockitoExtension.class)
public class RoomTypesServiceTest {

    @Mock
    private AEMRoomTypesService aemRoomTypesService;

    @Mock
    private AEMResponseConverter aemResponseConverter;

    @Mock
    private RoomTypesResponse roomTypesResponse;

    @InjectMocks
    private RoomTypesService service;

    private AEMRoomType aemRoomType_SB;

    private AEMRoomType aemRoomType_PB;

    private RoomType roomType_SB;

    private RoomType roomType_PB;

    @BeforeEach
    public void setup() {
        aemRoomType_SB = AEMRoomType.builder()
                .roomTypeCode(RoomTypeCode.builder().value("SB").build())
                .roomCategory(RoomCategory.builder().value("Standard").build())
                .roomLabel(RoomLabel.builder().value("Standard Room").build())
                .roomDescription(RoomDescription.builder().value("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.").build())
                .roomInfoLabel(RoomInfoLabel.builder().value("Room Information").build())
                .roomInfo(RoomInfo.builder().value(null).build())
                .roomImage(RoomImage.builder().value(null).build())
                .gridImage(GridImage.builder().value(null).build())
                .facilities(Facilities.builder().value(Collections.emptyList()).build())
                .build();

        aemRoomType_PB = AEMRoomType.builder()
                .roomTypeCode(RoomTypeCode.builder().value("PB").build())
                .roomCategory(RoomCategory.builder().value("Business").build())
                .roomLabel(RoomLabel.builder().value("Business Room").build())
                .roomDescription(RoomDescription.builder().value("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.").build())
                .roomInfoLabel(RoomInfoLabel.builder().value("Room Information").build())
                .roomInfo(RoomInfo.builder().value(null).build())
                .roomImage(RoomImage.builder().value(null).build())
                .gridImage(GridImage.builder().value(null).build())
                .facilities(null)
                .build();

        roomType_SB = RoomType.builder()
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

        roomType_PB = RoomType.builder()
                .roomTypeCode("PB")
                .roomCategory("Business")
                .roomLabel("Business Room")
                .roomDescription(null)
                .roomInfoLabel(null)
                .roomInfo(null)
                .roomImage(null)
                .gridImage(null)
                .facilities(null)
                .build();
    }

    @Test
    public void getAllRoomTypesSuccess() {

       List<AEMRoomType> aemRoomTypeList = new ArrayList<>();
        aemRoomTypeList.add(aemRoomType_SB);
        aemRoomTypeList.add(aemRoomType_PB);
        when(aemRoomTypesService.getRoomTypes("gb", "en", "pi"))
                .thenReturn(aemRoomTypeList);

       List<RoomType> roomTypesList = new ArrayList<>();
        roomTypesList.add(roomType_SB);
        roomTypesList.add(roomType_PB);
       when(aemResponseConverter.convertAEMResponse(aemRoomTypeList))
                .thenReturn(roomTypesList);

        final RoomTypesResponse roomTypes = service.getAllRoomTypes("gb", "en", "pi");

        assertThat(roomTypes.getRoomTypes().size()).isEqualTo(2);
        assertThat(roomTypes.getRoomTypes().get(0).getRoomTypeCode()).isEqualTo("SB");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomCategory()).isEqualTo("Standard");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomLabel()).isEqualTo("Standard Room");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomDescription()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomInfoLabel()).isEqualTo("Room Information");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomInfo()).isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
        assertThat(roomTypes.getRoomTypes().get(0).getGridImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(0).getRoomImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(0).getFacilities()).isNull();
        assertThat(roomTypes.getRoomTypes().get(1).getRoomTypeCode()).isEqualTo("PB");
        assertThat(roomTypes.getRoomTypes().get(1).getRoomCategory()).isEqualTo("Business");
        assertThat(roomTypes.getRoomTypes().get(1).getRoomLabel()).isEqualTo("Business Room");
        assertThat(roomTypes.getRoomTypes().get(1).getRoomDescription()).isNull();
        assertThat(roomTypes.getRoomTypes().get(1).getRoomInfoLabel()).isNull();
        assertThat(roomTypes.getRoomTypes().get(1).getRoomInfo()).isNull();
        assertThat(roomTypes.getRoomTypes().get(1).getGridImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(1).getRoomImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(1).getFacilities()).isNull();
    }


    @Test
    public void getRoomTypesForCodeSuccess() {

        List<AEMRoomType> aemRoomTypeList = new ArrayList<>();
        aemRoomTypeList.add(aemRoomType_SB);
        when(aemRoomTypesService.getRoomTypes("gb", "en", "pi"))
                .thenReturn(aemRoomTypeList);

        List<RoomType> roomTypesList = new ArrayList<>();
        roomTypesList.add(roomType_SB);
        when(aemResponseConverter.getRoomTypesForCode(aemRoomTypeList, "sb"))
                .thenReturn(roomTypesList);

        roomTypesResponse.setRoomTypes(roomTypesList);

        final RoomTypesResponse roomTypes = service.getRoomTypesForCode("gb", "en", "pi", "sb");
        assertThat(roomTypes.getRoomTypes().size()).isEqualTo(1);
        assertThat(roomTypes.getRoomTypes().get(0).getRoomTypeCode()).isEqualTo("SB");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomCategory()).isEqualTo("Standard");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomLabel()).isEqualTo("Standard Room");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomDescription()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomInfoLabel()).isEqualTo("Room Information");
        assertThat(roomTypes.getRoomTypes().get(0).getRoomInfo()).isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
        assertThat(roomTypes.getRoomTypes().get(0).getGridImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(0).getRoomImage()).isNull();
        assertThat(roomTypes.getRoomTypes().get(0).getFacilities()).isNull();
    }

    @Test
    public void getNonExistentRoomTypesFailure() {

        List<AEMRoomType> aemRoomTypeList = new ArrayList<>();
        aemRoomTypeList.add(aemRoomType_SB);
        when(aemRoomTypesService.getRoomTypes("gb", "en", "pi"))
                .thenReturn(aemRoomTypeList);

        List<RoomType> roomTypesList = new ArrayList<>();
        roomTypesList.add(roomType_SB);
        when(aemResponseConverter.getRoomTypesForCode(aemRoomTypeList, "fb"))
                .thenReturn(roomTypesList);

        roomTypesResponse.setRoomTypes(roomTypesList);

        final RoomTypesResponse roomTypes = service.getRoomTypesForCode("gb", "en", "pi", "fb");
        assertThat(roomTypes.getRoomTypes().isEmpty());
    }

}