package com.fmisser.gtc.social.job;

import com.fmisser.gtc.social.service.ImService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fmisser
 * @create 2021-04-13 下午5:32
 * @description
 */
@Component
public class CallCalcJob implements Job {

    private final ImService imService;

    public CallCalcJob(ImService imService) {
        this.imService = imService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        Long roomId = Long.parseLong(jobKey.getName());

        int ret = imService.calcGen(roomId);
        if (ret < 0) {
            imService.endGenByServer(roomId);
        }
    }
}
