package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.PatrimonioRepository;

public class PatrimonioService {

    private static final String ERRO_NUMERO_SERIE_DUPLICADO
            = "Número de série já cadastrado.";

    private static final String ERRO_PATRIMONIO_NAO_ENCONTRADO
            = "Patrimônio não encontrado.";
            
    private static final String ERRO_FALHA_ATUALIZACAO
            = "Falha ao atualizar patrimônio.";

    private final PatrimonioRepository repository;

    public PatrimonioService(PatrimonioRepository repository) {
        this.repository = repository;
    }

    public Patrimonio cadastrar(
            String nome,
            String descricao,
            Categoria categoria,
            Local local,
            String numeroSerie,
            double valor) {

        Patrimonio existente = repository.buscarPorNumeroSerie(numeroSerie);

        if (existente != null) {
            throw new IllegalArgumentException(ERRO_NUMERO_SERIE_DUPLICADO);
        }

        int id = repository.gerarProximoId();
        Patrimonio patrimonio = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor
        );
        repository.salvar(patrimonio);
        return patrimonio;
    }

    public void remover(int id) {
        buscarPorId(id);
        repository.remover(id);
    }

    public List<Patrimonio> listarTodos() {
        return repository.listarTodos();
    }

    public Patrimonio buscarPorId(int id) {
        Patrimonio patrimonio = repository.buscarPorId(id);
        if (patrimonio == null) {
            throw new IllegalArgumentException(ERRO_PATRIMONIO_NAO_ENCONTRADO);
        }
        return patrimonio;
    }

    public Patrimonio atualizar(
            int id,
            String nome,
            String descricao,
            Categoria categoria,
            Local local,
            String numeroSerie,
            double valor) {

        buscarPorId(id);

        Patrimonio existente = repository.buscarPorNumeroSerie(numeroSerie);
        if (existente != null && existente.getId() != id) {
            throw new IllegalArgumentException(ERRO_NUMERO_SERIE_DUPLICADO);
        }

        Patrimonio atualizado = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor
        );

        boolean atualizadoComSucesso = repository.atualizar(atualizado);
        if (!atualizadoComSucesso) {
            throw new IllegalStateException(ERRO_FALHA_ATUALIZACAO);
        }
        return atualizado;
    }
}
