package com.house.houseviewing.domain.subscriptions.repository;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
}
