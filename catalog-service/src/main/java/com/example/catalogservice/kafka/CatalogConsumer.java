package com.example.catalogservice.kafka;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CatalogConsumer {
    CatalogRepository repository;

    @Autowired
    public CatalogConsumer(CatalogRepository repository){
        this.repository=repository;
    }
    @KafkaListener(topics="example-catalog-topic") // 구독할 토픽을 지정
    public void updateQty(String Kafkamessage) {
        log.info("Kafka Message ====>" + Kafkamessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(Kafkamessage, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        CatalogEntity entity = repository.findByProductId((String) map.get("productId"));
        if (entity != null){
            entity.setStock(entity.getStock() - (Integer) map.get("qty")); // 재고 차감
            repository.save(entity);
        }
    }
}
