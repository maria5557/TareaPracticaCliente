package com.example.client.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Client extends MainTable {

    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;
}
