package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.repository.ProductRepository;
import com.fmisser.gtc.social.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getIapProductList() throws ApiException {
        return productRepository.getIapProductList();
    }

    @Override
    public Product getValidProduct(Long productId) throws ApiException {
        Optional<Product> optionalProduct = productRepository.getValidProduct(productId, new Date());
        if (!optionalProduct.isPresent()) {
            throw new ApiException(-1, "无效商品或商品不在有效期内");
        }
        return optionalProduct.get();
    }

    @Override
    public Product getProductByName(String name) throws ApiException {
        return productRepository.findByName(name);
    }
}
