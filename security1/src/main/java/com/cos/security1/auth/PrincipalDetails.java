package com.cos.security1.auth;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인 진행
//로그인 진행이 완료가 되면 시큐리티 session을 만듦(Security ContextHolder-키값)
//시큐리티는 자신만의 session이 있음(일반적인 세션과 같은데, 키값으로 구분)
//시큐리티 세션에 들어갈 수 있는 객체의 형태가 정해져 있음. 바로, Authenticaiton 타입의 객체
//Authentication 안에는 User 정보가 있어야 한다.
//User 오브젝트의 타입은 UserDetails 타입의 객체여야 한다.

//정리!
//시큐리티가 가지고 있는 시큐리티 세션 영역이 존재
//여기에 Authentication 객체로 세션 정보가 저장되어야 함.
//Authentication 객체에 유저 정보를 저장하기 위해 사용되는 객체는 UserDetails 객체와 OAuth2User


import com.cos.security1.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User { //PricipalDetails가 UserDetails이자 OAuth2User가 됨


    private User user; //composition
    private Map<String, Object> attributes;

    //일반 로그인할때 사용하는 생성자
    public PrincipalDetails(User user){ //생성자
        this.user = user;
    }

    //oauth 로그인할때 사용하는 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){ //PrincipalOauth2UserService -> loadUser 메소드에서 사용
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    //해당 유저의 권한을 반환하는 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        }); //유저의 권한을 String형으로 반환

        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override //계정이 만료되지 않았는지 여부
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //계정이 잠기지 않았는지 여부
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //비밀번호를 오래 사용하진 않았는지 여부
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //계정이 활성화 되어 있는지 여부
    public boolean isEnabled() {
        //만약 우리 웹 사이트에 1년동안 회원이 로그인을 안하면 휴면 계정으로 전환하기로 함
        //현재 시간 - 로그인 시간 => 1년을 초과하면 return false;
        //로그인 시간을 저장하는 칼럼이 필요해짐

        return true;
    }


    @Override
    public String getName() {
        return null;
    }
}
