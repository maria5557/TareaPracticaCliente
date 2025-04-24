package com.example.client.mappers;

import com.example.client.dto.ClientMerchantOutputDTO;
import com.example.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMerchantMapper {

    ClientMerchantMapper INSTANCE = Mappers.getMapper(ClientMerchantMapper.class);

    ClientMerchantOutputDTO clientToClientMerchantDTO(Client client);


}
