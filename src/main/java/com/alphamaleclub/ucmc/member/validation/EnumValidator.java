package com.alphamaleclub.ucmc.member.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

//ConstraintValidator 를 구현하고있는데 어떤 어노테이션 위에서작동할지, 어떤 dataType 에서 작동할지 명시되어있음.
public class EnumValidator implements ConstraintValidator<EnumValid, String> {

    //EnumValid 어노테이션을 저장함(여기안에 있는 EnumClass 를 이용하기 위해)
    private EnumValid annotation;

    @Override//인스턴스 생성시 호출되는 메서드, 사용된 필드의 어노테이션 값을 가져옴(어떤 이넘클래스인지 알아야하니까)
    public void initialize(EnumValid constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) { return true;} //value 가 null 이면 보통 같이쓴 NotBlank 에서 검출할꺼니까 패쓰


        //stream API 를 통해서 enum 의 값을 차례차례 불러오고 그게 value 랑 맞는지 확인함. 하나라도 맞으면 true 반환.
        return Arrays.stream(annotation.enumClass().getEnumConstants())
                .anyMatch(e -> e.name().equals(value));


    }
}
