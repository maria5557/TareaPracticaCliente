package com.example.client.controller;


import com.example.client.dto.ClientDTO;
import com.example.client.entity.Client;
import com.example.client.mappers.ClientMapper;
import com.example.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {


    private final ClientService clientService;

    // Endpoint para crear un cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO createClient(@RequestBody ClientDTO clientDTO) {
        Client savedClient = clientService.saveClient(ClientMapper.INSTANCE.clientDTOToClient(clientDTO));
        return ClientMapper.INSTANCE.clientToClientDTO(savedClient); // Convertimos la entidad a DTO antes de devolverlo
    }

    // Endpoint para obtener un cliente por ID con opción de "simpleOutput"
    @GetMapping("/{id}")
    public ClientDTO findById(
            @PathVariable String id,
            @RequestParam(value = "simpleOutput", required = false) String simpleOutput) {

        Client client = clientService.getClientById(id);

        if (client != null) {
            if ("simpleOutput".equals(simpleOutput)) {
                return new ClientDTO(id, null, null, null, null,null); // Solo devuelve la ID
            } else {
                return ClientMapper.INSTANCE.clientToClientDTO(client); // Convertimos la entidad a DTO
            }
        }
        return null;
    }

    // Endpoint para buscar clientes por nombre
    @GetMapping("/search")
    public Optional<List<ClientDTO>> findClientsByName(String name) {

        // Recuperamos la lista de clientes por nombre
        List<Client> clients = clientService.findClientsByName(name);

        // Convertimos las entidades Client a ClientDTO
        List<ClientDTO> clientDTOs = clients.stream()
                .map(ClientMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());

        // Retornamos el Optional con la lista de DTOs
        return Optional.of(clientDTOs);
    }

    // Endpoint para buscar un cliente por email
    @GetMapping("/email")
    public ClientDTO findClientByEmail(
            @RequestParam(required = true) @Pattern(regexp = "^(.+)@(.+)$", message = "Email inválido") String email) {
        Client client = clientService.findClientByEmail(email);
        return client != null ? ClientMapper.INSTANCE.clientToClientDTO(client) : null;
    }

}
