package com.example.client.dto;

import com.example.client.model.MerchantObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ClientMerchantOutputDTO {

    private String id;
    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;
    private List<MerchantObject> merchants;


}