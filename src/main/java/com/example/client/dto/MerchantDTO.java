package com.example.client.dto;

import com.example.client.model.MerchantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class MerchantDTO {

    private String id;
    private String name;
    private String address;
    private MerchantType merchantType;
}