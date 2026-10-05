package uk.co.whitbread.payments.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payments.model.BookingType;
import uk.co.whitbread.payments.model.ChannelType;
import uk.co.whitbread.payments.model.Currency;
import uk.co.whitbread.payments.model.PaymentSubType;
import uk.co.whitbread.payments.model.PaymentType;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderAccount {
    private BookingType bookingType;
    private PaymentType paymentType;
    private List<ChannelType> channelTypes;
    private List<PaymentSubType> paymentSubTypes;
    private Currency currency;
    private AccountConfigProperties configuration;
}
