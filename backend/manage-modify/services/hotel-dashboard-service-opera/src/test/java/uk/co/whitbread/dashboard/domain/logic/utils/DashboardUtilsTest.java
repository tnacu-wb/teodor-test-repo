package uk.co.whitbread.dashboard.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.encodeImagePath;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.ResultsDto;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.RoomsDto;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoImage;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.domain.model.in.RoomTypeInformation;
import uk.co.whitbread.dashboard.domain.model.out.Content;

class DashboardUtilsTest {

    private static final int MAX_DAYS = 14;

    @Test
    void isCheckedInTest() {
        assertTrue(DashboardUtils.isCheckedIn("CHECKEDIN"));
    }

    @Test
    void arrivalDateIsNull() {
        final LocalDate fortnight = null;
        assertFalse(DashboardUtils.isArrivalDateWithinRange(fortnight, MAX_DAYS));
    }

    @Test
    void setRoomsAndGuests() {
        final Content content = new Content();
        final ResultsDto reservationDetails = new ResultsDto();
        final RoomsDto roomsDto = new RoomsDto();
        roomsDto.setRoomType("DB");
        final List<RoomsDto> rooms = List.of(roomsDto);
        reservationDetails.setRooms(rooms);

        RoomType roomTypes = RoomType.builder().roomTypes(
            List.of(RoomTypeInformation.builder().roomTypeCode(List.of("DOUBLE"))
                .build())).build();

        DashboardUtils.setRoomsAndGuests(content, reservationDetails, roomTypes);
        assertEquals(1, content.getRooms().size());
    }

    @Test
    void getHotelImage() {
        final HotelInfo hotelInfo = new HotelInfo();
        final InfoImage infoImage = new InfoImage();
        infoImage.setFileReference("/path/with%20some%20spaces");
        hotelInfo.setImages(List.of(infoImage));
        final String environment = "environment";
        final String result = DashboardUtils.getHotelImage(hotelInfo, environment);
        assertEquals(environment + "/path/with%20some%20spaces", result);
    }

    @Test
    void arrivalDateIsWithinRange() {
        final LocalDate today = LocalDate.now();
        final LocalDate fortnight = today.plusDays(MAX_DAYS);
        assertTrue(DashboardUtils.isArrivalDateWithinRange(fortnight, MAX_DAYS));
    }

    @Test
    void arrivalDateIsNotWithinRange() {
        final LocalDate today = LocalDate.now();
        final LocalDate nextMonth = today.plusMonths(1);
        assertFalse(DashboardUtils.isArrivalDateWithinRange(nextMonth, MAX_DAYS));
    }

    @Test
    void encodeSpaces() {
        final String imagePath = "/path/with some spaces";
        final String expectedResult = "/path/with%20some%20spaces";
        final String result = encodeImagePath(imagePath);
        assertEquals(expectedResult, result);
    }
}