package com.alphamaleclub.ucmc.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
public class Member {

    @Id
    Long id;

    String email;

    Role role;

}
