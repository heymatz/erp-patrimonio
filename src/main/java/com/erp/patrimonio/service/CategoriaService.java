package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.repository.CategoriaRepository;

public class CategoriaService {

    private static final String ERRO_CATEGORIA_NAO_ENCONTRADA
            = "Categoria não encontrada.";

    private static final String ERRO_CATEGORIA_DUPLICADA
            = "Já existe uma categoria com esse nome.";

    private static final String ERRO_FALHA_ATUALIZACAO
            = "Falha ao atualizar a categoria. O registro pode ter sido alterado ou removido.";
            
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Categoria cadastrar(String nome, String descricao) {

        if (repository.buscarPorNome(nome) != null) {
            throw new DuplicidadeException(ERRO_CATEGORIA_DUPLICADA);
        }

        int id = repository.gerarProximoId();

        Categoria categoria = new Categoria(id, nome, descricao);

        repository.salvar(categoria);

        return categoria;
    }

    public Categoria atualizar(int id, String nome, String descricao) {

        Categoria categoria = buscarPorId(id);

        Categoria existente = repository.buscarPorNome(nome);

        if (existente != null && existente.getId() != id) {
            throw new DuplicidadeException(ERRO_CATEGORIA_DUPLICADA);
        }

        categoria.setNome(nome);
        categoria.setDescricao(descricao);

        boolean atualizado = repository.atualizar(categoria);

        if (!atualizado) {
            throw new EstadoInvalidoException(ERRO_FALHA_ATUALIZACAO);
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
            throw new EntidadeNaoEncontradaException(ERRO_CATEGORIA_NAO_ENCONTRADA);
        }

        return categoria;
    }

    public List<Categoria> listarTodos() {
        return repository.listarTodos();
    }
}
