package com.fmisser.gtc.social.job;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * @author fmisser
 * @create 2021-04-13 下午5:36
 * @description
 */
@Component
public class CallCalcJobScheduler {
    private final Scheduler scheduler;

    public CallCalcJobScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void startCallCalc(Long roomId) throws Exception {
        JobDetail calcJob = JobBuilder
                .newJob(CallCalcJob.class)
                .withIdentity(roomId.toString(), "calcGroup")
                .build();

        Trigger calcTrigger = newTrigger()
                .withIdentity(triggerKey(roomId.toString(), "calcGroup"))
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .startNow()
                .build();

        scheduler.scheduleJob(calcJob, calcTrigger);
    }

    public void startCallTimeout(Long roomId) throws Exception {
        JobDetail timeoutJob = JobBuilder
                .newJob(CallCalcJob.class)
                .withIdentity(roomId.toString(), "timeoutGroup")
                .build();

        Trigger timeoutTrigger = newTrigger()
                .withIdentity(triggerKey(roomId.toString(), "timeoutGroup"))
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(30)
                        .withRepeatCount(0))
                .startNow()
                .build();

        scheduler.scheduleJob(timeoutJob, timeoutTrigger);
    }

    public void stopCallCalc(Long roomId) throws Exception {
        scheduler.unscheduleJob(triggerKey(roomId.toString(), "calcGroup"));
    }

    public void stopCallTimeout(Long roomId) throws Exception {
        scheduler.unscheduleJob(triggerKey(roomId.toString(), "timeoutGroup"));
    }
}
