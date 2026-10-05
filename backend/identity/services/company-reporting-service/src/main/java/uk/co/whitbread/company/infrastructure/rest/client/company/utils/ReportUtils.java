package uk.co.whitbread.company.infrastructure.rest.client.company.utils;

import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.generateNoRecordsFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyResultsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.GuestsDto;
import uk.co.whitbread.company.domain.model.in.EmergencyReportRequest;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.domain.model.out.Booker;
import uk.co.whitbread.company.domain.model.out.Booking;
import uk.co.whitbread.company.domain.model.out.CompanyAnswers;
import uk.co.whitbread.company.domain.model.out.EmployeeAnswers;
import uk.co.whitbread.company.domain.model.out.Price;

@UtilityClass
public class ReportUtils {

  private static final String EMPTY_STRING = "";
  private static final List<String> UPCOMING_STATUS = List.of("arrival", "reserved");
  private static final String UPCOMING = "Upcoming";
  private static final String CHECKED_IN = "Checked In";
  private static final String CHECKED_OUT_STATUS = "CheckedOut";
  private static final String CHECKED_OUT = "Checked Out";
  private static final String NO_SHOW = "No Show";


  public static List<List<String>> getValuesForManagementInformationReport(List<Booking> bookings,
      ManagementInformationRequest managementInformationRequest,
      List<String> managementInformationReportColumns, List<String> miReportStatusColumns) {
    List<List<String>> rows = new ArrayList<>();
    int miReportColumnsSize = managementInformationReportColumns.size();
    (bookings).forEach(booking -> {
      List<String> cells = new ArrayList<>();
      String cdhBookingStatus = booking.bookingStatus().replaceAll("\\s", "");
      if (miReportStatusColumns.contains(cdhBookingStatus)) {
        cells.add(booking.bookingReference());
        cells.add(cdhBookingStatus.equalsIgnoreCase(CHECKED_OUT_STATUS)
            ? CHECKED_OUT : NO_SHOW);
        cells.add(String.valueOf(Optional.ofNullable(booking).map(
            Booking::totalCost).map(
            Price::amount
        ).orElse(BigDecimal.ZERO)));
        cells.add(String.valueOf(Optional.ofNullable(booking).map(
            Booking::totalCost).map(
            Price::netAmount
        ).orElse(BigDecimal.ZERO)));
        cells.add(String.valueOf(Optional.ofNullable(booking).map(
            Booking::roomCost).map(
            Price::amount
        ).orElse(BigDecimal.ZERO)));
        cells.add(String.valueOf(Optional.ofNullable(booking).map(
            Booking::roomCost).map(
            Price::netAmount
        ).orElse(BigDecimal.ZERO)));
        cells.add(String.valueOf(Optional.ofNullable(booking).map(
            Booking::upsellTotalCost).map(
            Price::amount
        ).orElse(BigDecimal.ZERO)));
        cells.add(String.valueOf(Optional.ofNullable(booking).map(
            Booking::upsellTotalCost).map(
            Price::netAmount
        ).orElse(BigDecimal.ZERO)));
        cells.add(Integer.toString(booking.rooms()));
        cells.add(Integer.toString(booking.nights()));
        cells.add(Integer.toString(booking.noOfAdults()));
        cells.add(Integer.toString(booking.noOfChildren()));
        cells.add(booking.bookingDate());
        cells.add(booking.arrivalDate());
        cells.add(booking.departureDate());
        cells.add(Integer.toString(booking.leadTime()));
        cells.add(booking.rateCategory());
        cells.add(Optional.ofNullable(booking).map(
            Booking::booker).map(
            Booker::companyName
        ).orElse(EMPTY_STRING));
        cells.add(booking.cardType());
        cells.add(booking.cardNumber());
        cells.add(booking.hotelName());
        cells.add(Optional.ofNullable(booking).map(
            Booking::booker).map(
            Booker::firstName
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::booker).map(
            Booker::lastName
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::guest).map(
            Booker::firstName
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::guest).map(
            Booker::lastName
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::booker).map(
            Booker::emailAddress
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::booker).map(
            Booker::mobile
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::guest).map(
            Booker::emailAddress
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::guest).map(
            Booker::mobile
        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::employeeAnswers).map(
            EmployeeAnswers::purchaseOrderAnswer

        ).orElse(EMPTY_STRING));
        cells.add(Optional.ofNullable(booking).map(
            Booking::employeeAnswers).map(
            EmployeeAnswers::customerReferenceAnswer
        ).orElse(EMPTY_STRING));

        if (Boolean.TRUE.equals(managementInformationRequest.showQnAcolumns())
            && null != booking.employeeAnswers() && CollectionUtils.isNotEmpty(
            booking.employeeAnswers().companyAnswers())) {
          List<CompanyAnswers> userDefinedAnswers = booking.employeeAnswers().companyAnswers();

          int companyAnswersListSize =
              (managementInformationReportColumns.size() - miReportColumnsSize)
                  + userDefinedAnswers.size();
          List<String> companyAnswersList = new ArrayList<>(
              Collections.nCopies(companyAnswersListSize, EMPTY_STRING));
          cells.addAll(companyAnswersList);
          for (CompanyAnswers userDefinedAnswer : userDefinedAnswers) {
            String title =
                !StringUtils.hasLength(userDefinedAnswer.header()) ? userDefinedAnswer.label()
                    : userDefinedAnswer.header();
            if (!managementInformationReportColumns.contains(title)) {
              managementInformationReportColumns.add(title);
            }
            int indexOfHeader = managementInformationReportColumns.indexOf(title);
            cells.set(indexOfHeader, userDefinedAnswer.answer());
          }
        }
        rows.add(cells);
      }
    });

    return rows;
  }

  public List<List<String>> getValuesForEmergencyReport(
      List<EmergencyResultsDto> emergencyResultsDtos,
      List<String> emergencyReportColumns,
      EmergencyReportRequest request) {

    List<List<String>> rows = new ArrayList<>();

    emergencyResultsDtos.forEach(emergencyResultsDto -> {
      List<String> cells = new ArrayList<>();
      if (emergencyReportColumns.contains(emergencyResultsDto.getStatus())) {
        cells.add(getOrDefault(emergencyResultsDto.getBookingReference()));
        cells.add(getOrDefault(emergencyResultsDto.getHotelArea()));
        cells.add(getOrDefault(emergencyResultsDto.getHotelName()));
        cells.add(getOrDefault(emergencyResultsDto.getHotelPostcode()));
        cells.add(getOrDefault(emergencyResultsDto.getHotelPhoneNumber()));
        cells.add(getOrDefault(emergencyResultsDto.getArrivalDate()));
        cells.add(getOrDefault(emergencyResultsDto.getDepartureDate()));
        cells.add(getOrDefault(emergencyResultsDto.getNoOfAdults()));
        cells.add(getOrDefault(emergencyResultsDto.getNoOfChildren()));
        if (!emergencyResultsDto.getGuests().isEmpty()) {
          GuestsDto firstGuest = emergencyResultsDto.getGuests().get(0);
          cells.add(getOrDefault(firstGuest.getFirstName()));
          cells.add(getOrDefault(firstGuest.getLastName()));
          cells.add(getOrDefault(firstGuest.getEmailAddress()));
          cells.add(getOrDefault(firstGuest.getTelephoneNumber(), String::valueOf));

        } else {
          cells.add(EMPTY_STRING);
          cells.add(EMPTY_STRING);
          cells.add(EMPTY_STRING);
          cells.add(EMPTY_STRING);
          cells.add(EMPTY_STRING);

        }
        cells.add(getOrDefault(
            emergencyResultsDto.getBooker() != null ? emergencyResultsDto.getBooker()
                .getEmailAddress() : EMPTY_STRING));
        cells.add(getOrDefault(
            emergencyResultsDto.getBooker() != null ? emergencyResultsDto.getBooker()
                .getTelephoneNumber() : EMPTY_STRING));
        cells.add(getOrDefault(
            emergencyResultsDto.getBooker() != null ? emergencyResultsDto.getBooker()
                .getMobileNumber() : EMPTY_STRING));

        cells.add(UPCOMING_STATUS.contains(emergencyResultsDto.getStatus().toLowerCase())
            ? getOrDefault(UPCOMING) : CHECKED_IN);

        rows.add(cells);
      }

    });
    if (rows.isEmpty()) {
      throw generateNoRecordsFoundException(request.companyId(),
          request);
    }
    return rows;
  }

  private <T> String getOrDefault(T value, Function<T, String> mapper) {
    return Optional.ofNullable(value).map(mapper).orElse(EMPTY_STRING);
  }

  private <T> String getOrDefault(T value) {
    return getOrDefault(value, Object::toString);
  }

}
