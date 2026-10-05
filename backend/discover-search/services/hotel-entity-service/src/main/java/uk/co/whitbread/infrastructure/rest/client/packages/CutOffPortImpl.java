package uk.co.whitbread.infrastructure.rest.client.packages;


import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.packages.in.CutOffMinute;
import uk.co.whitbread.domain.model.packages.in.HotelInformationExtended;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.CutOffExtras;
import uk.co.whitbread.domain.ports.secondary.AvailableCleanRoomOutPort;
import uk.co.whitbread.domain.ports.secondary.CutOffOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.infrastructure.config.PackagesProperties;


@Slf4j
@Component
@RequiredArgsConstructor
public class CutOffPortImpl implements CutOffOutPort {

  private static final String HSCKIN = "HSCKIN";
  private static final String HSCOU2 = "HSCOU2";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final Pattern WHITESPACE_BETWEEN_TIME_AND_AMPM =
      Pattern.compile("\\s+(?=[AaPp][Mm]$)");
  private final HotelInfoOutPort hotelInfoOutPort;
  private final AvailableCleanRoomOutPort cleanRoomPort;
  private final PackagesProperties packagesProperties;

  /**
   * Determines whether the ECI and LCO extras are within the cut-off window for both CIOL (Check-In
   * On-Line) and standard booking flows, based on the hotel's local time zone and configured
   * cut-off thresholds.
   *
   * <p>The method fetches the hotel's check-in time and time zone from Opera, computes the
   * current time and the arrival date-time in epoch minutes, then evaluates each cut-off condition
   * against the configured thresholds (defaulting to the application-level cut-off when none is
   * set).
   *
   * @param packagesRequest the incoming packages request containing hotel ID, start date, country
   *                        and language used to look up hotel and content information
   * @return a {@link CutOffExtras} containing four boolean flags: {@code isEciCutOffCiol},
   *        {@code isEciCutOffBooking}, {@code isLcoCutOffCiol}, {@code isLcoCutOffBooking} — each
   *        {@code true} when the arrival is still within the respective cut-off window
   */
  @Override
  public CutOffExtras isCutOffByHotel(PackagesRequest packagesRequest, CutOffMinute cutOffMinutes) {

    var operaInfo = hotelInfoOutPort.getHotelInfo(packagesRequest.getHotelId());
    var startDate = packagesRequest.getStartDate();
    var hotelCheckInTime = operaInfo.getCheckInTime();
    var timeZone = operaInfo.getHotelTimeZone();
    var zoneId = calculateZoneId(timeZone);

    var arrivalTime =
        calculateArrivalDateTimeInMinutes(startDate, hotelCheckInTime, zoneId).toEpochSecond()
            / 60;
    var currentDateTime = ZonedDateTime.now(zoneId);
    var currentDateTimeInMinutes = currentDateTime.toEpochSecond() / 60;

    //arrival is the current date
    Long availableRooms = null;
    var isCiol = Boolean.TRUE.equals(packagesRequest.getIsCiol());
    var arrivalWindowInMinutes = arrivalTime - currentDateTimeInMinutes;
    if (isCiol && (cutOffMinutes.eciCutOffMinutesCiol() != null)
        && arrivalWindowInMinutes >= 0
        && arrivalWindowInMinutes < 1440) {
      log.debug("Arrival is the current date; check whether a clean room is available in Opera.");
      availableRooms = cleanRoomPort.availableCleanRooms(packagesRequest);
    }

    return CutOffExtras.builder()
        .isEciCutOffCiol(
            isOutsideCutOff(cutOffMinutes.eciCutOffMinutesCiol(), startDate, arrivalTime,
                currentDateTimeInMinutes))
        .isEciCutOffBooking(
            isOutsideCutOff(cutOffMinutes.eciCutOffMinutesBooking(), startDate, arrivalTime,
                currentDateTimeInMinutes))
        .isLcoCutOffCiol(
            isOutsideCutOff(cutOffMinutes.lcoCutOffMinutesCiol(), startDate, arrivalTime,
                currentDateTimeInMinutes))
        .isLcoCutOffBooking(
            isOutsideCutOff(cutOffMinutes.lcoCutOffMinutesBooking(), startDate, arrivalTime,
                currentDateTimeInMinutes))
        .ciolCurrentDateAvailableRooms(availableRooms)
        .build();
  }

  /**
   * Determines whether a specific extras package code (e.g. {@code "ECI"} or {@code "LCO"}) is
   * still available to display, based on the applicable cut-off flags and the request context.
   *
   * <p>If the request originates from the manage-booking page, the method always returns
   * {@code true} (outside cut-off, always show). Otherwise, it delegates to the appropriate cut-off
   * flag inside {@code cutOff} depending on whether the request is a CIOL flow
   * ({@link PackagesRequest#getIsCiol()}) or a standard booking flow.
   *
   * @param code            the package code to evaluate — currently {@code "ECI"} or {@code "LCO"}
   * @param packagesRequest the incoming packages request, used to determine the flow type
   * @param cutOff          the pre-computed cut-off flags
   * @return {@code true} if the extra should be shown (within cut-off window or manage-booking),
   *         {@code false} otherwise
   */
  public boolean isOutsideCutOffTime(String code, PackagesRequest packagesRequest,
      CutOffExtras cutOff) {
    if (Boolean.TRUE.equals(packagesRequest.getIsManageBookingPage())) {
      return true;
    }

    if (Boolean.TRUE.equals(packagesRequest.getIsCiol())) {
      if (HSCKIN.equals(code)) {
        return cutOff.getIsEciCutOffCiol();
      } else if (HSCOU2.equals(code)) {
        return cutOff.getIsLcoCutOffCiol();
      }
    } else {
      if (HSCKIN.equals(code)) {
        return cutOff.getIsEciCutOffBooking();
      } else if (HSCOU2.equals(code)) {
        return cutOff.getIsLcoCutOffBooking();
      }
    }

    return true;
  }

  /*
   * For ECI in the CIOL flow, starting from the current date:
   * - If the number of available clean rooms is greater than 0 and less than the extras count,
   * return the number of available clean rooms.
   * - If there are no available clean rooms, return 0.
   * - Otherwise, return the number of available rooms from the Opera inventory.

   * @param cutOff extras
   * @param available rooms number
   * @return available clean rooms number
   */
  @Override
  public int availableRooms(PackagesRequest packagesRequest, CutOffExtras cutOff,
      int available) {

    var isCiol = Boolean.TRUE.equals(packagesRequest.getIsCiol());
    if (!isCiol || cutOff.getCiolCurrentDateAvailableRooms() == null) {
      return available;
    }

    var availableCleanRooms = cutOff.getCiolCurrentDateAvailableRooms();

    if (availableCleanRooms > 0) {
      return Math.min(available, availableCleanRooms.intValue());
    } else if (availableCleanRooms == 0) {
      return 0;
    }

    return available;
  }

  @Override
  public CutOffMinute getCutOffMinute(HotelInformationExtended contentInfo) {
    if (contentInfo != null && contentInfo.getExtrasCutoffs() != null) {
      return buildCutOffMinuteFromContent(contentInfo);
    }
    log.debug(
        "eciCiolCutOff, lcoCiolCutOff,eciBookingCutOff, lcoBookingCutOff",
         null, null, null, null);
    return new CutOffMinute(null, null, null, null);
  }

  private CutOffMinute buildCutOffMinuteFromContent(HotelInformationExtended contentInfo) {
    Integer eciCiolCutOff = null;
    Integer eciBookingCutOff = null;
    Integer lcoCiolCutOff = null;
    Integer lcoBookingCutOff = null;
    for (var cutOff : contentInfo.getExtrasCutoffs()) {
      if (HSCKIN.equals(cutOff.getCode())) {
        eciCiolCutOff = cutOff.getCutOffMinutesCIOL();
        eciBookingCutOff = cutOff.getCutOffMinutesBooking();
        lcoCiolCutOff = cutOff.getCutOffMinutesCIOL();
        lcoBookingCutOff = cutOff.getCutOffMinutesBooking();
      } else if (HSCOU2.equals(cutOff.getCode())) {
        lcoCiolCutOff = cutOff.getCutOffMinutesCIOL();
        lcoBookingCutOff = cutOff.getCutOffMinutesBooking();
      }
    }
    var cutOffMinutes = new CutOffMinute(eciCiolCutOff, eciBookingCutOff, lcoCiolCutOff,
        lcoBookingCutOff);
    log.debug(
        "eciCiolCutOff, lcoCiolCutOff,eciBookingCutOff, lcoBookingCutOff",
        eciCiolCutOff, lcoCiolCutOff, eciBookingCutOff, lcoBookingCutOff);
    return cutOffMinutes;
  }


  /**
   * Evaluates whether an extra is still outside cut-off based on explicit minute thresholds.
   *
   * <p>When a positive {@code minutes} value is provided, the method compares the arrival
   * date-time (hotel local time) against the current date-time in the same zone. If no explicit
   * threshold is provided, it falls back to {@link #isOutsideDefaultCutOffTime(String)}.
   *
   * @param minutes          explicit cut-off threshold in minutes; when null or non-positive,
   *                         default logic is used
   * @param startDate        arrival date string (expected format: {@code yyyy-MM-dd})
   * @param arrivalTime      arrival date-time
   * @param currentDateTimeInMinutes current date-time
   * @return {@code true} when the arrival is outside cut-off and the extra should be shown
   */
  private boolean isOutsideCutOff(Integer minutes, String startDate, long arrivalTime,
      long currentDateTimeInMinutes) {
    if (minutes != null && minutes > 0) {
      return arrivalTime - currentDateTimeInMinutes > minutes;
    }
    return isOutsideDefaultCutOffTime(startDate);
  }


  /**
   * Applies default cut-off behaviour using {@code packagesProperties.getCutOffTime()}.
   *
   * <p>If cut-off days are configured, the arrival date must be strictly after
   * {@code LocalDate.now().plusDays(cutOffTime)}. If no cut-off is configured, the method returns
   * {@code true}.
   *
   * @param arrival arrival date string in {@code yyyy-MM-dd}
   * @return {@code true} when arrival is outside the configured default cut-off window
   */
  private boolean isOutsideDefaultCutOffTime(String arrival) {
    LocalDate startDate = LocalDate.parse(arrival, DATE_FORMATTER);

    var cutOffTime = packagesProperties.getCutOffTime();
    if (cutOffTime != null) {
      LocalDate cutOffDate = LocalDate.now(ZoneId.systemDefault()).plusDays(cutOffTime);
      return startDate.isAfter(cutOffDate);
    }
    return true;
  }


  private ZoneId calculateZoneId(String hotelTimeZone) {
    try {
      return StringUtils.isNotBlank(hotelTimeZone)
          ? ZoneId.of(hotelTimeZone)
          : ZoneId.systemDefault();
    } catch (DateTimeException exception) {
      log.warn("Invalid hotelTimeZone '{}', falling back to system default timezone",
          hotelTimeZone);
      return ZoneId.systemDefault();
    }
  }

  /**
   * Builds the hotel-local arrival date-time by combining reservation start date and hotel check-in
   * time.
   *
   * @param startDate        reservation start date in {@code yyyy-MM-dd}
   * @param hotelCheckInTime check-in date-time text where only the time is used (expected format:
   *                         {@code M/d/yy, h:mm a})
   * @param zoneId           target hotel time zone
   * @return {@link ZonedDateTime} representing arrival date at hotel check-in time in hotel zone
   */
  private ZonedDateTime calculateArrivalDateTimeInMinutes(
      String startDate,          // e.g. "2026-07-15"
      String hotelCheckInTime,   // e.g. "1/1/70, 3:00 PM"
      ZoneId zoneId) {

    // Extract the time part from the hotel check-in time
    DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("M/d/yy, h:mm a", Locale.ENGLISH);

    LocalTime checkInTime =
        LocalDateTime.parse(normalizeCheckInTime(hotelCheckInTime), timeFormatter)
            .toLocalTime();

    // Parse reservation start date, using yyyy-MM-dd  format
    LocalDate arrivalDate = LocalDate.parse(startDate, DATE_FORMATTER);

    // Combine date + check-in time
    return ZonedDateTime.of(arrivalDate, checkInTime, zoneId);

  }

  /**
   * Normalizes incoming check-in text to improve parser compatibility.
   *
   * <p>This replaces non-breaking and narrow non-breaking spaces with regular spaces and trims
   * surrounding whitespace.
   *
   * @param hotelCheckInTime raw check-in text from upstream source
   * @return normalized check-in text; {@code null} when input is null
   */
  private String normalizeCheckInTime(String hotelCheckInTime) {
    if (hotelCheckInTime == null) {
      return null;
    }
    var normalized = hotelCheckInTime
        .replace('\u00A0', ' ')
        .replace('\u202F', ' ')
        .trim();
    return WHITESPACE_BETWEEN_TIME_AND_AMPM.matcher(normalized).replaceAll(" ");
  }
}
