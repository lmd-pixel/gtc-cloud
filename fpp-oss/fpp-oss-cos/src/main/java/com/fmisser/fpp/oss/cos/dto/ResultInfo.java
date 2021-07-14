package com.fmisser.fpp.oss.cos.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author by fmisser
 * @create 2021/7/9 1:42 下午
 * @description TODO
 */

@Data
public class ResultInfo {
    @JacksonXmlProperty(localName = "Code")
    private int code;

    @JacksonXmlProperty(localName = "Msg")
    private String msg;

    @JacksonXmlProperty(localName = "HitFlag")
    private int hitFlag;

    @JacksonXmlProperty(localName = "Score")
    private int score;

    @JacksonXmlProperty(localName = "Label")
    private String label;
}
