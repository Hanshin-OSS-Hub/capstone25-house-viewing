package com.example.houseviewing;

import com.example.houseviewing.domain.Member;
import com.example.houseviewing.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HouseViewingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseViewingApplication.class, args);
	}

    @Bean
    CommandLineRunner test(MemberRepository repo) {
        return args -> {
            Member m = new Member();
            m.setName("테스트회원");
            repo.save(m);
        };
    }

}


