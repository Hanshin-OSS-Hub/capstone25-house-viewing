package com.example.houseviewing.service;ㅇ

import com.example.houseviewing.domain.Board;
import com.example.houseviewing.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired private BoardService boardService;
    @Autowired private UserService userService;

    @Test
    @DisplayName("게시판 등록")
    void saveBoard(){
        User user = getUser();
        Board board = new Board("인사", "안녕하세요", user);
        boardService.saveBoard(board);
        System.out.println("board.getId() = " + board.getId());
        System.out.println("board.getTitle() = " + board.getTitle());
        System.out.println("board.getContent() = " + board.getContent());
        System.out.println("board.getUser() = " + board.getUser());
        Assertions.assertNotNull(board);

    }

    private User getUser() {
        User user = new User("yooyoo9191@gmail.com", "yoo", "1234", "유인근");
        userService.saveUser(user);
        return user;
    }
}