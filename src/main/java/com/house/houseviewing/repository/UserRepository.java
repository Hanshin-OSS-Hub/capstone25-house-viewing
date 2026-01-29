package com.house.houseviewing.repository;

import com.house.houseviewing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndName(String email, String name);
    boolean existsAllByLoginId(String loginId);
    Optional<User> findByLoginIdAndPassword(String loginId, String password);
}
