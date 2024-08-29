package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.*;
import com.datn.shop_app.entity.*;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.repository.*;
import com.datn.shop_app.response.variant.VariantResponse;
import com.datn.shop_app.service.VariantService;
import com.datn.shop_app.utils.FileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {
    private final VariantRepository variantRepository;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;
    private final VariantValueRepository variantValueRepository;
    private final DelegatingApplicationListener delegatingApplicationListener;

    @Override
    public List<VariantResponse> getVariants(Integer productId) {
        List<Variant> variants = variantRepository.findAllByProductId(productId);
        List<VariantResponse> variantResponses = new ArrayList<>();
        for (Variant variant : variants) {
            VariantResponse variantResponse = VariantResponse.fromVariant(variant);
            variantResponses.add(variantResponse);
        }

        return variantResponses;
    }

    @Override
    public Page<VariantResponse> getVariants(String name, Boolean active, Pageable pageable) {
        Page<Variant> variants = variantRepository.findAllVariants(name, active, pageable);
        return variants.map(VariantResponse::fromVariant);
    }

    @Override
    public Page<VariantResponse> getVariants(String name, String productName, Boolean active, Pageable pageable) {
        Page<Variant> variants = variantRepository.findAllVariants(name, productName, true, pageable);
        return variants.map(VariantResponse::fromVariant);
    }

    @Override
    public Page<VariantResponse> getVariants(String name, String productName, Integer promotionId, Boolean active, Pageable pageable) {
        if (active) {
            Page<Variant> variants = variantRepository.findAllVariants(name, productName, promotionId, true, pageable);
            return variants.map(VariantResponse::fromVariant);
        }
        Page<Variant> variants = variantRepository.findAllVariants(name, productName, true, pageable);
        return variants.map(VariantResponse::fromVariant);
    }

    @Override
    public Variant getVariantById(Integer id) throws DataNotFoundException {
        Optional<Variant> variant = variantRepository.findByIdAndActive(id, true);
        return variant.orElse(null);
    }

    @Override
    public List<VariantResponse> getVariantByIds(List<Integer> variantIds) {
        List<Variant> variants = variantRepository.getVariantsByIds(variantIds);
        List<VariantResponse> variantResponses = new ArrayList<>();
        for (Variant variant : variants) {
            VariantResponse variantResponse = VariantResponse.fromVariant(variant);
            variantResponses.add(variantResponse);
        }

        return variantResponses;
    }

    @Override
    public void saveAllImage(List<Image> images) {
        imageRepository.saveAll(images);
    }

    @Override
    public List<Variant> save(InsertVariantDTO variantDTO) throws DataNotFoundException {

        Optional<Product> product = productRepository.findByIdAndActive(variantDTO.getProductId(), true);
        List<Variant> variants = new ArrayList<>();
        if (product.isPresent()) {

            List<Integer> optionIds = variantDTO.getOptions().stream().map(OptionVariantDTO::getOptionId).toList();
            List<Option> options = optionRepository.findOptionByIds(optionIds, true);

            List<Integer> optionValueIds = new ArrayList<>();
            variantDTO.getOptions().forEach(e -> {
                optionValueIds.addAll(e.getOptionValueIds());
            });

            List<OptionValue> optionValues = optionValueRepository.findAllOptionValues(optionValueIds);
            List<Map<List<Integer>, List<String>>> attributes = new ArrayList<>();

            for (Option option : options) {
                List<Integer> ids = option.getOptionValues().stream()
                        .filter(optionValues::contains)
                        .map(OptionValue::getId)
                        .toList();
                List<String> values = option.getOptionValues().stream()
                        .filter(optionValues::contains)
                        .map(OptionValue::getOptionValue)
                        .toList();
                Map<List<Integer>, List<String>> attribute = new HashMap<>();
                attribute.put(ids, values);
                attributes.add(attribute);
            }

            List<String> properties = new ArrayList<>();
            // Kết hợp các thuộc tính
            this.generateCombinations(attributes, 0, new ArrayList<>(), new ArrayList<>(), properties);

            for (String property : properties) {
                Variant variant = new Variant();
                BeanUtils.copyProperties(variantDTO, variant);
                variant.setProduct(product.get());
                variant.setQuantity(0);
                variant.setActive(true);

                // Tìm vị trí của chuỗi "(IDs: "
                int index = property.indexOf("(IDs:");

                // Tách phần "black-s-cotton"
                String attributePart = property.substring(0, index).trim();

                String variantName = this.generateVariantName(product.get().getProductName(), attributePart, product.get().getId());
                variant.setVariantName(variantName);

                String SkuId = this.generateSKU(product.get().getProductName(), attributePart, product.get().getId());
                variant.setSkuId(SkuId);

                variant = variantRepository.save(variant);
                variants.add(variant);
                List<VariantValue> variantValues = new ArrayList<>();

                // Tách phần "(IDs: [10, 12, 13])"
                String idsPart = property.substring(index).trim();
                // Tách phần "10, 12, 13" từ chuỗi "(IDs: [10, 12, 13])"
                String idsOnly = idsPart.substring(idsPart.indexOf("[") + 1, idsPart.indexOf("]"));
                // Tách các số ID thành mảng
                String[] idsArray = idsOnly.split(", ");
                List<Integer> ids = Arrays.stream(idsArray).map(Integer::parseInt).toList();
                List<OptionValue> optionValueFilter = optionValues.stream().filter(e -> ids.contains(e.getId())).toList();
                for (OptionValue optionValue : optionValueFilter) {
                    VariantValue variantValue = new VariantValue();
                    variantValue.setOptionValue(optionValue);
                    variantValue.setVariant(variant);
                    variantValue.setOption(optionValue.getOption());
                    variantValues.add(variantValue);
                }

                variantValueRepository.saveAll(variantValues);
            }

            return variants;
        }

        throw new DataNotFoundException("Product is not found");
    }

    @Override
    public List<String> validateInsertion(InsertVariantDTO variantDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return errors;
        }

        Optional<Product> product = productRepository.findByIdAndActive(variantDTO.getProductId(), true);
        if (product.isEmpty()) {
            errors.add("Product is not found");
        }

        List<OptionVariantDTO> optionParams = variantDTO.getOptions();
        if (optionParams == null || optionParams.isEmpty()) {
            errors.add("Options is not empty");
        } else {
            List<Integer> optionIds = optionParams.stream().map(OptionVariantDTO::getOptionId).toList();
            List<Option> options = optionRepository.findOptionByIds(optionIds, true);

            if (options == null && options.isEmpty()) {
                errors.add("Options is not exist");
            } else {
                List<Integer> optionIdExists = options.stream().map(Option::getId).toList();

                for (OptionVariantDTO optionParam : optionParams) {
                    if (!optionIdExists.contains(optionParam.getOptionId())) continue;

                    Option option = options.stream().filter(e -> Objects.equals(e.getId(), optionParam.getOptionId())).findFirst().get();

                    List<Integer> optionValueIds = option.getOptionValues().stream()
                            .map(OptionValue::getId)
                            .filter(e -> optionParam.getOptionValueIds().contains(e))
                            .distinct()
                            .toList();

                    List<OptionValue> optionValues = optionValueRepository.findAllOptionValues(optionValueIds);
                    if (optionValues == null)
                        errors.add(String.format("Options values of %s is not exist", option.getOptionName()));

                    OptionVariantDTO optionVariantDTO = optionParams.stream().filter(e -> Objects.equals(e.getOptionId(), option.getId())).findFirst().get();

                    int optionValueIdParamSize = optionVariantDTO.getOptionValueIds().size();
                    int optionValueExistSize = option.getOptionValues().size();
                    if (optionValueExistSize < optionValueIdParamSize) {
                        String error = String.format("There are a total of %d/%d options that do not exist.", optionValueIdParamSize - optionValueExistSize, optionValueIdParamSize);
                        errors.add(error);
                    }

                }
            }

            if (options.size() < optionParams.size()) {
                String error = String.format("There are a total of %d/%d options that do not exist.", optionParams.size() - options.size(), optionParams.size());
                errors.add(error);
            }
        }

        return errors;
    }

    @Override
    public List<String> validateUpdateQuantity(List<QuantityVariantDTO> variantDTOs) {
        List<String> errors = new ArrayList<>();
        if (variantDTOs == null || variantDTOs.isEmpty()) {
            errors.add("Variant cannot be Empty");
            return errors;
        }

        for (QuantityVariantDTO variantDTO : variantDTOs) {
            if (variantDTO.getQuantity() < 0) {
                String error = String.format("SkuId: %s must be greater than 0", variantDTO.getSkuId());
                errors.add(error);
            }

            Variant variant = variantRepository.findBySkuIdAndActive(variantDTO.getSkuId(), true);
            if (variant == null) {
                String error = String.format("SkuId: %s is not found", variantDTO.getSkuId());
                errors.add(error);
            }
        }

        return errors;
    }

    @Override
    public Integer updateQuantity(List<QuantityVariantDTO> variantDTOs) {
        int countVariant = 0;
        variantDTOs = this.mergeVariant(variantDTOs);
        List<Variant> variants = new ArrayList<>();

        for (QuantityVariantDTO variantDTO : variantDTOs) {
            Variant variant = variantRepository.findBySkuIdAndActive(variantDTO.getSkuId(), true);
            if (variant != null) {
                int quantity = variant.getQuantity() + variantDTO.getQuantity();
                variant.setQuantity(quantity);
                variants.add(variant);
                countVariant++;
            }
        }

        variantRepository.saveAll(variants);

        return countVariant;
    }

    @Override
    @Transactional
    public Variant update(Integer id, UpdateVariantDTO variantDTO) throws DataNotFoundException, IOException {
        Optional<Variant> variantOpt = variantRepository.findByIdAndActive(id, true);
        if (variantOpt.isPresent()) {
            Variant variant = variantOpt.get();
            variant.setQuantity(variantDTO.getQuantity());
            variant.setPrice(variantDTO.getPrice());

            List<VariantValue> variantValues = variant.getVariantValues();
            if (!variantValues.isEmpty()) {
                variantValueRepository.deleteAll(variantValues);
            }

            List<OptionValue> optionValues = optionValueRepository.findAllOptionValues(variantDTO.getProperties());
            List<String> properties = optionValues.stream().map(OptionValue::getOptionValue).collect(Collectors.toList());

            String variantName = this.generateVariantName(variant.getProduct().getProductName(), properties, variant.getProduct().getId());
            variant.setVariantName(variantName);

            String SkuId = this.generateSKU(variant.getProduct().getProductName(), properties, variant.getProduct().getId());
            variant.setSkuId(SkuId);

            variant = variantRepository.save(variant);

            variantValues = new ArrayList<>();

            for (OptionValue optionValue : optionValues) {
                VariantValue variantValue = new VariantValue();
                variantValue.setOptionValue(optionValue);
                variantValue.setVariant(variant);
                variantValues.add(variantValue);
            }

            List<Integer> imageIds = variantDTO.getImageIds();
            List<Integer> deleteImageIds = variant.getImages().stream().map(Image::getId)
                    .filter(e -> !imageIds.contains(e))
                    .toList();

            List<Image> images = imageRepository.findAllImages(deleteImageIds);
            for (Image image : images) {
                FileUtils.deleteFile(image.getImageUrl());
            }
            imageRepository.deleteAll(images);


            variantValueRepository.saveAll(variantValues);

            return variant;
        }

        return new Variant();
    }

    @Override
    public List<String> validateUpgrade(Integer id, UpdateVariantDTO variantDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return errors;
        }

        Optional<Variant> variant = variantRepository.findByIdAndActive(id, true);
        if (variant.isEmpty()) {
            errors.add("Variant is not found");
        }

        Optional<Product> product = productRepository.findByIdAndActive(variantDTO.getProductId(), true);
        if (product.isEmpty()) {
            errors.add("Product is not found");
        }

        if (variantDTO.getProperties() == null || variantDTO.getProperties().isEmpty()) {
            errors.add("Properties is not empty");
        } else {
            List<OptionValue> optionValues = optionValueRepository.findAllOptionValues(variantDTO.getProperties());
            if (optionValues == null || optionValues.isEmpty()) {
                errors.add("Properties is not exist");
            }
        }

        return errors;
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        Optional<Variant> variant = variantRepository.findByIdAndActive(id, true);
        if (variant.isPresent()) {
            variant.get().setActive(false);
            variantRepository.save(variant.get());
        }
    }

    private String generateVariantName(String productName, List<String> properties, int productId) {
        // Example: TSHIRT-RED-S-001
        return productName.toUpperCase() + "-" + properties.stream().map(String::toUpperCase).collect(Collectors.joining("-")) + "-" + String.format("%03d", productId);
    }

    private String generateSKU(String productName, List<String> properties, int productId) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return productName.replace("-", "").substring(0, 2).toUpperCase() + "-" +
                properties.stream().map(e -> {
                    if (e.length() >= 2) {
                        return e.substring(0, 2).toUpperCase();
                    } else {
                        return e.toUpperCase();
                    }
                }).collect(Collectors.joining("-")) + "-" +
                String.format("%03d", productId) + "-" + timeStamp;
    }

    public void generateCombinations(List<List<String>> attributes, int index, String
            current, List<String> properties) {
        if (index == attributes.size()) {
            properties.add(current);
            return;
        }

        // Duyệt qua từng giá trị của thuộc tính hiện tại
        for (String value : attributes.get(index)) {
            generateCombinations(attributes, index + 1, current.isEmpty() ? value : current + "-" + value, properties);
        }
    }

    public void generateCombinations(
            List<Map<List<Integer>, List<String>>> attributes,
            int index,
            List<String> currentValues,
            List<Integer> currentIds,
            List<String> properties) {

        if (index == attributes.size()) {
            System.out.println(String.join("-", currentValues) + " (IDs: " + currentIds + ")");
            properties.add(String.join("-", currentValues) + " (IDs: " + currentIds + ")");
            return;
        }

        Map<List<Integer>, List<String>> attribute = attributes.get(index);
        for (Map.Entry<List<Integer>, List<String>> entry : attribute.entrySet()) {
            List<Integer> ids = entry.getKey();
            List<String> values = entry.getValue();

            for (int i = 0; i < values.size(); i++) {
                List<String> newValues = new ArrayList<>(currentValues);
                List<Integer> newIds = new ArrayList<>(currentIds);

                newValues.add(values.get(i));
                newIds.add(ids.get(i));

                generateCombinations(attributes, index + 1, newValues, newIds, properties);
            }
        }
    }

    private String generateSKU(String productName, String property, int productId) {
        List<String> properties = Arrays.stream(property.split("-")).toList();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return productName.replace("-", "").substring(0, 2).toUpperCase() + "-" +
                properties.stream().map(e -> {
                    if (e.length() >= 2) {
                        return e.substring(0, 2).toUpperCase();
                    } else {
                        return e.toUpperCase();
                    }
                }).collect(Collectors.joining("-")) + "-" +
                String.format("%03d", productId) + "-" + timeStamp;
    }

    private String generateVariantName(String productName, String property, int productId) {
        // Example: TSHIRT-RED-S-001
        return productName.toUpperCase() + "-" + property.toUpperCase() + "-" + String.format("%03d", productId);
    }

    public List<QuantityVariantDTO> mergeVariant(List<QuantityVariantDTO> variants) {
        // Sử dụng Stream để gộp các đối tượng theo id
        return variants.stream()
                .collect(Collectors.groupingBy(QuantityVariantDTO::getSkuId, Collectors.reducing(this::combineVariant)))
                .values().stream()
                .filter(Optional::isPresent) // Loại bỏ Optional rỗng
                .map(Optional::get)          // Lấy giá trị từ Optional
                .collect(Collectors.toList());
    }

    // Phương thức để gộp hai đối tượng PurchaseOrder
    private QuantityVariantDTO combineVariant(QuantityVariantDTO v1, QuantityVariantDTO v2) {
        v1.setQuantity(v1.getQuantity() + v2.getQuantity());
        return v1;
    }
}
//
//    public static void main(String[] args) {
//        // Tạo một danh sách động chứa các danh sách các thuộc tính
//        List<List<String>> attributes = new ArrayList<>();
//
//        // Thêm các thuộc tính vào danh sách
//        attributes.add(List.of("black", "yellow")); // colors
//        attributes.add(List.of("30", "31", "32"));  // sizes
//        attributes.add(List.of("s", "m", "l"));     // lengths
//        attributes.add(List.of("cotton", "polyester")); // materials (thêm một thuộc tính mới)
//
//        generateCombinations(attributes, 0, "");
//    }
//}
