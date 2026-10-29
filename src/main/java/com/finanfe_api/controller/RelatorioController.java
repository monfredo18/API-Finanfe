package com.finanfe_api.controller;

import com.finanfe_api.dto.RelatorioDTO;
import com.finanfe_api.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    /**
     * GET /api/relatorios/mensal
     * GET /api/relatorios/mensal?ano=2024&mes=10
     */
    @GetMapping("/mensal")
    public ResponseEntity<RelatorioDTO> relatorioMensal(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        if (ano == null) ano = LocalDate.now().getYear();
        if (mes == null) mes = LocalDate.now().getMonthValue();
        return ResponseEntity.ok(relatorioService.relatoriMensal(ano, mes));
    }

    /**
     * GET /api/relatorios/semanal?referencia=2024-10-28
     */
    @GetMapping("/semanal")
    public ResponseEntity<RelatorioDTO> relatorioSemanal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referencia) {
        if (referencia == null) referencia = LocalDate.now();
        return ResponseEntity.ok(relatorioService.relatorioSemanal(referencia));
    }

    /**
     * GET /api/relatorios/periodo?inicio=2024-01-01&fim=2024-12-31
     */
    @GetMapping("/periodo")
    public ResponseEntity<RelatorioDTO> relatorioPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(relatorioService.relatorioPeriodo(inicio, fim));
    }
}
