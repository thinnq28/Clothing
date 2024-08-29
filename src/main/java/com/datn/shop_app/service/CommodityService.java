package com.datn.shop_app.service;

import com.datn.shop_app.DTO.CommodityDTO;
import com.datn.shop_app.entity.Commodity;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.response.commodity.CommodityResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface CommodityService {
    @Transactional
    Commodity save(CommodityDTO commodityDTO);

    Page<CommodityResponse> getAllCommodities(String name, Boolean active, Pageable pageable);


    List<CommodityResponse> getAllCommodities(String name, Boolean active);

    Commodity getCommodity(Integer id);

    @Transactional
    Commodity update(Integer id, CommodityDTO commodityDTO);

    void delete(Integer id);
}
