package com.erp.patrimonio.repository;

import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.model.Categoria;

public class CategoriaRepository {

    private int proximoId = 1;
    private final List<Categoria> categorias;

    public CategoriaRepository() {
        categorias = new ArrayList<>();
    }

    public void salvar(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }
        categorias.add(categoria);
    }

    public boolean atualizar(Categoria categoria) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == categoria.getId()) {
                categorias.set(i, categoria);
                return true;
            }
        }
        return false;
    }

    public boolean remover(int id) {
        return categorias.removeIf(categoria -> categoria.getId() == id);
    }

    public Categoria buscarPorId(int id) {
        for (Categoria categoria : categorias) {
            if (categoria.getId() == id) {
                return categoria;
            }
        }
        return null;
    }

    public List<Categoria> listarTodos() {
        return new ArrayList<>(categorias);
    }

    public int gerarProximoId() {
        return proximoId++;
    }
}
