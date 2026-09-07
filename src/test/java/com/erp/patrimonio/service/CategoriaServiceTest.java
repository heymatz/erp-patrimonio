package com.erp.patrimonio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    // =========================================================================
    // TESTES DE CADASTRAR
    // =========================================================================

    @Test
    public void testCadastrarCategoriaComSucesso() {
        // Arrange
        String nome = "Eletrônicos";
        String descricao = "Equipamentos de TI";

        when(repository.buscarPorNome(nome)).thenReturn(null);

        // Act
        service.cadastrar(nome, descricao);

        // Assert
        verify(repository, times(1)).salvar(any(Categoria.class));
    }

    @Test
    public void testCadastrarCategoriaComNomeDuplicado() {
        // Arrange
        String nome = "Eletrônicos";
        String descricao = "Equipamentos de TI";
        Categoria categoriaExistente = new Categoria(1, nome, "Outra descrição");

        when(repository.buscarPorNome(nome)).thenReturn(categoriaExistente);

        // Act & Assert
        DuplicidadeException exception = assertThrows(DuplicidadeException.class, () -> {
            service.cadastrar(nome, descricao);
        });

        assertEquals("Já existe uma categoria com esse nome.", exception.getMessage());
        verify(repository, never()).salvar(any(Categoria.class));
    }

    @ParameterizedTest(name = "Falha ao cadastrar categoria com nome inválido: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    public void testCadastrarCategoriaComNomeInvalido(String nomeInvalido) {
        // Arrange
        // Act & Assert
        assertThrows(ValidacaoException.class, () -> {
            service.cadastrar(nomeInvalido, "Equipamentos de TI");
        });

        verify(repository, never()).salvar(any(Categoria.class));
    }

    @ParameterizedTest(name = "Falha ao cadastrar categoria com descrição inválida: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    public void testCadastrarCategoriaComDescricaoInvalida(String descricaoInvalida) {
        // Act & Assert
        assertThrows(ValidacaoException.class, () -> {
            service.cadastrar("Eletrônicos", descricaoInvalida);
        });

        verify(repository, never()).salvar(any(Categoria.class));
    }

    @Test
    public void testCadastrarCategoriaComNomeMaiorQue100Caracteres() {
        // Arrange
        String nomeGigante = "A".repeat(101);
        // Act & Assert 
        assertThrows(ValidacaoException.class, () -> {
            service.cadastrar(nomeGigante, "Equipamentos de TI");
        });
    }

    @Test
    public void testCadastrarCategoriaComDescricaoMaiorQue255Caracteres() {
        // Arrange
        String descricaoGigante = "A".repeat(256);
        // Act & Assert
        assertThrows(ValidacaoException.class, () -> {
            service.cadastrar("Eletrônicos", descricaoGigante);
        });
    }

    // =========================================================================
    // TESTES DE BUSCAR E REMOVER
    // =========================================================================

    @Test
    public void testBuscarPorIdComSucesso() {
        // Arrange
        Categoria mockCategoria = new Categoria(1, "Eletrônicos", "Equipamentos de TI");
        when(repository.buscarPorId(1)).thenReturn(mockCategoria);

        // Act
        Categoria resultado = service.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Eletrônicos", resultado.getNome());
    }

    @Test
    public void testBuscarPorIdInexistente() {
        when(repository.buscarPorId(999)).thenReturn(null);

        assertThrows(EntidadeNaoEncontradaException.class, () -> {
            service.buscarPorId(999);
        });
    }

    @Test
    public void testRemoverCategoriaComSucesso() {
        // Arrange
        Categoria categoriaNoBanco = new Categoria(1, "Eletrônicos", "TI");
        when(repository.buscarPorId(1)).thenReturn(categoriaNoBanco);

        // Act
        service.remover(1);

        // Assert
        verify(repository, times(1)).remover(1);
    }

    @Test
    public void testRemoverCategoriaInexistente() {
        when(repository.buscarPorId(999)).thenReturn(null);

        assertThrows(EntidadeNaoEncontradaException.class, () -> {
            service.remover(999);
        });

        verify(repository, never()).remover(1);
    }

    // =========================================================================
    // TESTES DE ATUALIZAR
    // =========================================================================

    @Test
    public void testAtualizarCategoriaComSucesso() {
        // Arrange
        Categoria categoriaNoBanco = new Categoria(1, "Antigo", "Descricao antiga");

        when(repository.buscarPorId(1)).thenReturn(categoriaNoBanco);
        when(repository.buscarPorNome("Novo Nome")).thenReturn(null);
        when(repository.atualizar(any(Categoria.class))).thenReturn(true);

        // Act
        Categoria atualizada = service.atualizar(1, "Novo Nome", "Nova Descricao");

        // Assert
        assertEquals("Novo Nome", atualizada.getNome());
        verify(repository, times(1)).atualizar(any(Categoria.class));
    }

    @Test
    public void testAtualizarCategoriaMantendoMesmoNomeComSucesso() {
        // Arrange
        Categoria categoriaNoBanco = new Categoria(1, "Eletrônicos", "Antiga");

        // Ensina o Mock: Busca por ID acha o local. Busca por nome acha o mesmo local.
        when(repository.buscarPorId(1)).thenReturn(categoriaNoBanco);
        when(repository.buscarPorNome("Eletrônicos")).thenReturn(categoriaNoBanco);
        when(repository.atualizar(any(Categoria.class))).thenReturn(true);

        // Act
        service.atualizar(1, "Eletrônicos", "Nova descrição");

        // Assert
        verify(repository, times(1)).atualizar(any(Categoria.class));
    }

    @Test
    public void testAtualizarCategoriaFalhandoNoBancoDeDados() {
        // Arrange
        Categoria categoriaNoBanco = new Categoria(1, "Antigo", "Descricao antiga");

        // Ensina o Mock: Busca por ID acha o local. Busca por nome não acha nenhum local com o novo nome.
        when(repository.buscarPorId(1)).thenReturn(categoriaNoBanco);
        when(repository.buscarPorNome("Novo Nome")).thenReturn(null);
        when(repository.atualizar(any(Categoria.class))).thenReturn(false); // Força a falha simulando o BD

        // Act & Assert
        assertThrows(EstadoInvalidoException.class, () -> {
            service.atualizar(1, "Novo Nome", "Nova Descricao");
        });
    }

    @Test
    public void testAtualizarCategoriaInexistente() {
        // Arrange
        when(repository.buscarPorId(999)).thenReturn(null);

        // Act & Assert
        assertThrows(EntidadeNaoEncontradaException.class, () -> {
            service.atualizar(999, "Nome", "Descricao");
        });

        verify(repository, never()).atualizar(any(Categoria.class));
    }

    @Test
    public void testAtualizarCategoriaComNomeDuplicado() {
        // Arrange
        Categoria categoriaExistente = new Categoria(1, "Antigo", "Descricao");
        Categoria categoriaDuplicada = new Categoria(2, "Novo Nome", "Outra");

        // Ensina o Mock: Busca por ID acha o local. Busca por nome acha outro local com o mesmo nome.
        when(repository.buscarPorId(1)).thenReturn(categoriaExistente);
        when(repository.buscarPorNome("Novo Nome")).thenReturn(categoriaDuplicada);

        // Act & Assert
        assertThrows(DuplicidadeException.class, () -> {
            service.atualizar(1, "Novo Nome", "Descricao");
        });

        verify(repository, never()).atualizar(any(Categoria.class));
    }

    @ParameterizedTest(name = "Falha ao atualizar com nome inválido: [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    public void testAtualizarCategoriaComNomeInvalido(String nomeInvalido) {
        // Arrange
        Categoria categoriaExistente = new Categoria(1, "Antigo", "Descricao");
        when(repository.buscarPorId(1)).thenReturn(categoriaExistente);

        // Act & Assert
        assertThrows(ValidacaoException.class, () -> {
            service.atualizar(1, nomeInvalido, "Nova Descricao");
        });

        verify(repository, never()).atualizar(any(Categoria.class));
    }

    // =========================================================================
    // TESTES DE LISTAGEM
    // =========================================================================

    @Test
    public void testListarTodasCategorias() {
        // Arrange
        Categoria cat1 = new Categoria(1, "Eletrônicos", "TI");
        Categoria cat2 = new Categoria(2, "Móveis", "Escritório");

        when(repository.listarTodos()).thenReturn(java.util.Arrays.asList(cat1, cat2));

        // Act
        java.util.List<Categoria> categorias = service.listarTodos();

        // Assert
        assertNotNull(categorias);
        assertEquals(2, categorias.size());
    }

    @Test
    public void testListarTodasCategoriasVazio() {
        // Arrange
        when(repository.listarTodos()).thenReturn(java.util.Collections.emptyList());
        
        // Act
        java.util.List<Categoria> categorias = service.listarTodos();
        
        // Assert
        assertNotNull(categorias);
        assertEquals(0, categorias.size());
    }

    @Test
    public void testListarTodasCategoriasNulo() {
        // Arrange
        // Se o banco retornar nulo, o Service deve blindar e retornar lista vazia
        when(repository.listarTodos()).thenReturn(null);

        // Act
        java.util.List<Categoria> categorias = service.listarTodos();

        // Assert
        assertNotNull(categorias);
        assertEquals(0, categorias.size());
    }
}
