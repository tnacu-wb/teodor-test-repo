package uk.co.whitbread.booking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.booking.domain.logic.BookingInPortImpl;
import uk.co.whitbread.booking.domain.logic.CheckInOnlineLogic;
import uk.co.whitbread.booking.domain.logic.DigitalKeyFeature;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.booking.domain.ports.primary.BookingInPort;
import uk.co.whitbread.booking.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.PaymentInfoOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.ReservationOutPort;
import uk.co.whitbread.booking.domain.properties.CheckInOnlineProperties;
import uk.co.whitbread.booking.domain.properties.DigitalKeyProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.BasketOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.BookingOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper.StayRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper.StayResponseMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.BookingClient;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.HotelAccountClient;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.CdhClient;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.CdhOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.cdh.mapper.UpcomingBookingsCdhResponseMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.CdhAdapterClient;
import uk.co.whitbread.booking.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.HotelInfoOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.PaymentInfoOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.ReservationOutPortImpl;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice.InvoiceDownloadService;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ContentRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ReservationResponseConverter;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties.ReservationsManagerProperties;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.service.TokenService;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public BookingInPort bookingInPort(
      final BookingOutPort bookingOutPort,
      final ReservationOutPort reservationOutPort,
      final ReservationsManagerProperties reservationsManagerProperties,
      final CheckInOnlineLogic checkInOnlineLogic,
      final UnleashWrapper<FeatureFlag> unleashWrapper,
      final CdhOutPort cdhOutPort,
      final DigitalKeyFeature digitalKeyFeature,
      final AuthenticatedUserService authenticatedUserService) {
    return new BookingInPortImpl(bookingOutPort, reservationOutPort,
          reservationsManagerProperties.getOldBookingThreshold(), checkInOnlineLogic, unleashWrapper,
          cdhOutPort, authenticatedUserService, digitalKeyFeature);
  }

  @Bean
  public CdhOutPort cdhOutPort(
        final CdhClient cdhClient,
        final UpcomingBookingsCdhResponseMapper upcomingBookingsResponseMapper) {
    return new CdhOutPortImpl(cdhClient, upcomingBookingsResponseMapper);
  }

  @Bean
  public BookingOutPort bookingOutPort(
      final StayResponseMapper responseMapper,
      final StayRequestMapper requestMapper,
      final TokenService tokenService,
      final BookingClient bookingClient,
      final HotelAccountClient hotelAccountClient,
      final ContentClient contentClient,
      final AuthenticatedUserService authenticatedUserService) {
    return new BookingOutPortImpl(responseMapper, requestMapper,
        tokenService, bookingClient, hotelAccountClient, contentClient, authenticatedUserService);
  }

  @Bean
  public ReservationOutPort operaBookingOutPort(
      final ReservationClient reservationClient,
      final BasketClient basketClient,
      final ContentClient contentClient,
      final ReservationResponseConverter reservationResponseMapper,
      final ReservationRequestMapper reservationRequestMapper,
      final ContentRequestMapper mealsRequestMapper,
      final AuthenticatedUserService authenticatedUserService,
      final OhipAdapterClient ohipAdapterClient,
      final CdhAdapterClient cdhAdapterClient,
      final InvoiceDownloadService invoiceDownloadService) {

    return new ReservationOutPortImpl(reservationClient, basketClient,
        contentClient, reservationResponseMapper,
        reservationRequestMapper, mealsRequestMapper,
        authenticatedUserService, ohipAdapterClient, cdhAdapterClient, invoiceDownloadService);
  }

  @Bean
  public BasketOutPort basketOutPort(final BasketClient basketClient) {
    return new BasketOutPortImpl(basketClient);
  }

  @Bean
  public HotelInfoOutPort hotelInfoOutPort(final OhipAdapterClient ohipAdapterClient) {
    return new HotelInfoOutPortImpl(ohipAdapterClient);
  }

  @Bean
  public PaymentInfoOutPort paymentInfoOutPort(final OhipAdapterClient ohipAdapterClient,
      final BasketOutPort basketOutPort) {
    return new PaymentInfoOutPortImpl(ohipAdapterClient, basketOutPort);
  }

  @Bean
  public CheckInOnlineLogic checkInOnlineLogic(CheckInOnlineProperties checkInOnlineProperties,
      BasketOutPort basketOutPort, HotelInfoOutPort hotelInfoOutPort,
      PaymentInfoOutPort paymentInfoOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new CheckInOnlineLogic(basketOutPort, checkInOnlineProperties, hotelInfoOutPort,
        paymentInfoOutPort, unleashWrapper);
  }

  @Bean
  public DigitalKeyFeature digitalKeyFeature(DigitalKeyProperties digitalKeyProperties,
                                             UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new DigitalKeyFeature(digitalKeyProperties, unleashWrapper);
  }

}
