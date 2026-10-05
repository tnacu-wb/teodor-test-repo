package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.DailyRatesOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RateCategory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRatesInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanSchedule;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.opera.HotelDailyRatesClient;

@Slf4j
@RequiredArgsConstructor
public class DailyRatesService implements DailyRatesOutPort {

    private final HotelDailyRatesClient hotelDailyRatesClient;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final long LIMIT = 1000;

    @Override
    public DailyRates getDailyRates(final DailyRatesInput dailyRatesInput) {
        dailyRatesInput.setLimit(LIMIT);
        log.debug("Requesting Hotel Daily Rates for input: {}", dailyRatesInput);
        DailyRates dailyRates = hotelDailyRatesClient.getDailyRates(dailyRatesInput.getHotelId(),
            dailyRatesInput.getRatePlanCode(), dailyRatesInput.getLimit(),
            dailyRatesInput.getStartDate(), dailyRatesInput.getEndDate());
        log.trace("Opera Daily Rates Response: {}", dailyRates);
        return dailyRates;
    }

    @Override
    public Map<String, DailyRates> getOperaDailyRates(final String hotelCode,
        final LocalDate startDate, final LocalDate endDate) {
        log.trace("Executing getOperaDailyRates()....");
        final Map<String, DailyRates> rates = new HashMap<>();
        //Get daily rates for each of configured rate plan codes
        Stream.of(RateCategory.values()).forEach(rateCategory -> {
            final String ratePlanCode = rateCategory.name();
            final DailyRates dailyRates = processOperaDailyRates(hotelCode, startDate, endDate,
                ratePlanCode);
            if (dailyRates != null) {
                rates.put(ratePlanCode, dailyRates);
            }
        });
        return rates;
    }

    private DailyRates processOperaDailyRates(final String hotelCode, final LocalDate startDate,
        final LocalDate endDate, final String ratePlanCode) {
        log.trace("Executing processOperaDailyRates()....");
        //1. Get daily Rates
        final DailyRatesInput dailyRatesInput = dailyRatesInputBuilder(hotelCode, startDate, endDate,
            ratePlanCode);
        final DailyRates dailyRates = getDailyRates(dailyRatesInput);
        if (isNotEmptyDailyRates(dailyRates, ratePlanCode, hotelCode) && isValidDailyRates(dailyRates,
            ratePlanCode, hotelCode)) {
            //2. Get remaining rate plan schedules, if the 1st response has field "hasMore = true" from Opera
            if (dailyRates.getRatePlanScheduleList().isHasMore()) {
                final Set<RatePlanSchedule> completeRatePlanSchedules = getRemainingRatePlanSchedules(
                    dailyRates, hotelCode, endDate, ratePlanCode);
                //3. Update the daily rates object
                dailyRates.getRatePlanScheduleList()
                    .setRatePlanSchedule(new ArrayList<>(completeRatePlanSchedules));
            }
            return dailyRates;
        }
        return null;
    }

    private boolean isNotEmptyDailyRates(final DailyRates dailyRates, final String ratePlanCode,
        final String hotelCode) {
        boolean isNotEmptyDailyRateResponse = false;
        if (dailyRates != null && dailyRates.getRatePlanMasterInfo() != null
            && dailyRates.getRatePlanScheduleList().getRatePlanSchedule() != null
            && !dailyRates.getRatePlanScheduleList().getRatePlanSchedule().isEmpty()) {
            log.debug("Received daily rates for input rateplan code:{} , input hotel code:{}",
                ratePlanCode, hotelCode);
            isNotEmptyDailyRateResponse = true;
        } else {
            log.debug(
                "Received empty rate plans for rateplan code:{}, hotel code:{}. Skipping daily rates",
                ratePlanCode, hotelCode);
        }
        return isNotEmptyDailyRateResponse;
    }

    private boolean isValidDailyRates(final DailyRates dailyRates, final String ratePlanCode,
        final String hotelCode) {
        final String ratePlanCodeRsp = dailyRates.getRatePlanMasterInfo().getRatePlanCode();
        final String hotelCodeRsp = dailyRates.getRatePlanMasterInfo().getHotelId();
        log.debug("Daily Rates Response => rateplan code:{}, hotel code:{}", ratePlanCodeRsp,
            hotelCodeRsp);
        boolean isValidDailyRate = false;
        if (ratePlanCode.equalsIgnoreCase(ratePlanCodeRsp) && hotelCode.equalsIgnoreCase(
            hotelCodeRsp)) {
            isValidDailyRate = true;
        } else {
            log.error("RatePlan code mismatch. Input RatePlan code:{}, Response RatePlan code:{}",
                ratePlanCode, ratePlanCodeRsp);
        }
        return isValidDailyRate;
    }

    private Set<RatePlanSchedule> getRemainingRatePlanSchedules(final DailyRates dailyRates,
        final String hotelCode, final LocalDate endDate, final String ratePlanCode) {
        log.trace("Executing getRemainingRatePlanSchedules()....");
        DailyRates tempDailyRates = dailyRates;
        List<RatePlanSchedule> tempRatePlanSchedules = tempDailyRates.getRatePlanScheduleList()
            .getRatePlanSchedule();
        Set<RatePlanSchedule> completeRatePlanSchedules = new HashSet<>(tempRatePlanSchedules);
        boolean hasMore = dailyRates.getRatePlanScheduleList().isHasMore();
        while (hasMore) {
            log.debug("Opera daily rates response with field 'hasMore' : {}", true);
            tempRatePlanSchedules = tempDailyRates.getRatePlanScheduleList().getRatePlanSchedule();
            if (!tempRatePlanSchedules.isEmpty()) {
                //retrieve last element from response
                final DailyRates nextDailyRates = getDailyRates(hotelCode, endDate, ratePlanCode,
                    tempRatePlanSchedules);
                //merge the remaining daily rates
                if (isNotEmptyDailyRates(nextDailyRates, ratePlanCode, hotelCode) && isValidDailyRates(
                    nextDailyRates, ratePlanCode, hotelCode)) {
                    completeRatePlanSchedules.addAll(
                        nextDailyRates.getRatePlanScheduleList().getRatePlanSchedule());
                    tempDailyRates = nextDailyRates;
                    hasMore = nextDailyRates.getRatePlanScheduleList().isHasMore();
                } else {
                    hasMore = false;
                }
            }
        }
        dailyRates.getRatePlanScheduleList()
            .setRatePlanSchedule(new ArrayList<>(completeRatePlanSchedules));
        return completeRatePlanSchedules;
    }

    private DailyRates getDailyRates(final String hotelCode, final LocalDate endDate,
        final String ratePlanCode, final List<RatePlanSchedule> tempRatePlanSchedules) {
        final RatePlanSchedule lastRatePlanScheduleElement = tempRatePlanSchedules.get(
            tempRatePlanSchedules.size() - 1);
        final LocalDate startDateInLastElement = LocalDate.parse(
            lastRatePlanScheduleElement.getRatePlanScheduleDetail().getStart());
        //get daily rates from Opera with the 'start date' from the last element in the response
        final DailyRatesInput updatedDailyRatesInput = dailyRatesInputBuilder(hotelCode,
            startDateInLastElement, endDate, ratePlanCode);
        return getDailyRates(updatedDailyRatesInput);
    }

    private static DailyRatesInput dailyRatesInputBuilder(final String hotelCode,
        final LocalDate startDate, final LocalDate endDate, final String ratePlanCode) {
        log.trace("Executing dailyRatesInputBuilder()....");
        return DailyRatesInput.builder().hotelId(hotelCode).ratePlanCode(ratePlanCode)
            .startDate(startDate.format(formatter)).endDate(endDate.format(formatter)).build();
    }
}
