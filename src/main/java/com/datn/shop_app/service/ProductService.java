package com.datn.shop_app.service;

import com.datn.shop_app.DTO.ProductDTO;
import com.datn.shop_app.entity.Product;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.response.product.ProductResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface ProductService {
    ProductResponse save(ProductDTO productDTO);

    ProductResponse update(Integer id, ProductDTO productDTO);

    List<String> validateInsert(ProductDTO productDTO, BindingResult bindingResult);

    List<String> validateUpdate(Integer productId, ProductDTO productDTO, BindingResult bindingResult);

    Product getProductById(Integer productId) throws DataNotFoundException;

    Page<ProductResponse> getProducts(String name, String supplierName, String commodityName, Boolean active, Pageable pageable);

    List<ProductResponse> getProductByName(String name, Boolean active);

    @Transactional
    void delete(Integer productId);
}
