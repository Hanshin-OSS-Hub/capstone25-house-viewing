package com.house.houseviewing.domain.subscriptions;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import com.house.houseviewing.domain.subscriptions.dto.request.SubscriptionPremiumRequest;
import com.house.houseviewing.domain.subscriptions.repository.SubscriptionRepository;
import com.house.houseviewing.domain.subscriptions.service.SubscriptionService;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.dto.request.UserRegisterRequest;
import com.house.houseviewing.domain.users.service.UserService;
import com.house.houseviewing.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SubscriptionIntegrationTest {

    @Autowired SubscriptionService subscriptionService;
    @Autowired SubscriptionRepository subscriptionRepository;
    @Autowired UserService userService;

    @Test
    @DisplayName("무료 구독권")
    void 무료_구둑권(){
        // given
        UserEntity user = getUserEntity();
        SubscriptionEntity subscription = user.getSubscription();
        // when
        // then
        assertThat(subscription.getPlanType()).isEqualTo(PlanType.FREE);
        assertThat(subscription.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("프리미엄 구독권")
    void 프리미엄_구독권(){
        // given
        UserEntity user = getUserEntity();
        SubscriptionEntity subscription = user.getSubscription();
        SubscriptionPremiumRequest request = SubscriptionPremiumRequest.builder()
                .userId(user.getId())
                .build();
        // when
        SubscriptionEntity premium = subscriptionService.premium(request);
        // then
        assertThat(premium.getPlanType()).isEqualTo(PlanType.PREMIUM);
        assertThat(premium.getUser()).isEqualTo(user);
    }

    private UserEntity getUserEntity() {
        UserEntity build = UserFixture.createDefault().build();
        UserRegisterRequest build1 = UserFixture.createRegister(build).build();
        UserEntity register = userService.register(build1);
        return register;
    }
}
