package com.example.client.mappers;

import com.example.client.model.dto.ClientOutputDTO;
import com.example.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientOutputMapper {

    ClientOutputMapper INSTANCE = Mappers.getMapper(ClientOutputMapper.class);

    // Mapea la entidad Client a ClientDTO
    ClientOutputDTO clientToClientDTO(Client client);
}
