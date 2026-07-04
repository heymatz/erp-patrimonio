package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.PatrimonioRepository;

public class PatrimonioService {

    private final PatrimonioRepository repository;

    public PatrimonioService(PatrimonioRepository repository) {
        this.repository = repository;
    }

    public void cadastrar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new IllegalArgumentException("O patrimônio não pode ser nulo.");
        }

        Patrimonio existente = repository.buscarPorNumeroSerie(patrimonio.getNumeroSerie());

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Número de série já cadastrado."
            );
        }
        repository.salvar(patrimonio);

    }

    public void atualizar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new IllegalArgumentException("O patrimônio não pode ser nulo.");
        }

        Patrimonio existente = repository.buscarPorNumeroSerie(patrimonio.getNumeroSerie());

        if (existente != null && existente.getId() != patrimonio.getId()) {
            throw new IllegalArgumentException(
                    "Número de série já cadastrado."
            );
        }
        repository.atualizar(patrimonio);
    }

    public void remover(int id) {
        Patrimonio existente = repository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException(
                    "Patrimônio não encontrado."
            );
        }
        repository.remover(id);
    }

    public List<Patrimonio> listarTodos() {
        return repository.listarTodos();
    }

    public Patrimonio buscarPorId(int id) {
        Patrimonio existente = repository.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException(
                    "Patrimônio não encontrado."
            );
        }
        return existente;
    }

}
