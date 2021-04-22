package com.fmisser.gtc.social.job;

import com.fmisser.gtc.social.domain.Call;
import com.fmisser.gtc.social.service.ActiveService;
import com.fmisser.gtc.social.service.CallService;
import com.fmisser.gtc.social.service.ImService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author fmisser
 * @create 2021-04-21 下午4:56
 * @description 通话心跳检测定时任务
 */
@Component
public class CallHeartbeatJob implements Job {
    @Autowired
    private ImService imService;

    @Autowired
    private CallService callService;

    @Autowired
    private ActiveService activeService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        Long roomId = Long.parseLong(jobKey.getName());

        Call call = callService.getCallByRoomId(roomId);
        if (call.getIsFinished() == 0) {
          if (!activeService.isUserCalling(call.getUserIdFrom(), roomId) ||
                !activeService.isUserCalling(call.getUserIdTo(), roomId)) {
              // 有一个不在，结束房间
              imService.endGenByServer(roomId);
          }
        }
    }
}
