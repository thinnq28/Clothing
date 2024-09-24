package com.datn.shop_app.component;

import com.datn.shop_app.entity.Commodity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(id = "groupA", topics = { "get-commodity", "create-commodity"  })
public class MyKafkaListener {
    @KafkaHandler
    public void listenCommodity(Commodity commodity) {
        System.out.println("Received: " + commodity);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        System.out.println("Received unknown: " + object);
    }

    @KafkaHandler
    public void listenListOfCommodities(List<Commodity> commodities) {
        System.out.println("Received: " + commodities);
    }

}
