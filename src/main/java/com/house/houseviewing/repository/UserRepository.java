package com.house.houseviewing.repository;

import com.house.houseviewing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmailAndName(String email, String name);
}
