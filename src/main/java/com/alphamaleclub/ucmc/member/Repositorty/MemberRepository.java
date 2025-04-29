package com.alphamaleclub.ucmc.member.Repositorty;

import com.alphamaleclub.ucmc.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAccountId (String username);

    Optional<Member> findByEmail (String email);

    Optional<Member> findByMobile (String mobile);

    Optional<Member> findByNickname (String nickname);

}
