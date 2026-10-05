package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.ToString;
import uk.co.whitbread.hotel.account.validation.AdditionalGuestConstraint;

@Data
@ToString(exclude = {"password", "newPassword"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRequest {

    private String password;

    private String newPassword;

    @Valid
    private ContactDetail contactDetail;

    @Valid
    private BookingPreference bookingPreference;

    @Valid
    private PaymentPreference paymentPreference;

    private MarketingPreference marketingPreference;

    @Valid
    private List<@AdditionalGuestConstraint BaseContact> additionalGuests;

    private Long guestId;

    private String guestHistoryNumber;

    /**
     * @deprecated DMS-3579
     * This field will be replaced by contactDetail.address.companyName,
     * which will now be mapped to getUserResult.guestDetails.companyName.
     */
    @Deprecated
    private String companyName;

    private String companyId;

}