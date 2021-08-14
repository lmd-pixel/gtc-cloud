package com.fmisser.gtc.base.dto.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *  社交服务关注实体dto
 */
@Data
public class FollowDto {

    private Long userIdFrom;

    private Long userIdTo;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private int status;

    public FollowDto(){

    }


    public FollowDto(Long userIdFrom,Long userIdTo,Date createTime,int status){
        this.userIdFrom=userIdFrom;
        this.userIdTo=userIdTo;
        this.createTime=createTime;
        this.status=status;

    }

}
