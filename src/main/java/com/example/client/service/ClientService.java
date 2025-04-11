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

    public Client saveClient(Client client) {
        client.setPk("clientEntity");
        client.setSk("documentID#" + client.getCifNifNie());
        client.setId(UUID.randomUUID().toString());
        return clientRepository.save(client);
    }

    // Método para obtener un cliente por ID con opción de "simpleOutput"
    public Client getClientById(String id) {
        return clientRepository.findById(id);
    }

    // Método para buscar clientes por nombre
    public List<Client> findClientsByName(String name) {
        return clientRepository.findByName(name);
    }

    // Método para buscar un cliente por su email
    public Client findClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}
