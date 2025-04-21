package com.alphamaleclub.ucmc.tradeBoard.repository;

import com.alphamaleclub.ucmc.tradeBoard.domain.TradePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {

    @Override
    Optional<TradePost> findById(Long aLong);


    List<TradePost> findTop10ByOrderByUpdatedAtDesc();


}
