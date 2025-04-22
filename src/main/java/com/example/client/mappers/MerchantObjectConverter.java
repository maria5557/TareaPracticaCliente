package com.example.client.mappers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.example.client.model.MerchantObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MerchantObjectConverter implements DynamoDBTypeConverter<String, List<MerchantObject>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(List<MerchantObject> merchantObjects) {
        try {
            return objectMapper.writeValueAsString(merchantObjects);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing merchant list", e);
        }
    }

    @Override
    public List<MerchantObject> unconvert(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<MerchantObject>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing merchant list", e);
        }
    }
}
