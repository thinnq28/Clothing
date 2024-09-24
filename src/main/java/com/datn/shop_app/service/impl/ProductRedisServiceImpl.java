package com.datn.shop_app.service.impl;

import com.datn.shop_app.response.product.ProductResponse;
import com.datn.shop_app.service.ProductRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductRedisServiceImpl implements ProductRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    private String getKeyFrom(String name, String supplierName,
                              String commodityName, Boolean active,
                              PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();

        String sortDirection = Objects.requireNonNull(sort.getOrderFor("id"))
                .getDirection() == Sort.Direction.ASC ? "asc" : "desc";

        return String.format("products:%d:%d:%s:%s:%s:%s:%s",
                pageNumber, pageSize, sortDirection, name, supplierName, commodityName, active);
    }

    @Override
    public List<ProductResponse> getProducts(String name, String supplierName,
                                             String commodityName, Boolean active,
                                             PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(name, supplierName, commodityName, active, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<ProductResponse> products =
                json != null ?
                        redisObjectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {})
                        : null;
        return products;
    }

    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    //save data to redis
    public void saveProducts(List<ProductResponse> productResponses,
                             String name, String supplierName,
                             String commodityName, Boolean active,
                             PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(name, supplierName, commodityName, active, pageRequest);
        String json = redisObjectMapper.writeValueAsString(productResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
