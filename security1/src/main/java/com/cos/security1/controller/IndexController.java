package com.cos.security1.controller;

import com.cos.security1.auth.PrincipalDetails;
import com.cos.security1.domain.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //!스프링 시큐리티!
    //원래 세션 내부에 시큐리티가 관리하는  세션 영역이 존재
    //시큐리티가 관리하는 세션에 들어갈 수 있는 타입은 Authentication 객체밖에 없다!
    //필요하면 controller에서 di하여 접근
    //Authentication 객체에 UserDetails 타입과 OAuth2User 타입이 들어갈 수 있다!
    //UserDetails는 일반 로그인, OAuth2User는 OAuth 로그인을 할 경우에 사용
    //로그인 방식에 따라 가져오는 방식이 달라서 불편 -> UserDetails와 OAuth2User를 implements 하는 객체를 생성하여 사용(이 객체는 부모가 UserDetails이면서 OAuth2User-다중상속)

    //PrincipalDetails는 user(일반), userRequest(oauth)를 UserDetails,  OAuth2User 타입으로 넣어주는 매개체 역할

    @GetMapping("/test/login") //일반 로그인한 유저의 정보를 가져오는 법 - PrincipalDetails가 UserDetails와 같음
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){ //DI(의존성 주입)
        System.out.println("/test/login =====================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); //userDetails 타입이 들어옴
        System.out.println("authentication: "+principalDetails.getUser()); //1번 방법 - 구글 로그인으로 하면 오류남.

        System.out.println("userDetails: " + userDetails.getUser()); //2번 방법


        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login") //구글 로그인한 유저의 정보 가져오는 법 1. Authentication 객체로 접근 2. @AuthenticationPrincipal 사용
    public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){ //DI(의존성 주입)
        System.out.println("/test/login =====================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); //OAuth2User 타입이 들어옴
        System.out.println("authentication: "+oAuth2User.getAttributes()); //Authentication을 이용

        System.out.println("oauth2User: "+oauth.getAttributes()); //@Authentication을 이용

        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"", "/"}) // localhost:8080와 localthost:8080/
    public String index(){
        //머스테치 - 템플릿 엔진(지정된 템플릿 양식과 데이터가 합쳐져 HTML 문서를 출력하는 소프트웨어)
        //기본 폴더는 src/main/resources
        //뷰리졸버 설정: templates (prefix), .mustache(suffix) - 생략 가능한 설정(application.properties)

        //return "index"; //src/main/resources/templates/index.mustache를 찾음(까다로워짐)
        return "index"; //WebMvcConfig에서 설정해줌
    }

    //OAuth 로그인을 해도 PrincipalDetails
    //일반 로그인을 해도 PrincipalDetails
    //위처럼 분배할 필요가 x
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails: " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    @GetMapping("/loginForm") // - 스프링 시큐리티가 해당 주소를 낚아채버림. - SecurityConfig 파일 생성 후 작동 안함.
    public String loginForm(){
        return "loginForm";
     }

     @Secured("ROLE_ADMIN") //관리자만 info에 접근 가능
     @GetMapping("/info")
     public @ResponseBody String info(){
        return "개인정보";
     }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //하나만 걸고 싶으면 @Secured 사용, data 함수 실행 전에 거는 것
    //@PostAuthorize도 있음. 메소드가 종료되고 나서 거는 것
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }

     @GetMapping("/joinForm")
     public String joinForm(){
        return "joinForm";
     }

    @PostMapping("/join")
    public String join(User user){
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword(); //이용자가 입력한 비밀번호
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); //인코딩된 비밀번호
        user.setPassword(encPassword); //인코딩된 비밀번호로 바꿈
        userRepository.save(user); //회원가입. but, 비밀번호: 1234와 같은 형식이라 시큐리티로 로그인X. 패스워드가 암호화 되어 있지 않아서
        return "redirect:/loginForm";
    }

}
