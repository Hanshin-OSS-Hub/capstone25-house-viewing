package com.house.houseviewing.domain.global.jpa.repository;

import com.house.houseviewing.domain.global.jpa.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndName(String email, String name);
    Optional<UserEntity> findByLoginIdAndPassword(String loginId, String password);
    Optional<UserEntity> findByEmailAndNameAndLoginId(String email, String name, String loginId);
}
