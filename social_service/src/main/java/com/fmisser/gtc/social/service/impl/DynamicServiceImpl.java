package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.aop.ReTry;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.DynamicComment;
import com.fmisser.gtc.social.domain.DynamicHeart;
import com.fmisser.gtc.social.repository.DynamicCommentRepository;
import com.fmisser.gtc.social.repository.DynamicHeartRepository;
import com.fmisser.gtc.social.repository.DynamicRepository;
import com.fmisser.gtc.social.service.DynamicService;
import org.hibernate.PessimisticLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DynamicServiceImpl implements DynamicService {
    private final DynamicRepository dynamicRepository;
    private final DynamicHeartRepository dynamicHeartRepository;
    private final DynamicCommentRepository dynamicCommentRepository;

    public DynamicServiceImpl(DynamicRepository dynamicRepository,
                              DynamicCommentRepository dynamicCommentRepository,
                              DynamicHeartRepository dynamicHeartRepository) {
        this.dynamicRepository = dynamicRepository;
        this.dynamicHeartRepository = dynamicHeartRepository;
        this.dynamicCommentRepository = dynamicCommentRepository;
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
    public List<Dynamic> getUserDynamicList(long userId, long selfUserId, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<Dynamic> dynamicList =  dynamicRepository
                .findByUserIdAndIsDeleteOrderByCreateTimeDesc(userId, 0, pageable)
                .getContent();

        return _prepareDynamicResponse(dynamicList, selfUserId);
    }

    @Override
    @ReTry(value = {PessimisticLockingFailureException.class})
    public int dynamicHeart(long dynamicId, long userId, String nickname, int isCancel) throws ApiException {
        Optional<Dynamic> optionalDynamic = dynamicRepository.findById(dynamicId);
        if (!optionalDynamic.isPresent()) {
            throw new ApiException(-1, "动态数据不存在或已删除！");
        }
        Dynamic dynamic = optionalDynamic.get();

        DynamicHeart dynamicHeart;
        Optional<DynamicHeart> optionalDynamicHeart = dynamicHeartRepository.findByDynamicIdAndUserId(dynamicId, userId);

        if (optionalDynamicHeart.isPresent()) {
            dynamicHeart = optionalDynamicHeart.get();

            if (dynamicHeart.getIsCancel() == isCancel) {
                throw new ApiException(-1, "已经点赞或取消，请勿重复操作！");
            }

            dynamicHeart.setIsCancel(isCancel);
            dynamicHeart.setDynamic(dynamic);
        } else {

            if (isCancel == 1) {
                throw new ApiException(-1, "没有点赞记录无法取消！");
            }

            dynamicHeart = new DynamicHeart();
            dynamicHeart.setUserId(userId);
            dynamicHeart.setNickname(nickname);
            dynamicHeart.setDynamic(dynamic);
            dynamic.getHearts().add(dynamicHeart);
        }

        dynamicRepository.save(dynamic);

        return 1;
    }

    @Override
    @ReTry(value = {PessimisticLockingFailureException.class})
    public int newDynamicComment(long dynamicId, long userId, String nickname, String content) throws ApiException {
        Optional<Dynamic> optionalDynamic = dynamicRepository.findById(dynamicId);
        if (!optionalDynamic.isPresent()) {
            throw new ApiException(-1, "动态数据不存在或已删除！");
        }
        Dynamic dynamic = optionalDynamic.get();

        DynamicComment dynamicComment = new DynamicComment();
        dynamicComment.setUserIdFrom(userId);
        dynamicComment.setNicknameFrom(nickname);
        dynamicComment.setContent(content);
        dynamicComment.setDynamic(dynamic);
        dynamic.getComments().add(dynamicComment);

        dynamicRepository.save(dynamic);
        return 1;
    }

    @Override
    @ReTry(value = {PessimisticLockingFailureException.class})
    public int delDynamicComment(long commentId, long userId) throws ApiException {
        Optional<DynamicComment> optionalDynamicComment = dynamicCommentRepository.findById(commentId);
        if (!optionalDynamicComment.isPresent()) {
            throw new ApiException(-1, "评论数据不存在或已删除！");
        }

        DynamicComment dynamicComment = optionalDynamicComment.get();
        if (dynamicComment.getUserIdFrom() != userId) {
            throw new ApiException(-1, "无权限删除评论！");
        }

        if (dynamicComment.getIsDelete() == 1) {
            throw new ApiException(-1, "请勿重复删除！");
        }

        dynamicComment.setIsDelete(1);
        Dynamic dynamic = dynamicComment.getDynamic();
        dynamicRepository.save(dynamic);

        return 1;
    }

    @Override
    public List<DynamicComment> getDynamicCommentList(long dynamicId, int page, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(page, pageSize);
        return dynamicCommentRepository
                .findByDynamicIdAndIsDeleteOrderByCreateTimeDesc(dynamicId, 0, pageable)
                .getContent();
    }

    private List<Dynamic> _prepareDynamicResponse(List<Dynamic> dynamicList, Long selfUserId) {
        for (Dynamic dynamic:
             dynamicList) {

            List<DynamicComment> commentList = dynamic.getComments()
                    .stream()
                    .filter( dc -> dc.getIsDelete() == 0)
                    .collect(Collectors.toList());

            dynamic.setComments(commentList);
            dynamic.setCommentCount(commentList.size());

            Set<DynamicHeart> heartList = dynamic.getHearts()
                    .stream()
                    .filter((dh -> dh.getIsCancel() == 0))
                    .peek(dh -> {
                        if (dh.getUserId().equals(selfUserId)) {
                            dynamic.setSelfHeart(1);
                        }
                    })
                    .collect(Collectors.toSet());

            dynamic.setHearts(heartList);
            dynamic.setHeartCount(heartList.size());
        }

        return dynamicList;
    }

}
