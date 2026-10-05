package uk.co.whitbread.reservation.infrastructure.rest.client.reservation;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResp;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Company;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Site;
import uk.co.whitbread.reservation.domain.model.out.slots.Dates;
import uk.co.whitbread.reservation.domain.model.out.slots.Session;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionDates;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.SlotsResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Times;
import uk.co.whitbread.reservation.domain.ports.secondary.TableReservationOutPort;
import uk.co.whitbread.reservation.infrastructure.config.ZonalConfigurationProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.exception.ResponseParsingException;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.service.TableBookingEnum;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.exception.RequestValidator;


@Slf4j
@Service
public class TableReservationOutPortImpl implements TableReservationOutPort {

  private final RestClient restClient;

  private final ZonalConfigurationProperties zonalConfigurationProperties;


  public TableReservationOutPortImpl(
      @Qualifier("zonalRestClient") RestClient restClient,
      ZonalConfigurationProperties zonalConfigurationProperties) {
    this.restClient = restClient;
    this.zonalConfigurationProperties = zonalConfigurationProperties;
  }

  public static boolean isTimeInRange(String timeString, String startTime, String endTime) {

    LocalTime time = LocalTime.parse(timeString);
    LocalTime startTime1 = LocalTime.parse(startTime);
    LocalTime endTime1 = LocalTime.parse(endTime);
    return !time.isBefore(startTime1) && !time.isAfter(endTime1);
  }

  // Utility method to check if a string can be parsed as an integer
  private static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  @Override
  public ResponseEntity<String> check() {
    return restClient.get()
        .uri(zonalConfigurationProperties.getBaseUri() + "/events/v1/check")
        .retrieve()
        .toEntity(String.class);
  }

  @Override
  public SessionResponse slots(String from, String until, String time, String adult,
      String children,
      String siteId) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
        zonalConfigurationProperties.getBaseUri() + zonalConfigurationProperties.getSlotsUri()
    );
    if (StringUtils.isNotBlank(from)) {
      uriBuilder.queryParam(TableBookingEnum.FROM.getValue(), from);
    }
    if (StringUtils.isNotBlank(until)) {
      uriBuilder.queryParam(TableBookingEnum.UNTIL.getValue(), until);
    }
    if (StringUtils.isNotBlank(time)) {
      uriBuilder.queryParam("time", time);
    }
    if (StringUtils.isNotBlank(adult) && isInteger(adult)) {
      uriBuilder.queryParam("adults", adult);
    }
    if (StringUtils.isNotBlank(children) && isInteger(children)) {
      uriBuilder.queryParam("children", children);
    }
    if (StringUtils.isNotBlank(siteId)) {
      uriBuilder.queryParam(TableBookingEnum.SITE_ID.getValue(), siteId);
    }

    UriComponents slotUriComponent = uriBuilder.build();
    SlotsResponse slotsResponse = restClient.get()
        .uri(slotUriComponent.toUriString())
        .retrieve()
        .body(SlotsResponse.class);
    try {
      assert slotsResponse != null;
      return convertToSessionResponse(slotsResponse);
    } catch (Exception e) {
      log.error("Error while parsing slots");
      throw new ResponseParsingException("Error parsing SlotsResponse", e);
    }
  }

  @Override
  public OutletResponse outlets(String location, String id) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
        zonalConfigurationProperties.getBaseUri() + zonalConfigurationProperties.getOutletsUri()
    );
    if (StringUtils.isNotBlank(location)) {
      uriBuilder.queryParam("location", location);
    }
    UriComponents outletUriComponent = uriBuilder.build();
    OutletResponse outletResponse = restClient.get()
        .uri(outletUriComponent.toUriString())
        .retrieve()
        .body(OutletResponse.class);
    return getCompany(outletResponse, id);
  }

  private OutletResponse getCompany(OutletResponse outletResponse, String id) {
    OutletResponse filteredResponse = new OutletResponse();
    if (outletResponse == null || outletResponse.getCompanies() == null) {
      return filteredResponse;
    }

    Optional<Company> foundCompany = outletResponse.getCompanies().stream()
        .filter(company -> company != null && company.getSites() != null)
        .filter(company -> company.getSites().stream()
            .filter(Objects::nonNull)
            .anyMatch(site -> {
              String aztecSiteReference = site.getAztecSiteReference();
              return StringUtils.isNotBlank(aztecSiteReference)
                  && aztecSiteReference.equalsIgnoreCase(id);
            })).findFirst();
    Optional<Site> foundSite = foundCompany.stream()
        .flatMap(company -> company.getSites().stream())
        .filter(Objects::nonNull)
        .filter(site -> {
          String aztecSiteReference = site.getAztecSiteReference();
          return StringUtils.isNotBlank(aztecSiteReference) && aztecSiteReference.equalsIgnoreCase(
              id);
        }).findFirst();
    if (foundCompany.isPresent() && foundSite.isPresent()) {
      foundCompany.get().setSites(List.of(foundSite.get()));
      filteredResponse.setCompanies(List.of(foundCompany.get()));
    }
    return filteredResponse;
  }


  @Override
  public EventsResponse events(EventOrEquiryRequest eventRequest) {
    String validationName = RequestValidator.isValidName(eventRequest.getFirstname());
    if (StringUtils.isNotBlank(validationName)) {
      throw new IllegalArgumentException(validationName);
    }

    String validateName = RequestValidator.isValidName(eventRequest.getLastname());
    if (StringUtils.isNotBlank(validateName)) {
      throw new IllegalArgumentException(validateName);
    }

    String validatePhoneNumber = RequestValidator.isValidPhoneNumber(
        eventRequest.getTelephoneNumber());
    if (StringUtils.isNotBlank(validatePhoneNumber)) {
      throw new IllegalArgumentException(validatePhoneNumber);
    }
    String validateEmail = RequestValidator.isValidEmailAddress(eventRequest.getEmailAddress());
    if (StringUtils.isNotBlank(validateEmail)) {
      throw new IllegalArgumentException(validateEmail);
    }

    String validateAdults = RequestValidator.isValidInteger(eventRequest.getAdults());
    if (!validateAdults.isEmpty()) {
      throw new IllegalArgumentException("Provide a proper Count of Adults.");
    }

    String validateChildren = RequestValidator.isValidInteger(eventRequest.getChildren());
    if (!validateChildren.isEmpty()) {
      throw new IllegalArgumentException("Provide a proper Count of Children.");
    }

    String uri = zonalConfigurationProperties.getBaseUri()
        + zonalConfigurationProperties.getEventsUri();
    return restClient.post()
        .uri(uri)
        .body(eventRequest)
        .retrieve()
        .body(EventsResponse.class);
  }

  @Override
  public EventsResponse getEvent(String eventId) {
    UriComponents eventUriComponent = UriComponentsBuilder.fromUriString(
            zonalConfigurationProperties.getBaseUri()
                + zonalConfigurationProperties.getEventsUri() + "/" + eventId)
        .build();
    return restClient.get()
        .uri(eventUriComponent.toUriString())
        .retrieve()
        .body(EventsResponse.class);
  }

  @Override
  public EnquiryResponse createEnquiry(EventOrEquiryRequest eventOrEquiryRequest) {
    String validationName = RequestValidator.isValidName(eventOrEquiryRequest.getFirstname());
    if (StringUtils.isNotBlank(validationName)) {
      throw new IllegalArgumentException(validationName);
    }

    String validateName = RequestValidator.isValidName(eventOrEquiryRequest.getLastname());
    if (StringUtils.isNotBlank(validateName)) {
      throw new IllegalArgumentException(validateName);
    }

    String validatePhoneNumber = RequestValidator.isValidPhoneNumber(
        eventOrEquiryRequest.getTelephoneNumber());
    if (StringUtils.isNotBlank(validatePhoneNumber)) {
      throw new IllegalArgumentException(validatePhoneNumber);
    }

    String validateEmail = RequestValidator.isValidEmailAddress(
        eventOrEquiryRequest.getEmailAddress());
    if (StringUtils.isNotBlank(validateEmail)) {
      throw new IllegalArgumentException(validateEmail);
    }

    String validateAdults = RequestValidator.isValidInteger(eventOrEquiryRequest.getAdults());
    if (!validateAdults.isEmpty()) {
      throw new IllegalArgumentException("Provide a proper Count of Adults.");
    }

    String validateChildren = RequestValidator.isValidInteger(eventOrEquiryRequest.getChildren());
    if (!validateChildren.isEmpty()) {
      throw new IllegalArgumentException("Provide a proper Count of Children.");
    }

    String url = zonalConfigurationProperties.getBaseUri()
        + zonalConfigurationProperties.getEnquiriesUri();
    return restClient.post()
        .uri(url)
        .body(eventOrEquiryRequest)
        .retrieve()
        .body(EnquiryResponse.class);
  }

  @Override
  public EnquiryResponse getEnquiry(String enquiryId) {
    UriComponents eventUriComponent = UriComponentsBuilder.fromUriString(
            zonalConfigurationProperties.getBaseUri()
                + zonalConfigurationProperties.getEnquiriesUri() + "/" + enquiryId)
        .queryParam("enquiryId", enquiryId).build();
    return restClient.get()
        .uri(eventUriComponent.toUriString())
        .retrieve()
        .body(EnquiryResponse.class);
  }

  @Override
  public OccasionsResponse occasions(String from, String until, String siteId) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
        zonalConfigurationProperties.getBaseUri()
            + zonalConfigurationProperties.getOccasionsUri());
    // Check if from is not null before adding the query parameter
    if (StringUtils.isNotBlank(from)) {
      uriBuilder.queryParam(TableBookingEnum.FROM.getValue(), from);
    }

    // Check if until is not null before adding the query parameter
    if (StringUtils.isNotBlank(until)) {
      uriBuilder.queryParam(TableBookingEnum.UNTIL.getValue(), until);
    }

    // Check if siteId is not null before adding the query parameter
    if (StringUtils.isNotBlank(siteId)) {
      uriBuilder.queryParam(TableBookingEnum.SITE_ID.getValue(), siteId);
    }

    UriComponents occasionUriComponent = uriBuilder.build();
    log.info("Occasions Uri::::{}", occasionUriComponent.toUriString());
    return restClient.get()
        .uri(occasionUriComponent.toUriString())
        .retrieve()
        .body(OccasionsResponse.class);
  }

  @Override
  public MenuResponse getMenu(String siteId, String from, String until, String time,
      String occassionId) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(
        zonalConfigurationProperties.getBaseUri()
            + zonalConfigurationProperties.getMenusUri());
    if (StringUtils.isNotBlank(siteId)) {
      uriBuilder.queryParam(TableBookingEnum.SITE_ID.getValue(), siteId);
    }
    if (StringUtils.isNotBlank(from)) {
      uriBuilder.queryParam(TableBookingEnum.FROM.getValue(), from);
    }

    if (StringUtils.isNotBlank(until)) {
      uriBuilder.queryParam(TableBookingEnum.UNTIL.getValue(), until);
    }
    // Check if time is not null before adding the query parameter
    if (StringUtils.isNotBlank(time)) {
      uriBuilder.queryParam("time", time);
    }
    if (StringUtils.isNotBlank(occassionId)) {
      uriBuilder.queryParam("occasionId", occassionId);
    }
    UriComponents menuUriComponent = uriBuilder.build();
    MenuResponse response = restClient.get()
        .uri(menuUriComponent.toUriString())
        .retrieve()
        .body(MenuResponse.class);
    if (response != null && response.getMenus() != null) {
      List<MenuResp> filteredMenus = response.getMenus().stream()
          .filter(MenuResp::isAvailable)
          .toList();
      MenuResponse menuResponse = new MenuResponse();
      menuResponse.setMenus(filteredMenus);
      return menuResponse;
    }

    return response;
  }

  SessionResponse convertToSessionResponse(SlotsResponse slotsResponse) {

    ArrayList<SessionDates> sessionDatesList = new ArrayList<>();
    for (Dates slotsDate : slotsResponse.getDates()) {
      SessionDates sessionDates = new SessionDates();
      sessionDates.setDate(slotsDate.getDate());

      List<Times> slotTimes = slotsDate.getTimes();
      List<Times> breakfast = slotTimes.stream()
          .filter(t -> isTimeInRange(t.getTime(), "06:00", "11:15")).toList();

      List<Times> lunch = slotTimes.stream()
          .filter(t -> isTimeInRange(t.getTime(), "11:30", "16:45")).toList();
      List<Times> dinner = slotTimes.stream()
          .filter(t -> isTimeInRange(t.getTime(), "17:00", "23:30")).toList();
      Session sessionsTimes = new Session();
      sessionsTimes.setLunch(lunch);

      sessionsTimes.setBreakFast(breakfast);
      sessionsTimes.setDinner(dinner);

      sessionDates.setSession(sessionsTimes);
      sessionDates.setDinnerAvailable(
          !dinner.stream().filter(Times::isAvailable).toList().isEmpty());
      sessionDates.setBreakFastAvailable(
          !breakfast.stream().filter(Times::isAvailable).toList().isEmpty());
      sessionDates.setLunchAvailable(!lunch.stream().filter(Times::isAvailable).toList().isEmpty());
      sessionDatesList.add(sessionDates);
    }
    return new SessionResponse(sessionDatesList);
  }
}
