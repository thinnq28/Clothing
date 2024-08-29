package com.datn.shop_app.response.commodity;

import com.datn.shop_app.entity.Commodity;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.response.supplier.SupplierResponse;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommodityResponse {
    private Integer id;

    private String commodityName;

    private Boolean active;

    public static CommodityResponse fromCommodity(Commodity commodity) {
        CommodityResponse commodityResponse = new CommodityResponse();
        BeanUtils.copyProperties(commodity, commodityResponse);
        return commodityResponse;
    }
}
