package com.alphamaleclub.ucmc.chat.entity;


import com.alphamaleclub.ucmc.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "chat_room_id"})
})
public class UserChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public static UserChatRoom toEntity(Member member, ChatRoom chatRoom) {
        UserChatRoom userChatRoom = new UserChatRoom();
        userChatRoom.member = member;
        userChatRoom.chatRoom = chatRoom;
        return userChatRoom;
    }

}
