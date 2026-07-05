package com.erp.patrimonio.repository;

import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepository {

    private int proximoId = 1;
    private final List<Patrimonio> patrimonios;

    public PatrimonioRepository() {
        patrimonios = new ArrayList<>();
    }

    public void salvar(Patrimonio patrimonio) {
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
        patrimonios.removeIf(patrimonio -> patrimonio.getId() == id);
    }

    public boolean atualizar(Patrimonio patrimonio) {
        for (int i = 0; i < patrimonios.size(); i++) {
            if (patrimonios.get(i).getId() == patrimonio.getId()) {
                patrimonios.set(i, patrimonio);
                return true;
            }
        }
        return false;
    }

    public Patrimonio buscarPorNumeroSerie(String numeroSerie) {
        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getNumeroSerie().equals(numeroSerie)) {
                return patrimonio;
            }
        }
        return null;
    }

    public int gerarProximoId() {
        return proximoId++;
    }
}
