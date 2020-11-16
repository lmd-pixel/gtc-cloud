package com.fmisser.gtc.social.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 动态评论
 */
@Entity
@Table(name = "t_dynamic_comment",
        indexes = {@Index(columnList = "dynamic_id,isDelete,createTime")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class DynamicComment {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dynamic_id", referencedColumnName = "id")
    private Dynamic dynamic;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userIdFrom;

    @Column(nullable = false)
    private String nicknameFrom;

    @Column
    private Long userIdTo;

    @Column
    private String nicknameTo;

    @Column
    private String content;

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
    private int isDelete = 0;

    // 防止嵌套调用引起的堆栈溢出
    @Override
    public String toString() {
        return "DynamicComment{" +
                "id=" + id +
                ",userIdFrom=" + userIdFrom +
                ",nicknameFrom=" + nicknameFrom +
                ",userIdTo=" + userIdTo +
                ",nicknameTo=" + nicknameTo +
                ",content=" + content +
                ",createTime=" + createTime.toString() +
                ",modifyTime=" + modifyTime.toString() +
                ",isDelete=" + isDelete;
    }
}
