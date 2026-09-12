package com.erp.patrimonio.repository;

import java.util.List;

import com.erp.patrimonio.model.HistoricoMov;

public interface HistoricoMovRepository {
    void salvar(HistoricoMov historico);
    List<HistoricoMov> listarPorPatrimonio(int patrimonioId);
}
