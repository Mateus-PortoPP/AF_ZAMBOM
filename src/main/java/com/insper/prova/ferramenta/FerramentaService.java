package com.insper.prova.ferramenta;

import com.insper.prova.usuario.UsuarioDTO;
import com.insper.prova.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FerramentaService {

    @Autowired
    private FerramentaRepository ferramentaRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Ferramenta cadastrar(String email, Ferramenta ferramenta) {
        UsuarioDTO usuario = usuarioService.validarAdmin(email);
        ferramenta.setNomeUsuario(usuario.getNome());
        ferramenta.setEmailUsuario(usuario.getEmail());
        return ferramentaRepository.save(ferramenta);
    }

    public void deletar(String email, String id) {
        usuarioService.validarAdmin(email);
        ferramentaRepository.deleteById(id);
    }

    public List<Ferramenta> listar() {
        return ferramentaRepository.findAll();
    }
}