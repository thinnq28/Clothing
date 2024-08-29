package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.OptionDTO;
import com.datn.shop_app.entity.Option;
import com.datn.shop_app.entity.OptionValue;
import com.datn.shop_app.repository.OptionRepository;
import com.datn.shop_app.repository.OptionValueRepository;
import com.datn.shop_app.response.option.OptionResponse;
import com.datn.shop_app.service.OptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;

    private final OptionValueRepository optionValueRepository;

    @Override
    @Transactional
    public Option save(OptionDTO optionDTO) {
        Option option = new Option();
        if (optionDTO.getIsCreateNew()) {
            option.setOptionName(optionDTO.getOptionName());
            option.setIsMultipleUsage(optionDTO.getIsMultipleUsage());
            option.setActive(true);
            option = optionRepository.save(option);

        } else {
            option = optionRepository.findByIdAndActive(optionDTO.getOptionId(), true).orElse(null);
        }

        if (!optionDTO.getOptionValues().isEmpty()) {
            List<OptionValue> optionValues = new ArrayList<>();
            for (String optionValueName : optionDTO.getOptionValues()) {
                OptionValue optionValue = new OptionValue();
                optionValue.setOption(option);
                optionValue.setActive(true);
                optionValue.setOptionValue(optionValueName);
                optionValues.add(optionValue);
            }

            optionValueRepository.saveAll(optionValues);
        }

        return option;
    }

    @Override
    public List<String> validateOption(BindingResult result, OptionDTO optionDTO) {
        List<String> errors = new ArrayList<>();

        Boolean isCreateNew = optionDTO.getIsCreateNew();
        String optionName = optionDTO.getOptionName();
        Integer optionId = optionDTO.getOptionId();

        if (isCreateNew && (optionName == null || optionName.isEmpty())) {
            errors.add("Option name cannot be empty");
        } else if (optionName.length() > 255) {
            errors.add("Option name has max length 255");
        }

        if (!isCreateNew && optionId == null) {
            errors.add("Option cannot be null");
        }

        Optional<Option> option = optionRepository.findByIdAndActive(optionId, true);
        if (!isCreateNew && option.isEmpty()) {
            errors.add("Option does not exist");
        }

        if(optionDTO.getOptionValues() == null || optionDTO.getOptionValues().isEmpty()) {
            errors.add("Option values cannot be empty");
        }else{
            for (String optionValue : optionDTO.getOptionValues()) {
                if(optionValue == null || optionValue.isEmpty()) {
                    errors.add("Option value name cannot be empty");
                    break;
                }
            }
        }

        return errors;
    }

    @Override
    @Transactional
    public Option update(Integer id, OptionDTO optionDTO) {
        Optional<Option> option = optionRepository.findByIdAndActive(id, true);
        if (option.isPresent()) {
            option.get().setOptionName(optionDTO.getOptionName());
            option.get().setIsMultipleUsage(optionDTO.getIsMultipleUsage());
            return optionRepository.save(option.get());
        }
        return new Option();
    }

    @Override
    public Page<OptionResponse> getOptions(String name, Boolean active, Pageable pageable) {
        Page<Option> optionPage = optionRepository.findAllOptions(name, active, pageable);
        return optionPage.map(OptionResponse::fromOption);
    }

    @Override
    public List<OptionResponse> getOptions(String name, Boolean active) {
        List<Option> optionPage = optionRepository.findAllOptions(name, active);
        List<OptionResponse> optionResponses = new ArrayList<>();
        for (Option option : optionPage) {
            optionResponses.add(OptionResponse.fromOption(option));
        }
        return optionResponses;
    }


    @Override
    public Option getOption(Integer id) {
        Option option = optionRepository.findByIdAndActive(id, true).orElse(null);
        return option;
    }

    @Override
    public void deleteOption(Integer id) {
        Optional<Option> option = optionRepository.findByIdAndActive(id, true);
        if (option.isPresent()) {
            option.get().setActive(false);
            optionRepository.save(option.get());
        }
    }
}
