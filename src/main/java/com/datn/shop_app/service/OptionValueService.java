package com.datn.shop_app.service;

import com.datn.shop_app.DTO.OptionValueDTO;
import com.datn.shop_app.entity.OptionValue;
import com.datn.shop_app.response.option_value.OptionValueResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface OptionValueService {
    List<String> validateOptionValue(Integer optionValueId, BindingResult result, OptionValueDTO optionValueDTO);

    Page<OptionValueResponse> getOptionValues(Integer optionId, String name, Boolean active, Pageable pageable);

    OptionValueResponse update(Integer optionValueId, OptionValueDTO optionValueDTO);

    OptionValue getOptionValue(Integer id);

    void deleteOptionValue(Integer id);
}
