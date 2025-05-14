package com.example.client.controller;

import com.example.client.model.dto.ClientInputDTO;
import com.example.client.model.dto.ClientMerchantOutputDTO;
import com.example.client.model.dto.ClientOutputDTO;
import com.example.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.NoSuchElementException;

@Api(value = "Controlador de clientes", tags = "Clientes")
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    @ApiOperation(value = "Crear un nuevo cliente")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientOutputDTO> createClient(@RequestBody @Valid ClientInputDTO clientInputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(clientInputDTO));
    }

    @ApiOperation(value = "Obtener cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> findById(@PathVariable String id,
                                                    @RequestParam(value = "simpleOutput", required = false) String simpleOutput) {
        ClientOutputDTO dto = clientService.getClientById(id, simpleOutput);
        if (dto == null) {
            throw new NoSuchElementException("Cliente no encontrado con el ID: " + id);
        }
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "Buscar clientes por nombre")
    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClientOutputDTO>> findClientsByName(@PathVariable String name) {
        List<ClientOutputDTO> result = clientService.findClientsByName(name);
        if (result.isEmpty()) {
            throw new NoSuchElementException("No se encontraron clientes con el nombre: " + name);
        }
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Buscar cliente por email")
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientOutputDTO> findClientByEmail(
            @PathVariable @Valid @Pattern(regexp = "^(.+)@(.+)$", message = "Email inválido") String email) {
        ClientOutputDTO dto = clientService.findClientByEmail(email);
        if (dto == null) {
            throw new NoSuchElementException("Cliente no encontrado con el email: " + email);
        }
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "Actualizar un cliente existente")
    @PutMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> updateClient(@PathVariable String id,
                                                        @RequestBody @Valid ClientInputDTO clientInputDTO) {
        ClientOutputDTO dto = clientService.updateClient(id, clientInputDTO);
        if (dto == null) {
            throw new NoSuchElementException("Cliente no encontrado con el ID: " + id);
        }
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "Verificar existencia de un merchant")
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<Void> checkMerchantExists(@PathVariable String merchantId) {
        boolean exists = clientService.merchantExists(merchantId);
        if (!exists) {
            throw new NoSuchElementException("Merchant no encontrado con el ID: " + merchantId);
        }
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Asociar un merchant a un cliente")
    @PutMapping("/{clientId}/merchants/{merchantId}")
    public ResponseEntity<ClientMerchantOutputDTO> addMerchantToClient(@PathVariable String clientId,
                                                                       @PathVariable String merchantId) {
        try {
            ClientMerchantOutputDTO dto = clientService.addMerchantToClient(clientId, merchantId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Cliente o merchant no encontrado (ID Cliente: " + clientId + ", ID Merchant: " + merchantId + ")");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Obtener merchants de un cliente")
    @GetMapping("/{id}/merchants")
    public ResponseEntity<ClientMerchantOutputDTO> getMerchantsByClientId(@PathVariable String id) {
        ClientMerchantOutputDTO dto = clientService.getMerchantsByClientId(id);
        if (dto == null) {
            throw new NoSuchElementException("Cliente no encontrado con el ID: " + id);
        }
        return ResponseEntity.ok(dto);
    }

    @ApiOperation(value = "Listar todos los clientes")
    @GetMapping
    public ResponseEntity<List<ClientOutputDTO>> getAllClients() {
        List<ClientOutputDTO> clientes = clientService.getAllClients();
        if (clientes == null || clientes.isEmpty()) {
            throw new NoSuchElementException("No hay clientes registrados");
        }
        return ResponseEntity.ok(clientes);
    }

    @ApiOperation(value = "Eliminar un cliente por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        boolean deleted = clientService.deleteClient(id);
        if (!deleted) {
            throw new NoSuchElementException("Cliente no encontrado con el ID: " + id);
        }
        return ResponseEntity.ok().build();
    }
}
