package uk.co.whitbread.ondemandrefreshservice.infrastructure.util;

public final class SharedLogMessages {

    private SharedLogMessages(){}

    public static final String SCHEDULER_CALLED = "hotelAvailabilityCronSchedulerSvc called!";
    public static final String MIGRATION_STATUS_SCHEDULER_CALLED =
        "MigrationStatusCronSchedulerSvc called!";
    public static final String ERROR_WHILE_SCHEDULING_JOB_MESSAGE = "Error while trying to schedule %s.";
    public static final String REFRESH_CRON_ERROR_MESSAGE = "Error while trying to refresh cron triggers";
    public static final String HOTEL_CODES_FOUND = "{} hotel codes found.";
}
