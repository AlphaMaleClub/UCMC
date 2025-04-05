package com.alphamaleclub.ucmc.tradeBoard.service.impl;


import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.system.exception.tradeboard.PostNotFoundException;
import com.alphamaleclub.ucmc.tradeBoard.domain.Status;
import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import com.alphamaleclub.ucmc.tradeBoard.dto.*;
import com.alphamaleclub.ucmc.tradeBoard.repository.TradePostRepository;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageConvertService;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageService;
import com.alphamaleclub.ucmc.tradeBoard.service.S3StorageService;
import com.alphamaleclub.ucmc.tradeBoard.service.TradePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    String baseUrl = "https://ucmcbucket.s3.ap-northeast-2.amazonaws.com/";



    @Override
    public TradePostMessageResponse createTradePost(CreateTradeBoardRequest request, List<MultipartFile> sourceImage) throws IOException {

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

        return TradePostMessageResponse.builder()
                .message("Success Created Trade Post")
                .result(true)
                .build();


    }

    // member 파라미터로 받아서 따로 추가해주는 작업 해야함, principle 사용
    @Override
    public TradePost saveTradePost(CreateTradeBoardRequest request) {

        // 현재는 더미 member
        Member member = Member.builder()
                .nickname("시현")
                .build();

        memberRepository.save(member);
        log.info("member = {}", member);

        TradePost tradePost = TradePost.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .locate(request.getLocate())
                .contents(request.getContent())
                .deliveryType(request.getDeliveryType())
                .member(member)
                .build();
        tradePostRepository.save(tradePost);

        return tradePost;

    }


    
    @Override
    public TradePostMessageResponse updateTradePost(UpdatePostRequest request, List<MultipartFile> sourceImage) throws IOException {


        Long postNumber = request.getPostNumber();

        // 기존 trade post를 가져온다.
        TradePost tradePost = tradePostRepository.findById(postNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다. ID: " + postNumber));

        // 기존 trade post 업데이트
        tradePost.updateTradePost(request.getStatus(), request.getTitle(), request.getPrice(), request.getLocate(), request.getContent(),request.getDeliveryType(),request.getDumpedCount(),LocalDateTime.now());
        TradePost saved = tradePostRepository.save(tradePost);

        // 새로운 이미지 변환
        List<byte[]> newImage = productImageConvertService.convert(sourceImage);

        // 기존 이미지 가져오기
        List<ProductImage> byPostTypeAndPostNumber = productImageService.getTradeProductImagesByPostTypeAndPostNumber(request.getPostType(),postNumber);

        int minSize = Math.min(newImage.size(), byPostTypeAndPostNumber.size());

        //  새 이미지
        if (newImage.size() < byPostTypeAndPostNumber.size()) {
            log.info("새 < 기");
            for (int i = 0; i < byPostTypeAndPostNumber.size(); i++) {
                if (i < minSize) {
                    byte[] fileData = newImage.get(i);
                    ProductImage productImage = byPostTypeAndPostNumber.get(i);
                    String beforeImageUrl = productImage.getImageUrl();
                    s3StorageService.upload(fileData, beforeImageUrl);
                    log.info("새 < 기 if 1 " + i);
                }

                if (i >= newImage.size()) {
                    log.info("기존 이미지 삭제 로직 = {}", i);
                    ProductImage productImage = byPostTypeAndPostNumber.get(i);
                    log.info(i +"회차 조회"+"productImage = {}", productImage);
                    String imageUrl = productImage.getImageUrl();

                    String key = imageUrl.replaceFirst(baseUrl, "");

                    s3StorageService.delete(key);

                    productImageService.deleteProductImage(productImage);

                    log.info("새 < 기 if 2 " + i);
                }
            }
        }

        // 기존 이미지와 새로운 이미지 개수가 같으면 업데이트
        if (newImage.size() == byPostTypeAndPostNumber.size()) {

            for (int i = 0; i < minSize; i++) {
                byte[] fileData = newImage.get(i);
                ProductImage productImage = byPostTypeAndPostNumber.get(i);
                s3StorageService.upload(fileData, productImage.getImageUrl());
            }
        }

        // 새로운 이미지가 기존 이미지보다 많을 경우 추가
        if (newImage.size() > byPostTypeAndPostNumber.size()) {
            log.info("기 < 새");
            for (int i = 0; i < newImage.size(); i++) {
                if (i < minSize) {
                    byte[] fileData = newImage.get(i);
                    ProductImage productImage = byPostTypeAndPostNumber.get(i);
                    s3StorageService.upload(fileData, productImage.getImageUrl());
                } else {
                    byte[] fileData = newImage.get(i);
                    String fileName = UUID.randomUUID().toString();
                    String imageUrl = s3StorageService.upload(fileData, fileName);
                    productImageService.createTradeProductImage(tradePost.getPostId(), imageUrl);
                }
            }
        }

        return TradePostMessageResponse.builder()
                .message("Success Updated Trade Post")
                .result(true)
                .build();

    }

    @Override
    public TradePostMessageResponse deleteTradePost(Long postId) {

        PostType postType = PostType.TRADE;
        Long postNum = postId;

        // productImage type을 가져온다.
        List<ProductImage> productImages = productImageService.getTradeProductImagesByPostTypeAndPostNumber(postType,postNum);

        // for eact 문을 돌려서 s3 버킷의 이미지 및 db의  productImage 를 지운다.
        for (ProductImage productImage : productImages) {

            String imageUrl = productImage.getImageUrl();

            String key = imageUrl.replaceFirst(baseUrl, "");

            s3StorageService.delete(key);

            productImageService.deleteProductImage(productImage);

        }

        // db에서 tradePost 도 지운다.
        tradePostRepository.deleteById(postNum);

        return TradePostMessageResponse.builder()
                .message("Success Deleted Trade Post")
                .result(true)
                .build();

    }

    public GetAllTradePostAndImagesMessageResponse getAllTradePost(int page) {

        Pageable pageable = PageRequest.of(page, 20);


        Page<TradePost> tradePosts = tradePostRepository.findAll(pageable);
        Page<ProductImage> images = productImageService.findAllProductImagesOnlyTradePost(pageable);


        return GetAllTradePostAndImagesMessageResponse.builder()
                .message("Success GetAllTradePostAndImagesMessageResponse")
                .result(true)
                .tradePosts(tradePosts)
                .images(images)
                .build();
    }


    @Override
    public TradePostAndProductImageResponse getTradePost(Long postId) {
        Long postNum = postId;

        Optional<TradePost> byId = tradePostRepository.findById(postNum);
        TradePost tradePost = byId.orElseThrow();

        List<ProductImage> productImages = productImageService.getTradeProductImagesByPostTypeAndPostNumber(PostType.TRADE,postNum);

        return TradePostAndProductImageResponse.builder()
                .title(tradePost.getTitle())
                .contents(tradePost.getContents())
                .price(tradePost.getPrice())
                .status(tradePost.getStatus())
                .locate(tradePost.getLocate())
                .createdAt(tradePost.getCreatedAt())
                .deliveryType(tradePost.getDeliveryType())
                .bumpedCount(tradePost.getBumpedCount())
                .nickName(tradePost.getMember().getNickname())
                .productImages(productImages)
                .build();

    }

    @Override
    public TradePostMessageResponse updateOnlyStatusTradePost(Long postId, Status status) {

        Optional<TradePost> byId = tradePostRepository.findById(postId);
        TradePost tradePost = byId.orElseThrow();

        tradePost.updateTradePost(status,tradePost.getTitle(),tradePost.getPrice(),tradePost.getLocate(),
                tradePost.getContents(),tradePost.getDeliveryType(),tradePost.getBumpedCount(),LocalDateTime.now());

        return TradePostMessageResponse.builder()
                .message("Success UpdateOnly Status To Trade Post")
                .result(true)
                .build();

    }


    @Override
    public TradePostMessageResponse updateOnlyUpdatedAt(Long postId) {

        Optional<TradePost> byId = tradePostRepository.findById(postId);
        TradePost tradePost = byId.orElseThrow();

        Long newBumpedCount;

        if (tradePost.getBumpedCount() >= 3) {

            return  TradePostMessageResponse.builder()
                    .message("사용 가능한 끌어올리기 횟수를 다 썼습니다 .가능한 횟수는 최대 3회 까지 입니다.")
                    .result(false)
                    .build();
        }

        if (tradePost.getBumpedCount() <= 2 ) {
            newBumpedCount = tradePost.getBumpedCount() + 1;
            log.info("newBumpedCount = {}", newBumpedCount);


            tradePost.updateTradePost(tradePost.getStatus(),tradePost.getTitle(),tradePost.getPrice(),
                    tradePost.getLocate(),tradePost.getContents(),tradePost.getDeliveryType(),newBumpedCount,LocalDateTime.now());

            return  TradePostMessageResponse.builder()
                    .message("현재 끌어올리기를" + newBumpedCount + "회 했습니다. 남은 횟수는 " + (3-newBumpedCount) + "회 입니다." )
                    .result(true)
                    .build();

        }

        return  TradePostMessageResponse.builder()
                .message("failed update Created")
                .result(true)
                .build();
    }

    @Override
    public TradePost getPostById(Long postId) {
        return tradePostRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("게시글을 찾을 수 없습니다")
        );
    }


}
