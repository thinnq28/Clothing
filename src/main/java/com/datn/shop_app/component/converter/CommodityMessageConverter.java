package com.datn.shop_app.component.converter;

import com.datn.shop_app.entity.Commodity;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CommodityMessageConverter extends JsonMessageConverter {
    public CommodityMessageConverter() {
        super();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("com.datn.project.shop_app");
        typeMapper.setIdClassMapping(Collections.singletonMap("category", Commodity.class));
        this.setTypeMapper(typeMapper);
    }
}
