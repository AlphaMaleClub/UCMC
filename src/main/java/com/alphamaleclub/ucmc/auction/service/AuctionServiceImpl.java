package com.alphamaleclub.ucmc.auction.service;

import com.alphamaleclub.ucmc.auction.domain.Auction;
import com.alphamaleclub.ucmc.auction.domain.AuctionImage;
import com.alphamaleclub.ucmc.auction.domain.AuctionStatus;
import com.alphamaleclub.ucmc.auction.dto.AuctionRequest;
import com.alphamaleclub.ucmc.auction.dto.AuctionResponse;
import com.alphamaleclub.ucmc.auction.repository.AuctionRepository;
import com.alphamaleclub.ucmc.system.exception.auction.AuctionNotExistException;
import com.alphamaleclub.ucmc.system.exception.auction.ImageNotFoundException;
import com.alphamaleclub.ucmc.system.exception.auction.InvalidImageCountException;
import com.alphamaleclub.ucmc.tradeBoard.service.S3StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionServiceImpl {

    private final AuctionRepository auctionRepository;

    private final S3StorageService s3StorageService;

    private static final String BASE_URL = "https://ucmcbucket.s3.ap-northeast-2.amazonaws.com/";

    // 경매글 생성 시 dto에서 변환
    public Long createAuction(AuctionRequest dto, List<MultipartFile> imageFiles) throws IOException {
        // 이미지 수 검증: 최소 1개, 최대 5개
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new IllegalArgumentException("최소 1개의 이미지는 첨부되어야 합니다.");
        }
        if (imageFiles.size() > 5) {
            throw new IllegalArgumentException("첨부 가능한 이미지 수는 최대 5개입니다.");
        }

        // Auction 엔티티 생성
        Auction auction = Auction.from(dto);

        // 각 파일을 S3에 업로드 후 AuctionImage 생성 및 추가
        for (MultipartFile file : imageFiles) {
            // S3StorageService.upload()를 사용하여 파일 업로드 후 URL 반환
            String fileName = UUID.randomUUID().toString();
            String imageUrl = s3StorageService.upload(file.getBytes(), fileName);

            AuctionImage auctionImage = AuctionImage.builder()
                    .imageUrl(imageUrl)
                    .auction(auction)
                    .build();
            auction.addImage(auctionImage);
        }

        Auction saved = auctionRepository.save(auction);
        return saved.toDto().getId();
    }

    // 경매글 조회 시 dto로 반환
    public AuctionResponse getAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException("경매글이 존재하지 않습니다."));
        return auction.toDto();
    }

    // 입찰 진행
    public void bidAuction(Long auctionId, int bidPrice) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException("경매글이 존재하지 않습니다."));
        auction.placeBid(bidPrice);
    }

    // 경매글 수정 (입찰이 없을 때만 가능)
    public void updateAuction(Long auctionId, AuctionRequest dto) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException("경매글이 존재하지 않습니다."));
        auction.updateAuction(dto.getTitle(), dto.getContent(), dto.getDescription());
    }

    // 경매글 첨부 사진 수정 (원하는 사진만 골라서 수정 가능하도록)
    public void updateAuctionImages(Long auctionId, Map<Long, MultipartFile> imagesToUpdate) throws IOException {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException("경매글이 존재하지 않습니다."));

        // imagesToUpdate : 수정할 이미지의 ID와 새 파일
        for (Map.Entry<Long, MultipartFile> entry : imagesToUpdate.entrySet()) {
            Long imageId = entry.getKey();
            MultipartFile newFile = entry.getValue();

            // 해당 이미지 엔티티 찾기
            AuctionImage auctionImage = auction.getModifiableImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new ImageNotFoundException("해당 이미지가 존재하지 않습니다."));

            // 새 파일 업로드 (기존 파일 삭제 후 새로운 파일 업로드하는 로직 포함 가능)
            String newFileName = UUID.randomUUID().toString();
            String newImageUrl = s3StorageService.upload(newFile.getBytes(), newFileName);

            // AuctionImage의 imageUrl 업데이트
            auctionImage.updateImageUrl(newImageUrl);
        }

        // 수정 후 전체 첨부 개수가 1 ~ 5개인지 최종 검증
        if (auction.getModifiableImages().isEmpty() || auction.getModifiableImages().size() > 5) {
            throw new InvalidImageCountException("첨부 이미지 수는 최소 1개, 최대 5개여야 합니다.");
        }
    }

    // 경매글 첨부 사진 삭제
    // imageIds 가 null 혹은 빈리스트인 경우 전체삭제 아닌경우 해당 이미지들만 삭제
    // 이미지 식별을 위해 baseurl 사용. 해당 url 안 객체의 key 기준으로 삭제
    public void deleteAuctionImages(Long auctionId, List<Long> imageIds) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException("경매글이 존재하지 않습니다."));

        if (imageIds == null || imageIds.isEmpty()) {
            // 전체 삭제
            for (AuctionImage image : auction.getModifiableImages()) {
                String key = image.getImageUrl().replace(BASE_URL, "");
                s3StorageService.delete(key);
            }
            auction.getModifiableImages().clear();
        } else {
            // 선택 삭제
            List<AuctionImage> imagesToRemove = auction.getModifiableImages().stream()
                    .filter(image -> imageIds.contains(image.getId()))
                    .toList();
            for (AuctionImage image : imagesToRemove) {
                String key = image.getImageUrl().replace(BASE_URL, "");
                s3StorageService.delete(key);
                auction.removeImage(image);
            }
        }

        if (auction.getModifiableImages().isEmpty()) {
            throw new InvalidImageCountException("경매글에는 최소 1개의 이미지는 첨부되어야 합니다.");
        }
    }

    // 경매글 삭제 (입찰이 없을 때만 가능)
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotExistException("경매글이 존재하지 않습니다."));
        auction.validateDeletable();
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
