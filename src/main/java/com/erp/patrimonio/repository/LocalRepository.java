package com.erp.patrimonio.repository;

import java.util.List;

import com.erp.patrimonio.model.Local;

public interface LocalRepository {

    void salvar(Local local);

    boolean atualizar(Local local);

    boolean remover(int id);

    Local buscarPorNome(String nome);

    Local buscarPorDescricao(String descricao);

    Local buscarPorId(int id);

    List<Local> listarTodos();
}
