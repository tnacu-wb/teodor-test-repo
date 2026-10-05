package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.RateRestrictionOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.raterestriction.opera.HotelRateRestrictionClient;

@Slf4j
@RequiredArgsConstructor
public class RateRestrictionService implements RateRestrictionOutPort {

    private final HotelRateRestrictionClient hotelRateRestrictionClient;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public RateRestrictionResponse searchRateRestrictionCriteria(RateRestrictionInput rateRestrictionInput) {
        log.debug("Requesting Hotel Rate Restriction for input: {}", rateRestrictionInput);
        RateRestrictionResponse rateRestrictionResponse =
            hotelRateRestrictionClient.searchRateRestrictionCriteria(
                rateRestrictionInput.getHotelId(),
                rateRestrictionInput.getStartDate(),
                rateRestrictionInput.getEndDate());
        log.trace("Opera Rate Restriction Response: {}", rateRestrictionResponse);
        return rateRestrictionResponse;
    }

    @Override
    public RateRestrictionResponse getRateRestrictions(final String hotelCode, final LocalDate startDate,
        final LocalDate endDate) {
        RateRestrictionResponse rateRestrictionRsp = null;
        if (isValidInput(hotelCode, startDate, endDate)) {
            log.trace("Executing getRateRestrictionResponse()....");
            final RateRestrictionInput rateRestrictionInput = RateRestrictionInput.builder()
                .hotelId(hotelCode)
                .startDate(startDate.format(formatter))
                .endDate(endDate.format(formatter))
                .build();
            rateRestrictionRsp = searchRateRestrictionCriteria(rateRestrictionInput);
            if (rateRestrictionRsp != null && rateRestrictionRsp.getRestrictionsByDateRange() != null) {
                final String hotelCodeRsp = rateRestrictionRsp.getRestrictionsByDateRange()
                    .getRestrictionsByDateRange().getHotelId();
                if (hotelCode.equalsIgnoreCase(hotelCodeRsp)) {
                    return rateRestrictionRsp;
                }
            }
        }
        return rateRestrictionRsp;
    }

    private boolean isValidInput(final String hotelCode, final LocalDate startDate, final LocalDate endDate) {
        return (StringUtils.isNotEmpty(hotelCode) && startDate != null && endDate != null);
    }
}

