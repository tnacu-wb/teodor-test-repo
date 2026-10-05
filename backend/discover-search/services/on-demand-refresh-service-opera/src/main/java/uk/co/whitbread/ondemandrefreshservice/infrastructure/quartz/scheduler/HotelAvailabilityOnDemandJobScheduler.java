package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler;

import static java.util.Optional.ofNullable;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.LocalDateTimeToDateConverter.convertToDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.model.SchedulerRequest;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job.HotelAvailabilityOnDemandJob;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilityOnDemandJobScheduler {

    private static final String TRIGGER_NAME = "%s_Trigger";
    private static final String GROUP = "HotelAvailabilityOnDemandJob";
    private static final String JOB_KEY_NAME = "%s_%s_%s";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String HOTEL_IDS = "HOTEL_IDS";
    private final Scheduler scheduler;

    public void scheduleOnDemandRefreshJob(final Set<SchedulerRequest> schedulerRequest) {
        log.info("Start scheduling scheduleOnDemandRefreshJob");
        final LocalDateTime currentTimestamp = LocalDateTime.now();
        log.debug("current timestamp - {}", currentTimestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ofNullable(schedulerRequest)
                .orElse(Collections.emptySet())
                .forEach(request -> {
                    try {
                        final JobKey jobKey = new JobKey(generateKeyName(request), GROUP);
                        if (!scheduler.checkExists(jobKey) && isValidTriggerTime(request)) {
                            log.trace("Scheduling scheduleOnDemandRefreshJob for {}", jobKey);
                            scheduler.scheduleJob(buildJobDetail(jobKey,request), buildTrigger(request, jobKey.getName()));
                            log.info("scheduleOnDemandRefreshJob with JobKey {} has been created.", jobKey);
                        }
                    } catch (SchedulerException e) {
                        log.error("Error while trying to schedule job for {}.", request, e);
                    }
                });
        log.info("End after scheduling the scheduleOnDemandRefreshJob");
    }

    private JobDetail buildJobDetail(final JobKey jobKey, final SchedulerRequest schedulerRequest) {
        log.trace("Creating JobDetail with jobKey {}", jobKey.getName());
        final JobDataMap jobData = new JobDataMap();
        jobData.put(START_DATE, schedulerRequest.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        jobData.put(END_DATE, schedulerRequest.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        final StringBuilder hotelIds = new StringBuilder();
        schedulerRequest.getHotelIds().forEach(hotelId -> hotelIds.append(hotelId).append(","));
        jobData.put(HOTEL_IDS, hotelIds.subSequence(0,hotelIds.length()-1).toString());
        return newJob(HotelAvailabilityOnDemandJob.class).setJobData(jobData)
                .withIdentity(jobKey)
                .build();
    }

    private Trigger buildTrigger(final SchedulerRequest schedulerRequest, final String jobKeyName)
        throws SchedulerException {
        if(schedulerRequest.getJobTriggerTime() == null){
            final String errorMsg = "Missing on-demand hotel availability trigger time, cannot schedule job";
            log.error(errorMsg);
            throw new SchedulerException(errorMsg);
        }
        final Date triggerAtStartDate = convertToDate(schedulerRequest.getJobTriggerTime());
        final TriggerKey triggerKey = new TriggerKey(String.format(TRIGGER_NAME, jobKeyName), GROUP);
        log.trace("Creating trigger {}", triggerKey.getName());
        return newTrigger()
                .withIdentity(triggerKey)
                .startAt(triggerAtStartDate)
                .build();
    }

    private String generateKeyName(final SchedulerRequest schedulerRequest) {
        return String.format(JOB_KEY_NAME,
            schedulerRequest.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
            schedulerRequest.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
            schedulerRequest.getJobTriggerTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private boolean isValidTriggerTime(final SchedulerRequest schedulerRequest){
        final LocalDateTime currentTimestamp = LocalDateTime.now();
        boolean isValidJobTriggerTime = false;
        if(!schedulerRequest.getJobTriggerTime().isBefore(currentTimestamp)){
            isValidJobTriggerTime = true;
        }
        else{
            log.error("On Demand hotel availability job trigger time:{} is in the past, cannot schedule job.", schedulerRequest.getJobTriggerTime());
        }
        return isValidJobTriggerTime;
    }

}
