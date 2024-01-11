package com.cos.security1.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

//순환 참조가 나는 이유:
//SpringContainer에서 처음 빈으로 등록하기 위해 객체를 생성하여 준다.(싱글톤)
//그래서 SecurityConfig 객체를 생성하던 중
//PrincipalOauth2UserService 객체를 의존하고 있네? -> PrincipalOauth2UserService를 생성
//어라? PrincipalOauth2UserService에서도 SecurityConfig에 등록한 BCryptPasswordEncoder를 참조하고 있네?
//참조가 순환이 되네?? -> 오류 발생

@Component
public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder { //순환관계 오류로 인해 생성
}
