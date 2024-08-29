package com.datn.shop_app.response.product;

import com.datn.shop_app.entity.*;
import com.datn.shop_app.response.image.ImageResponse;
import com.datn.shop_app.response.option.OptionResponse;
import com.datn.shop_app.response.option_value.OptionValueResponse;
import com.datn.shop_app.response.variant.VariantResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductResponse {
    private Integer id;

    private String productName;

    private String supplierName;

    private Integer supplierId;

    private String commodityName;

    private Integer commodityId;

    private List<OptionResponse> options;

    private String description;

    private String imageUrl;

    private Boolean active;

    private int totalPages;

    private VariantResponse variant;

    public static ProductResponse fromProduct(Product product) {
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        if (product.getSupplier() != null) {
            productResponse.setSupplierName(product.getSupplier().getSupplierName());
            productResponse.setSupplierId(product.getSupplier().getId());
        }

        if (product.getCommodity() != null) {
            productResponse.setCommodityId(product.getCommodity().getId());
            productResponse.setCommodityName(product.getCommodity().getCommodityName());
        }

        if (product.getProductOptions() != null && !product.getProductOptions().isEmpty()) {
            List<OptionResponse> options = new ArrayList<>();
            for (ProductOption productOption : product.getProductOptions()) {
                OptionResponse optionResponse = OptionResponse.fromOption(productOption.getOption());
                options.add(optionResponse);
            }
            productResponse.setOptions(options);
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            Variant variant = getVariant(product);
            VariantResponse variantResponse = fromVariant(variant);
            productResponse.setVariant(variantResponse);
        }

        return productResponse;
    }

    private static Variant getVariant(Product product) {
        Variant variant = new Variant();
        boolean isHasPromotion = false;
        for (Variant productVariant : product.getVariants()) {
            if (productVariant.getPromotionVariants() != null && !productVariant.getPromotionVariants().isEmpty()) {
                variant = productVariant;
                isHasPromotion = true;
                break;
            }
        }
        if (!isHasPromotion) {
            variant = product.getVariants().get(0);
        }
        return variant;
    }

    public static VariantResponse fromVariant(Variant variant) {
        VariantResponse variantResponse = new VariantResponse();
        BeanUtils.copyProperties(variant, variantResponse);

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
