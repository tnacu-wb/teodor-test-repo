package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron;

import static org.quartz.CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;

import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CronJobCreator {

    /**
     * Create Quartz Job.
     *
     * @param jobClass  Class whose executeInternal() method needs to be called.
     * @param context   Spring application context.
     * @param jobName   Job name.
     * @param jobGroup  Job group.
     * @return JobDetail object
     */
    public JobDetail createJob(Class<? extends QuartzJobBean> jobClass,
                               final ApplicationContext context, final String jobName, final String jobGroup) {
        log.info("Start JobScheduleCreator.createJob()");
        final JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /**
     * Create cron trigger.
     *
     * @param triggerName       Trigger name.
     * @param group             Group name.
     * @param cronExpression    Cron expression.
     * @return {@link CronTrigger}
     */
    public CronTrigger createCronTrigger(final String triggerName,
                                         final String group, final String cronExpression) throws ParseException {
        log.info("Start JobScheduleCreator.createCronTrigger()");
        final CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName(triggerName);
        factoryBean.setGroup(group);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}

