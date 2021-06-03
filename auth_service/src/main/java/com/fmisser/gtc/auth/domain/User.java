package com.fmisser.gtc.auth.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "t_user")
// unique 默认会创建索引，字符串类型请指定length
//uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String username;

    /**
     * 0: 通过账号密码方式创建的账户,username 可以是手机号也可以是其他
     * 1: 通过手机号验证码创建的账户,username 是手机号
     * 2：通过手机号一键登录创建的账户，username 是手机号
     * 11：第三方苹果登录
     * 12：wx登录
     */
    @Column(nullable = false, columnDefinition = "int default 0")
    private int type = 0;

    @Column
    private String password;

    @Column(name = "create_time", nullable = false, updatable = false)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer locked = 0;

    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer enabled = 1;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled == 1;
    }
}
