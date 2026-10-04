package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.enums.TipoItem;
import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.repository.CategoriaRepository;

public class CategoriaService {

    private static final String ERRO_CATEGORIA_NAO_ENCONTRADA = "Categoria não encontrada.";

    private static final String ERRO_CATEGORIA_DUPLICADA = "Já existe uma categoria com esse nome.";

    private static final String ERRO_FALHA_ATUALIZACAO = "Falha ao atualizar a categoria. O registro pode ter sido alterado ou removido.";

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Categoria cadastrar(String nome, String descricao, TipoItem tipoItem) {

        if (repository.buscarPorNome(nome) != null) {
            throw new DuplicidadeException(ERRO_CATEGORIA_DUPLICADA);
        }

        Categoria categoria = new Categoria(0, nome, descricao, tipoItem);

        repository.salvar(categoria);

        return categoria;
    }

    public Categoria buscarPorId(int id) {

        Categoria categoria = repository.buscarPorId(id);

        if (categoria == null) {
            throw new EntidadeNaoEncontradaException(ERRO_CATEGORIA_NAO_ENCONTRADA);
        }

        return categoria;
    }

    public List<Categoria> listarTodos() {
        List<Categoria> categorias = repository.listarTodos();
        // Se o banco retornar null, devolvemos uma lista vazia para evitar
        // NullPointerException
        return categorias != null ? categorias : java.util.Collections.emptyList();
    }

public Categoria atualizar(int id, String nome, String descricao, TipoItem novoTipoItem) {

        Categoria categoriaAtual = buscarPorId(id);
        Categoria existente = repository.buscarPorNome(nome);

        if (existente != null && existente.getId() != id) {
            throw new DuplicidadeException(ERRO_CATEGORIA_DUPLICADA);
        }

        // Se o usuário tentar mudar o tipo e a categoria tem itens cadastrados, bloqueia a operação e lança uma exceção
        if (categoriaAtual.getTipoItem() != novoTipoItem) {
            if (repository.isCategoriaEmUso(id)) {
                throw new ValidacaoException(
                    "TRAVA DE SEGURANÇA: Não é permitido alterar o Tipo de uma categoria que já possui itens vinculados. " +
                    "Para alterar, exclua os itens vinculados primeiro ou crie uma nova categoria."
                );
            }
        }

        categoriaAtual.setNome(nome);
        categoriaAtual.setDescricao(descricao);
        categoriaAtual.setTipoItem(novoTipoItem);

        boolean atualizado = repository.atualizar(categoriaAtual);
        if (!atualizado) {
            throw new EstadoInvalidoException(ERRO_FALHA_ATUALIZACAO);
        }

        return categoriaAtual;
    }

    public void remover(int id) {
        // Aproveita o buscarPorId para lançar exceção se não existir antes de mandar deletar
        buscarPorId(id);
        repository.remover(id);
    }
}
