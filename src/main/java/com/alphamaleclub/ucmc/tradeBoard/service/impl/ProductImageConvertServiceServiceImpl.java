package com.alphamaleclub.ucmc.tradeBoard.service.impl;

import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.S3Image.ImageConvertException;
import com.alphamaleclub.ucmc.system.exception.S3Image.ImageValidationException;
import com.alphamaleclub.ucmc.system.exception.S3Image.MaxFileLimitExceededException;
import com.alphamaleclub.ucmc.tradeBoard.service.ProductImageConvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageConvertServiceServiceImpl implements ProductImageConvertService {

    @Override
    public List<byte[]> convert(List<MultipartFile> files) {

        // 파일이 없으면 오류를 띄운다.
        if (files == null || files.isEmpty()) {
            throw new ImageConvertException(ExceptionMessage.Image.IMAGE_FILES_EMPTY_EXCEPTION);
        }


        // 파일이 5개 이상일때는 오류를 띄운다.
        if (files.size() > 5) {
            throw new MaxFileLimitExceededException(ExceptionMessage.Image.IMAGES_FILES_LIMIT_EXCEEDED_EXCEPTION);
        }


        // 여러개의 이미지를 받아서 등록을 해야함으로 바이트 배열 리스트를 생성해준다.
        List<byte[]> convertedImages = new ArrayList<>();

        //
        for (MultipartFile file : files) {
            try {
                // 사용 할 수 있는 이미지 인지 확인을 한다.
                BufferedImage sourceImage = checkValidImg(file);

                // 이미지를 바이트 배열로 컨버트 한다.
                byte[] imageBytes = convertBufferedImageToByteArray(sourceImage);
                convertedImages.add(imageBytes);

            } catch (IOException e) {
                throw new ImageConvertException(ExceptionMessage.Image.IMAGE_CONVERT_EXCEPTION, e);
            }
        }
        return convertedImages;
    }

    protected BufferedImage checkValidImg(MultipartFile file) throws IOException {


        //이미지 크기 편집할 수 있게끔 만들기
        //실제 이미지를 버퍼드 이미지라는 형식로 만들어준다.
        //우리가 생각하는 편집이 단계가 있다.
        //파일의 실제 내용(바이트 데이터)을 스트림으로 반환합니다.
        //스트림을 사용하면 파일의 내용을 직접 읽거나 변환할 수 있다.
        BufferedImage image = ImageIO.read(file.getInputStream());

        // 이미지가 null인지 확인 (지원되지 않는 형식이거나 손상된 파일이면 에러 띄우기)
        if (image == null) {
            throw new ImageValidationException(ExceptionMessage.Image.INVALID_IMAGE_FORMAT_EXCEPTION);
        }

        // 파일 사이즈 체크 (허용된 용량보다 크다면 에러를 띄운다.)
        if (file.getSize() > MAX_SIZE) {
            throw new ImageValidationException(ExceptionMessage.Image.IMAGE_FILE_TOO_LARGE_EXCEPTION);
        }

        // 파일의 픽셀 체크 (픽셀이 500px, 500px 이하 일 경우 에러를 띄움)
        if (image.getWidth() < VALID_MIN_WIDTH || image.getHeight() < VALID_MIN_HEIGHT) {
            throw new ImageValidationException(ExceptionMessage.Image.IMAGE_DIMENSION_TOO_SMALL_EXCEPTION);
        }

        //파일의 픽셀 체크 (픽셀이 5000px , 5000px 이상 일  경우 에러를 띄움)
        if (image.getWidth() > VALID_MAX_WIDTH || image.getHeight() > VALID_MAX_HEIGHT) {
            throw new ImageValidationException(ExceptionMessage.Image.IMAGE_DIMENSION_EXCEEDED_EXCEPTION);
        }




        return image;
    }

    private byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException {


        // 파일을 생성하지 않고 바이트 데이터를 저장 가능
        // 메모리에서 직접 관리하는 바이트 기반 출력 스트림
        // bufferdImage -> byte[]로 변환 하기 위한 일반적인 방법으로 ByteArrayOutputStream을 사용한다.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // bufferdImage를 JPEG 형식으로 변경하여 ByteArrayOutputStream에 저장한다.
        // JPEG 형식으로 변환 하는 이유
        // 1. 저장 공간 절약
        // 2. 웹에서 png보다 빠르게 로딩 됨
        // 3. api 응답시 데이터 전송 속도가 빨라짐
        ImageIO.write(image, "jpg", baos);

        return baos.toByteArray();
    }




}
