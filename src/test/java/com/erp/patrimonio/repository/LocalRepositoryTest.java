package com.erp.patrimonio.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Local;

public class LocalRepositoryTest {

    private LocalRepository localRepository;
    private Local criarLocal() {
        return new Local(
            1,
            "Sala 101",
            "Primeiro andar"
        );
    }

    @BeforeEach
    void setUp() {
        localRepository = new LocalRepository();
    }

    @Test
    void deveSalvarLocalComDadosValidos() {
        Local local = criarLocal();
        localRepository.salvar(local);

        assertEquals(1, localRepository.listarTodos().size());
    }

    @Test
    void deveLancarExcecaoQuandoSalvarLocalNulo() {
        assertThrows(
                ValidacaoException.class,
                () -> localRepository.salvar(null)
        );
    }

    @Test
    void deveAtualizarLocalExistente() {
        Local local = criarLocal();
        localRepository.salvar(local);

        local.setNome("Sala 101 atualizada");

        boolean atualizado = localRepository.atualizar(local);

        assertTrue(atualizado);
        assertEquals(
                "Sala 101 atualizada",
                localRepository.buscarPorId(1).getNome()
        );
    }

    @Test
    void deveRemoverLocalExistente() {
        Local local = criarLocal();
        localRepository.salvar(local);

        boolean removido = localRepository.remover(1);

        assertTrue(removido);
        assertEquals(0, localRepository.listarTodos().size());
    }

    @Test
    void deveBuscarLocalPorIdExistente() {
        Local local = criarLocal();
        localRepository.salvar(local);
        Local encontrado = localRepository.buscarPorId(1);
        assertEquals(local, encontrado);
    }

    @Test
    void deveListarTodosOsLocaisExistentes() {
        Local local1 = criarLocal();

        Local local2 = new Local(
                2,
                "Sala 201",
                "Segundo andar"
        );

        localRepository.salvar(local1);
        localRepository.salvar(local2);

        List<Local> locals = localRepository.listarTodos();

        assertEquals(2, locals.size());
        assertEquals("Sala 101", locals.get(0).getNome());
        assertEquals("Sala 201", locals.get(1).getNome());
        assertEquals("Primeiro andar", locals.get(0).getDescricao());
        assertEquals("Segundo andar", locals.get(1).getDescricao());
    }

    @Test
    void deveRetornarFalseAoRemoverLocalInexistente() {
        boolean removido = localRepository.remover(99);

        assertFalse(removido);
    }

    @Test
    void deveRetornarFalseAoAtualizarLocalInexistente() {
        Local local = criarLocal();

        boolean atualizado = localRepository.atualizar(local);

        assertFalse(atualizado);
    }

    @Test
    void deveRetornarNullAoBuscarLocalInexistente() {
        Local local = localRepository.buscarPorId(99);

        assertNull(local);
    }

}
