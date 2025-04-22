package com.alphamaleclub.ucmc.auction.service;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.auction.domain.AuctionImage;
import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import com.alphamaleclub.ucmc.auction.dto.AuctionImageResponse;
import com.alphamaleclub.ucmc.auction.dto.AuctionRequest;
import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.auction.repository.AuctionRepository;
import com.alphamaleclub.ucmc.member.Repositorty.MemberRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionNotExistException;
import com.alphamaleclub.ucmc.system.exception.auction.ImageNotFoundException;
import com.alphamaleclub.ucmc.system.exception.auction.InvalidImageCountException;
import com.alphamaleclub.ucmc.system.exception.auction.InvalidStartPriceException;
import com.alphamaleclub.ucmc.system.exception.member.LoginRequiredException;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageConvertService;
import com.alphamaleclub.ucmc.tradeBoard.service.S3StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.alphamaleclub.ucmc.system.exception.ExceptionMessage.Auction.*;
import static com.alphamaleclub.ucmc.system.exception.ExceptionMessage.Member.LOGIN_REQUIRED_EXCEPTION;
import static com.alphamaleclub.ucmc.system.exception.ExceptionMessage.Member.USER_NOT_FOUND_EXCEPTION;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionServiceImpl {

    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;
    private final S3StorageService s3StorageService;
    private final ProductImageConvertService imageConvertService;
    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    // key → URL 변환용
    private String getBaseUrl() {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/";
    }

    // 경매글 생성 시 dto에서 변환
    public Long createAuction(AuctionRequest dto, List<MultipartFile> imageFiles) throws IOException {
        Long memberId = SecurityUtil.getCurrentMemberId();
        if (memberId == null) {
            throw new LoginRequiredException(LOGIN_REQUIRED_EXCEPTION);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION));

        // 이미지 수 검증: 최소 1개, 최대 5개
        if (imageFiles == null || imageFiles.isEmpty() || imageFiles.size() > 5) {
            throw new InvalidImageCountException(INVALID_IMAGE_COUNT_EXCEPTION);
        }

        // 시작가 0원 이하 방지 엔티티 내부에서도 체크하지만 서비스 계층에서 한 번 더 방어
        if (dto.getPrice() <= 0) {
            throw new InvalidStartPriceException(INVALID_START_PRICE_EXCEPTION);
        }

        // Auction 엔티티 생성
        Auction auction = dto.toEntity(member);

        List<byte[]> converted = imageConvertService.convert(imageFiles);

        // 각 파일을 S3에 업로드 후 AuctionImage 생성 및 추가
        for (byte[] bytes : converted) {
            // S3StorageService.upload()를 사용하여 파일 업로드 후 URL 반환
            // 프리픽스로 경로 구분 ex) auction/UUID.jpg
            String fileName = "auction/" + UUID.randomUUID() + ".jpg";
            String imageUrl = s3StorageService.upload(bytes, fileName);

            AuctionImage auctionImage = AuctionImage.builder()
                    .imageUrl(imageUrl)
                    .auction(auction)
                    .build();
            auction.addImage(auctionImage);
        }

        // id 를 얻고자 하는데 불필요한 오버헤드가 생기는것 같음.
        // dto 를 거치지않고 캡슐화된 id를 가져오도록 할지 고민 후 결정
        Auction saved = auctionRepository.save(auction);
        return saved.toDto().getId();
    }

    // 새 이미지 업로드
    public void addAuctionImages(Long auctionId, List<MultipartFile> newFiles) throws IOException {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));
        // 입찰 있으면 막기
        auction.validateEditableOrDeletable();

        int remain = 5 - auction.getModifiableImages().size();
        if (newFiles == null || newFiles.isEmpty() || newFiles.size() > remain) {
            throw new InvalidImageCountException(INVALID_IMAGE_COUNT_EXCEPTION);
        }

        List<byte[]> converted = imageConvertService.convert(newFiles);

        for (byte[] bytes : converted) {
            String key  = "auction/" + UUID.randomUUID() + ".jpg";
            String url  = s3StorageService.upload(bytes, key);

            auction.addImage(
                    AuctionImage.builder()
                            .imageUrl(url)
                            .auction(auction)
                            .build()
            );
        }
    }

    // 경매글 첨부 사진 수정 (원하는 사진만 골라서 수정 가능하도록)
    public void updateAuctionImages(Long auctionId, Map<Long, MultipartFile> imagesToUpdate) throws IOException {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));

        auction.validateEditableOrDeletable();

        List<String> oldKeysToDelete = new ArrayList<>();

        // imagesToUpdate : 수정할 이미지의 ID와 새 파일
        for (Map.Entry<Long, MultipartFile> entry : imagesToUpdate.entrySet()) {
            Long imageId = entry.getKey();
            MultipartFile newFile = entry.getValue();

            // 해당 이미지 엔티티 찾기
            AuctionImage auctionImage = auction.getModifiableImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new ImageNotFoundException(IMAGE_NOT_FOUND_EXCEPTION));

            // 새 파일 업로드
            byte[] bytes = imageConvertService.convert(List.of(newFile)).get(0);
            String newFileName = "auction/" + UUID.randomUUID() + ".jpg";
            String newImageUrl = s3StorageService.upload(bytes, newFileName);

            // 기존 이미지 key 저장 후 URL 교체
            oldKeysToDelete.add(auctionImage.getImageUrl().replace(getBaseUrl(), ""));
            auctionImage.updateImageUrl(newImageUrl);
        }

        // 수정 후 전체 첨부 개수가 1 ~ 5개인지 최종 검증
        if (auction.getModifiableImages().isEmpty() || auction.getModifiableImages().size() > 5) {
            throw new InvalidImageCountException(INVALID_IMAGE_COUNT_EXCEPTION);
        }

        // 데이터 정합성을 위해 트랜잭션 커밋 이후 S3 에서 객체 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                oldKeysToDelete.forEach(s3StorageService::delete);
            }
        });
    }

    // 경매글 첨부 사진 삭제
    // imageIds 가 null 혹은 빈리스트인 경우 전체삭제 아닌경우 해당 이미지들만 삭제
    // 이미지 식별을 위해 baseurl 사용. 해당 url 안 객체의 key 기준으로 삭제
    public void deleteAuctionImages(Long auctionId, List<Long> imageIds) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));

        auction.validateEditableOrDeletable();

        List<String> keysToDelete = new ArrayList<>();

        if (imageIds == null || imageIds.isEmpty()) {
            // 전체 삭제
            for (AuctionImage image : auction.getModifiableImages()) {
                String key = image.getImageUrl().replace(getBaseUrl(), "");
                keysToDelete.add(key);
            }
            auction.getModifiableImages().clear();
        } else {
            // 선택 삭제
            List<AuctionImage> imagesToRemove = auction.getModifiableImages().stream()
                    .filter(image -> imageIds.contains(image.getId()))
                    .toList();
            for (AuctionImage image : imagesToRemove) {
                String key = image.getImageUrl().replace(getBaseUrl(), "");
                keysToDelete.add(key);
                auction.removeImage(image);
            }
        }

        if (auction.getModifiableImages().isEmpty()) {
            throw new InvalidImageCountException(INVALID_IMAGE_COUNT_EXCEPTION);
        }

        // 데이터 정합성을 위해 트랜잭션 커밋 이후 S3 에서 객체 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                keysToDelete.forEach(s3StorageService::delete);
            }
        });
    }

    @Transactional
    public List<AuctionImageResponse> getAuctionImages(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));

        // 엔티티 → DTO 변환
        return auction.getModifiableImages().stream()
                .map(img -> new AuctionImageResponse(img.getId(), img.getImageUrl()))
                .toList();
    }

    // 경매글 조회 시 dto로 반환
    public AuctionResponse getAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));
        return auction.toDto();
    }

    // 입찰 진행
    public void bidAuction(Long auctionId, int bidPrice) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));
        auction.placeBid(bidPrice);
    }

    // 경매글 수정 (입찰이 없을 때만 가능)
    public void updateAuction(Long auctionId, AuctionRequest dto) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));
        auction.updateAuction(dto.getTitle(), dto.getContent(), dto.getDescription());
    }

    // 경매글 삭제 (입찰이 없을 때만 가능)
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException(AUCTION_NOT_EXIST_EXCEPTION));
        auction.validateEditableOrDeletable();
        auctionRepository.delete(auction);
    }

    // 경매글 목록 (검색, 페이징, 정렬, 옵션)
    public Page<AuctionResponse> getAuctionList(String title, String nickname, boolean ongoingOnly, Pageable pageable) {
        // null 또는 빈 문자열 이스케이프
        String safeTitle = (title == null) ? "" : title.trim();
        String safeNickname = (nickname == null) ? "" : nickname.trim();

        Page<Auction> auctions;

        if (ongoingOnly) {
            // 경매 상태가 ONGOING 인 것만
            auctions = auctionRepository.searchAuctionsByStatus(
                    safeTitle,
                    safeNickname,
                    AuctionStatus.ONGOING,
                    pageable
            );
        } else {
            // 전체 상태
            auctions = auctionRepository.searchAuctions(
                    safeTitle,
                    safeNickname,
                    pageable
            );
        }

        // Page<Auction> -> Page<AuctionResponse> 페이징된 채로 dto로 반환
        return auctions.map(Auction::toDto);
    }

}
