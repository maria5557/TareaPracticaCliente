package com.example.client.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DynamoDBTable(tableName = "MainTable")
public class MainTableEntity {

    private String id;
    private String pk;
    private String sk;
    private String status;
    private String gIndex2Pk;
    private LocalDate createdDate;

}