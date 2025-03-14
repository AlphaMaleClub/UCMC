package com.alphamaleclub.ucmc.tradeBoard.service.impl;


import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.MemberRepository;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import com.alphamaleclub.ucmc.tradeBoard.dto.CreateBoardRequest;
import com.alphamaleclub.ucmc.tradeBoard.repository.TradePostRepository;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageConvertService;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageService;
import com.alphamaleclub.ucmc.tradeBoard.service.S3StorageService;
import com.alphamaleclub.ucmc.tradeBoard.service.TradePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TradePostServiceImpl implements TradePostService {

    private final TradePostRepository tradePostRepository;

    private final ProductImageConvertService productImageConvertService;
    private final ProductImageService productImageService;
    private final S3StorageService s3StorageService;

    private final MemberRepository memberRepository;

    @Override
    public String createTradePost(CreateBoardRequest request, List<MultipartFile> sourceImage) throws IOException {

        // product image를 추가 하려면 먼저 tradePost를 선 생성 해야한다.
        // member 파라미터로 받아서 따로 추가해주는 작업 해야함, principle 사용
        TradePost tradePost = saveTradePost(request);
        log.info("tradePost = {}", tradePost);

        // 컨버트한 바이트 배열 리스트.
        List<byte[]> files = productImageConvertService.convert(sourceImage);


        for (byte[] fileData : files) {

            //유니크 파일 경로 만들기
            String fileName = UUID.randomUUID().toString();

            //s3 업로드
            String imageUrl = s3StorageService.upload(fileData,fileName);
            System.out.println(imageUrl);

            // product image 객체 생성
            ProductImage productImage = productImageService.createTradeProductImage(tradePost.getPostId(), imageUrl);

        }




        return "ok";

    }

    // member 파라미터로 받아서 따로 추가해주는 작업 해야함, principle 사용
    @Override
    public TradePost saveTradePost(CreateBoardRequest request) {

        // 현재는 더미 member
        Member member = Member.builder()
                .nickName("시현")
                .build();

        memberRepository.save(member);
        log.info("member = {}", member);

        TradePost tradePost = TradePost.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .locate(request.getLocate())
                .contents(request.getContent())
                .member(member)
                .build();
        tradePostRepository.save(tradePost);

        return tradePost;

    }




}
