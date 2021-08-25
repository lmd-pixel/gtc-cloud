package com.fmisser.gtc.social.service.impl;


import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.SysAppConfig;
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

    /****
     * 获取审核版本的审核状态
     * @param channelId
     * @param version
     * @return
     * @throws ApiException
     * @throws ParseException
     */
    @Override
    public String getAppAuditVersionEx(String channelId, String version) throws ApiException, ParseException {
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
                    return  sysAppConfig.getDynamicIsSwitch();
                }else{
                    return  "0";
                }
            }else{
                return  "0";
            }
        }else{
            return  "0";
        }
    }

    /****
     * 获取审核版本的查询截至时间
     * @param channelId
     * @param version
     * @return
     * @throws ApiException
     * @throws ParseException
     */
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

    /****
     * 获取审核版本
     * @param version
     * @return
     * @throws ApiException
     */
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

    /****
     * 获取审核版本是否在有效时间范围内
     * @param version
     * @return
     * @throws ApiException
     */
    @Override
    public boolean getAppAuditVersionTime(String version) throws ApiException {
        SysAppConfig sysAppConfig = sysAppConfigRepository.findByName(version);
        if (Objects.isNull(sysAppConfig)) {
            return false;
        }
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

    /****
     * 获取审核版本的数据,当前数据行配置的数据信息
     * @param version
     * @return
     * @throws ApiException
     */
    @Override
    public SysAppConfig getSysAppconfig(String version) throws ApiException {
        SysAppConfig sysAppConfig = sysAppConfigRepository.findByName(version);
        return sysAppConfig;
    }


}
