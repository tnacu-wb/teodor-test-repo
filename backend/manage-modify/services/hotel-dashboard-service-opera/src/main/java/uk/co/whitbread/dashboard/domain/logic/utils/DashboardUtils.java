package uk.co.whitbread.dashboard.domain.logic.utils;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.ResultsDto;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.RoomsDto;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoImage;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.domain.model.in.RoomTypeInformation;
import uk.co.whitbread.dashboard.domain.model.out.Content;
import uk.co.whitbread.dashboard.domain.model.out.Room;

@Slf4j
public class DashboardUtils {

  public static final String BOOKING_CHANNEL_MOBILE = "MOBILE";
  public static final String BOOKING_CHANNEL_CBT = "CBT";
  private static final String[] CHECKED_STATUSES = {"ARRIVED", "INHOUSE", "CHECKED_IN", "CHECKEDIN", "CHECKED IN"};
  private static final String PRE_CHECKED_IN = "PRE_CHECKED_IN";

  private DashboardUtils() {
  }

  public static boolean isCheckedIn(final String status) {
    return Arrays.stream(CHECKED_STATUSES).anyMatch(
        checkedInStatus -> checkedInStatus.equalsIgnoreCase(status));
  }

  public static boolean isCiolPerformed(final String status) {
    return PRE_CHECKED_IN.equalsIgnoreCase(status);
  }


  public static String getHotelImage(final HotelInfo hotelInfo, final String environment) {
    final String fileReference = ofNullable(hotelInfo)
        .map(HotelInfo::getImages)
        .flatMap(infoImages ->
            infoImages.stream()
                .findFirst())
        .orElse(InfoImage.builder().build())
        .getFileReference();

    return environment + encodeImagePath(fileReference);
  }

  public static String encodeImagePath(final String imagePath) {
    if (imagePath == null) {
      return "";
    }
    return imagePath.replace(" ", "%20");
  }
  
  public static void setRoomsAndGuests(final Content content, final ResultsDto reservationDetails,
      RoomType roomTypeInfo) {
    content.setRooms(new ArrayList<>());
    
    ofNullable(reservationDetails)
        .map(ResultsDto::getRooms)
        .ifPresent(reservationRooms ->
            reservationRooms.forEach(
                room -> assignRoomsAndCalculateGuests(room, content, roomTypeInfo)));
  }

  private static void assignRoomsAndCalculateGuests(final RoomsDto room, final Content content,
      final RoomType roomTypeInfo) {
    assignRooms(room, content, roomTypeInfo);
    calculateGuests(room, content);
  }

  private static void assignRooms(final RoomsDto reservationRoom, final Content content, final RoomType roomTypeInfo) {
    content.getRooms().add(convertRoom(reservationRoom, roomTypeInfo));
  }

  protected static Room convertRoom(final RoomsDto reservationRoom, RoomType roomTypeInfo) {
    String roomTypeLabel = roomTypeInfo.getRoomTypes().stream()
        .filter(roomTypeInformation -> roomTypeInformation.getRoomTypeCode().contains(reservationRoom.getRoomType()))
        .findAny()
        .map(RoomTypeInformation::getRoomLabel)
        .orElse(reservationRoom.getRoomType());

    return Room.builder().type(roomTypeLabel).build();
  }

  private static void calculateGuests(final RoomsDto room, final Content content) {
    try {
      final int adults = Integer.parseInt(room.getNoOfAdults());
      final int children = Integer.parseInt(room.getNoOfChildren());

      content.setGuests(content.getGuests() + adults + children);
    } catch (final NumberFormatException e) {
      log.error("Invalid number format: {}", e.getMessage());
    }
  }

  public static boolean isArrivalDateWithinRange(final LocalDate arrivalDate, final int maxDays) {
    if (Objects.isNull(arrivalDate)) {
      return false;
    }

    final LocalDate today = LocalDate.now();

    final long dayDifference = ChronoUnit.DAYS.between(today, arrivalDate);

    return dayDifference >= 0 && dayDifference <= maxDays;
  }

}
