package com.example.client.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.client.entity.Client;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ClientRepository  {

    private final DynamoDBMapper dynamoDBMapper;

    public Client save(Client client) {
        dynamoDBMapper.save(client);
        return client;
    }

    // Método para encontrar un cliente por su ID
    public Client findById(String id) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":id", new AttributeValue().withS(id));

        DynamoDBQueryExpression<Client> queryExpression = new DynamoDBQueryExpression<Client>()
                .withIndexName("IdIndex") // Especificamos el índice global secundario "gIndex2"
                .withConsistentRead(false)
                .withKeyConditionExpression("id = :id")
                .withExpressionAttributeValues(eav);

        List<Client> clients = dynamoDBMapper.query(Client.class, queryExpression);

        return clients.isEmpty() ? null : clients.get(0);
    }

    // Metodo para buscar clientes por nombre (sin distinción de mayúsculas y minúsculas)
    public List<Client> findByName(String name) {

        // Convertimos el nombre a minúsculas para la búsqueda
        String lowerCaseName = name.toLowerCase();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":pk", new AttributeValue().withS("clientEntity"));
        expressionAttributeValues.put(":name", new AttributeValue().withS(lowerCaseName));

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#nameLowerCase", "nameLowerCase");

        // Crear la expresión de la consulta
        DynamoDBQueryExpression<Client> queryExpression = new DynamoDBQueryExpression<Client>()
                .withKeyConditionExpression("PK = :pk")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withExpressionAttributeNames(expressionAttributeNames)
                .withFilterExpression("contains(#nameLowerCase, :name)")
                .withConsistentRead(false);

        // Realizamos la consulta
        return dynamoDBMapper.query(Client.class, queryExpression);
    }

    // Método para buscar un cliente por email
    public Client findByEmail(String email) {
        // Crear el mapa de valores de expresión
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":email", new AttributeValue().withS(email));

        // Crear la expresión de la consulta
        DynamoDBQueryExpression<Client> queryExpression = new DynamoDBQueryExpression<Client>()
                .withIndexName("EmailIndex") // Especificamos el índice secundario "EmailIndex"
                .withConsistentRead(false) // No es necesario tener consistencia en la lectura
                .withKeyConditionExpression("email = :email") // Utilizamos el email como clave
                .withExpressionAttributeValues(eav);

        // Realizar la consulta
        List<Client> clients = dynamoDBMapper.query(Client.class, queryExpression);

        return clients.isEmpty() ? null : clients.get(0);
    }
}


