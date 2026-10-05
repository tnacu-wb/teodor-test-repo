package uk.co.whitbread.hotel.account.model;

import lombok.Data;

@Data
public class RoomTypes {

    private String roomType;
    private String bookingStatus;
    private boolean carDataPresent;
    private PersonDetails personDetails;

    /**
     * @deprecated To be removed when all FE teams start using PersonDetails object containing:
     * title, firstName, lastName
     */
    @Deprecated
    private String leadGuest;
}
