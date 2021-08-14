package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DynamicHeartListDto {

    public DynamicHeartListDto(Long id, Long dynamicId, Long userId, Date createTime,  int isCancel) {
        this.id = id;
        this.dynamicId = dynamicId;
        this.userId = userId;
        this.createTime = createTime;
        this.isCancel = isCancel;

    }

    public DynamicHeartListDto() {
    }

    private Long id;
    private Long dynamicId;
    private Long userId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private int isCancel;
}
