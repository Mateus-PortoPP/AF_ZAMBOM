package com.insper.prova.service;


import com.insper.prova.ferramenta.Ferramenta;
import com.insper.prova.ferramenta.FerramentaRepository;
import com.insper.prova.ferramenta.FerramentaService;
import com.insper.prova.usuario.UsuarioDTO;
import com.insper.prova.usuario.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FerramentaServiceTests {

    @InjectMocks
    private FerramentaService ferramentaService;

    @Mock
    private FerramentaRepository ferramentaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Test
    void testCadastrarFerramentaComoAdmin() {
        Ferramenta ferramenta = new Ferramenta();
        ferramenta.setNome("Furadeira");
        ferramenta.setDescricao("Furadeira de impacto");
        ferramenta.setCategoria("Mecânica");

        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNome("Ricardo");
        usuario.setEmail("admin@empresa.com");
        usuario.setPapel("ADMIN");

        when(usuarioService.validarAdmin("admin@empresa.com")).thenReturn(usuario);
        when(ferramentaRepository.save(any(Ferramenta.class))).thenAnswer(i -> i.getArgument(0));

        Ferramenta resultado = ferramentaService.cadastrar("admin@empresa.com", ferramenta);

        assertEquals("Ricardo", resultado.getNomeUsuario());
        assertEquals("admin@empresa.com", resultado.getEmailUsuario());
        assertEquals("Mecânica", resultado.getCategoria());
    }

    @Test
    void testListarFerramentas() {
        Ferramenta f1 = new Ferramenta();
        f1.setNome("Mouse");

        when(ferramentaRepository.findAll()).thenReturn(List.of(f1));

        List<Ferramenta> ferramentas = ferramentaService.listar();

        assertEquals(1, ferramentas.size());
        assertEquals("Mouse", ferramentas.get(0).getNome());
    }

    @Test
    void testDeletarFerramentaComoAdmin() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setPapel("ADMIN");

        when(usuarioService.validarAdmin("admin@empresa.com")).thenReturn(usuario);

        ferramentaService.deletar("admin@empresa.com", "abc123");

        verify(ferramentaRepository, times(1)).deleteById("abc123");
    }

    @Test
    void testCadastrarFerramenta_UsuarioNaoExiste() {
        Ferramenta ferramenta = new Ferramenta();
        ferramenta.setNome("Scanner");

        when(usuarioService.validarAdmin("naoexiste@teste.com"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            ferramentaService.cadastrar("naoexiste@teste.com", ferramenta);
        });

        assertEquals(404, ex.getStatusCode().value());
        assertEquals("Usuário não encontrado", ex.getReason());
    }

    @Test
    void testCadastrarFerramenta_SemPermissao() {
        Ferramenta ferramenta = new Ferramenta();
        ferramenta.setNome("Projetor");

        when(usuarioService.validarAdmin("user@teste.com"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissão negada"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            ferramentaService.cadastrar("user@teste.com", ferramenta);
        });

        assertEquals(403, ex.getStatusCode().value());
        assertEquals("Permissão negada", ex.getReason());
    }

}