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

    public boolean atualizar(Patrimonio patrimonio) {
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

    public Patrimonio buscarPorId(int id) {
        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getId() == id) {
                return patrimonio;
            }
        }
        return null;
    }

    public Patrimonio buscarPorNumeroSerie(String numeroSerie) {
        for (Patrimonio patrimonio : patrimonios) {
            if (patrimonio.getNumeroSerie().equals(numeroSerie)) {
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
