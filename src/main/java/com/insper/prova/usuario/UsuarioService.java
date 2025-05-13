package com.insper.prova.usuario;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UsuarioService {

    private final RestTemplate restTemplate;
    private final String URL_BASE = "http://56.124.127.89:8080/api/usuario/";

    // 🔧 Construtor que permite injeção no teste
    public UsuarioService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UsuarioDTO validarAdmin(String email) {
        try {
            ResponseEntity<UsuarioDTO> response = restTemplate.getForEntity(URL_BASE + email, UsuarioDTO.class);
            UsuarioDTO usuario = response.getBody();

            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
            }

            if (!"ADMIN".equals(usuario.getPapel())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissão negada");
            }

            return usuario;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao buscar usuário");
            }
        } catch (HttpServerErrorException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissão negada (API de usuários falhou)");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado");
        }
    }

    public UsuarioDTO buscarUsuario(String email) {
        try {
            ResponseEntity<UsuarioDTO> response = restTemplate.getForEntity(URL_BASE + email, UsuarioDTO.class);
            return response.getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }
}