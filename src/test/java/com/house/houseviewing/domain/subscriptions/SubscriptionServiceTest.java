package com.house.houseviewing.domain.subscriptions;

import com.house.houseviewing.domain.subscriptions.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscriptions.enums.PlanType;
import com.house.houseviewing.domain.subscriptions.dto.request.SubscriptionPremiumRequest;
import com.house.houseviewing.domain.subscriptions.repository.SubscriptionRepository;
import com.house.houseviewing.domain.subscriptions.service.SubscriptionService;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.repository.UserRepository;
import com.house.houseviewing.fixture.SubscriptionFixture;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks SubscriptionService subscriptionService;

    @Mock SubscriptionRepository subscriptionRepository;
    @Mock UserRepository userRepository;

    @Nested
    @DisplayName("프리미엄 구독권")
    class Premium{

        @Test
        @DisplayName("성공")
        void 성공(){
            // given
            UserEntity user = UserFixture.createDefault().id(1L).build();
            SubscriptionEntity subscription = SubscriptionFixture.createDefault(user).id(1L).build();
            SubscriptionPremiumRequest request = SubscriptionFixture.createPremium(subscription).build();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            // when
            SubscriptionEntity premium = subscriptionService.premium(request);
            // then
            assertThat(premium.getPlanType()).isEqualTo(PlanType.PREMIUM);
        }

        @Test
        @DisplayName("실패: 유저를 찾을 수 없음")
        void 실패(){
            // given
            UserEntity user = UserFixture.createDefault().id(1L).build();
            SubscriptionEntity subscription = SubscriptionFixture.createDefault(user).id(1L).build();
            SubscriptionPremiumRequest request = SubscriptionFixture.createPremium(subscription).build();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());
            // when
            // then
            assertThatThrownBy(() -> subscriptionService.premium(request))
                    .isInstanceOf(AppException.class)
                    .extracting("exceptionCode")
                    .isEqualTo(ExceptionCode.USER_NOT_FOUND);
        }
    }

}