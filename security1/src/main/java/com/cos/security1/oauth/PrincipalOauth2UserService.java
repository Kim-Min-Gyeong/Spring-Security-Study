package com.cos.security1.oauth;

import com.cos.security1.auth.PrincipalDetails;
import com.cos.security1.domain.User;
import com.cos.security1.oauth.provider.FacebookUserInfo;
import com.cos.security1.oauth.provider.GoogleUserInfo;
import com.cos.security1.oauth.provider.NaverUserInfo;
import com.cos.security1.oauth.provider.OAuth2UserInfo;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

//Authentication 객체에 OAuth2User 타입을 저장하기 위한 서비스
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService { //후처리하는 곳

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    //함수 종료 시 @AuthenticationPrincipal 어노테이션 생성됨.
    @Override //OAuth부터 받은 userRequest 데이터에 대한 후처리되는 함수
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration: "+ userRequest.getClientRegistration()); // - registraionId로 어떤 OAuth로 로그인했는지 확인 가능
        System.out.println("getAccessToken: "+ userRequest.getAccessToken());

        //username = "google_sub"
        //password = "암호화(겟인데어)"
        //email = email
        //role = "ROLE_USER"
        //provider = "google"
        //providerId = sub

        OAuth2User oAuth2User = super.loadUser(userRequest);
        //구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
        //userRequest 정보 -> 회원 프로필을 받아야 함(loadUser 함수 호출) -> 구글로부터 회원 프로필을 받음
        System.out.println("getAttributes: "+ oAuth2User.getAttributes()); //sub - id

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("우리는 구글과 페이스북과 네이버만 지원해요");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId; //ex. google_918409328502 - 유저 이름 충돌 방지
        String password = bCryptPasswordEncoder.encode("겟인데어"); //oauth 로그인의 경우 비밀번호가 의미 없어서 이와 같이 아무 의미없는 말을 암호화
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username); //이미 존재하는 회원인지 검사
        if(userEntity == null){ //회원가입 진행
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity); //회원 정보 저장
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); //Authentication 객체에 저장
    }
}
