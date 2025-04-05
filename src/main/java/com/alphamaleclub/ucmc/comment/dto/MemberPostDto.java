package com.alphamaleclub.ucmc.comment.dto;




import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberPostDto {
    private Member member;
    private TradePost post;

}
