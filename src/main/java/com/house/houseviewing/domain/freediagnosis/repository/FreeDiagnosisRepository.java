package com.house.houseviewing.domain.freediagnosis.repository;

import com.house.houseviewing.domain.freediagnosis.entity.FreeDiagnosisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeDiagnosisRepository extends JpaRepository<FreeDiagnosisEntity, Long> {
}
