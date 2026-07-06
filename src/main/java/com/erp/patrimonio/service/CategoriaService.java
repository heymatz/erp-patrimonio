package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.repository.CategoriaRepository;

public class CategoriaService {

    private static final String ERRO_CATEGORIA_NAO_ENCONTRADA
            = "Categoria não encontrada.";

    private static final String ERRO_FALHA_ATUALIZACAO
            = "Falha ao atualizar categoria.";

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Categoria cadastrar(String nome, String descricao) {

        int id = repository.gerarProximoId();

        Categoria categoria = new Categoria(id, nome, descricao);

        repository.salvar(categoria);

        return categoria;
    }

    public Categoria atualizar(int id, String nome, String descricao) {

        Categoria categoria = buscarPorId(id);

        categoria.setNome(nome);
        categoria.setDescricao(descricao);

        boolean atualizado = repository.atualizar(categoria);

        if (!atualizado) {
            throw new IllegalStateException(ERRO_FALHA_ATUALIZACAO);
        }

        return categoria;
    }

    public void remover(int id) {
        buscarPorId(id);
        repository.remover(id);
    }

    public Categoria buscarPorId(int id) {

        Categoria categoria = repository.buscarPorId(id);

        if (categoria == null) {
            throw new IllegalArgumentException(ERRO_CATEGORIA_NAO_ENCONTRADA);
        }

        return categoria;
    }

    public List<Categoria> listarTodos() {
        return repository.listarTodos();
    }
}
