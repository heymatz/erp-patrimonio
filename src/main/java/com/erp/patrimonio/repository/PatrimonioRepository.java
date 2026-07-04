package com.erp.patrimonio.repository;

import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepository {

    private final List<Patrimonio> patrimonios;

    public PatrimonioRepository() {
        patrimonios = new ArrayList<>();
    }

    public void salvar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new IllegalArgumentException("O patrimônio não pode ser nulo.");
        }
        patrimonios.add(patrimonio);
    }

    public List<Patrimonio> listarTodos() {
        return new ArrayList<>(patrimonios);
    }

    public Patrimonio buscarPorId(int id) {
        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getId() == id) {
                return patrimonio;
            }
        }
        return null;
    }

    public void remover(int id) {
        if (buscarPorId(id) == null) {
            throw new IllegalArgumentException("O patrimônio com o ID " + id + " não existe.");
        }
        patrimonios.removeIf(patrimonio -> patrimonio.getId() == id);
    }

    public void atualizar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new IllegalArgumentException(
                    "O patrimônio não pode ser nulo."
            );
        }
        for (int i = 0; i < patrimonios.size(); i++) {
            if (patrimonios.get(i).getId() == patrimonio.getId()) {
                patrimonios.set(i, patrimonio);
                return;
            }
        }
        throw new IllegalArgumentException(
                "Patrimônio não encontrado."
        );
    }

    public Patrimonio buscarPorNumeroSerie(String numeroSerie) {
        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new IllegalArgumentException("O número de série é obrigatório.");
        }
        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getNumeroSerie().equals(numeroSerie)) {
                return patrimonio;
            }
        }
        return null;
    }
}
