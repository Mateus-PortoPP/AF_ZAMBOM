package com.insper.prova.ferramenta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ferramentas")
public class FerramentaController {

    @Autowired
    private FerramentaService ferramentaService;

    @PostMapping
    public ResponseEntity<Ferramenta> criar(
            @RequestHeader("email") String email,
            @RequestBody Ferramenta ferramenta
    ) {
        return ResponseEntity.ok(ferramentaService.cadastrar(email, ferramenta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @RequestHeader("email") String email,
            @PathVariable String id
    ) {
        ferramentaService.deletar(email, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Ferramenta>> listar() {
        return ResponseEntity.ok(ferramentaService.listar());
    }
}