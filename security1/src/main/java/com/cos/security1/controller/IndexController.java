package com.cos.security1.controller;

import com.cos.security1.domain.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.geom.RectangularShape;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping({"", "/"}) // localhost:8080와 localthost:8080/
    public String index(){
        //머스테치 - 템플릿 엔진(지정된 템플릿 양식과 데이터가 합쳐져 HTML 문서를 출력하는 소프트웨어)
        //기본 폴더는 src/main/resources
        //뷰리졸버 설정: templates (prefix), .mustache(suffix) - 생략 가능한 설정(application.properties)

        //return "index"; //src/main/resources/templates/index.mustache를 찾음(까다로워짐)
        return "index"; //WebMvcConfig에서 설정해줌
    }

    @GetMapping("/user")
    public @ResponseBody String user(){
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
