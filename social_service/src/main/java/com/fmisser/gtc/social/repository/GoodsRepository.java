package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    @Query(value = "SELECT * FROM t_goods WHERE name = 'vip'", nativeQuery = true)
    Goods getVipGoods();
}
