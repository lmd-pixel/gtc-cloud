package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.Interact;
import com.fmisser.gtc.social.repository.DynamicRepository;
import com.fmisser.gtc.social.repository.InteractRepository;
import com.fmisser.gtc.social.service.DynamicService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DynamicServiceImpl implements DynamicService {
    private final DynamicRepository dynamicRepository;
    private final InteractRepository interactRepository;

    public DynamicServiceImpl(DynamicRepository dynamicRepository,
                              InteractRepository interactRepository) {
        this.dynamicRepository = dynamicRepository;
        this.interactRepository = interactRepository;
    }

    @Override
    public Dynamic create(long userId, int type, String content, Map<String, MultipartFile> multipartFileMap) throws ApiException {
        Dynamic dynamic = new Dynamic();
        dynamic.setUserId(userId);
        dynamic.setType(type);
        dynamic.setContent(content);

        return dynamicRepository.save(dynamic);
    }

    @Override
    public List<Dynamic> getUserDynamicList(long userId, int page, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(page, pageSize);
        return dynamicRepository
                .findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0, pageable)
                .getContent();
    }

    @Transactional
    @Override
    public Interact newInteract(long dynamicId, long userId, int type, String content) throws ApiException {

        if (type == 0) {
            Optional<Interact> optionalInteract = interactRepository.findByDynamicIdAndUserId(dynamicId, userId);
            if (optionalInteract.isPresent()) {
                if (optionalInteract.get().getType() == 0 && optionalInteract.get().getIsDelete() == 0) {
                    // 已经点赞过
                    throw new ApiException(-1, "请勿重复点赞！");
                }
            }
        }

        Optional<Dynamic> optionalDynamic = dynamicRepository.findById(dynamicId);
        if (!optionalDynamic.isPresent()) {
            throw new ApiException(-1, "动态数据不存在或已删除！");
        }

        Dynamic dynamic = optionalDynamic.get();

        Interact interact = new Interact();
        interact.setUserId(userId);
        if (type == 0) {
            dynamic.setHeart(dynamic.getHeart() + 1);
            interact.setType(0);
        } else {
            dynamic.setComment(dynamic.getComment() + 1);
            interact.setType(1);
            interact.setContent(content);
        }

        interact.setDynamic(dynamic);
        dynamic.getInteracts().add(interact);

        // 更新dynamic
        dynamicRepository.save(dynamic);

        return interact;
    }

    @Transactional
    @Override
    public Interact cancelInteract(long interactId, long userId) throws ApiException {
        Optional<Interact> optionalInteract = interactRepository.findById(interactId);
        if (!optionalInteract.isPresent()) {
            throw new ApiException(-1, "动态交互数据不存在或已删除!");
        }

        Interact interact = optionalInteract.get();
        Dynamic dynamic = interact.getDynamic();
        if (dynamic == null) {
            throw new ApiException(-1, "动态数据不存在或已删除!");
        }

        if (interact.getIsDelete() == 1) {
            throw new ApiException(-1, "请勿重复操作");
        }

        interact.setIsDelete(1);

        int type = interact.getType();
        if (type == 0) {
            dynamic.setHeart(dynamic.getHeart() - 1);
        } else {
            dynamic.setComment(dynamic.getComment() - 1);
        }

        dynamicRepository.save(dynamic);

        return interact;
    }

    @Override
    public List<Interact> getCommentList(long dynamicId, int page, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(page, pageSize);
        return interactRepository
                .findByDynamicIdAndTypeAndIsDeleteOrderByCreateTimeDesc(dynamicId, 1, 0, pageable)
                .getContent();
    }
}
