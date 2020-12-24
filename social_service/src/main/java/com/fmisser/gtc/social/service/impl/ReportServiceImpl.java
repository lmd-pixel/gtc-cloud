package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.Report;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.DynamicRepository;
import com.fmisser.gtc.social.repository.ReportRepository;
import com.fmisser.gtc.social.service.ReportService;
import com.fmisser.gtc.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    private final DynamicRepository dynamicRepository;

    public ReportServiceImpl(ReportRepository reportRepository,
                             DynamicRepository dynamicRepository) {
        this.reportRepository = reportRepository;
        this.dynamicRepository = dynamicRepository;
    }

    @Override
    public int reportUser(User user, User dstUser, String message) throws ApiException {
        Report report = new Report();
        report.setUserId(user.getId());
        report.setType(1);
        report.setDstRelatedId(dstUser.getId());
        report.setMessage(message);
        reportRepository.save(report);

        return 1;
    }

    @Override
    public int reportDynamic(User user, Long dynamicId, String message) throws ApiException {

        Optional<Dynamic> dynamic = dynamicRepository.findById(dynamicId);
        if (!dynamic.isPresent()) {
            throw new ApiException(-1, "动态不存在!");
        }

        Report report = new Report();
        report.setUserId(user.getId());
        report.setType(2);
        report.setDstRelatedId(dynamic.get().getId());
        report.setMessage(message);
        reportRepository.save(report);

        return 1;
    }
}
