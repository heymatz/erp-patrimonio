package com.erp.patrimonio.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.PatrimonioRepository;
import com.erp.patrimonio.repository.PatrimonioRepositoryInMemory;

class PatrimonioServiceTest {

    private PatrimonioRepository patrimonioRepository;
    private PatrimonioService patrimonioService;

    private Categoria categoria;
    private Local local;

    @BeforeEach
    void configurar() {
        this.patrimonioRepository = new PatrimonioRepositoryInMemory(); 
        this.patrimonioService = new PatrimonioService(this.patrimonioRepository);

        this.categoria = new Categoria(
                1,
                "Eletrônicos",
                "Equipamentos de informática"
        );

        this.local = new Local(
                1,
                "Sala 101",
                "Primeiro andar"
        );
    }

    private Patrimonio cadastrarPatrimonio() {

        return patrimonioService.cadastrar(
                "Notebook",
                "Notebook Dell",
                categoria,
                local,
                "SN123456",
                5000.00,
                UnidadeMedida.UNIDADE
        );
    }

    @Test
    void deveCadastrarPatrimonioComDadosValidos() {

        Patrimonio patrimonio = cadastrarPatrimonio();

        assertNotNull(patrimonio);
        assertEquals(1, patrimonio.getId());
        assertEquals("Notebook", patrimonio.getNome());
        assertEquals("Notebook Dell", patrimonio.getDescricao());
        assertEquals(categoria, patrimonio.getCategoria());
        assertEquals(local, patrimonio.getLocal());
        assertEquals("SN123456", patrimonio.getNumeroSerie());
        assertEquals(5000.00, patrimonio.getValor());
        assertEquals(
                UnidadeMedida.UNIDADE,
                patrimonio.getUnidadeMedida()
        );
    }

    @Test
    void deveGerarIdAutomaticamenteAoCadastrarPatrimonios() {

        Patrimonio patrimonio1 = patrimonioService.cadastrar(
                "Notebook",
                "Notebook Dell",
                categoria,
                local,
                "SN123456",
                5000.00,
                UnidadeMedida.UNIDADE
        );

        Patrimonio patrimonio2 = patrimonioService.cadastrar(
                "Computador",
                "Computador Dell",
                categoria,
                local,
                "SN654321",
                4000.00,
                UnidadeMedida.UNIDADE
        );

        assertEquals(1, patrimonio1.getId());
        assertEquals(2, patrimonio2.getId());
    }

    @Test
    void deveLancarExcecaoQuandoNomeEstiverDuplicado() {

        cadastrarPatrimonio();

        assertThrows(
                DuplicidadeException.class,
                () -> patrimonioService.cadastrar(
                        "Notebook",
                        "Outro notebook",
                        categoria,
                        local,
                        "SN999999",
                        4500.00,
                        UnidadeMedida.UNIDADE
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoNumeroSerieEstiverDuplicado() {

        cadastrarPatrimonio();

        assertThrows(
                DuplicidadeException.class,
                () -> patrimonioService.cadastrar(
                        "Outro Notebook",
                        "Outro notebook",
                        categoria,
                        local,
                        "SN123456",
                        4500.00,
                        UnidadeMedida.UNIDADE
                )
        );
    }

    @Test
    void deveBuscarPatrimonioPorIdExistente() {

        Patrimonio cadastrado = cadastrarPatrimonio();

        Patrimonio encontrado = patrimonioService.buscarPorId(
                cadastrado.getId()
        );

        assertEquals(cadastrado, encontrado);
    }

    @Test
    void deveLancarExcecaoQuandoBuscarPatrimonioInexistente() {

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> patrimonioService.buscarPorId(99)
        );
    }

    @Test
    void deveListarTodosOsPatrimonios() {

        cadastrarPatrimonio();

        patrimonioService.cadastrar(
                "Computador",
                "Computador Dell",
                categoria,
                local,
                "SN654321",
                4000.00,
                UnidadeMedida.UNIDADE
        );

        List<Patrimonio> patrimonios = patrimonioService.listarTodos();

        assertEquals(2, patrimonios.size());
        assertEquals("Notebook", patrimonios.get(0).getNome());
        assertEquals("Computador", patrimonios.get(1).getNome());
    }

    @Test
    void deveRemoverPatrimonioExistente() {

        Patrimonio patrimonio = cadastrarPatrimonio();

        patrimonioService.remover(patrimonio.getId());

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> patrimonioService.buscarPorId(patrimonio.getId())
        );
    }

    @Test
    void deveLancarExcecaoAoRemoverPatrimonioInexistente() {

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> patrimonioService.remover(99)
        );
    }

    @Test
    void deveAtualizarPatrimonioExistente() {

        Patrimonio patrimonio = cadastrarPatrimonio();

        Patrimonio atualizado = patrimonioService.atualizar(
                patrimonio.getId(),
                "Notebook Atualizado",
                "Notebook Dell Atualizado",
                categoria,
                local,
                "SN999999",
                6000.00,
                UnidadeMedida.CAIXA
        );

        assertEquals(patrimonio.getId(), atualizado.getId());
        assertEquals("Notebook Atualizado", atualizado.getNome());
        assertEquals(
                "Notebook Dell Atualizado",
                atualizado.getDescricao()
        );
        assertEquals("SN999999", atualizado.getNumeroSerie());
        assertEquals(6000.00, atualizado.getValor());
        assertEquals(UnidadeMedida.CAIXA, atualizado.getUnidadeMedida());
    }

    @Test
    void devePermitirAtualizarMantendoMesmoNome() {

        Patrimonio patrimonio = cadastrarPatrimonio();

        Patrimonio atualizado = patrimonioService.atualizar(
                patrimonio.getId(),
                "Notebook",
                "Descrição atualizada",
                categoria,
                local,
                "SN999999",
                5500.00,
                UnidadeMedida.UNIDADE
        );

        assertEquals("Notebook", atualizado.getNome());
        assertEquals(
                "Descrição atualizada",
                atualizado.getDescricao()
        );
    }

    @Test
    void devePermitirAtualizarMantendoMesmoNumeroSerie() {

        Patrimonio patrimonio = cadastrarPatrimonio();
        Patrimonio atualizado = patrimonioService.atualizar(
                patrimonio.getId(),
                "Notebook Atualizado",
                "Notebook Dell Atualizado",
                categoria,
                local,
                "SN123456",
                6000.00,
                UnidadeMedida.CAIXA // Modifique para uma unidade diferente
        );

        assertEquals("Notebook Atualizado", atualizado.getNome());
        assertEquals("SN123456", atualizado.getNumeroSerie());
        assertEquals(UnidadeMedida.CAIXA, atualizado.getUnidadeMedida());
    }

    @Test
    void deveLancarExcecaoAoAtualizarPatrimonioInexistente() {

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> patrimonioService.atualizar(
                        99,
                        "Notebook",
                        "Notebook Dell",
                        categoria,
                        local,
                        "SN123456",
                        5000.00,
                        UnidadeMedida.UNIDADE
                )
        );
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNomeDuplicado() {

        cadastrarPatrimonio();

        Patrimonio segundoPatrimonio = patrimonioService.cadastrar(
                "Computador",
                "Computador Dell",
                categoria,
                local,
                "SN654321",
                4000.00,
                UnidadeMedida.UNIDADE
        );

        assertThrows(
                DuplicidadeException.class,
                () -> patrimonioService.atualizar(
                        segundoPatrimonio.getId(),
                        "Notebook",
                        "Computador Atualizado",
                        categoria,
                        local,
                        "SN999999",
                        4500.00,
                        UnidadeMedida.UNIDADE
                )
        );
    }

    @Test
    void deveLancarExcecaoAoAtualizarComNumeroSerieDuplicado() {

        cadastrarPatrimonio();

        Patrimonio segundoPatrimonio = patrimonioService.cadastrar(
                "Computador",
                "Computador Dell",
                categoria,
                local,
                "SN654321",
                4000.00,
                UnidadeMedida.UNIDADE
        );

        assertThrows(
                DuplicidadeException.class,
                () -> patrimonioService.atualizar(
                        segundoPatrimonio.getId(),
                        "Computador Atualizado",
                        "Computador Dell Atualizado",
                        categoria,
                        local,
                        "SN123456",
                        4500.00,
                        UnidadeMedida.UNIDADE
                )
        );
    }
}
