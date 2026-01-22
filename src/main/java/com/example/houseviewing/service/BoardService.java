package com.example.houseviewing.service;

import com.example.houseviewing.domain.Board;
import com.example.houseviewing.repository.BoardRepository;
import com.example.houseviewing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public void saveBoard(Board board){
        boardRepository.save(board);
    }


}
