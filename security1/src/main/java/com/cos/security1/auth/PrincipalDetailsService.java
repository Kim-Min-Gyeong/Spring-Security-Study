package com.cos.security1.auth;

import com.cos.security1.domain.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//시큐리티 설정에서 SecurityConfig- loginProcessiongUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 타입으로 되어 있는 loadUserByUsername 함수가 실행

//Authentication 객체에 UserDetails 타입을 저장하기 위한 서비스
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired //필드 주입 방법(생성자 주입 방법이 가장 좋음)
    private UserRepository userRepository;

    //함수 종료 시 @AuthenticationPrincipal 어노테이션 생성됨.
    //시큐리티 세션 -> Authentication -> UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //html의 name의 username과 이 함수의 매개변수 명과 일치해야 한다. - 변경도 가능함(추가 설정 필요-SpringConfig->usernameParameter)
        //System.out.println("username: " + username);
        User user = userRepository.findByUsername(username); //해당 이름을 가진 유저가 있는지 찾음
        //findBy는 규칙 username은 문법
        //select * from user where username = ?

        if(user!=null)
            return new PrincipalDetails(user); //Authentication 객체에 저장

        return null;
    }
}
