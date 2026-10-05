package uk.co.whitbread.reservation.domain.ports.primary;


import org.springframework.http.ResponseEntity;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;

public interface TableReservationInPort {

  ResponseEntity<String> check();

  SessionResponse slots(String from, String until, String time, String adult, String children,
      String siteId);

  OutletResponse outlets(String location, String id);

  EventsResponse events(EventOrEquiryRequest eventRequest);

  EventsResponse getEvent(String eventId);


  EnquiryResponse createEnquiry(EventOrEquiryRequest eventOrEquiryRequest);

  EnquiryResponse getEnquiry(String enquiryId);

  OccasionsResponse occasions(String from, String until, String siteId);

  MenuResponse getMenu(String siteId, String from, String until, String time, String occassionId);


}
