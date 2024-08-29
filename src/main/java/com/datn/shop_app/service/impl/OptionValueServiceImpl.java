package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.OptionValueDTO;
import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.OptionValue;
import com.datn.shop_app.repository.OptionRepository;
import com.datn.shop_app.repository.OptionValueRepository;
import com.datn.shop_app.response.option_value.OptionValueResponse;
import com.datn.shop_app.service.OptionValueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionValueServiceImpl implements OptionValueService {

    private final OptionValueRepository optionValueRepository;

    private final OptionRepository optionRepository;

    @Override
    public List<String> validateOptionValue(Integer optionValueId, BindingResult result, OptionValueDTO optionValueDTO) {
        List<String> errors = new ArrayList<>();
        if(result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for(FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }

            return errors;
        }

        Optional<OptionValue> optionValue = optionValueRepository.findByIdAndActive(optionValueId, true);
        if(optionValue.isEmpty()) {
            errors.add("Option value is not exist");
        }

        Optional<Option> option = optionRepository.findByIdAndActive(optionValueDTO.getOptionId(), true);
        if(option.isEmpty()){
            errors.add("Option is not exist");
        }

        return errors;
    }

    @Override
    public Page<OptionValueResponse> getOptionValues(Integer optionId, String name, Boolean active, Pageable pageable) {
        Page<OptionValue> optionValuePage = optionValueRepository.findAllOptionValues(optionId, name, active, pageable);
        return optionValuePage.map(OptionValueResponse::fromOptionValue);
    }

    @Override
    @Transactional
    public OptionValueResponse update(Integer optionValueId, OptionValueDTO optionValueDTO) {
        Optional<OptionValue> optionValue = optionValueRepository.findByIdAndActive(optionValueId, true);
        Optional<Option> option = optionRepository.findByIdAndActive(optionValueDTO.getOptionValueId(), true);

        if(optionValue.isPresent()) {
            optionValue.get().setOptionValue(optionValueDTO.getOptionValueName());
            if(option.isPresent() && !optionValueDTO.getOptionValueId().equals(option.get().getId())) {
                optionValue.get().setOption(option.get());
            }

            OptionValue updatedOptionValue =  optionValueRepository.save(optionValue.get());
            return OptionValueResponse.fromOptionValue(updatedOptionValue);
        }

        return null;
    }

    @Override
    public OptionValue getOptionValue(Integer id) {
        OptionValue option = optionValueRepository.findByIdAndActive(id, true).orElse(null);
        return option;
    }


    @Override
    @Transactional
    public void deleteOptionValue(Integer id) {
        Optional<OptionValue> option = optionValueRepository.findByIdAndActive(id, true);
        if (option.isPresent()) {
            option.get().setActive(false);
            optionValueRepository.save(option.get());
        }
    }
}
