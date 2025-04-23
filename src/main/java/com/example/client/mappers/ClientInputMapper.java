package com.example.client.mappers;

import com.example.client.dto.ClientInputDTO;
import com.example.client.dto.ClientOutputDTO;
import com.example.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientInputMapper {

    ClientInputMapper INSTANCE = Mappers.getMapper(ClientInputMapper.class);

    Client clientInputToClient(ClientInputDTO clientInputDTO);

}
