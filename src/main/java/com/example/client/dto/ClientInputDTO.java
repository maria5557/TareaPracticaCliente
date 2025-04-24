package com.example.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "DTO para la creación o actualización de un cliente")
@Data
@AllArgsConstructor
@Getter
@Setter
public class ClientInputDTO {

    @ApiModelProperty(value = "Nombre del cliente")
    private String name;

    @ApiModelProperty(value = "Apellido del cliente")
    private String surname;

    @ApiModelProperty(value = "Documento de identidad (CIF/NIF/NIE)")
    private String cifNifNie;

    @ApiModelProperty(value = "Teléfono de contacto")
    private String phone;

    @ApiModelProperty(value = "Correo electrónico del cliente")
    private String email;
}