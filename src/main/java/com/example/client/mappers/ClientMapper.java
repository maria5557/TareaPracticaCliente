package com.example.client.mappers;

import com.example.client.dto.ClientDTO;
import com.example.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    // Mapea la entidad Client a ClientDTO
    ClientDTO clientToClientDTO(Client client);

    // Mapea el DTO ClientDTO a la entidad Client
    Client clientDTOToClient(ClientDTO clientDTO);
}
