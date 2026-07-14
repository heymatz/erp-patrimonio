package com.erp.patrimonio.repository;

import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.model.Local;

public class LocalRepository {

    private int proximoId = 1;
    private final List<Local> locais;

    public LocalRepository() {
        locais = new ArrayList<>();
    }

    public void salvar(Local local) {
        if (local == null) {
            throw new IllegalArgumentException("Local não pode ser nulo.");
        }

        locais.add(local);
    }
    
    public boolean atualizar(Local local) {
        for (int i = 0; i < locais.size(); i++) {
            if (locais.get(i).getId() == local.getId()) {
                locais.set(i, local);
                return true;
            }
        }
        return false;
    }

    public boolean remover(int id) {
        return locais.removeIf(local -> local.getId() == id);
    }

    public Local buscarPorNome(String nome) {
        if (nome == null) {
            return null;
        }

        nome = nome.trim();

        for (Local local : locais) {
            if (local.getNome().equalsIgnoreCase(nome)) {
                return local;
            }
        }
        return null;
    }

    public Local buscarPorId(int id) {
        for (Local local : locais) {
            if (local.getId() == id) {
                return local;
            }
        }
        return null;
    }

    public List<Local> listarTodos() {
        return new ArrayList<>(locais);
    }

    public int gerarProximoId() {
        return proximoId++;
    }
}
