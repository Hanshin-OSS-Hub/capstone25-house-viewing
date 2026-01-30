package com.house.houseviewing.domain.global.jpa.repository;

import com.house.houseviewing.domain.global.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndName(String email, String name);
    boolean existsAllByLoginId(String loginId);
    Optional<UserEntity> findByLoginIdAndPassword(String loginId, String password);
}
