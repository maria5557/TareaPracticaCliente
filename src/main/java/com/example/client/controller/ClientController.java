package com.example.client.controller;

import com.example.client.dto.ClientInputDTO;
import com.example.client.dto.ClientMerchantOutputDTO;
import com.example.client.dto.ClientOutputDTO;
import com.example.client.dto.MerchantOutputDTO;
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
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "Controlador de clientes", tags = "Clientes")
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;
    private final MerchantClient merchantClient;

    @ApiOperation(value = "Crear un nuevo cliente")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Cliente creado exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientOutputDTO> createClient(
            @ApiParam(value = "Datos del cliente", required = true)
            @RequestBody @Valid ClientInputDTO clientInputDTO) {
        Client savedClient = clientService.createClient(ClientInputMapper.INSTANCE.clientInputToClient(clientInputDTO));
        return ResponseEntity.ok(ClientOutputMapper.INSTANCE.clientToClientDTO(savedClient));
    }

    @ApiOperation(value = "Obtener cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> findById(
            @ApiParam(value = "ID del cliente", required = true) @PathVariable String id,
            @ApiParam(value = "Si se pasa 'simpleOutput', devuelve solo el ID")
            @RequestParam(value = "simpleOutput", required = false) String simpleOutput) {

        Client client = clientService.getClientById(id);
        if (client != null) {
            if (StringUtils.equals("simpleOutput", simpleOutput)) {
                return ResponseEntity.ok(new ClientOutputDTO(client.getId(), null, null, null, null, null));
            } else {
                return ResponseEntity.ok(ClientOutputMapper.INSTANCE.clientToClientDTO(client));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ApiOperation(value = "Buscar clientes por nombre", notes = "Devuelve una lista de clientes que contengan dicho nombre")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Clientes encontrados"),
            @ApiResponse(code = 404, message = "No se encontraron clientes")
    })
    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClientOutputDTO>> findClientsByName(
            @ApiParam(value = "Nombre del cliente a buscar", required = true) @PathVariable String name) {
        List<Client> clients = clientService.findClientsByName(name);

        if (clients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ClientOutputDTO> clientOutputDTOS = clients.stream()
                .map(ClientOutputMapper.INSTANCE::clientToClientDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientOutputDTOS);
    }

    @ApiOperation(value = "Buscar cliente por email", notes = "Devuelve un cliente con el email especificado")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientOutputDTO> findClientByEmail(
            @ApiParam(value = "Email del cliente", required = true)
            @PathVariable @Valid @Pattern(regexp = "^(.+)@(.+)$", message = "Email inválido") String email) {
        Client client = clientService.findClientByEmail(email);
        ClientOutputDTO clientOutputDTO = ClientOutputMapper.INSTANCE.clientToClientDTO(client);
        return client != null ? ResponseEntity.ok(clientOutputDTO) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ApiOperation(value = "Actualizar un cliente existente", notes = "Modifica los datos de un cliente con el ID proporcionado")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente actualizado"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> updateClient(
            @ApiParam(value = "ID del cliente", required = true) @PathVariable String id,
            @ApiParam(value = "Datos actualizados del cliente", required = true) @RequestBody @Valid ClientInputDTO clientInputDTO) {

        Client existingClient = clientService.getClientById(id);

        if (existingClient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        existingClient.setName(clientInputDTO.getName());
        existingClient.setSurname(clientInputDTO.getSurname());
        existingClient.setCifNifNie(clientInputDTO.getCifNifNie());
        existingClient.setPhone(clientInputDTO.getPhone());
        existingClient.setEmail(clientInputDTO.getEmail());

        Client updatedClient = clientService.saveClient(existingClient);

        return ResponseEntity.ok(ClientOutputMapper.INSTANCE.clientToClientDTO(updatedClient));
    }

    @ApiOperation(value = "Verificar existencia de un merchant", notes = "Confirma si un merchant existe mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Merchant existe"),
            @ApiResponse(code = 404, message = "Merchant no encontrado"),
            @ApiResponse(code = 500, message = "Error interno")
    })
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<Void> checkMerchantExists(
            @ApiParam(value = "ID del merchant", required = true) @PathVariable String merchantId) {
        try {
            MerchantOutputDTO merchant = merchantClient.findMerchantById(merchantId);
            return merchant != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Asociar un merchant a un cliente", notes = "Agrega un merchant a la lista de un cliente específico")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Merchant asociado exitosamente"),
            @ApiResponse(code = 404, message = "Cliente o merchant no encontrado"),
            @ApiResponse(code = 409, message = "Merchant ya asociado al cliente"),
            @ApiResponse(code = 500, message = "Error interno")
    })
    @PutMapping("/{clientId}/merchants/{merchantId}")
    public ResponseEntity<ClientMerchantOutputDTO> addMerchantToClient(
            @ApiParam(value = "ID del cliente", required = true) @PathVariable String clientId,
            @ApiParam(value = "ID del merchant", required = true) @PathVariable String merchantId) {

        Client client = clientService.getClientById(clientId);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            MerchantOutputDTO merchantOutputDTO = merchantClient.findMerchantById(merchantId);
            System.out.println("he llegado hasta aqui");
            System.out.println("El merchant es " + merchantOutputDTO.getName());
            if (merchantOutputDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            MerchantObject merchantObject = new MerchantObject(
                    merchantOutputDTO.getId(),
                    merchantOutputDTO.getName()
            );

            List<MerchantObject> merchantList = client.getMerchants();
            if (merchantList != null && merchantList.stream().anyMatch(m -> m.getId().equals(merchantId))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(client));
            }

            client.getMerchants().add(merchantObject);
            Client updatedClient = clientService.saveClient(client);

            merchantOutputDTO.setIdCliente(clientId);
            merchantClient.updateMerchant(merchantId, merchantOutputDTO);

            return ResponseEntity.ok(ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(updatedClient));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Obtener merchants de un cliente", notes = "Devuelve la lista de merchants asociados al cliente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de merchants devuelta"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @GetMapping("/{id}/merchants")
    public ResponseEntity<ClientMerchantOutputDTO> getMerchantsByClientId(
            @ApiParam(value = "ID del cliente", required = true) @PathVariable String id) {

        Client client = clientService.getClientById(id);

        if (client != null) {
            return ResponseEntity.ok(ClientMerchantMapper.INSTANCE.clientToClientMerchantDTO(client));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}