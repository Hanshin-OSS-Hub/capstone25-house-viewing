package com.house.houseviewing.domain.house;

import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.house.service.HouseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {

    @InjectMocks HouseRepository houseRepository;
    @Mock HouseService houseService;



}