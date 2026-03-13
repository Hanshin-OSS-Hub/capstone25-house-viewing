package com.house.houseviewing.domain.house.repository;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRepository extends JpaRepository<HouseEntity, Long> {

    List<HouseEntity> findByUserEntityId(Long userId);
}
