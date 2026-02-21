package com.house.houseviewing.domain.subscription.controller;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.model.SubscriptionPremiumRQ;
import com.house.houseviewing.domain.subscription.model.SubscriptionPremiumRS;
import com.house.houseviewing.domain.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/premium")
    public ResponseEntity<SubscriptionPremiumRS> premium(@Valid @RequestBody SubscriptionPremiumRQ request){
        SubscriptionEntity subscription = subscriptionService.premium(request);
        SubscriptionPremiumRS result = SubscriptionPremiumRS.builder()
                .userId(subscription.getUser().getId())
                .subscriptionId(subscription.getId())
                .planType(subscription.getPlanType())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
