package com.house.houseviewing.repository;

import com.house.houseviewing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
