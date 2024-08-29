package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.ProductDTO;
import com.datn.shop_app.entity.*;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.repository.*;
import com.datn.shop_app.response.product.ProductResponse;
import com.datn.shop_app.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final SupplierRepository supplierRepository;

    private final ProductOptionRepository productOptionRepository;

    private final OptionRepository optionRepository;
    private final CommodityRepository commodityRepository;

    @Override
    public ProductResponse save(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);

        Optional<Supplier> supplier = supplierRepository.findByIdAndActive(productDTO.getSupplierId(), true);
        if (supplier.isPresent()) {
            product.setSupplier(supplier.get());
        }

        Commodity commodity = commodityRepository.findByIdAndActive(productDTO.getCommodityId(), true);
        if (commodity != null) {
            product.setCommodity(commodity);
        }

        product.setActive(true);
        product = productRepository.save(product);

        List<Integer> optionIds = productDTO.getOptionIds().stream().distinct().toList();
        List<Option> options = optionRepository.findOptionByIds(optionIds, true);

        List<ProductOption> productOptions = new ArrayList<>();
        for (Option option : options) {
            ProductOption productOption = new ProductOption();
            productOption.setProduct(product);
            productOption.setOption(option);
            productOptions.add(productOption);
        }

        productOptionRepository.saveAll(productOptions);
        return ProductResponse.fromProduct(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Integer id, ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findByIdAndActive(id, true);
        if(productOptional.isPresent()) {
            Product product = productOptional.get();
            BeanUtils.copyProperties(productDTO, product);

            Optional<Supplier> supplier = supplierRepository.findByIdAndActive(productDTO.getSupplierId(), true);
            if (supplier.isPresent()) {
                product.setSupplier(supplier.get());
            }

            Commodity commodity = commodityRepository.findByIdAndActive(productDTO.getCommodityId(), true);
            if (commodity != null) {
                product.setCommodity(commodity);
            }

            product = productRepository.save(product);

            if(!product.getProductOptions().isEmpty()){
                productOptionRepository.deleteAll(product.getProductOptions());
            }


            List<Integer> optionIds = productDTO.getOptionIds().stream().distinct().toList();
            List<Option> options = optionRepository.findOptionByIds(optionIds, true);

            List<ProductOption> productOptions = new ArrayList<>();
            for (Option option : options) {
                ProductOption productOption = new ProductOption();
                productOption.setProduct(product);
                productOption.setOption(option);
                productOptions.add(productOption);
            }

            productOptionRepository.saveAll(productOptions);
            return ProductResponse.fromProduct(product);
        }
        return new ProductResponse();
    }

    @Override
    public List<String> validateInsert(ProductDTO productDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return errors;
        }

        Optional<Supplier> supplier = supplierRepository.findByIdAndActive(productDTO.getSupplierId(), true);
        if (supplier.isEmpty()) {
            errors.add("Supplier cannot be found");
        }

        Commodity commodity = commodityRepository.findByIdAndActive(productDTO.getCommodityId(), true);
        if (commodity == null) {
            errors.add("Commodity cannot be found");
        }

        List<Integer> optionIds = productDTO.getOptionIds();
        if (optionIds == null || optionIds.isEmpty()) {
            errors.add("Options cannot be empty");
        } else {
            optionIds = optionIds.stream().distinct().toList();
            List<Option> options = optionRepository.findOptionByIds(optionIds, true);
            if (options.isEmpty()) {
                errors.add("Options is not exist");
            } else {
                for (Option option : options) {
                    if (!optionIds.contains(option.getId())) {
                        errors.add("Some option does not exist");
                        break;
                    }
                }
            }
        }

        return errors;
    }

    @Override
    public List<String> validateUpdate(Integer productId, ProductDTO productDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return errors;
        }

        Optional<Product> product = productRepository.findByIdAndActive(productId, true);
        if (product.isEmpty()) {
            errors.add("Product cannot be found");
            return errors;
        }

        Optional<Supplier> supplier = supplierRepository.findByIdAndActive(productDTO.getSupplierId(), true);
        if (supplier.isEmpty()) {
            errors.add("Supplier cannot be found");
        }

        Commodity commodity = commodityRepository.findByIdAndActive(productDTO.getCommodityId(), true);
        if (commodity == null) {
            errors.add("Commodity cannot be found");
        }

        List<Integer> optionIds = productDTO.getOptionIds();
        if (optionIds == null || optionIds.isEmpty()) {
            errors.add("Options cannot be empty");
        } else {
            optionIds = optionIds.stream().distinct().toList();
            List<Option> options = optionRepository.findOptionByIds(optionIds, true);
            if (options.isEmpty()) {
                errors.add("Options is not exist");
            } else {
                for (Option option : options) {
                    if (!optionIds.contains(option.getId())) {
                        errors.add("Some option does not exist");
                        break;
                    }
                }
            }
        }

        return errors;
    }

    @Override
    public Product getProductById(Integer productId) throws DataNotFoundException {
        Optional<Product> product = productRepository.findByIdAndActive(productId, true);
        if(product.isPresent()) {
            return product.get();
        }
        throw new DataNotFoundException("Cannot find product with id =" + productId);
    }

    @Override
    public Page<ProductResponse> getProducts(String name, String supplierName, String commodityName, Boolean active, Pageable pageable){
        Page<Product> product = productRepository.findAllProducts(name, supplierName, commodityName, active, pageable);
        return product.map(ProductResponse::fromProduct);
    }

    @Override
    public List<ProductResponse> getProductByName(String name, Boolean active) {
        List<Product> products = productRepository.findProductsByName(name, active);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(ProductResponse.fromProduct(product));
        }
        return productResponses;
    }

    @Override
    @Transactional
    public void delete(Integer productId) {
        Optional<Product> product = productRepository.findByIdAndActive(productId, true);
        if (product.isPresent()) {
            product.get().setActive(false);
            productRepository.save(product.get());
        }
    }
}
