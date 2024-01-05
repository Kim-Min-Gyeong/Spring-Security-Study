package com.cos.security1.repository;

import com.cos.security1.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JpaRepository가 들고 있음.
// @Repository라는 어노테이션이 없어도 IoC 가능. JpaRepository를 상속해서(자동 BEAN 등록)
public interface UserRepository extends JpaRepository<User, Integer> { //jpa query methods 참고

    //select * from user where username = ?
    public User findByUsername(String username);
}
