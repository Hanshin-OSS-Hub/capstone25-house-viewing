package com.example.houseviewing.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Board {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String title;

    @Column(name = "board_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
