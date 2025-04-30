package com.alphamaleclub.ucmc.chat.repository;


import com.alphamaleclub.ucmc.chat.entity.ChatRoom;
import com.alphamaleclub.ucmc.chat.entity.UserChatRoom;
import com.alphamaleclub.ucmc.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    boolean existsByMemberAndChatRoom(Member joinMember, ChatRoom joinRoom);

    Optional<UserChatRoom> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);

    //  member1, member2가 포함된 2명인 1:1 채팅방 chatRoomId찾기
    @Query("SELECT uc.chatRoom.id FROM UserChatRoom uc " +
            "WHERE uc.member IN (:member1, :member2) " +
            "GROUP BY uc.chatRoom.id " +
            "HAVING COUNT(DISTINCT uc.member) = 2")
    Optional<Long> findChatRoomIdsByMembers(@Param("member1") Member member1,
                                        @Param("member2") Member member2);

    List<ChatRoom> findByMemberId(Long memberId);

}
