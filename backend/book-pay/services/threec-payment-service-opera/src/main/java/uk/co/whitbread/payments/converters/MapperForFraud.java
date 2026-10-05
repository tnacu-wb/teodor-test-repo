package uk.co.whitbread.payments.converters;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Guest;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.Room;
import uk.co.whitbread.payments.model.threec.FraudCheckData;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class MapperForFraud implements CustomMapper {

    private static final String COMMA_DELIMITER = ",";
    private static final String EMPTY = "";
    @Value("${3c.fraud.enabled}")
    private boolean fraudScreeningEnabled;
    @Value("${3c.fraud.mode}")
    private String fraudMode;

    @Override
    public void mapForNoCardRead(final ProviderAccount account, final NoCardReadTransactionRequest.Params params, final PaymentRequest paymentRequest) {
        if (fraudScreeningEnabled) {
            var profileName = retrieveFraudProfileName(account);
            profileName.ifPresent(fraudProfileName -> params.setFraudCheckData(buildFraudBlockBy(paymentRequest, fraudProfileName)));
        }
    }

    @Override
    public void mapForInitialise(final ProviderAccount account, final InitialiseRequest initialiseRequest, PaymentRequest paymentRequest) {
        if (fraudScreeningEnabled) {
            var profileName = retrieveFraudProfileName(account);
            profileName.ifPresent(fraudProfileName -> initialiseRequest.setFraudCheckData(buildFraudBlockBy(paymentRequest, fraudProfileName)));
        }
    }

    @Override
    public void mapForPaypal(final ProviderAccount account, final PaypalForwardAPITransactionRequest params, final PaymentRequest paymentRequest) {
        if (fraudScreeningEnabled) {
            var profileName = retrieveFraudProfileName(account);
            profileName.ifPresent(fraudProfileName -> params.setSensitiveData(buildPaypalFraudBlockBy(paymentRequest, fraudProfileName)));
        }
    }

    private FraudCheckData buildFraudBlockBy(PaymentRequest paymentRequest, String fraudProfileName) {
        var bookingRequest = paymentRequest.getBooking();

        if (null == bookingRequest) {
            return FraudCheckData.builder().build();
        }

        return FraudCheckData.builder()
                .channel(ofNullable(bookingRequest.getChannel()).orElse(EMPTY))
                .subChannel(ofNullable(bookingRequest.getSubChannel()).orElse(EMPTY))//MDD2
                .website(ofNullable(bookingRequest.getLanguage()).orElse(EMPTY))//MDD3 - gb / de
                .hoursUntilCheckIn(calculateHoursUntilCheckIn(bookingRequest.getArrivalDate()))//MDD4
                .checkedInOnline("no")//MDD5 - yes / no hardcoded until checkinonline is implemented
                .arrivalDate(bookingRequest.getArrivalDate())//MDD6
                .noOfNights(calculateNumberOfNight(bookingRequest.getArrivalDate(), bookingRequest.getDepartureDate()))//MDD7
                .roomType(concatRoomTypes(bookingRequest.getRooms()))//MDD8
                .noOfRooms(calculateNumOfRooms(bookingRequest))//MDD9
                .guestName(getLeadGuestName(bookingRequest))//MDD10
                .bookerName(getLeadGuestName(bookingRequest))//MDD11
                .hotelName(bookingRequest.getBusinessSite().getName())//MDD12
                .hotelCode(bookingRequest.getBusinessSite().getIdentifier())//MDD13
                .hotelCity(bookingRequest.getBusinessSite().getLocation())//MDD14
                .memberRegistered(getMemberRegistered(bookingRequest))//MDD15
                .noOfGuests(obtainTotalAdults(bookingRequest.getRooms()))//MDD16
                .roomRateType(concatRoomRateTypes(bookingRequest.getRooms()))//MDD17
                .additionalServices(concatAdditionalServices(bookingRequest.getBusinessSite()))//MDD18
                .memberRegisteredSinceDays(calculateDaysRegistered(bookingRequest.getLeadGuest()))//MDD19
                .previousBookingsCount(getPreviousBookingsCount(bookingRequest))//MDD10
                .fraudProfileName(fraudProfileName)
                .fraudMode(fraudMode)
                .build();
    }

    private PaypalForwardAPITransactionRequest.SensitiveData buildPaypalFraudBlockBy(PaymentRequest paymentRequest, String fraudProfileName) {
        var bookingRequest = paymentRequest.getBooking();

        if (null == bookingRequest) {
            return PaypalForwardAPITransactionRequest.SensitiveData.builder().build();
        }

        return PaypalForwardAPITransactionRequest.SensitiveData.builder()
                .channel(ofNullable(bookingRequest.getChannel()).orElse(EMPTY))
                .subChannel(ofNullable(bookingRequest.getSubChannel()).orElse(EMPTY))//MDD2
                .website(ofNullable(bookingRequest.getLanguage()).orElse(EMPTY))//MDD3 - gb / de
                .hoursUntilCheckIn(calculateHoursUntilCheckIn(bookingRequest.getArrivalDate()))//MDD4
                .checkedInOnline("no")//MDD5 - yes / no hardcoded until checkinonline is implemented
                .arrivalDate(bookingRequest.getArrivalDate().toString())//MDD6
                .noOfNights(calculateNumberOfNight(bookingRequest.getArrivalDate(), bookingRequest.getDepartureDate()))//MDD7
                .roomType(concatRoomTypes(bookingRequest.getRooms()))//MDD8
                .noOfRooms(calculateNumOfRooms(bookingRequest))//MDD9
                .guestName(getLeadGuestName(bookingRequest))//MDD10
                .bookerName(getLeadGuestName(bookingRequest))//MDD11
                .hotelName(bookingRequest.getBusinessSite().getName())//MDD12
                .hotelCode(bookingRequest.getBusinessSite().getIdentifier())//MDD13
                .hotelCity(bookingRequest.getBusinessSite().getLocation())//MDD14
                .memberRegistered(getMemberRegistered(bookingRequest))//MDD15
                .noOfGuests(obtainTotalAdults(bookingRequest.getRooms()))//MDD16
                .roomRateType(concatRoomRateTypes(bookingRequest.getRooms()))//MDD17
                .additionalServices(concatAdditionalServices(bookingRequest.getBusinessSite()))//MDD18
                .memberRegisteredSinceDays(calculateDaysRegistered(bookingRequest.getLeadGuest()))//MDD19
                .previousBookingsCount(getPreviousBookingsCount(bookingRequest))//MDD10
                .fraudProfileName(fraudProfileName)
                .fraudMode(fraudMode)
                .build();
    }

    private Integer getPreviousBookingsCount(Booking bookingRequest) {

        if(null == bookingRequest.getLeadGuest()){
            return 0;
        }
        return bookingRequest.getLeadGuest().getPreviousBookings();
    }

    private String concatAdditionalServices(BusinessSite businessSite) {

        if (null == businessSite) {
            return EMPTY;
        }
        var additionalServices = businessSite.getAdditionalServices();
        if (null == additionalServices) {
            return EMPTY;
        }

        return String.join(COMMA_DELIMITER, additionalServices);
    }

    private String getMemberRegistered(Booking bookingRequest) {

        if (null == bookingRequest.getLeadGuest()) {
            return EMPTY;
        }
        return format("%s", bookingRequest.getLeadGuest().isRegistered());
    }

    private Integer calculateNumOfRooms(Booking bookingRequest) {

        if (null == bookingRequest.getRooms()) {
            return 0;
        }
        return bookingRequest.getRooms().size();
    }

    private String getLeadGuestName(Booking bookingRequest) {

        if (null == bookingRequest.getLeadGuest()) {
            return EMPTY;
        }
        return bookingRequest.getLeadGuest().getName();
    }

    private Optional<String> retrieveFraudProfileName(final ProviderAccount account) {

        if (account.getConfiguration().isFraudScreened()) {
            return Optional.of(account.getConfiguration().getFraudProfile());
        }
        return Optional.empty();
    }

    private Integer obtainTotalAdults(final List<Room> rooms) {

        if (null == rooms) {
            return 0;
        }
        return rooms.stream()
                .map(Room::getAdults)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private Integer calculateDaysRegistered(final Guest guest) {

        if(null == guest){
            return 0;
        }

        var registeredSince = guest.getRegisteredSince();
        if (null == registeredSince) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(registeredSince, LocalDate.now());
    }

    private String concatRoomTypes(final List<Room> roomsList) {

        if (null == roomsList) {
            return EMPTY;
        }
        return roomsList.stream()
                .map(Room::getType)
                .collect(Collectors.joining(COMMA_DELIMITER));
    }

    private String concatRoomRateTypes(final List<Room> roomsList) {

        if (null == roomsList) {
            return EMPTY;
        }
        return roomsList.stream()
                .map(Room::getRate)
                .collect(Collectors.joining(COMMA_DELIMITER));
    }

    private Integer calculateNumberOfNight(final LocalDate arrivalDate, final LocalDate departureDate) {

        if (null == arrivalDate || null == departureDate) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(arrivalDate, departureDate);
    }

    private Integer calculateHoursUntilCheckIn(LocalDate arrivalDate) {

        if (null == arrivalDate) {
            return 0;
        }
        var checkingDateTime = arrivalDate.atTime(15, 0);
        return (int) ChronoUnit.HOURS.between(LocalDateTime.now(), checkingDateTime);
    }
}
