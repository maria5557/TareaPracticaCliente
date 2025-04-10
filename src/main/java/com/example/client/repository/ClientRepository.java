package com.example.client.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.example.client.entity.Client;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ClientRepository  {

    private final DynamoDBMapper dynamoDBMapper;


    // Método para guardar o actualizar un cliente
    public Client save(Client client) {
        dynamoDBMapper.save(client);
        return client;
    }

    // Método para encontrar un cliente por su ID
    public Client findById(String id) {
        return dynamoDBMapper.load(Client.class, id);
    }

    // Método para buscar clientes por nombre (sin distinción de mayúsculas y minúsculas)
    public List<Client> findByName(String name) {

        List<Client> allClients = dynamoDBMapper.scan(Client.class, new DynamoDBScanExpression());
        return allClients.stream()
                .filter(client -> client.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Método para buscar un cliente por email
    public Client findByEmail(String email) {
        // Aquí puedes buscar el cliente por email si tienes un índice global secundario en DynamoDB para el email.
        return dynamoDBMapper.load(Client.class, email);
    }
}


