package com.fmisser.gtc.social.service.impl;

import com.fmisser.fpp.cache.redis.service.RedisService;
import com.fmisser.gtc.base.dto.social.ForbiddenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.DeviceForbidden;
import com.fmisser.gtc.social.domain.Forbidden;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserDevice;
import com.fmisser.gtc.social.repository.DeviceForbiddenRepository;
import com.fmisser.gtc.social.repository.ForbiddenRepository;
import com.fmisser.gtc.social.service.ForbiddenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForbiddenServiceImpl implements ForbiddenService {

    private final ForbiddenRepository forbiddenRepository;
    private  final DeviceForbiddenRepository deviceForbiddenRepository;
    private final RedisService redisService;

    public ForbiddenServiceImpl(ForbiddenRepository forbiddenRepository, DeviceForbiddenRepository deviceForbiddenRepository, RedisService redisService) {
        this.forbiddenRepository = forbiddenRepository;
        this.deviceForbiddenRepository = deviceForbiddenRepository;
        this.redisService = redisService;
    }



    @Override
    public int forbidden(User user, int days, String message) throws ApiException {
        Forbidden forbidden = getUserForbidden(user);
        if (Objects.nonNull(forbidden)) {
            throw new ApiException(-1, "用户已经被封号或账号已注销！");
        }

        forbidden = new Forbidden();
        forbidden.setUserId(user.getId());
        forbidden.setMessage(message);
        forbidden.setDays(days);

        Date startTime = new Date();
        forbidden.setStartTime(startTime);

        if (days > 0) {

            Date endTime = new Date(startTime.getTime() + (long) days * 3600 * 1000 * 24);
            forbidden.setEndTime(endTime);
            redisService.set("user:forbidden:"+user.getPhone(),user.getPhone(),(long) days * 3600 * 24);
        }else{

            redisService.set("user:forbidden:"+user.getPhone(),user.getPhone(),365*5 * 3600 * 24);
        }


        forbiddenRepository.save(forbidden);

        return 1;
    }

    /*****
     * 封设备或者ip，插入数据到t_device+forbidden中
     * 将对应封锁的设备或者IP存入redis中便于登录或者注册查询时使用
     * @param userDevice
     * @param type
     * @param days
     * @param message
     * @return
     * @throws ApiException
     */
    @Override
    public int deviceceForbidden(UserDevice userDevice,String type, int days, String message) throws ApiException {
        DeviceForbidden deviceForbidden=new DeviceForbidden();
        if(type.equals("1")){
            deviceForbidden=deviceForbiddenRepository.getDeviceForbiddenByUserIdAndDeviceIdAndDisable(userDevice.getUserId(),userDevice.getId(),0);
            if (Objects.nonNull(deviceForbidden)) {
                throw new ApiException(-1, "该用户的此设备已经被封号或账号已注销！");
            }
        }
        if(type.equals("2")){
            deviceForbidden=deviceForbiddenRepository.getDeviceForbiddenByUserIdAndIpAndDisable(userDevice.getUserId(),userDevice.getIpAddr(),0);
            if (Objects.nonNull(deviceForbidden)) {
                throw new ApiException(-1, "该用户的此ip已经被封号或账号已注销！");
            }
        }

         deviceForbidden=new DeviceForbidden();
        deviceForbidden.setMessage(message);
        deviceForbidden.setDays(days);
        deviceForbidden.setType(Integer.valueOf(type));
        deviceForbidden.setStartTime(new Date());
        deviceForbidden.setCreateTime(new Date());
        Date startTime = new Date();
        if (days > 0) {
            Date endTime = new Date(startTime.getTime() + (long) days * 3600 * 1000 * 24);
            deviceForbidden.setEndTime(endTime);
        }
        deviceForbidden.setUserId(userDevice.getUserId());
        if(type.equals("1")){
            deviceForbidden.setDeviceId(userDevice.getId());
            //存入redis中设置key为设备ID+用户ID+设备码
            if (days > 0) {
                redisService.set(userDevice.getId()+":"+userDevice.getUserId()+":"+userDevice.getDeviceAndroidId(),userDevice.getDeviceAndroidId(),(long) days * 3600 * 24);
            }else{
                redisService.set(userDevice.getId()+":"+userDevice.getUserId()+":"+userDevice.getDeviceAndroidId(),userDevice.getDeviceAndroidId(),365*5 * 3600 * 24);
            }

        }
        if(type.equals("2")){
            deviceForbidden.setIp(userDevice.getIpAddr());
            //存入redis中设置key为设备ID+用户ID+IP
            if (days > 0) {
                redisService.set(userDevice.getId()+":"+userDevice.getUserId()+":"+userDevice.getIpAddr(),userDevice.getIpAddr(),(long) days * 3600  * 24);
            }else{
                redisService.set(userDevice.getId()+":"+userDevice.getUserId()+":"+userDevice.getIpAddr(),userDevice.getIpAddr(),365*5 * 3600  * 24);

            }

        }
         deviceForbiddenRepository.save(deviceForbidden);

        return 1;
    }

    @Override
    public int disableForbidden(User user) throws ApiException {
        List<Forbidden> forbiddenList = forbiddenRepository.findByUserIdAndDisable(user.getId(), 0);
        List<Forbidden> newForbiddenList = forbiddenList.stream()
                .peek(forbidden -> forbidden.setDisable(1)).collect(Collectors.toList());
        forbiddenRepository.saveAll(newForbiddenList);

        return 1;
    }

    @Override
    public Forbidden getUserForbidden(User user) throws ApiException {
        return forbiddenRepository.getCurrentForbiddenV2(user.getId(), new Date());
    }

    @Override
    public Pair<List<ForbiddenDto>, Map<String, Object>> getForbiddenList(String digitId, String nick, Integer identity,
                                                         Integer pageSize, Integer pageIndex) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<ForbiddenDto> forbiddenDtoPage = forbiddenRepository
                .getForbiddenListV2(digitId, nick, identity, new Date(), pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", forbiddenDtoPage.getTotalPages() );
        extra.put("totalEle", forbiddenDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(forbiddenDtoPage.getContent(), extra);
    }

    @Override
    public int disableForbidden(Long forbiddenId) throws ApiException {
        Optional<Forbidden> optionalForbidden = forbiddenRepository.findById(forbiddenId);
        if (!optionalForbidden.isPresent()) {
            throw new ApiException(-1, "封号不存在!");
        }

        Forbidden forbidden = optionalForbidden.get();
        forbidden.setDisable(1);
        String key="user:forbidden:"+forbidden.getUserId();
        if(redisService.hasKey(key)){
            redisService.delKey(key);
        }
        forbiddenRepository.save(forbidden);

        return 1;
    }
}
