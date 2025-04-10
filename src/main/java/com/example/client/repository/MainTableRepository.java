package com.example.client.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.client.entity.MainTableEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MainTableRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public MainTableRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void save(MainTableEntity entity) {
        dynamoDBMapper.save(entity);
    }

    public MainTableEntity load(String pk, String sk) {
        return dynamoDBMapper.load(MainTableEntity.class, pk, sk);
    }
}