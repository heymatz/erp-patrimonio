package com.erp.patrimonio.repository;

import java.util.List;

import com.erp.patrimonio.model.Patrimonio;

public interface PatrimonioRepository {

    void salvar(Patrimonio patrimonio);

    boolean atualizar(Patrimonio patrimonio);

    boolean remover(int id);

    Patrimonio buscarPorNome(String nome);

    Patrimonio buscarPorId(int id);

    Patrimonio buscarPorNumeroSerie(String numeroSerie);

    List<Patrimonio> listarTodos();
}
