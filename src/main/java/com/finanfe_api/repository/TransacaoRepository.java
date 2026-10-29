package com.finanfe_api.repository;

import com.finanfe_api.model.TipoTransacao;
import com.finanfe_api.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // Filtrar por tipo
    List<Transacao> findByTipo(TipoTransacao tipo);

    // Filtrar por categoria
    List<Transacao> findByCategoriaId(Long categoriaId);

    // Filtrar por período
    List<Transacao> findByDataBetweenOrderByDataDesc(LocalDate inicio, LocalDate fim);

    // Filtrar por tipo e período
    List<Transacao> findByTipoAndDataBetween(TipoTransacao tipo, LocalDate inicio, LocalDate fim);

    // Total gasto por tipo no período
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.tipo = :tipo AND t.data BETWEEN :inicio AND :fim")
    BigDecimal somarPorTipoEPeriodo(@Param("tipo") TipoTransacao tipo,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fim") LocalDate fim);

    // Gastos agrupados por categoria num período
    @Query("SELECT t.categoria.nome, COALESCE(SUM(t.valor), 0) FROM Transacao t " +
           "WHERE t.tipo = 'DESPESA' AND t.data BETWEEN :inicio AND :fim " +
           "GROUP BY t.categoria.nome ORDER BY SUM(t.valor) DESC")
    List<Object[]> totalPorCategoriaEPeriodo(@Param("inicio") LocalDate inicio,
                                              @Param("fim") LocalDate fim);

    // Gastos mensais agrupados por ano/mês
    @Query("SELECT YEAR(t.data), MONTH(t.data), COALESCE(SUM(t.valor), 0) FROM Transacao t " +
           "WHERE t.tipo = :tipo " +
           "GROUP BY YEAR(t.data), MONTH(t.data) ORDER BY YEAR(t.data) DESC, MONTH(t.data) DESC")
    List<Object[]> totaisMensaisPorTipo(@Param("tipo") TipoTransacao tipo);
}

