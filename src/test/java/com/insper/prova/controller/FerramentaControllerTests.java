package com.insper.prova.controller;


import com.insper.prova.ferramenta.Ferramenta;
import com.insper.prova.ferramenta.FerramentaController;
import com.insper.prova.ferramenta.FerramentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FerramentaControllerTests {

    @InjectMocks
    private FerramentaController ferramentaController;

    @Mock
    private FerramentaService ferramentaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(ferramentaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCriarFerramenta() throws Exception {
        Ferramenta ferramenta = new Ferramenta();
        ferramenta.setNome("Notebook");
        ferramenta.setDescricao("Equipamento usado para desenvolvimento");
        ferramenta.setCategoria("Escritório");
        ferramenta.setNomeUsuario("Ricardo");
        ferramenta.setEmailUsuario("admin@empresa.com");

        when(ferramentaService.cadastrar(eq("admin@empresa.com"), any(Ferramenta.class)))
                .thenReturn(ferramenta);

        mockMvc.perform(post("/api/ferramentas")
                        .header("email", "admin@empresa.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ferramenta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Notebook"))
                .andExpect(jsonPath("$.categoria").value("Escritório"));
    }

    @Test
    void testListarFerramentas() throws Exception {
        Ferramenta f = new Ferramenta();
        f.setNome("Monitor");
        f.setCategoria("Escritório");

        when(ferramentaService.listar()).thenReturn(List.of(f));

        mockMvc.perform(get("/api/ferramentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Monitor"));
    }

    @Test
    void testDeletarFerramenta() throws Exception {
        doNothing().when(ferramentaService).deletar("admin@empresa.com", "f123");

        mockMvc.perform(delete("/api/ferramentas/f123")
                        .header("email", "admin@empresa.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testErro403_UsuarioSemPermissao() throws Exception {
        Ferramenta f = new Ferramenta();
        f.setNome("Projetor");

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissão negada"))
                .when(ferramentaService).cadastrar(eq("user@teste.com"), any(Ferramenta.class));

        mockMvc.perform(post("/api/ferramentas")
                        .header("email", "user@teste.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testErro404_UsuarioNaoExiste() throws Exception {
        Ferramenta f = new Ferramenta();
        f.setNome("Scanner");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"))
                .when(ferramentaService).cadastrar(eq("naoexiste@teste.com"), any(Ferramenta.class));

        mockMvc.perform(post("/api/ferramentas")
                        .header("email", "naoexiste@teste.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isNotFound());
    }
}