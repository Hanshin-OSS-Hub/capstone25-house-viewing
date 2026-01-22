package com.example.houseviewing.repository;

import com.example.houseviewing.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
