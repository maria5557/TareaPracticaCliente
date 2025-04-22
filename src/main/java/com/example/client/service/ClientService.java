package com.example.client.service;

import com.example.client.entity.Client;
import com.example.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(Client client) {
        client.setPk("clientEntity");
        client.setSk("documentID#" + client.getCifNifNie());
        client.setId(UUID.randomUUID().toString());
        return clientRepository.save(client);
    }

    public Client saveClient(Client client) {
        client.setPk("clientEntity");
        client.setSk("documentID#" + client.getCifNifNie());
        return clientRepository.save(client);
    }

    public Client getClientById(String id) {
        return clientRepository.findById(id);
    }

    public List<Client> findClientsByName(String name) {
        return clientRepository.findByName(name);
    }

    public Client findClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}
