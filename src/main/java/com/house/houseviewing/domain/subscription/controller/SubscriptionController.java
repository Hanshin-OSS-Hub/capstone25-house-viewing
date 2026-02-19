package com.house.houseviewing.domain.subscription.controller;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.subscription.model.SubscriptionUpdateRQ;
import com.house.houseviewing.domain.subscription.model.SubscriptionUpdateRS;
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

    @PostMapping("/register")
    public ResponseEntity<SubscriptionUpdateRS> join(@Valid @RequestBody SubscriptionUpdateRQ request){
        SubscriptionEntity register = subscriptionService.register(request);

        SubscriptionUpdateRS result = SubscriptionUpdateRS.builder()
                .userId(register.getUser().getId())
                .subscriptionId(register.getId())
                .planType(register.getPlanType())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
