package uk.co.whitbread.hotel.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public abstract class BaseStay implements Comparable<BaseStay> {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String hotelCode;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2015-10-20")
    private LocalDate arrivalDate;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2015-10-20")
    private LocalDate departureDate;
    private LocalDate bookingDate;
    private Price totalCost;
    private Price cityTax;
    private List<RoomTypes> roomTypes;
    private int noOfRooms;
    private String customerReference;
    private int historyRecordNumber;
    private String purchaseOrder;
    private String confirmationNumber;
    private Price prePaidAmount;
    private String paymentStatus;
    private boolean cancelled;
    private boolean checkInOnline;
    private boolean checkedIn;
    private String rateClass;
    @Schema(example = "2015-10-20")
    private String checkInDate;
    private String promotionText;
    private String cellCodeLegend;
    private boolean carDataRequired;

    @Override
    public int compareTo(BaseStay other) {
        if (arrivalDate == null) {
            return (other.arrivalDate == null) ? 0 : 1;
        } else {
            return (other.arrivalDate == null) ? -1 : arrivalDate.compareTo(other.arrivalDate);
        }
    }
}
