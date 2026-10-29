package com.finanfe_api.dto;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioDTO {

    private String periodo;
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldo;
    private List<GastoPorCategoriaDTO> gastosPorCategoria;

    // Constructors
    public RelatorioDTO() {}

    public RelatorioDTO(String periodo, BigDecimal totalReceitas, BigDecimal totalDespesas,
                        List<GastoPorCategoriaDTO> gastosPorCategoria) {
        this.periodo = periodo;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldo = totalReceitas.subtract(totalDespesas);
        this.gastosPorCategoria = gastosPorCategoria;
    }

    // Getters & Setters
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public BigDecimal getTotalReceitas() { return totalReceitas; }
    public void setTotalReceitas(BigDecimal totalReceitas) { this.totalReceitas = totalReceitas; }

    public BigDecimal getTotalDespesas() { return totalDespesas; }
    public void setTotalDespesas(BigDecimal totalDespesas) { this.totalDespesas = totalDespesas; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public List<GastoPorCategoriaDTO> getGastosPorCategoria() { return gastosPorCategoria; }
    public void setGastosPorCategoria(List<GastoPorCategoriaDTO> gastosPorCategoria) {
        this.gastosPorCategoria = gastosPorCategoria;
    }

    // Inner DTO
    public static class GastoPorCategoriaDTO {
        private String categoria;
        private BigDecimal total;
        private double percentual;

        public GastoPorCategoriaDTO() {}

        public GastoPorCategoriaDTO(String categoria, BigDecimal total, double percentual) {
            this.categoria = categoria;
            this.total = total;
            this.percentual = percentual;
        }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }

        public double getPercentual() { return percentual; }
        public void setPercentual(double percentual) { this.percentual = percentual; }
    }
}

