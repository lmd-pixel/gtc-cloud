package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.AssetRepository;
import com.fmisser.gtc.social.service.AssetService;
import com.fmisser.gtc.social.service.ConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ConsumeServiceImpl implements ConsumeService {

    @Autowired
    private AssetRepository assetRepository;

    @Transactional
    @Override
    public int buyVip(User user) throws ApiException {

        final BigDecimal vipPrice = BigDecimal.valueOf(888);

        Asset asset = assetRepository.findByUserId(user.getId());
//        if (asset.getVipLevel() > 0) {
//            throw new ApiException(-1, "您已购买过VI，无需重复购买");
//        }

        // 由于上面的查询Asset为快照读，并没有加锁操作，
        // 所以当前判断和后面的减金币操作之间也有可能存在其他并发行为去减少金币，
        // 导致这里的判断满足条件，但真正减少金币的时候不满足条件
        // 方案1：将上面的读取变成当前读加锁，这样到整个事务提交不会存在别的行为去修改金币,性能问题不考虑
        // 方案2：在执行更新后再读取一次（因为更新操作存在当前读行为，所以更新后的读会读取到最新的数据，而非最开始读的数据，这跟RR机制并不冲突），
        // 判断金额是否小于0，小于0说明之前有其他行为减少金币，则可以认为不正确，回滚操作
        // 但也有可能出现ABA问题，即其他行为修改成小于0后又修改成大于0了，程序逻辑整体是有问题的，但业务上最终数据是对的
        // 方案3：jpa乐观锁，使用jpa乐观锁算是比较简单的方案，保存修改操作使用jpa默认的save方法，
        // 不要混合使用自定义update、delete操作，除非你知道你在做什么，确保针对某个实体要么整体jpa乐观锁，要么不用
        // 方案4：自定义乐观锁类似机制，使用原生sql时，可以考虑自己实现 update ... set coin = coin - xxx where coin >= xxx ,通过这样的方式保证逻辑正确
        if (asset.getCoin().compareTo(vipPrice) < 0) {
            throw new ApiException(-1, "您的聊币不足以支付，请先充值聊币后再购买!");
        }

        // 方案2
//        asset = assetRepository.findByUserId(user.getId());
//        if (asset.getCoin().compareTo(BigDecimal.ZERO) < 0) {
//            throw new ApiException(-1, "您的聊币不足以支付，请先充值聊币后再购买!");
//        }

        // 方案3 乐观锁方式，由于其他地方没有使用乐观锁，所以这里暂时不用
//        asset.setCoin(asset.getCoin().subtract(vipPrice));
//        asset.setVipLevel(1);
//        assetRepository.save(asset);

        // 方案4
        int ret = assetRepository.buyVip(user.getId(), vipPrice);
        if (ret != 1) {
            throw new ApiException(-1, "购买VIP失败，请稍后重试!");
        }

        return 1;
    }
}
