package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BlockDto {
    private Long userId;
    private Long blockUserId;
    private Long blockDynamicId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public  BlockDto(Long userId,Long blockUserId,Long blockDynamicId,Date createTime){
        this.userId=userId;
        this.blockUserId=blockUserId;
        this.blockDynamicId=blockDynamicId;
        this.createTime=createTime;
    }
}
