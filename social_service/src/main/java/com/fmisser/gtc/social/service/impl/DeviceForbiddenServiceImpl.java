package com.fmisser.gtc.social.service.impl;

import com.fmisser.fpp.cache.redis.service.RedisService;
import com.fmisser.gtc.base.dto.social.DeviceForbiddenDto;
import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.DeviceForbidden;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;
import com.fmisser.gtc.social.repository.DeviceForbiddenRepository;
import com.fmisser.gtc.social.repository.UserDeviceRepository;
import com.fmisser.gtc.social.service.DeviceForbiddenService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author by fmisser
 * @create 2021/6/23 2:34 下午
 * @description TODO
 */

@Service
@AllArgsConstructor
public class DeviceForbiddenServiceImpl implements DeviceForbiddenService {
    private final DeviceForbiddenRepository deviceForbiddenRepository;
    private final RedisService redisService;
    private final UserDeviceRepository userDeviceRepository;

    @Override
    public int forbidden(int type, String identity, int days, String message) throws ApiException {
        return 0;
    }

    @Override
    public int disableForbidden(String identity) throws ApiException {
        return 0;
    }

    @Override
    public DeviceForbidden getDeviceForbidden(String identity) throws ApiException {
        return null;
    }

    /*****
     * 解封删除redis对应的记录，对应的数据行
     * @param forbiddenId
     * @return
     * @throws ApiException
     */
    @Override
    public int disableForbidden(Long forbiddenId) throws ApiException {
        Optional<DeviceForbidden> optionalForbidden = deviceForbiddenRepository.findById(forbiddenId);
        if (!optionalForbidden.isPresent()) {
            throw new ApiException(-1, "封号不存在!");
        }
        DeviceForbidden deviceForbidden=optionalForbidden.get();

        //解封时删除redis对应的数据（解封设备）
        if(deviceForbidden.getType()==1){
            Optional<UserDevice> optionalUserDevice=   userDeviceRepository.findById(deviceForbidden.getDeviceId());
            if(optionalUserDevice.isPresent()){
                String redisKey=deviceForbidden.getUserId()+":"+optionalUserDevice.get().getDeviceAndroidId();
                if(redisService.hasKey(redisKey)){
                    redisService.delKey(redisKey);
                }
            }
        }
        //解封时删除redis对应的数据（解封IP）
        if(deviceForbidden.getType()==2){
            String redisKey=deviceForbidden.getUserId()+":"+deviceForbidden.getIp();
            if(redisService.hasKey(redisKey )){
                redisService.delKey(redisKey);
            }
        }
        //解封删除对应的行记录
        deviceForbiddenRepository.delete(deviceForbidden);
        return 1;
    }

    @Override
    public Pair<List<DeviceForbiddenDto>, Map<String, Object>> getDeviceForbiddenList(String digitId, String nick, Integer identity,String deviceName,String ipAddress, Integer pageSize, Integer pageIndex) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<DeviceForbiddenDto> forbiddenDtoPage = deviceForbiddenRepository
                .getDeviceForbiddenList(digitId, nick, identity,deviceName,ipAddress, new Date(), pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", forbiddenDtoPage.getTotalPages() );
        extra.put("totalEle", forbiddenDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);


        return Pair.of(forbiddenDtoPage.getContent(), extra);
    }

}
