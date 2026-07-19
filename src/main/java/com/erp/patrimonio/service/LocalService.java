package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.repository.LocalRepository;

public class LocalService {

    private static final String ERRO_LOCAL_NAO_ENCONTRADO
            = "Local não encontrado.";

    private static final String ERRO_LOCAL_DUPLICADO
            = "Já existe um local com esse nome.";

    private static final String ERRO_FALHA_ATUALIZACAO
            = "Falha ao atualizar local. O registro pode ter sido alterado ou removido.";

    private final LocalRepository repository;

    public LocalService(LocalRepository repository) {
        this.repository = repository;
    }

    public Local cadastrar(String nome, String descricao) {

        if (repository.buscarPorNome(nome) != null) {
            throw new DuplicidadeException(ERRO_LOCAL_DUPLICADO);
        }

        int id = repository.gerarProximoId();

        Local local = new Local(id, nome, descricao);

        repository.salvar(local);

        return local;
    }

    public Local atualizar(int id, String nome, String descricao) {
        Local local = buscarPorId(id);
        Local existente = repository.buscarPorNome(nome);

        if (existente != null && existente.getId() != id) {
            throw new DuplicidadeException(ERRO_LOCAL_DUPLICADO);
        }

        local.setNome(nome);
        local.setDescricao(descricao);

        boolean atualizado = repository.atualizar(local);

        if (!atualizado) {
            throw new EstadoInvalidoException(ERRO_FALHA_ATUALIZACAO);
        }
        return local;

    }

    public void remover(int id) {
        buscarPorId(id);
        repository.remover(id);
    }

    public Local buscarPorId(int id) {
        Local local = repository.buscarPorId(id);
        if (local == null) {
            throw new EntidadeNaoEncontradaException(ERRO_LOCAL_NAO_ENCONTRADO);
        }
        return local;
    }

    public List<Local> listarTodos() {
        return repository.listarTodos();
    }
}
