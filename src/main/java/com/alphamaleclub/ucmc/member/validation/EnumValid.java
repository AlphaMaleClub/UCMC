package com.alphamaleclub.ucmc.member.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD}) //어디에서 붙여 사용할지
@Retention(RetentionPolicy.RUNTIME) //어떤 때 실행될 지
@Constraint(validatedBy = EnumValidator.class) // ConstrainValidator 를 구현한 클래스 중 어떤 걸 검사할것인지. 이거보고 EnumValidator 가 인스턴스로 생성됨.
public @interface EnumValid {
    Class<? extends Enum<?>> enumClass(); //어떤 enum 클래스인지 기억하는 필드.
    String message() default "값이 올바르지 않습니다."; //기본적으로 가지고있을 메세지(실패하면 이거 날라감)
    Class<?>[] groups() default {}; // 있어야하는거
    Class<? extends Payload>[] payload() default {}; //이것도 있어야하는거 일종의 시그니쳐임
}
