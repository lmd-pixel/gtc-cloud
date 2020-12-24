package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Block;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.BlockRepository;
import com.fmisser.gtc.social.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;

    public BlockServiceImpl(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @Override
    public int blockOneDynamic(User user, User dstUser, Long dynamicId, int isBlock) throws ApiException {
        return _innerSetBlock(12, user, dstUser, dynamicId, isBlock);
    }

    @Override
    public int blockUserDynamic(User user, User dstUser, int isBlock) throws ApiException {
        return _innerSetBlock(10, user, dstUser, null, isBlock);
    }

    @Override
    public int blockUser(User user, User dstUser, int isBlock) throws ApiException {
        return _innerSetBlock(20, user, dstUser, null, isBlock);
    }

    private int _innerSetBlock(int type, User user, User dstUser, Long dynamicId, int isBlock) throws ApiException {
        Block block = blockRepository.findByUserIdAndTypeAndBlockUserId(user.getId(), type, dstUser.getId());
        if(block == null) {
            block = new Block();
            block.setUserId(user.getId());
            block.setBlockUserId(dstUser.getId());
            if (Objects.nonNull(dynamicId)) {
                block.setBlockDynamicId(dynamicId);
            }
            block.setType(type);
        } else {
            if (block.getBlock() == isBlock) {
                throw new ApiException(-1, "无效操作!");
            } else {
                block.setBlock(isBlock);
            }
        }

        blockRepository.save(block);

        return 1;
    }
}
