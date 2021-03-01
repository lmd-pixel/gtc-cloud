package com.fmisser.gtc.auth.scheduler;

import com.fmisser.gtc.auth.domain.AppleAuthKey;
import com.fmisser.gtc.auth.feign.AppleAuthFeign;
import com.fmisser.gtc.auth.repository.AppleAuthKeyRepository;
import com.fmisser.gtc.base.dto.apple.PublicKeysDto;
import com.fmisser.gtc.base.prop.AppleConfProp;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AppAuthKeyScheduler {
    @Autowired
    private AppleAuthKeyRepository appleAuthKeyRepository;

    @Autowired
    private AppleAuthFeign appleAuthFeign;

    @Autowired
    private AppleConfProp appleConfProp;

    @SneakyThrows
    @Scheduled(cron = "0 0/30 * * * ?")
    public void getAppleAuthKey() {
        PublicKeysDto publicKeysDto = appleAuthFeign.getAuthKeys(new URI(appleConfProp.getAuthKeysUrl()));

        List<AppleAuthKey> appleAuthKeyList = new ArrayList<>();
        for (PublicKeysDto.KeyItem keyItem:
             publicKeysDto.getKeys()) {

            AppleAuthKey appleAuthKey = appleAuthKeyRepository.findByKid(keyItem.getKid());
            if (Objects.isNull(appleAuthKey)) {
                appleAuthKey = new AppleAuthKey();
                appleAuthKey.setKid(keyItem.getKid());
            }

            appleAuthKey.setKty(keyItem.getKty());
            appleAuthKey.setUse(keyItem.getUse());
            appleAuthKey.setAlg(keyItem.getAlg());
            appleAuthKey.setN(keyItem.getN());
            appleAuthKey.setE(keyItem.getE());

            appleAuthKeyList.add(appleAuthKey);
        }

        if (appleAuthKeyList.size() > 0) {
            appleAuthKeyRepository.saveAll(appleAuthKeyList);
        }
    }
}
