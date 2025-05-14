package com.insper.prova.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> criarFeedback(
            @RequestHeader("email") String email,
            @RequestBody Feedback feedback
    ) {
        return ResponseEntity.ok(feedbackService.cadastrar(email, feedback));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFeedback(
            @RequestHeader("email") String email,
            @PathVariable String id
    ) {
        // Somente ADMIN pode deletar feedback
        feedbackService.deletar(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> listarFeedbacks() {
        // Qualquer um pode listar os feedbacks
        return ResponseEntity.ok(feedbackService.listar());
    }
}
