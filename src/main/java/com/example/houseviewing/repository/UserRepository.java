package com.example.houseviewing.repository;

import com.example.houseviewing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
