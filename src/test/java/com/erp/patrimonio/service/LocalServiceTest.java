package com.erp.patrimonio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.repository.LocalRepository;

class LocalServiceTest {

    private LocalRepository localRepository;
    private LocalService localService;

    @BeforeEach
    void setUp() {
        localRepository = new LocalRepository();
        localService = new LocalService(localRepository);
    }

    @Test
    void deveCadastrarLocalComSucesso() {

        Local local = localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        assertEquals(1, local.getId());
        assertEquals("Armazém 01", local.getNome());
        assertEquals(
                "Local de produtos eletrônicos",
                local.getDescricao()
        );

        assertEquals(1, localRepository.listarTodos().size());
    }

    @Test
    void naoDeveCadastrarLocalDuplicado() {

        // Arrange
        localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        // Act + Assert
        assertThrows(
                DuplicidadeException.class,
                () -> localService.cadastrar(
                        "Armazém 01",
                        "Local de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComNomeMaiorQue100Caracteres() {

        String nome = "A".repeat(101);

        assertThrows(ValidacaoException.class,
                () -> localService.cadastrar(
                        nome,
                        "Local de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComNomeVazio() {
        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        "",
                        "Local de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComNomeEmBranco() {
        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        " ",
                        "Local de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComNomeNulo() {
        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        null,
                        "Local de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComDescricaoMaiorQue255Caracteres() {

        String descricao = "A".repeat(256);

        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        "Armazém 01",
                        descricao
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComDescricaoVazia() {
        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        "Armazém 01",
                        ""
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComDescricaoEmBranco() {
        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        "Armazém 01",
                        " "
                )
        );
    }

    @Test
    void naoDeveCadastrarLocalComDescricaoNula() {
        assertThrows(
                ValidacaoException.class,
                () -> localService.cadastrar(
                        "Armazém 01",
                        null
                )
        );
    }

    @Test
    void deveBuscarLocalPorId() {

        Local local = localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        Local encontrada = localService.buscarPorId(local.getId());

        assertEquals(local.getId(), encontrada.getId());
        assertEquals("Armazém 01", encontrada.getNome());
        assertEquals("Local de produtos eletrônicos", encontrada.getDescricao());
    }

    @Test
    void naoDeveBuscarLocalInexistente() {
        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> localService.buscarPorId(999)
        );
    }

    @Test
    void deveAtualizarLocalComSucesso() {

        Local criada = localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        Local local = localService.atualizar(
                criada.getId(),
                "Armazém 02",
                "Local de peças de reposição"
        );

        assertEquals(criada.getId(), local.getId());
        assertEquals("Armazém 02", local.getNome());
        assertEquals(
                "Local de peças de reposição",
                local.getDescricao()
        );
    }

    @Test
    void naoDeveAtualizarLocalDuplicado() {

        Local local1 = localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        Local local2 = localService.cadastrar(
                "Armazém 02",
                "Local de peças de reposição"
        );

        assertThrows(
                DuplicidadeException.class,
                () -> localService.atualizar(
                        local2.getId(),
                        "Armazém 01",
                        "Local de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveAtualizarLocalInexistente() {
        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> localService.atualizar(
                        999,
                        "Armazém 02",
                        "Local de peças de reposição"
                )
        );
    }

    @Test
    void deveRemoverLocalComSucesso() {
        Local local = localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        localService.remover(local.getId());

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> localService.buscarPorId(local.getId())
        );

    }

    @Test
    void naoDeveRemoverLocalInexistente() {
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> localService.remover(999)
        );
    }

    @Test
    void deveListarLocais() {

        localService.cadastrar(
                "Armazém 01",
                "Local de produtos eletrônicos"
        );

        localService.cadastrar(
                "Armazém 02",
                "Local de peças de reposição"
        );

        assertEquals(2, localService.listarTodos().size());
    }
}
