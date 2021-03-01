package com.fmisser.gtc.auth.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_apple_auth_key")
@Data
public class AppleAuthKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 16)
    private String kty;

    @Column(unique = true, length = 16)
    private String kid;

    @Column(length = 16, name = "key_use")
    private String use;

    @Column(length = 16)
    private String alg;

    @Column(length = 4096)
    private String n;

    @Column(length = 16)
    private String e;
}
