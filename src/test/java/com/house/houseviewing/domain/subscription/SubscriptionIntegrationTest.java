package com.house.houseviewing.domain.subscription;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.subscription.model.SubscriptionPremiumRQ;
import com.house.houseviewing.domain.subscription.repository.SubscriptionRepository;
import com.house.houseviewing.domain.subscription.service.SubscriptionService;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.fixture.UserFixture;
import org.assertj.core.api.Assertions;
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
        SubscriptionPremiumRQ request = SubscriptionPremiumRQ.builder()
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
        UserRegisterRQ build1 = UserFixture.createRegister(build).build();
        UserEntity register = userService.register(build1);
        return register;
    }
}
