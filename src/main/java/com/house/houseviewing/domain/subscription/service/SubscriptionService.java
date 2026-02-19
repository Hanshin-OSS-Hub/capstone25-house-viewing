package com.house.houseviewing.domain.subscription.service;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.subscription.model.SubscriptionUpdateRQ;
import com.house.houseviewing.domain.subscription.repository.SubscriptionRepository;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public SubscriptionEntity register(SubscriptionUpdateRQ request){

        Long userId = request.getUserId();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .planType(PlanType.FREE)
                .startedAt(LocalDateTime.now())
                .build();
        SubscriptionEntity save = subscriptionRepository.save(subscription);

        return save;
    }

}
