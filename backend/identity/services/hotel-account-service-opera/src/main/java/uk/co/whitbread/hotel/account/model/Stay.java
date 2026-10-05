package uk.co.whitbread.hotel.account.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"hotelName", "leadGuest", "booker"})
public class Stay extends BaseStay {

    private BookingStatus bookingStatus;
    private Boolean amendable;
    private String guestHistoryNumber;
    private String bookedBy;
    private Price bookingFee;
    private Price outstandingAmount;
    private String frequentBooking;
    private boolean mpibooking;
    private String rateName;
    //Choosing this spelling to match retrieveBooking.reservationDetails
    private Boolean cancelable;
    private String cancellationId;
    private String cancellationDate;;
    private String hotelName;
    private String leadGuest;
    private String leadGuestSurname;
    private Booker booker;

    public void assignLeadGuestForBusiness() {

        Optional.ofNullable(getRoomTypes())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(RoomTypes::getPersonDetails)
                .ifPresent(
                        guest -> {
                            leadGuestSurname = guest.getLastName();
                            leadGuest = joinLeadGuest(guest);
                        }
                );
    }

    private String joinLeadGuest(PersonDetails personDetails) {
        return String.join(StringUtils.SPACE, personDetails.getTitle(),
                personDetails.getFirstName(), personDetails.getLastName());
    }
}
