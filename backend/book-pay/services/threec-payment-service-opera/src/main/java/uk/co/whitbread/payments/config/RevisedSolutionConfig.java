package uk.co.whitbread.payments.config;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;

import java.util.List;
import java.util.Optional;

import static uk.co.whitbread.payments.model.ChannelType.*;

@ConfigurationProperties(prefix = "revisedsolution")
@Configuration
@Data
@Slf4j
public class RevisedSolutionConfig {

    private boolean featureEnabled;
    @Getter
    private List<String> hotelList;
    private boolean mobileAppEnabled;
    private boolean motoBookingEnabled;
    private boolean androidAppEnabled;
    private boolean iosAppEnabled;

    public boolean checkIfHotelConfiguredForRevisedSolution(PaymentsSchema paymentsSchema){
        var hotelSelected = Optional.ofNullable(paymentsSchema.getBooking()).map(Booking::getBusinessSite).map(BusinessSite::getIdentifier).orElse("");
        var bookingChannel = Optional.ofNullable(paymentsSchema.getBooking()).map(Booking::getChannel).orElse("");

        if(!featureEnabled){
            return false;
        }
        if (!hotelList.contains(hotelSelected)) {

            if(bookingChannel.equals(CCC.toString()) || bookingChannel.equals(GDS.toString()) || bookingChannel.equals(FRONT_DESK.toString())) {
                return false;
            }

            if ((bookingChannel.equals(APPS_ANDROID.toString())&& androidAppEnabled) || bookingChannel.equals(APPS_IOS.toString())&& iosAppEnabled){
                log.info("Hotel code {} and name {} configured for revised solution from apps channel {}", hotelSelected, paymentsSchema.getBooking().getBusinessSite().getName(),bookingChannel);
                return true;
            }

            if((bookingChannel.equals(PI.toString()) || bookingChannel.equals(WEB.toString()) || bookingChannel.equals(BB.toString())) && featureEnabled) {
                log.info("Hotel code {} and name {} configured for revised solution from channel {}", hotelSelected, paymentsSchema.getBooking().getBusinessSite().getName(),bookingChannel);
                return true;
            }
        }
        return false;
    }
}
