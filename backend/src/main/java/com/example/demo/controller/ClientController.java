package com.example.demo.controller;

import com.example.demo.client.ClientService;
import com.example.demo.config.TenantContext;
import com.example.demo.domain.Client;
import com.example.demo.dto.ClientDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients().stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(@RequestBody ClientDto dto) {
        Client client = new Client();
        client.setTenantId(TenantContext.getCurrentTenant());
        client.setName(dto.getName());
        client.setBillingAddress(dto.getBillingAddress());
        client.setShippingAddress(dto.getShippingAddress());
        client.setGstin(dto.getGstin());
        client.setState(dto.getState());
        
        Client saved = clientService.createClient(client);
        return toDto(saved);
    }
    
    private ClientDto toDto(Client c) {
        ClientDto dto = new ClientDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setBillingAddress(c.getBillingAddress());
        dto.setShippingAddress(c.getShippingAddress());
        dto.setGstin(c.getGstin());
        dto.setState(c.getState());
        return dto;
    }
}
