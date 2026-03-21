package com.house.houseviewing.domain.subscriptions.service;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import com.house.houseviewing.domain.subscriptions.dto.request.SubscriptionPremiumRequest;
import com.house.houseviewing.domain.subscriptions.repository.SubscriptionRepository;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public SubscriptionEntity premium(SubscriptionPremiumRequest request){

        UserEntity user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        SubscriptionEntity subscription = user.getSubscription();
        if (subscription == null){
            throw new AppException(ExceptionCode.SUBSCRIPTION_NOT_FOUND);
        }
        subscription.updatePlanType(PlanType.PREMIUM);

        return subscription;
    }
}
