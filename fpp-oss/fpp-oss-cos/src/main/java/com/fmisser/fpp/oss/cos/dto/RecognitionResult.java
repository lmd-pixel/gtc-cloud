package com.fmisser.fpp.oss.cos.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author by fmisser
 * @create 2021/7/9 1:39 下午
 * @description TODO
 */

@Data
@JacksonXmlRootElement(localName = "RecognitionResult")
public class RecognitionResult {
    @JacksonXmlProperty(localName = "PornInfo")
    private ResultInfo pornInfo;

    @JacksonXmlProperty(localName = "PoliticsInfo")
    private ResultInfo politicsInfo;

    @JacksonXmlProperty(localName = "TerroristInfo")
    private ResultInfo terroristInfo;

    @JacksonXmlProperty(localName = "AdsInfo")
    private ResultInfo adsInfo;
}
