package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.payments.validation.ValidRefundRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static java.util.Optional.of;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ValidRefundRequest
public class RefundRequest extends BaseRequest {
    @Valid
    @NotNull
    @Schema(required = true, description = "Information on Refund to process.")
    private Refund refund;

    @Valid
    @NotNull
    @Schema(required = true, description = "Information regarding the booking made.")
    private Booking booking;


    public String getHotelCode() {
        return of(this)
                .map(RefundRequest::getBooking)
                .map(Booking::getBusinessSite)
                .map(BusinessSite::getIdentifier)
                .orElse(null);
    }
}
