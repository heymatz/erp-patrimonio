package com.erp.patrimonio.model;

import java.time.LocalDateTime;

public class HistoricoMov {

    private Integer id;
    private Patrimonio patrimonio;
    private Local localOrigem;
    private Local localDestino;
    private LocalDateTime dataMovimentacao;
    private String motivo;

    // Construtor vazio para frameworks e bibliotecas que necessitam de um construtor padrão
    public HistoricoMov() {
    }

    // Construtor com parâmetros para facilitar a criação de objetos HistoricoMovimentacao
    public HistoricoMov(Patrimonio patrimonio, Local localOrigem, Local localDestino, String motivo) {
        this.patrimonio = patrimonio;
        this.localOrigem = localOrigem;
        this.localDestino = localDestino;
        this.dataMovimentacao = LocalDateTime.now(); // Define a data e hora atual como a data de movimentação
        this.motivo = motivo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patrimonio getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(Patrimonio patrimonio) {
        this.patrimonio = patrimonio;
    }

    public Local getLocalOrigem() {
        return localOrigem;
    }

    public void setLocalOrigem(Local localOrigem) {
        this.localOrigem = localOrigem;
    }

    public Local getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(Local localDestino) {
        this.localDestino = localDestino;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
