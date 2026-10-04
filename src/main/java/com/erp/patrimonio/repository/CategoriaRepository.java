package com.erp.patrimonio.repository;

import java.util.List;

import com.erp.patrimonio.model.Categoria;

public interface CategoriaRepository {

    void salvar(Categoria categoria);

    boolean atualizar(Categoria categoria);

    boolean remover(int id);

    Categoria buscarPorNome(String nome);

    Categoria buscarPorDescricao(String descricao);

    Categoria buscarPorId(int id);

    List<Categoria> listarTodos();

    boolean isCategoriaEmUso(int id); // Método para verificar o uso da categoria em patrimônios
}
