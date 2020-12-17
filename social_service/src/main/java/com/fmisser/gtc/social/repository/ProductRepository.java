package com.fmisser.gtc.social.repository;

import com.fmisser.gtc.social.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);

    // 通过名字查找是否存在有效的产品
    @Query(value = "SELECT * FROM t_product WHERE disable = 0 AND name = ?1 AND " +
            "(NOW() >= valid_begin OR valid_begin IS NULL) AND " +
            "(NOW() <= valid_end OR valid_end IS NULL)",
            nativeQuery = true)
    Optional<Product> getValidProduct(String name);

    // 获得苹果商品列表
    @Query(value = "SELECT * FROM t_product WHERE plt = 0 AND disable = 0 ORDER BY level",
            nativeQuery = true)
    List<Product> getIapProductList();
}
