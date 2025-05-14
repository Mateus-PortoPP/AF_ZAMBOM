package com.insper.prova.feedback;

import com.insper.prova.usuario.UsuarioDTO;
import com.insper.prova.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Feedback cadastrar(String email, Feedback feedback) {        
        // Qualquer usuário pode criar feedback
        UsuarioDTO usuario = usuarioService.buscarUsuario(email);
        
        // Validar nota (deve estar entre 0 e 10)
        if (feedback.getNota() == null || feedback.getNota() < 0 || feedback.getNota() > 10) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10");
        }
        
        feedback.setNomeUsuario(usuario.getNome());
        feedback.setEmailUsuario(usuario.getEmail());
        return feedbackRepository.save(feedback);
    }

    public void deletar(String email, String id) {
        // Apenas administradores podem deletar feedback
        usuarioService.validarAdmin(email);
        feedbackRepository.deleteById(id);
    }

    public List<Feedback> listar() {
        // Qualquer um pode listar os feedbacks
        return feedbackRepository.findAll();
    }
}
