package com.finanfe_api.controller;

import com.finanfe_api.dto.TransacaoDTO;
import com.finanfe_api.model.TipoTransacao;
import com.finanfe_api.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    /**
     * GET /api/transacoes
     * GET /api/transacoes?tipo=DESPESA
     * GET /api/transacoes?inicio=2024-01-01&fim=2024-01-31
     * GET /api/transacoes?categoriaId=1
     */
    @GetMapping
    public ResponseEntity<List<TransacaoDTO>> listar(
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long categoriaId) {

        if (tipo != null && inicio != null && fim != null) {
            return ResponseEntity.ok(transacaoService.listarPorPeriodo(inicio, fim)
                    .stream().filter(t -> t.getTipo() == tipo).toList());
        } else if (inicio != null && fim != null) {
            return ResponseEntity.ok(transacaoService.listarPorPeriodo(inicio, fim));
        } else if (tipo != null) {
            return ResponseEntity.ok(transacaoService.listarPorTipo(tipo));
        } else if (categoriaId != null) {
            return ResponseEntity.ok(transacaoService.listarPorCategoria(categoriaId));
        }

        return ResponseEntity.ok(transacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TransacaoDTO> criar(@Valid @RequestBody TransacaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoDTO> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody TransacaoDTO dto) {
        return ResponseEntity.ok(transacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        transacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

