package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Product;
import com.fmisser.gtc.social.repository.ProductRepository;
import com.fmisser.gtc.social.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
