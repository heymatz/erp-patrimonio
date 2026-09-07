package com.erp.patrimonio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    @Test
    public void testCadastrarCategoriaComSucesso() {
        // Arrange
        String nome = "Eletrônicos";
        String descricao = "Equipamentos de TI";
        
        // Ensina o Mock: simule que não existe nenhuma categoria com esse nome no banco
        when(repository.buscarPorNome(nome)).thenReturn(null);

        // Act
        service.cadastrar(nome, descricao);

        // Assert
        // Verifica se o método salvar do repositório foi chamado exatamente 1 vez com qualquer objeto Categoria
        verify(repository, times(1)).salvar(any(Categoria.class));
    }

    @Test
    public void testCadastrarCategoriaComNomeDuplicado() {
        // Arrange
        String nome = "Eletrônicos";
        String descricao = "Equipamentos de TI";
        Categoria categoriaExistente = new Categoria(1, nome, "Outra descrição");

        // Ensina o Mock: simule que essa categoria já existe no banco
        when(repository.buscarPorNome(nome)).thenReturn(categoriaExistente);

        // Act & Assert
        DuplicidadeException exception = assertThrows(DuplicidadeException.class, () -> {
            service.cadastrar(nome, descricao);
        });

        // Verifica se a mensagem de erro é a esperada
        assertEquals("Já existe uma categoria com esse nome.", exception.getMessage());
        
        // Garante que o repositório nunca foi chamado para salvar
        verify(repository, never()).salvar(any(Categoria.class));
    }

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
        verify(repository, times(1)).buscarPorId(1);
    }

    @Test
    public void testAtualizarCategoriaComSucesso() {
        // Arrange
        Categoria categoriaNoBanco = new Categoria(1, "Antigo", "Descricao antiga");
        
        // Simula que a categoria existe quando o service for buscar pelo ID
        when(repository.buscarPorId(1)).thenReturn(categoriaNoBanco);
        // Simula que o novo nome não está em uso por outra categoria
        when(repository.buscarPorNome("Novo Nome")).thenReturn(null);
        // Simula que o update no banco deu certo
        when(repository.atualizar(categoriaNoBanco)).thenReturn(true);

        // Act
        Categoria atualizada = service.atualizar(1, "Novo Nome", "Nova Descricao");

        // Assert
        assertEquals("Novo Nome", atualizada.getNome());
        assertEquals("Nova Descricao", atualizada.getDescricao());
        verify(repository, times(1)).atualizar(categoriaNoBanco);
    }

    @Test
    public void testRemoverCategoriaComSucesso() {
        // Arrange
        Categoria categoriaNoBanco = new Categoria(1, "Eletrônicos", "TI");
        
        // O remover exige que a categoria exista, então simulamos que ela existe
        when(repository.buscarPorId(1)).thenReturn(categoriaNoBanco);

        // Act
        service.remover(1);

        // Assert
        verify(repository, times(1)).remover(1);
    }
}
