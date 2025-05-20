package com.example.client.service;

import com.example.client.entity.Client;
import com.example.client.model.dto.LoginRequestDTO;
import com.example.client.repository.ClientRepository;
import com.example.client.interceptor.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;

    public String login(LoginRequestDTO request) {
        Client client = clientRepository.findByEmail(request.getEmail());

        if (client == null || !client.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return JwtUtil.generateToken(client.getId(), client.getName(), client.getEmail());    }
}
