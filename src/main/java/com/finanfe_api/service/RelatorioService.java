package com.finanfe_api.service;

import com.finanfe_api.dto.RelatorioDTO;
import com.finanfe_api.model.TipoTransacao;
import com.finanfe_api.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RelatorioService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    /**
     * Relatório do mês atual
     */
    public RelatorioDTO relatoriMensal(int ano, int mes) {
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        String periodo = String.format("%02d/%d", mes, ano);
        return gerarRelatorio(periodo, inicio, fim);
    }

    /**
     * Relatório da semana atual (baseado em uma data de referência)
     */
    public RelatorioDTO relatorioSemanal(LocalDate referencia) {
        // Semana começa na segunda-feira
        LocalDate inicio = referencia.with(java.time.DayOfWeek.MONDAY);
        LocalDate fim = referencia.with(java.time.DayOfWeek.SUNDAY);

        String periodo = String.format("Semana %s a %s",
                inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return gerarRelatorio(periodo, inicio, fim);
    }

    /**
     * Relatório de período customizado
     */
    public RelatorioDTO relatorioPeriodo(LocalDate inicio, LocalDate fim) {
        String periodo = String.format("%s a %s",
                inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return gerarRelatorio(periodo, inicio, fim);
    }

    private RelatorioDTO gerarRelatorio(String periodo, LocalDate inicio, LocalDate fim) {
        BigDecimal totalReceitas = transacaoRepository.somarPorTipoEPeriodo(TipoTransacao.RECEITA, inicio, fim);
        BigDecimal totalDespesas = transacaoRepository.somarPorTipoEPeriodo(TipoTransacao.DESPESA, inicio, fim);

        List<Object[]> rawGastos = transacaoRepository.totalPorCategoriaEPeriodo(inicio, fim);
        List<RelatorioDTO.GastoPorCategoriaDTO> gastosPorCategoria = new ArrayList<>();

        for (Object[] row : rawGastos) {
            String categoriaNome = row[0] != null ? (String) row[0] : "Sem categoria";
            BigDecimal total = (BigDecimal) row[1];
            double percentual = totalDespesas.compareTo(BigDecimal.ZERO) > 0
                    ? total.divide(totalDespesas, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;
            gastosPorCategoria.add(new RelatorioDTO.GastoPorCategoriaDTO(categoriaNome, total, percentual));
        }

        return new RelatorioDTO(periodo, totalReceitas, totalDespesas, gastosPorCategoria);
    }
}

