package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.payments.validation.ValidPaymentRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static java.util.Optional.of;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ValidPaymentRequest
public class PaymentRequest extends BaseRequest {

    @Schema(required = true, description = "BART Session Id")
    private String sessionId;

    @Valid
    @NotNull
    @Schema(required = true, description = "Information on the type of Payment to process.")
    private Payment payment;

    @Valid
    @NotNull
    @Schema(required = true, description = "Information regarding the booking being made.")
    private Booking booking;

    public String getLanguage() {
        return of(this)
                .map(PaymentRequest::getBooking).map(Booking::getLanguage)
                .orElse("en");
    }

    public String getHotelCode() {
        return of(this)
                .map(PaymentRequest::getBooking)
                .map(Booking::getBusinessSite)
                .map(BusinessSite::getIdentifier)
                .orElse(null);
    }

    public String getPaymentSubType() {
        return of(this)
                .map(PaymentRequest::getPayment)
                .map(Payment::getSubType)
                .orElse(null);
    }

    public String getCountryCode() {
        return of(this)
                .map(PaymentRequest::getPayment)
                .map(Payment::getBilling)
                .map(Billing::getAddress)
                .map(Address::getCountryCode)
                .orElse(null);
    }

    public Integer getAmount() {
        return of(this)
                .map(PaymentRequest::getPayment)
                .map(Payment::getAmount)
                .map(Amount::getMinorUnits)
                .orElse(null);
    }

    public String getChannel() {
        return of(this)
                .map(PaymentRequest::getBooking)
                .map(Booking::getChannel)
                .orElse(null);
    }

    public String getBookingType() {
        return of(this)
                .map(PaymentRequest::getBooking)
                .map(Booking::getType)
                .orElse(null);
    }
}
