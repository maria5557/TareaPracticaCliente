package com.example.client.controller;

import com.example.client.model.dto.ClientInputDTO;
import com.example.client.model.dto.ClientMerchantOutputDTO;
import com.example.client.model.dto.ClientOutputDTO;
import com.example.client.feign.MerchantClient;
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
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Cliente creado exitosamente"),
            @ApiResponse(code = 400, message = "Datos de entrada inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientOutputDTO> createClient(@RequestBody @Valid ClientInputDTO clientInputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(clientInputDTO));
    }

    @ApiOperation(value = "Obtener cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> findById(@PathVariable String id,
                                                    @RequestParam(value = "simpleOutput", required = false) String simpleOutput) {
        ClientOutputDTO dto = clientService.getClientById(id, simpleOutput);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }


    @ApiOperation(value = "Buscar clientes por nombre", notes = "Devuelve una lista de clientes que contengan dicho nombre")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Clientes encontrados"),
            @ApiResponse(code = 404, message = "No se encontraron clientes")
    })
    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClientOutputDTO>> findClientsByName(@PathVariable String name) {
        List<ClientOutputDTO> result = clientService.findClientsByName(name);
        return result.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Buscar cliente por email", notes = "Devuelve un cliente con el email especificado")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientOutputDTO> findClientByEmail(
            @PathVariable @Valid @Pattern(regexp = "^(.+)@(.+)$", message = "Email inválido") String email) {
        ClientOutputDTO dto = clientService.findClientByEmail(email);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Actualizar un cliente existente", notes = "Modifica los datos de un cliente con el ID proporcionado")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente actualizado"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientOutputDTO> updateClient(@PathVariable String id,
                                                        @RequestBody @Valid ClientInputDTO clientInputDTO) {
        ClientOutputDTO dto = clientService.updateClient(id, clientInputDTO);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
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
            boolean exists = clientService.merchantExists(merchantId);
            return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
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
    public ResponseEntity<ClientMerchantOutputDTO> addMerchantToClient(@PathVariable String clientId,
                                                                       @PathVariable String merchantId) {
        try {
            ClientMerchantOutputDTO dto = clientService.addMerchantToClient(clientId, merchantId);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Obtener merchants de un cliente", notes = "Devuelve la lista de merchants asociados al cliente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de merchants devuelta"),
            @ApiResponse(code = 404, message = "Cliente no encontrado")
    })
    @GetMapping("/{id}/merchants")
    public ResponseEntity<ClientMerchantOutputDTO> getMerchantsByClientId(@PathVariable String id) {
        ClientMerchantOutputDTO dto = clientService.getMerchantsByClientId(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

}