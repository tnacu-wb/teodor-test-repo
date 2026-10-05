package uk.co.whitbread.hotel.register.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

    private String captcha;

    @NotNull
    private String password;

    @NotNull
    @Valid
    private ContactDetail contactDetail;

    private String basketReference;

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
