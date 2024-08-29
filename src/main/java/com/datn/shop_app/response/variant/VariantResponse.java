package com.datn.shop_app.response.variant;

import com.datn.shop_app.entity.*;
import com.datn.shop_app.response.image.ImageResponse;
import com.datn.shop_app.response.option.OptionResponse;
import com.datn.shop_app.response.option_value.OptionValueResponse;
import com.datn.shop_app.response.product.ProductResponse;
import com.datn.shop_app.response.variant_value.VariantValueResponse;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantResponse {
    private Integer id;

    private String skuId;

    private String variantName;

    private Integer quantity;

    private Float price;

    private Boolean active;

    private List<String> imageUrls;

    private List<ImageResponse> images;

    private int totalPages;

    private List<Integer> promotionIds;

    private ProductResponse product;

    private List<Integer> optionValueIds;

    private int totalDiscountPercentage;

    private List<VariantValueResponse> variantValues;

    private List<OptionResponse> options;

    public static VariantResponse fromVariant(Variant variant) {
        VariantResponse variantResponse = new VariantResponse();
        BeanUtils.copyProperties(variant, variantResponse);

        ProductResponse productResponse = new ProductResponse();
        Product product = variant.getProduct();
        BeanUtils.copyProperties(product, productResponse);
        if (product.getProductOptions() != null && !product.getProductOptions().isEmpty()) {
            List<OptionResponse> options = new ArrayList<>();
            for (ProductOption productOption : product.getProductOptions()) {
                OptionResponse optionResponse = OptionResponse.fromOption(productOption.getOption());
                options.add(optionResponse);
            }
            productResponse.setOptions(options);
        }
        variantResponse.setProduct(productResponse);

        List<String> imageUrls = new ArrayList<>();
        List<ImageResponse> imageResponses = new ArrayList<>();

        if (variant.getImages() != null) {
            for (Image image : variant.getImages()) {
                ImageResponse imageResponse = new ImageResponse();
                imageResponse.setId(image.getId());
                imageResponse.setUrl(image.getImageUrl());
                imageResponses.add(imageResponse);
                imageUrls.add(image.getImageUrl());
            }
            variantResponse.setImages(imageResponses);
            variantResponse.setImageUrls(imageUrls);
        }

        if (variant.getVariantValues() != null) {
            List<Integer> optionValueIds = variant.getVariantValues().stream().map(e -> e.getOptionValue().getId()).toList();
            variantResponse.setOptionValueIds(optionValueIds);

            List<VariantValueResponse> variantValueResponses = new ArrayList<>();
            List<OptionResponse> options = new ArrayList<>();

            // Tạo Map để nhóm các optionValueId theo optionId
            Map<Option, List<OptionValue>> groupedByOption = variant.getVariantValues().stream()
                    .collect(Collectors.groupingBy(
                            VariantValue::getOption, // Nhóm theo option
                            Collectors.mapping(VariantValue::getOptionValue, Collectors.toList()) // Lấy optionValue và thu thập vào danh sách
                    ));

            for (Map.Entry<Option, List<OptionValue>> entry : groupedByOption.entrySet()) {
                OptionResponse optionResponse = new OptionResponse();
                BeanUtils.copyProperties(entry.getKey(), optionResponse);

                List<OptionValueResponse> optionValueResponses = entry.getValue().stream().map(OptionValueResponse::fromOptionValue).toList();
                optionResponse.setOptionValues(optionValueResponses);
                options.add(optionResponse);
            }

            variantResponse.setOptions(options);

            for (VariantValue variantValue : variant.getVariantValues()) {
                VariantValueResponse variantValueResponse = new VariantValueResponse();
                BeanUtils.copyProperties(variantValue, variantValueResponse);
                variantValueResponse.setId(variantValue.getId());
                variantValueResponses.add(variantValueResponse);
            }

            variantResponse.setVariantValues(variantValueResponses);
        }

        if (variant.getPromotionVariants() != null) {
            List<Integer> promotionIds = variant.getPromotionVariants().stream().map(e -> e.getPromotion().getId()).toList();
            variantResponse.setPromotionIds(promotionIds);
            variantResponse.setTotalDiscountPercentage(variant.getPromotionVariants()
                    .stream().map(PromotionVariant::getPromotion)
                    .map(Promotion::getDiscountPercentage)
                    .mapToInt(BigDecimal::intValue).sum());
        }

        return variantResponse;
    }
}
