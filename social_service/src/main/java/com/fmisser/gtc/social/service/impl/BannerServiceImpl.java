package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.social.domain.Banner;
import com.fmisser.gtc.social.repository.BannerRepository;
import com.fmisser.gtc.social.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private OssConfProp ossConfProp;

    @Override
    public List<Banner> getBannerList(String lang) throws ApiException {
        List<Banner> bannerList = bannerRepository.findAllByLangAndDisable(lang, 0);
        return _prepareBannerResponse(bannerList);
    }

    private List<Banner> _prepareBannerResponse(List<Banner> bannerList) {
        for (Banner banner: bannerList) {
            if (!StringUtils.isEmpty(banner.getImage())) {
                String imageUrl = String.format("%s/%s/%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getSystemConfigBucket(),
                        banner.getImage());
                banner.setImageUrl(imageUrl);
            }
        }
        return bannerList;
    }
}
