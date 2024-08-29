package com.datn.shop_app.response.supplier;

import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.response.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupplierResponse {

    private Integer id;

    private String supplierName;

    private String phoneNumber;

    private String address;

    private String email;

    private Boolean active;

    private int totalPages;

    public static SupplierResponse fromSupplier(Supplier supplier) {
        SupplierResponse supplierResponse = new SupplierResponse();
        BeanUtils.copyProperties(supplier, supplierResponse);
        return supplierResponse;
    }
}
