package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @deprecated for Auth0 integration (DMS-2752)
     */
    @Deprecated
    private String sessionId;

    private String customerAccountId;

    private ContactDetail contactDetail;

    private PaymentPreference paymentPreference;

    private List<BaseContact> additionalGuests;

    private BookingPreference bookingPreference;

    /**
     * @deprecated DMS-3579
     * This field will be replaced by contactDetail.address.companyName,
     * which will now be mapped to getUserResult.guestDetails.companyName.
     */
    @Deprecated
    private String companyName;

    private String companyId;

    private Boolean businessUse;

    /**
     * @deprecated for Auth0 integration (DMS-2752)
     */
    @Deprecated
    private String guestHistoryNumber;

    private Business business;

    /**
     * DNRQ-9607 - Needed for 3CP integration - fraud screening.
     */
    private LocalDate guestHistoryCreation;

    /**
     * DNRQ-9607 - Needed for 3CP integration - fraud screening.
     */
    private Long totalStays;

    public void setTetheredGuid(String tetheredGuid) {
        business.setTethered(true);
    }
}
