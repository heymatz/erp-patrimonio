package com.erp.patrimonio.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryTest {

    private PatrimonioRepository patrimonioRepository;

    private Patrimonio criarPatrimonio() {

        Categoria categoria = new Categoria(
                1,
                "Eletrônicos",
                "Equipamentos de informática"
        );

        Local local = new Local(
                1,
                "Sala 101",
                "Primeiro andar"
        );

        return new Patrimonio(
                1,
                "Computador",
                "Primeiro PC",
                categoria,
                local,
                "S123456T",
                5000.00,
                UnidadeMedida.UNIDADE
        );
    }

    private Patrimonio criarPatrimonio2() {
        Categoria categoria = new Categoria(
                2,
                "Móveis",
                "Mesa de Escritório"
        );

        Local local = new Local(
                2,
                "Sala 201",
                "Segundo andar"
        );

        return new Patrimonio(
                2,
                "Mesa",
                "Mesa escrivaninha para escritório",
                categoria,
                local,
                "S234567T",
                2000.00,
                UnidadeMedida.UNIDADE
        );
    }

    @BeforeEach
    void setUp() {
        PatrimonioRepository repository = new PatrimonioRepositoryInMemory();
    }

    @Test
    void deveSalvarPatrimonioComDadosValidos() {
        Patrimonio patrimonio = criarPatrimonio();
        patrimonioRepository.salvar(patrimonio);

        assertEquals(1, patrimonioRepository.listarTodos().size());
    }

    @Test
    void deveLancarExcecaoQuandoSalvarPatrimonioNulo() {
        assertThrows(
                ValidacaoException.class,
                () -> patrimonioRepository.salvar(null)
        );
    }

    @Test
    void deveAtualizarPatrimonioExistente() {
        Patrimonio patrimonio = criarPatrimonio();
        patrimonioRepository.salvar(patrimonio);

        patrimonio.setNome("Computador atualizado");

        boolean atualizado = patrimonioRepository.atualizar(patrimonio);

        assertTrue(atualizado);
        assertEquals(
                "Computador atualizado",
                patrimonioRepository.buscarPorId(1).getNome()
        );
    }

    @Test
    void deveRemoverPatrimonioExistente() {
        Patrimonio patrimonio = criarPatrimonio();
        patrimonioRepository.salvar(patrimonio);

        boolean removido = patrimonioRepository.remover(1);

        assertTrue(removido);
        assertEquals(0, patrimonioRepository.listarTodos().size());
    }

    @Test
    void deveBuscarPatrimonioPorIdExistente() {
        Patrimonio patrimonio = criarPatrimonio();
        patrimonioRepository.salvar(patrimonio);
        Patrimonio encontrado = patrimonioRepository.buscarPorId(1);
        assertEquals(patrimonio, encontrado);
    }

    @Test
    void deveListarTodosOsPatrimoniosExistentes() {
        Patrimonio patrimonio1 = criarPatrimonio();

        Patrimonio patrimonio2 = criarPatrimonio2();

        patrimonioRepository.salvar(patrimonio1);
        patrimonioRepository.salvar(patrimonio2);

        List<Patrimonio> patrimonios = patrimonioRepository.listarTodos();

        assertEquals(2, patrimonios.size());
        assertEquals("Computador", patrimonios.get(0).getNome());
        assertEquals("Mesa", patrimonios.get(1).getNome());
        assertEquals("Primeiro PC", patrimonios.get(0).getDescricao());
        assertEquals("Mesa escrivaninha para escritório", patrimonios.get(1).getDescricao());
        assertEquals("S123456T", patrimonios.get(0).getNumeroSerie());
        assertEquals(5000.00, patrimonios.get(0).getValor());
        assertEquals("S234567T", patrimonios.get(1).getNumeroSerie());
        assertEquals(2000.00, patrimonios.get(1).getValor());
        assertEquals(UnidadeMedida.UNIDADE, patrimonios.get(0).getUnidadeMedida());
    }

    @Test
    void deveRetornarFalseAoRemoverPatrimonioInexistente() {
        boolean removido = patrimonioRepository.remover(99);

        assertFalse(removido);
    }

    @Test
    void deveRetornarFalseAoAtualizarPatrimonioInexistente() {
        Patrimonio patrimonio = criarPatrimonio();

        boolean atualizado = patrimonioRepository.atualizar(patrimonio);

        assertFalse(atualizado);
    }

    @Test
    void deveRetornarNullAoBuscarPatrimonioInexistente() {
        Patrimonio patrimonio = patrimonioRepository.buscarPorId(99);

        assertNull(patrimonio);
    }

}
