package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.repository.LocalRepository;

public class LocalService {

    private static final String ERRO_LOCAL_NAO_ENCONTRADO
            = "Local não encontrado."; 
                           
    private static final String ERRO_FALHA_ATUALIZACAO
            = "Falha ao atualizar local.";

    private final LocalRepository repository;

    public LocalService(LocalRepository repository) {
        this.repository = repository;
    }

    public Local cadastrar(String nome, String descricao) {
        int id = repository.gerarProximoId();

        Local local = new Local(id, nome, descricao);

        repository.salvar(local);

        return local;
    }

    public Local atualizar(int id, String nome, String descricao) {
        Local local = buscarPorId(id);

        local.setNome(nome);
        local.setDescricao(descricao);

        boolean atualizado = repository.atualizar(local);

        if (!atualizado) {
            throw new IllegalStateException(ERRO_FALHA_ATUALIZACAO);
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
            throw new IllegalArgumentException(ERRO_LOCAL_NAO_ENCONTRADO);
        }
        return local;
    }

    public List<Local> listarTodos() {
        return repository.listarTodos();
    }
}
