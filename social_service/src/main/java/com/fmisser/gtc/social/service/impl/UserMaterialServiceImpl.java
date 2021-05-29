package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserMaterial;
import com.fmisser.gtc.social.repository.UserMaterialRepository;
import com.fmisser.gtc.social.service.UserMaterialService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author by fmisser
 * @create 2021/5/26 4:46 下午
 * @description TODO
 */

@Slf4j
@Service
@AllArgsConstructor
public class UserMaterialServiceImpl implements UserMaterialService {

    private final UserMaterialRepository userMaterialRepository;

    @Override
    public List<UserMaterial> getAuditPreparePhotos(User user) throws ApiException {
        return userMaterialRepository.getPhotos(user.getId(), 11);
    }

    @Override
    public List<UserMaterial> getAuditPhotos(User user) throws ApiException {
        return userMaterialRepository.getPhotos(user.getId(), 12);
    }

    @Override
    public List<UserMaterial> getPhotos(User user) throws ApiException {
        return userMaterialRepository.getPhotos(user.getId(), 0);
    }

    @Override
    public UserMaterial getAuditVideo(User user) throws ApiException {
        return userMaterialRepository.getAuditVideo(user.getId());
    }

    @Override
    public void deleteList(Collection<UserMaterial> userMaterialList) throws ApiException {
        if (userMaterialList.size() > 0) {
            userMaterialList.forEach(userMaterial -> userMaterial.setIsDelete(1));
            userMaterialRepository.saveAll(userMaterialList);
        }
    }

    @Override
    public void addList(Collection<UserMaterial> userMaterialList) throws ApiException {
        if (userMaterialList.size() > 0) {
            userMaterialRepository.saveAll(userMaterialList);
        }
    }

    @Override
    public void updateList(Collection<UserMaterial> userMaterialList) throws ApiException {
        if (userMaterialList.size() > 0) {
            userMaterialRepository.saveAll(userMaterialList);
        }
    }
}
