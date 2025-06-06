package com.example.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ClientOutputDTO {

    private String id;
    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;

}