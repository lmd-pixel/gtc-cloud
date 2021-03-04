package com.fmisser.gtc.social.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 标签
 */

@Entity
@Table(name = "t_label", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Data
public class Label implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int type = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int disabled = 0;

}
