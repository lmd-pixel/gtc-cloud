package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.District;
import com.fmisser.gtc.social.repository.DistrictRepository;
import com.fmisser.gtc.social.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public List<District> getDistrictList() throws ApiException {
        List<District> districtList = districtRepository.getDistrictList();

        List<District> provinceList = districtList.stream()
                .filter( district -> district.getType() == 1)
                .collect(Collectors.toList());

        List<District> cityList = districtList.stream()
                .filter(district -> district.getType() == 2)
                .collect(Collectors.toList());

        provinceList.forEach(province -> {
            province.setSubs(cityList.stream().filter(city ->
                city.getPid() == province.getId()).collect(Collectors.toList()));
        });

        return provinceList;
    }
}
