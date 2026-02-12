package com.house.houseviewing.domain.house.repository;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<HouseEntity, Long> {
}
