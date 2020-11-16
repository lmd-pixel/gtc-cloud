package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 动态点赞
 */

@Entity
@Table(name = "t_dynamic_heart",
        indexes = {@Index(columnList = "dynamic_id,isCancel"),
                @Index(columnList = "dynamic_id,userId")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class DynamicHeart {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dynamic_id", referencedColumnName = "id")
    private Dynamic dynamic;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date createTime;

    @LastModifiedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column
    private Date modifyTime;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int isCancel = 0;

    // 防止嵌套调用引起的堆栈溢出
    @Override
    public String toString() {
        return "DynamicHeart{" +
                "id=" + id +
                ",userId=" + userId +
                ",nickname=" + nickname +
                ",createTime=" + createTime.toString() +
                ",modifyTime=" + modifyTime.toString() +
                ",isDelete=" + isCancel;
    }
}
