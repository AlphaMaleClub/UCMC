package com.alphamaleclub.ucmc.member.Repositorty;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.SignUpTempMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignUpTempMemberRepository extends JpaRepository<SignUpTempMember,Long> {

    Optional<SignUpTempMember> findByEmail (String email);


}
