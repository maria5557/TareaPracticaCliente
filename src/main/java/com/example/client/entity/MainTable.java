package com.example.client.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "MainTable")
public class MainTable {

    @DynamoDBHashKey(attributeName = "PK")
    private String pk;

    @DynamoDBRangeKey(attributeName = "SK")
    private String sk;

    @DynamoDBAttribute(attributeName = "id")
    private String id;

    @DynamoDBAttribute(attributeName = "status")
    private String status;

    @DynamoDBAttribute(attributeName = "gIndex2Pk")
    private String gIndex2Pk;

    @DynamoDBAttribute(attributeName = "createdDate")
    private Date createdDate;

}