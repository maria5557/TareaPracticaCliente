package com.example.client.controller;

import com.example.client.dto.ClientDTO;
import com.example.client.dto.MerchantDTO;
import com.example.client.entity.Client;
import com.example.client.feign.MerchantClient;
import com.example.client.mappers.ClientMapper;
import com.example.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;
    private final MerchantClient merchantClient;


    // Endpoint para crear un cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {

        Client savedClient = clientService.createClient(ClientMapper.INSTANCE.clientDTOToClient(clientDTO));
        return ResponseEntity.ok(ClientMapper.INSTANCE.clientToClientDTO(savedClient));
    }



    // Endpoint para obtener un cliente por ID con opción de "simpleOutput"
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> findById(
            @PathVariable String id,
            @RequestParam(value = "simpleOutput", required = false) String simpleOutput) {

        Client client = clientService.getClientById(id);
        System.out.println("Valor de simpleOutput: " + simpleOutput);

        if (client != null) {
            if (StringUtils.equals("simpleOutput", simpleOutput)) {
                return ResponseEntity.ok(new ClientDTO(client.getId(), null, null, null, null, null));
            } else {
                return ResponseEntity.ok(ClientMapper.INSTANCE.clientToClientDTO(client));
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    // Endpoint para buscar clientes por nombre
    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClientDTO>> findClientsByName(@PathVariable String name) {

        // Recuperamos la lista de clientes por nombre
        List<Client> clients = clientService.findClientsByName(name);

        if (clients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Convertimos las entidades Client a ClientDTO
        List<ClientDTO> clientDTOs = clients.stream()
                .map(ClientMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());

        // Retornamos los clientes como un ResponseEntity con un código 200 OK
        return ResponseEntity.ok(clientDTOs);
    }



     // Endpoint para buscar un cliente por email
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientDTO> findClientByEmail(
            @PathVariable @Valid @Pattern(regexp = "^(.+)@(.+)$", message = "Email inválido") String email) {
        Client client = clientService.findClientByEmail(email);
        ClientDTO clientDTO = ClientMapper.INSTANCE.clientToClientDTO(client);
        return client != null ? ResponseEntity.ok(clientDTO) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Endpoint para modificar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable String id,
            @RequestBody @Valid ClientDTO clientDTO) {

        // Buscamos el cliente en la base de datos
        Client existingClient = clientService.getClientById(id);

        if (existingClient == null) {
            // Si no existe el cliente, retornamos un 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Actualizamos los campos del cliente con los nuevos datos del DTO
        existingClient.setName(clientDTO.getName());
        existingClient.setSurname(clientDTO.getSurname());
        existingClient.setCifNifNie(clientDTO.getCifNifNie());
        existingClient.setPhone(clientDTO.getPhone());
        existingClient.setEmail(clientDTO.getEmail());

        // Guardamos el cliente actualizado
        Client updatedClient = clientService.saveClient(existingClient);

        // Retornamos el cliente actualizado como respuesta
        return ResponseEntity.ok(ClientMapper.INSTANCE.clientToClientDTO(updatedClient));
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<Void> checkMerchantExists(@PathVariable String merchantId) {
        try {
            // Llamada al microservicio de merchant para obtener el merchant por ID
            MerchantDTO merchant = merchantClient.findMerchantById(merchantId);

            // Si el merchant existe, devolvemos 200 OK
            if (merchant != null) {
                return ResponseEntity.ok().build();
            } else {
                // Si el merchant no existe, devolvemos 404 Not Found
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
