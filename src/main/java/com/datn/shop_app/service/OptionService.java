package com.datn.shop_app.service;

import com.datn.shop_app.DTO.OptionDTO;
import com.datn.shop_app.entity.Option;
import com.datn.shop_app.response.option.OptionResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface OptionService {
    @Transactional
    Option save(OptionDTO optionDTO);

    List<String> validateOption(BindingResult result, OptionDTO optionDTO);

    @Transactional
    Option update(Integer id, OptionDTO optionDTO);

    Page<OptionResponse> getOptions(String name, Boolean active, Pageable pageable);

    List<OptionResponse> getOptions(String name, Boolean active);

    Option getOption(Integer id);

    void deleteOption(Integer id);
}
