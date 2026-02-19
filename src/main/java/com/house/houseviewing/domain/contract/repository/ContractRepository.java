package com.house.houseviewing.domain.contract.repository;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<ContractEntity, Long> {
}
