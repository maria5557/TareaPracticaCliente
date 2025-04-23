package com.example.client.controller;

import com.example.client.dto.ClientInputDTO;
import com.example.client.dto.ClientMerchantOutputDTO;
import com.example.client.dto.ClientOutputDTO;
import com.example.client.dto.MerchantFullDTO;
import com.example.client.entity.Client;
import com.example.client.feign.MerchantClient;
import com.example.client.mappers.ClientInputMapper;
import com.example.client.mappers.ClientOutputMapper;
import com.example.client.mappers.ClientMerchantMapper;
import com.example.client.model.MerchantObject;
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
    public ResponseEntity<ClientOutputDTO> createClient(@RequestBody @Valid ClientInputDTO clientInputDTO) {
        Client savedClient = clientService.createClient(ClientInputMapper.INSTANCE.clientInputToClient(clientInputDTO));
        return ResponseEntity.ok(ClientOutputMapper.INSTANCE.clientToClientDTO(savedClient));
    }


    // Endpoint para obtener un cliente por ID con opción de "simpleOutput"
    @GetMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> findById(
            @PathVariable String id,
            @RequestParam(value = "simpleOutput", required = false) String simpleOutput) {

        Client client = clientService.getClientById(id);
        System.out.println("Valor de simpleOutput: " + simpleOutput);

        if (client != null) {
            if (StringUtils.equals("simpleOutput", simpleOutput)) {
                return ResponseEntity.ok(new ClientOutputDTO(client.getId(), null, null, null, null, null));
            } else {
                return ResponseEntity.ok(ClientOutputMapper.INSTANCE.clientToClientDTO(client));
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    // Endpoint para buscar clientes por nombre
    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClientOutputDTO>> findClientsByName(@PathVariable String name) {

        // Recuperamos la lista de clientes por nombre
        List<Client> clients = clientService.findClientsByName(name);

        if (clients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Convertimos las entidades Client a ClientDTO
        List<ClientOutputDTO> clientOutputDTOS = clients.stream()
                .map(ClientOutputMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());

        // Retornamos los clientes como un ResponseEntity con un código 200 OK
        return ResponseEntity.ok(clientOutputDTOS);
    }



     // Endpoint para buscar un cliente por email
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientOutputDTO> findClientByEmail(
            @PathVariable @Valid @Pattern(regexp = "^(.+)@(.+)$", message = "Email inválido") String email) {
        Client client = clientService.findClientByEmail(email);
        ClientOutputDTO clientOutputDTO = ClientOutputMapper.INSTANCE.clientToClientDTO(client);
        return client != null ? ResponseEntity.ok(clientOutputDTO) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Endpoint para modificar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> updateClient(
            @PathVariable String id,
            @RequestBody @Valid ClientInputDTO clientInputDTO) {

        // Buscamos el cliente en la base de datos
        Client existingClient = clientService.getClientById(id);

        if (existingClient == null) {
            // Si no existe el cliente, retornamos un 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Actualizamos los campos del cliente con los nuevos datos del DTO
        existingClient.setName(clientInputDTO.getName());
        existingClient.setSurname(clientInputDTO.getSurname());
        existingClient.setCifNifNie(clientInputDTO.getCifNifNie());
        existingClient.setPhone(clientInputDTO.getPhone());
        existingClient.setEmail(clientInputDTO.getEmail());

        // Guardamos el cliente actualizado
        Client updatedClient = clientService.saveClient(existingClient);

        // Retornamos el cliente actualizado como respuesta
        return ResponseEntity.ok(ClientOutputMapper.INSTANCE.clientToClientDTO(updatedClient));
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<Void> checkMerchantExists(@PathVariable String merchantId) {
        try {
            // Llamada al microservicio de merchant para obtener el merchant por ID
            MerchantFullDTO merchant = merchantClient.findMerchantById(merchantId);

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


    @PutMapping("/{clientId}/merchants/{merchantId}")
    public ResponseEntity<ClientMerchantOutputDTO> addMerchantToClient(
            @PathVariable String clientId,
            @PathVariable String merchantId) {

        // 1. Verificamos que el cliente exista
        Client client = clientService.getClientById(clientId);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            // 2. Verificamos que el merchant exista
            MerchantFullDTO merchantFullDTO = merchantClient.findMerchantById(merchantId);
            if (merchantFullDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            // 3. Convertimos MerchantDTO a MerchantObject y lo añadimos a la lista
            MerchantObject merchantObject = new MerchantObject(
                    merchantFullDTO.getId(),
                    merchantFullDTO.getName()
            );

            List<MerchantObject> merchantList = client.getMerchants();

            if (merchantList != null && merchantList.stream().anyMatch(m -> m.getId().equals(merchantId))) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(client)
                );
            }

            client.getMerchants().add(merchantObject);

            // 4. Guardamos el cliente actualizado
            Client updatedClient = clientService.saveClient(client);

            // Actualizar merchant con idCliente en MS merchant
            merchantFullDTO.setIdCliente(clientId);
            merchantClient.updateMerchant(merchantId, merchantFullDTO);

            //de prueba
            System.out.println("id del cliente: " + merchantFullDTO.getIdCliente());
            MerchantFullDTO merchantFullDTO2 = merchantClient.findMerchantById(merchantId);
            System.out.println("id del cliente: " + merchantFullDTO2.getIdCliente());


            // 5. Devolvemos el cliente actualizado
            ClientMerchantOutputDTO clientMerchantOutputDTO = ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(updatedClient);
            return ResponseEntity.ok(clientMerchantOutputDTO);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/{id}/merchants")
    public ResponseEntity<ClientMerchantOutputDTO> getMerchantsByClientId(@PathVariable String id) {
        // Recuperamos el cliente por su ID
        Client client = clientService.getClientById(id);

        if (client != null) {
            // Mapeamos la entidad Client a ClientMerchantOutputDTO
            ClientMerchantOutputDTO clientMerchantOutputDTO = ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(client);

            // Retornamos el DTO con los merchants
            return ResponseEntity.ok(clientMerchantOutputDTO);
        }

        // Si el cliente no se encuentra, retornamos 404 Not Found
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }


}
