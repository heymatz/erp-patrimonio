package com.erp.patrimonio.repository;

import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryInMemory implements PatrimonioRepository {

    private int proximoId = 1;
    private final List<Patrimonio> patrimonios;

    public PatrimonioRepositoryInMemory() {
        patrimonios = new ArrayList<>();
    }

    public void salvar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new ValidacaoException("Patrimônio não pode ser nulo.");
        }

        patrimonios.add(patrimonio);
    }

    public boolean atualizar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new ValidacaoException("Patrimônio não pode ser nulo.");
        }

        for (int i = 0; i < patrimonios.size(); i++) {
            if (patrimonios.get(i).getId() == patrimonio.getId()) {
                patrimonios.set(i, patrimonio);
                return true;
            }
        }
        return false;
    }

    public boolean remover(int id) {
        return patrimonios.removeIf(patrimonio -> patrimonio.getId() == id);
    }

    public Patrimonio buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }

        nome = nome.trim();

        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getNome().equalsIgnoreCase(nome)) {
                return patrimonio;
            }
        }
        return null;
    }

    public Patrimonio buscarPorId(int id) {
        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getId() == id) {
                return patrimonio;
            }
        }
        return null;
    }

    public Patrimonio buscarPorNumeroSerie(String numeroSerie) {
        if (numeroSerie == null || numeroSerie.isBlank()) {
            return null;
        }

        numeroSerie = numeroSerie.trim();

        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getNumeroSerie().equalsIgnoreCase(numeroSerie)) {
                return patrimonio;
            }
        }

        return null;
    }

    public List<Patrimonio> listarTodos() {
        return new ArrayList<>(patrimonios);
    }

    public int gerarProximoId() {
        return proximoId++;
    }
}
