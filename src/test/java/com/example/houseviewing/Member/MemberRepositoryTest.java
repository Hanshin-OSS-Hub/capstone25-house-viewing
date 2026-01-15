package com.example.houseviewing.Member;

import com.example.houseviewing.domain.Member;
import com.example.houseviewing.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void memberSaveTest(){
        Member member = new Member();
        member.setName("yoo");

        Member saved = memberRepository.save(member);
        Member found = memberRepository.findById(saved.getId()).get();
        Assertions.assertThat(found.getName()).isEqualTo("yoo");
    }

}
