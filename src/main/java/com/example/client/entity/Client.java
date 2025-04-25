package com.example.client.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.example.client.mappers.MerchantObjectConverter;
import com.example.client.model.MerchantObject;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
    private String nameLowerCase;

    @DynamoDBTypeConverted(converter = MerchantObjectConverter.class)
    private List<MerchantObject> merchants = new ArrayList<>();
}
