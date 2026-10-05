package uk.co.whitbread.reservation.domain.logic;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.ports.primary.TableReservationInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.TableReservationOutPort;

@Service
@Slf4j
@Data
public class TableReservationInPortImpl implements TableReservationInPort {

  TableReservationOutPort tableReservationOutPortImpl;

  @Autowired
  public TableReservationInPortImpl(TableReservationOutPort tableReservationOutPortImpl) {
    this.tableReservationOutPortImpl = tableReservationOutPortImpl;
  }


  @Override
  public ResponseEntity<String> check() {
    return tableReservationOutPortImpl.check();
  }

  @Override
  public SessionResponse slots(String from, String until, String time, String adult,
      String children,
      String siteId) {
    return tableReservationOutPortImpl.slots(from, until, time, adult, children, siteId);
  }

  @Override
  public OutletResponse outlets(String location, String id) {
    return tableReservationOutPortImpl.outlets(location, id);
  }

  @Override
  public EventsResponse events(EventOrEquiryRequest eventRequest) {

    return tableReservationOutPortImpl.events(eventRequest);
  }

  @Override
  public EventsResponse getEvent(String eventId) {

    return tableReservationOutPortImpl.getEvent(eventId);

  }

  @Override
  public EnquiryResponse createEnquiry(EventOrEquiryRequest eventOrEquiryRequest) {

    return tableReservationOutPortImpl.createEnquiry(eventOrEquiryRequest);

  }

  @Override
  public EnquiryResponse getEnquiry(String enquiryId) {

    return tableReservationOutPortImpl.getEnquiry(enquiryId);

  }

  @Override
  public OccasionsResponse occasions(String from, String until, String siteId) {
    return tableReservationOutPortImpl.occasions(from, until, siteId);
  }

  @Override
  public MenuResponse getMenu(String siteId, String from, String until, String time,
      String ocassionId) {
    return tableReservationOutPortImpl.getMenu(siteId, from, until, time, ocassionId);

  }
}
