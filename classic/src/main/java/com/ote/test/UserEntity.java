package com.ote.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "T_USER")
@Data
@NoArgsConstructor
public class UserEntity {

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "INDEX")
    private Integer index;

    @Column(name = "NAME")
    private String name;
}