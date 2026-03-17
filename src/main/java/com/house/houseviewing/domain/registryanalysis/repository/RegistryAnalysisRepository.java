package com.house.houseviewing.domain.registryanalysis.repository;

import com.house.houseviewing.domain.registryanalysis.entity.RegistryAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistryAnalysisRepository extends JpaRepository<RegistryAnalysisEntity, Long> {
}
