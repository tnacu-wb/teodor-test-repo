package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerV2 {

    private String captcha;

    private String password;

    @NotNull
    @Valid
    private ContactDetail contactDetail;

    @Valid
    private BookingPreference bookingPreference;

    @Valid
    private PaymentPreference paymentPreference;

    @Valid
    private List<ContactDetail> additionalGuests;

    private long guestId;

    private String guestHistoryNumber;

    private String companyName;

    private String alternativeName;

    private String approxAnnualUKHotelSpend;

    private String companyId;
}
