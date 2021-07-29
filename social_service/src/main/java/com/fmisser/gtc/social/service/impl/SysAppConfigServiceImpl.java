package com.fmisser.gtc.social.service.impl;


import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.SysAppConfig;
import com.fmisser.gtc.social.domain.SysConfig;
import com.fmisser.gtc.social.repository.SysAppConfigRepository;
import com.fmisser.gtc.social.service.SysAppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SysAppConfigServiceImpl  implements SysAppConfigService {

    @Autowired
    private SysAppConfigRepository sysAppConfigRepository;

    @Override
    public String getAppAuditVersionEx(String channelId, String version) throws ApiException, ParseException {
        String versionStatus="";
        SysAppConfig sysAppConfig = sysAppConfigRepository.findByName(version);
        if (Objects.isNull(sysAppConfig)) {
            return "0";
        }
        List<String> versionLists = Arrays.asList(sysAppConfig.getVersion().split(","));
        if(versionLists.contains(version)){//判断是否在版本范围内
            if(!StringUtils.isEmpty(sysAppConfig.getWeek()) &&sysAppConfig.getWeek().contains(DateUtils.getWeek()+"") ){//判断是否是当前的week
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
                String formateDate=dateFormat.format(now);
                // 是否在开启时间内
                if(!StringUtils.isEmpty(sysAppConfig.getStartTime()) && !StringUtils.isEmpty(sysAppConfig.getEndTime()) &&
                        Integer.valueOf(formateDate)>=Integer.valueOf(sysAppConfig.getStartTime())  &&
                        Integer.valueOf(formateDate)<=Integer.valueOf(sysAppConfig.getEndTime())
                ){//判断是否是当前的时间点
                    versionStatus="1";
                }else{
                    versionStatus="0";
                }
            }else{
                versionStatus="0";
            }
        }else{
            versionStatus="0";
        }

        return versionStatus;
    }

    @Override
    public Date getAppAuditDynamicDateLimit(String channelId, String version) throws ApiException, ParseException {
        SysAppConfig sysAppConfig = sysAppConfigRepository.findByName(version);
        if (Objects.isNull(sysAppConfig)) {
            return null;
        }

        List<String> versionLists = Arrays.asList(sysAppConfig.getVersion().split(","));
        if(versionLists.contains(version)){//判断是否在版本范围内
            if(!StringUtils.isEmpty(sysAppConfig.getWeek()) &&sysAppConfig.getWeek().contains(DateUtils.getWeek()+"") ){//判断是否是当前的week
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
                String formateDate=dateFormat.format(now);
                // 是否在开启时间内
                if(!StringUtils.isEmpty(sysAppConfig.getStartTime()) && !StringUtils.isEmpty(sysAppConfig.getEndTime()) &&
                        Integer.valueOf(formateDate)>=Integer.valueOf(sysAppConfig.getStartTime())  &&
                        Integer.valueOf(formateDate)<=Integer.valueOf(sysAppConfig.getEndTime())
                ){//判断是否是当前的时间点
                    return  sysAppConfig.getDynamiChangeTime();
                }else{
                    return  null;
                }
            }else{
                return  null;
            }
        }else{
           return  null;
        }
    }

    @Override
    public String getAppAuditVersion(String version) throws ApiException {
        SysAppConfig sysConfig = sysAppConfigRepository.findByName(version);
        if (Objects.isNull(sysConfig)) {
            return "";
        }
        if (StringUtils.isEmpty(sysConfig.getVersion())) {
            return "";
        } else {
            return sysConfig.getVersion();
        }
    }


    @Override
    public boolean getAppAuditVersionTime(String version) throws ApiException {
        SysAppConfig sysAppConfig = sysAppConfigRepository.findByName(version);
        if(!StringUtils.isEmpty(sysAppConfig.getWeek()) &&sysAppConfig.getWeek().contains(DateUtils.getWeek()+"") ){//判断是否是当前的week
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
            String formateDate=dateFormat.format(now);
            // 是否在开启时间内
            if(!StringUtils.isEmpty(sysAppConfig.getStartTime()) && !StringUtils.isEmpty(sysAppConfig.getEndTime()) &&
                    Integer.valueOf(formateDate)>=Integer.valueOf(sysAppConfig.getStartTime())  &&
                    Integer.valueOf(formateDate)<=Integer.valueOf(sysAppConfig.getEndTime())
            ){//判断是否是当前的时间点
                return true;
            }else{
                return  false;
            }
        }else{
            return  false;
        }
    }

    @Override
    public SysAppConfig getSysAppconfig(String version) throws ApiException {
        SysAppConfig sysAppConfig = sysAppConfigRepository.findByName(version);

        return sysAppConfig;
    }


}
