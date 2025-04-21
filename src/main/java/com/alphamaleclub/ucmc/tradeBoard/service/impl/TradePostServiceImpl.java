package com.alphamaleclub.ucmc.tradeBoard.service.impl;


import com.alphamaleclub.ucmc.image.domain.PostType;
import com.alphamaleclub.ucmc.image.domain.ProductImage;
import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.Provider;
import com.alphamaleclub.ucmc.member.domain.Role;
import com.alphamaleclub.ucmc.system.exception.tradeboard.PostNotFoundException;
import com.alphamaleclub.ucmc.tradeBoard.domain.DeliveryType;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Top10PostResponse findTop10() {

        List<TradePost> top10ByOrderByUpdatedAtDesc = tradePostRepository.findTop10ByOrderByUpdatedAtDesc();

        List<ProductImageDto> images = new ArrayList<>();

        for (TradePost postInfo : top10ByOrderByUpdatedAtDesc) {

            try {
                Long postId = postInfo.getPostId();
                log.info("postId = {}", postId);

                ProductImage firstProductImage = productImageService.getProductImageByPostNumber(postId);

                if (firstProductImage == null) {
                    images.add(null);
                } else {
                    images.add(new ProductImageDto(firstProductImage.getPostNumber(), firstProductImage.getImageUrl()));
                }

                log.info("firstProductImage = {}", firstProductImage);

            } catch (Exception e) {
                log.warn("이미지 조회 중 에러 발생 - postId: {}", postInfo.getPostId(), e);
                images.add(null);
            }

        }

        return Top10PostResponse.builder()
                .message("Success GetAllTradePostAndImagesMessageResponse")
                .result(true)
                .tradePosts(top10ByOrderByUpdatedAtDesc)
                .images(images)
                .build();
    }

    @Override
    public GetAllTradePostAndImagesMessageResponse getAllTradePost(int page, String sort) {

        log.info("page = {}", page);
        log.info("sort = {}", sort);

        // sort 파라미터 파싱 (예: "price,asc")
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);

        Pageable pageable = PageRequest.of(page, 20, Sort.by(direction, sortBy));

        Page<TradePost> tradePosts = tradePostRepository.findAll(pageable);

        List<TradePost> content = tradePosts.getContent();
        log.info("content = {}", content);

        List<ProductImageDto> images = new ArrayList<>();

        for (TradePost postInfo : content) {
            try {
                Long postId = postInfo.getPostId();
                log.info("postId = {}", postId);

                ProductImage firstProductImage = productImageService.getProductImageByPostNumber(postId);

                if (firstProductImage == null) {
                    images.add(null);
                } else {
                    images.add(new ProductImageDto(firstProductImage.getPostNumber(), firstProductImage.getImageUrl()));
                }

                log.info("firstProductImage = {}", firstProductImage);

            } catch (Exception e) {
                log.warn("이미지 조회 중 에러 발생 - postId: {}", postInfo.getPostId(), e);
                images.add(null);
            }
        }

        if (!tradePosts.isEmpty()) {
            log.info("tradePosts.get(0) = {}", tradePosts.getContent().get(0).getTitle());
        } else {
            log.info("tradePosts is empty");
        }

        return GetAllTradePostAndImagesMessageResponse.builder()
                .message("Success GetAllTradePostAndImagesMessageResponse")
                .result(true)
                .tradePosts(tradePosts)
                .images(images)
                .build();
    }


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
                .email("asdf@naver.com")
                .password("1234")
                .provider(Provider.google)
                .role(Role.MEMBER)
                .accountId("1")
                .status(com.alphamaleclub.ucmc.member.domain.Status.active)
                .createdAt(LocalDateTime.now())
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
    public TradePostMessageResponse updateTradePost(Long postNumber,UpdatePostRequest request, List<MultipartFile> sourceImage) throws IOException {

        log.info("sourceImage = {}", sourceImage);

        // 기존 trade post를 가져온다.
        TradePost tradePost = tradePostRepository.findById(postNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다. ID: " + postNumber));

        // 기존 trade post 업데이트
        tradePost.updateTradePost(request.getStatus(), request.getTitle(), request.getPrice(), request.getLocate(), request.getContent(),request.getDeliveryType(),request.getBumpedCount(),LocalDateTime.now());
        TradePost saved = tradePostRepository.save(tradePost);

        // 기존 이미지 가져오기
        List<ProductImage> beforeImage = productImageService.getTradeProductImagesByPostNumber(postNumber);
        ProductImage beforeImage1 = beforeImage.get(0);


        log.info("beforeImage1.getImageUrl() = {}", beforeImage1.getImageUrl());


        // 기존 이미지 삭제
        for (ProductImage productImage : beforeImage) {
            String imageUrl = productImage.getImageUrl();
            String key = imageUrl.replaceFirst("https://ucmcbucket.s3.ap-northeast-2.amazonaws.com/", "");
            s3StorageService.delete(key);
            productImageService.deleteProductImage(productImage);
        }

        // 새로운 이미지 컨버트
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
                .message("Success Updated Trade Post")
                .result(true)
                .build();

    }

    @Override
    public TradePostMessageResponse deleteTradePost(Long postId) {

        PostType postType = PostType.TRADE;
        Long postNum = postId;

        // productImage type을 가져온다.
        List<ProductImage> productImages = productImageService.getTradeProductImagesByPostNumber(postNum);

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





    @Override
    public TradePostAndProductImageResponse getTradePost(Long postId) {
        Long postNum = postId;

        Optional<TradePost> byId = tradePostRepository.findById(postNum);
        TradePost tradePost = byId.orElseThrow();
        Long memberId = tradePost.getMember().getId();

        List<ProductImage> productImages = productImageService.getTradeProductImagesByPostNumber(postNum);

        return TradePostAndProductImageResponse.builder()
                .title(tradePost.getTitle())
                .content(tradePost.getContents())
                .price(tradePost.getPrice())
                .status(tradePost.getStatus())
                .locate(tradePost.getLocate())
                .createdAt(tradePost.getCreatedAt())
                .deliveryType(tradePost.getDeliveryType())
                .bumpedCount(tradePost.getBumpedCount())
                .nickName(tradePost.getMember().getNickname())
                .productImages(productImages)
                .memberId(memberId)
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

    @Override
    public void createDummyPost() {
        for (int i = 1; i < 100; i++) {
            Optional<Member> member = memberRepository.findById(1L);
            Member member1 = member.orElseThrow();

            TradePost tradePost = TradePost.builder()
                    .title("test" + i)
                    .price((long) i)
                    .locate("test" + i)
                    .contents("test" + i)
                    .deliveryType(DeliveryType.BOTH)
                    .member(member1)
                    .build();
            tradePostRepository.save(tradePost);
        }
    }


}
