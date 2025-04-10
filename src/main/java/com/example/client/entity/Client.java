package com.example.client.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.*;

@Getter
@Setter
@Data
@DynamoDBDocument
public class Client extends MainTableEntity{

    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;
}
