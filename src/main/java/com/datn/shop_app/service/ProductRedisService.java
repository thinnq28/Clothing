package com.datn.shop_app.service;

import com.datn.shop_app.response.product.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductRedisService {
    List<ProductResponse> getProducts(String name, String supplierName,
                                      String commodityName, Boolean active,
                                      PageRequest pageRequest) throws JsonProcessingException;

    void clear();

    void saveProducts(List<ProductResponse> productResponses,
                      String name, String supplierName,
                      String commodityName, Boolean active,
                      PageRequest pageRequest) throws JsonProcessingException;
}
