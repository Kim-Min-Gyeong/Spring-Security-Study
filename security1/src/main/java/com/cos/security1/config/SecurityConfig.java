package com.cos.security1.config;

import com.cos.security1.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록됨.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // @Secured 활성화, @preAuthorize와 @postAuthorize 활성화
public class SecurityConfig{

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    /*@Bean //해당 메소드의 리턴되는 오브젝트를 IoC로 등록해줌.
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder(); //비밀번호 암호화
    }*/

    // https://velog.io/@woosim34/Spring-Security-6.1.0%EC%97%90%EC%84%9C-is-deprecated-and-marked-for-removal-%EC%98%A4%EB%A5%98
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/user/**").authenticated() //인증만 되면 들어갈 수 있도록
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()
        );

        http.formLogin(formLogin ->
                formLogin
                        .loginPage("/loginForm") //로그인 페이지 설정
                        .loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행(따라서 컨트롤러에 /login을 안만들어도됨)
                        .defaultSuccessUrl("/")); //로그인 성공하면 메인 페이지로, 만약 특정 페이지를 요청하면 해당 페이지로

        // 1. 코드를 받음(인증됨) 2. access token을 받음(사용자 정보에 접근할 권한 얻음)
        // 3. 사용자 프로필 정보 가져오기 4. 그 정보를 토대로 회원가입을 자동으로 진행 or 사용자 정보
        http.oauth2Login(oauth2Login ->
                oauth2Login
                        .loginPage("/loginForm") //구글 로그인이 완료된 뒤의 후처리 필요. Tip! 코드x(액세스 토큰 + 사용자 프로필 정보 O)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService)));


        return http.build();
    }


}
