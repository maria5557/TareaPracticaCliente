package com.example.client.service;

import com.example.client.entity.Client;
import com.example.client.feign.MerchantClient;
import com.example.client.mappers.ClientInputMapper;
import com.example.client.mappers.ClientMerchantMapper;
import com.example.client.mappers.ClientOutputMapper;
import com.example.client.model.MerchantObject;
import com.example.client.model.dto.ClientInputDTO;
import com.example.client.model.dto.ClientMerchantOutputDTO;
import com.example.client.model.dto.ClientOutputDTO;
import com.example.client.model.dto.MerchantOutputDTO;
import com.example.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final MerchantClient merchantClient;

    public ClientOutputDTO createClient(ClientInputDTO dto) {
        Client client = ClientInputMapper.INSTANCE.clientInputToClient(dto);
        client.setId(UUID.randomUUID().toString());
        client.setPk("clientEntity");
        client.setSk("documentID#" + client.getCifNifNie());
        client.setNameLowerCase(client.getName().toLowerCase());  // Guardar el nombre en minúsculas
        return ClientOutputMapper.INSTANCE.clientToClientDTO(clientRepository.save(client));
    }

    public ClientOutputDTO getClientById(String id, String simpleOutput) {
        Client client = clientRepository.findById(id);
        if (client == null) return null;

        if ("simpleOutput".equals(simpleOutput)) {
            return new ClientOutputDTO(client.getId(), null, null, null, null, null);
        }

        return ClientOutputMapper.INSTANCE.clientToClientDTO(client);
    }

    public List<ClientOutputDTO> findClientsByName(String name) {
        return clientRepository.findByName(name)
                .stream()
                .map(ClientOutputMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());
    }

    public ClientOutputDTO findClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email);
        return client != null ? ClientOutputMapper.INSTANCE.clientToClientDTO(client) : null;
    }

    public ClientOutputDTO updateClient(String id, ClientInputDTO dto) {
        Client client = clientRepository.findById(id);
        if (client == null) return null;

        client.setName(dto.getName());
        client.setNameLowerCase(dto.getName().toLowerCase());  // Guardar el nombre en minúsculas
        client.setSurname(dto.getSurname());
        client.setCifNifNie(dto.getCifNifNie());
        client.setPhone(dto.getPhone());
        client.setEmail(dto.getEmail());
        client.setPk("clientEntity");
        client.setSk("documentID#" + client.getCifNifNie());

        return ClientOutputMapper.INSTANCE.clientToClientDTO(clientRepository.save(client));
    }

    public boolean merchantExists(String merchantId) {
        try {
            return merchantClient.findMerchantById(merchantId) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public ClientMerchantOutputDTO addMerchantToClient(String clientId, String merchantId) {
        Client client = clientRepository.findById(clientId);
        if (client == null) throw new NoSuchElementException("Client not found");

        MerchantOutputDTO merchant = merchantClient.findMerchantById(merchantId);
        if (merchant == null) throw new NoSuchElementException("Merchant not found");

        if (client.getMerchants().stream().anyMatch(m -> m.getId().equals(merchantId))) {
            throw new IllegalArgumentException("Merchant already associated");
        }

        client.getMerchants().add(new MerchantObject(merchant.getId(), merchant.getName()));
        Client updated = clientRepository.save(client);

        merchant.setIdCliente(clientId);
        merchantClient.updateMerchant(merchantId, merchant);

        return ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(updated);
    }

    public ClientMerchantOutputDTO getMerchantsByClientId(String id) {
        Client client = clientRepository.findById(id);
        return client != null ? ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(client) : null;
    }
}

