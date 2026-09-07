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
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.repository.LocalRepository;

@ExtendWith(MockitoExtension.class)
public class LocalServiceTest {

        @Mock
        private LocalRepository repository;

        @InjectMocks
        private LocalService service;

        @Test
        public void testCadastrarLocalComSucesso() {
                // Arrange
                String nome = "Armazém 01";
                String descricao = "Local de produtos eletrônicos";

                // Ensina o Mock: simule que não existe nenhum local com esse nome no banco
                when(repository.buscarPorNome(nome)).thenReturn(null);

                // Act
                service.cadastrar(nome, descricao);

                // Assert
                // Verifica se o método salvar do repositório foi chamado exatamente 1 vez com
                // qualquer objeto Local
                verify(repository, times(1)).salvar(any(Local.class));
        }

        @Test
        public void testCadastrarLocalComNomeDuplicado() {
                // Arrange
                String nome = "Armazém 01";
                String descricao = "Local de produtos eletrônicos";
                Local localExistente = new Local(1, nome, "Outro local");

                // Ensina o Mock: simule que esse local já existe no banco
                when(repository.buscarPorNome(nome)).thenReturn(localExistente);

                // Act & Assert
                assertThrows(DuplicidadeException.class, () -> {
                        service.cadastrar(nome, descricao);
                });
        }

        // =========================================================================
        // TESTES PARAMETRIZADOS PARA CADASTRAR
        // =========================================================================

        @ParameterizedTest(name = "Falha ao cadastrar local com nome inválido: [{0}]")
        @NullAndEmptySource // Passa null e ""
        @ValueSource(strings = { " ", "   " }) // Passa strings apenas com espaços
        public void testCadastrarLocalComNomeInvalido(String nomeInvalido) {
                // Arrange
                String descricao = "Local de produtos eletrônicos";

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.cadastrar(nomeInvalido, descricao);
                });

                // Verifica se o repositório nunca foi chamado
                verify(repository, never()).salvar(any(Local.class));
        }

        @ParameterizedTest(name = "Falha ao cadastrar local com descrição inválida: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "   " })
        public void testCadastrarLocalComDescricaoInvalida(String descricaoInvalida) {
                // Arrange
                String nome = "Armazém 01";

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.cadastrar(nome, descricaoInvalida);
                });

                verify(repository, never()).salvar(any(Local.class));
        }

        @Test
        public void testCadastrarLocalComNomeMaiorQue100Caracteres() {
                // Arrange
                String nome = "A".repeat(101);
                String descricao = "Local de produtos eletrônicos";

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.cadastrar(nome, descricao);
                });
        }

        @Test
        public void testCadastrarLocalComDescricaoMaiorQue255Caracteres() {
                // Arrange
                String nome = "Armazém 01";
                String descricao = "A".repeat(256);

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.cadastrar(nome, descricao);
                });
        }

        // =========================================================================
        // TESTES DE BUSCAR, REMOVER E ATUALIZAR
        // =========================================================================

        @Test
        public void testBuscarLocalPorIdComSucesso() {
                // Arrange
                int id = 1;
                String nome = "Armazém 01";
                String descricao = "Local de produtos eletrônicos";
                Local local = new Local(id, nome, descricao);

                // Ensina o Mock: simule que esse local existe no banco
                when(repository.buscarPorId(id)).thenReturn(local);

                // Act
                Local encontrada = service.buscarPorId(id);

                // Assert
                assertNotNull(encontrada);
                assertEquals(id, encontrada.getId());
                assertEquals(nome, encontrada.getNome());
                assertEquals(descricao, encontrada.getDescricao());
        }

        @Test
        public void testBuscarLocalPorIdInexistente() {
                // Arrange
                int id = 999;

                // Ensina o Mock: simule que esse local não existe no banco
                when(repository.buscarPorId(id)).thenReturn(null);

                // Act & Assert
                assertThrows(EntidadeNaoEncontradaException.class, () -> {
                        service.buscarPorId(id);
                });
        }

        @Test
        public void testRemoverLocalComSucesso() {
                // Arrange
                int id = 1;
                String nome = "Armazém 01";
                String descricao = "Local de produtos eletrônicos";
                Local local = new Local(id, nome, descricao);

                // Ensina o Mock: simule que esse local existe no banco
                when(repository.buscarPorId(id)).thenReturn(local);

                // Act
                service.remover(id);

                // Assert
                verify(repository, times(1)).remover(id);
        }

        @Test
        public void testRemoverLocalInexistente() {
                // Arrange
                int id = 999;

                // Ensina o Mock: simule que esse local não existe no banco
                when(repository.buscarPorId(id)).thenReturn(null);

                // Act & Assert
                assertThrows(EntidadeNaoEncontradaException.class, () -> {
                        service.remover(id);
                });

                // Verifica se o método remover do repositório nunca foi chamado
                verify(repository, never()).remover(id);
        }

        @Test
        public void testAtualizarLocalComSucesso() {
                // Arrange
                int id = 1;
                String nomeAtualizado = "Armazém 01 Atualizado";
                String descricaoAtualizada = "Local de produtos eletrônicos atualizado";
                Local localExistente = new Local(id, "Armazém 01", "Local de produtos eletrônicos");

                // Ensina o Mock: os três cenários que ele vai enfrentar
                when(repository.buscarPorId(id)).thenReturn(localExistente);
                when(repository.buscarPorNome(nomeAtualizado)).thenReturn(null);
                when(repository.atualizar(any(Local.class))).thenReturn(true);

                // Act
                service.atualizar(id, nomeAtualizado, descricaoAtualizada);

                // Assert
                verify(repository, times(1)).atualizar(any(Local.class));
        }

        @Test
        public void testAtualizarLocalInexistente() {
                // Arrange
                int id = 999;
                String nomeAtualizado = "Armazém 01 Atualizado";
                String descricaoAtualizada = "Local de produtos eletrônicos atualizado";

                // Ensina o Mock: simule que esse local não existe no banco
                when(repository.buscarPorId(id)).thenReturn(null);

                // Act & Assert
                assertThrows(EntidadeNaoEncontradaException.class, () -> {
                        service.atualizar(id, nomeAtualizado, descricaoAtualizada);
                });

                // Verifica se o método atualizar do repositório nunca foi chamado
                verify(repository, never()).atualizar(any(Local.class));
        }

        @Test
        public void testAtualizarLocalComNomeDuplicado() {
                // Arrange
                int id = 1;
                String nomeAtualizado = "Armazém 02"; // Nome duplicado
                String descricaoAtualizada = "Local de produtos eletrônicos atualizado";
                Local localExistente = new Local(id, "Armazém 01", "Local de produtos eletrônicos");
                Local localDuplicado = new Local(2, nomeAtualizado, "Outro local");

                // Ensina o Mock: simule que esse local existe no banco
                when(repository.buscarPorId(id)).thenReturn(localExistente);
                when(repository.buscarPorNome(nomeAtualizado)).thenReturn(localDuplicado);

                // Act & Assert
                assertThrows(DuplicidadeException.class, () -> {
                        service.atualizar(id, nomeAtualizado, descricaoAtualizada);
                });

                // Verifica se o método atualizar do repositório nunca foi chamado
                verify(repository, never()).atualizar(any(Local.class));
        }

        @Test
        public void testAtualizarLocalMantendoMesmoNomeComSucesso() {
                // Arrange
                int id = 1;
                String nome = "Armazém 01";
                String novaDescricao = "Descrição alterada, nome mantido";
                Local localExistente = new Local(id, nome, "Descrição antiga");

                // Ensina o Mock: Busca por ID acha o local. Busca por nome acha o mesmo local.
                when(repository.buscarPorId(id)).thenReturn(localExistente);
                when(repository.buscarPorNome(nome)).thenReturn(localExistente);
                when(repository.atualizar(any(Local.class))).thenReturn(true);

                // Act
                service.atualizar(id, nome, novaDescricao);

                // Assert
                verify(repository, times(1)).atualizar(any(Local.class));
        }

        @Test
        public void testAtualizarLocalFalhandoNoBancoDeDados() {
                // Arrange
                int id = 1;
                Local localExistente = new Local(id, "Armazém 01", "Desc");

                when(repository.buscarPorId(id)).thenReturn(localExistente);
                when(repository.buscarPorNome("Armazém 01")).thenReturn(null);
                
                // Ensina o Mock: Simula que o comando UPDATE falhou lá no MySQL e retornou FALSE
                when(repository.atualizar(any(Local.class))).thenReturn(false);

                // Act & Assert
                // Como retornou false, a regra de negócio precisa lançar a EstadoInvalidoException
                assertThrows(com.erp.patrimonio.exception.EstadoInvalidoException.class, () -> {
                        service.atualizar(id, "Armazém 01", "Nova desc");
                });
        }

        @Test
        public void testAtualizarLocalComNomeMaiorQue100Caracteres() {
                // Arrange
                int id = 1;
                Local localExistente = new Local(id, "Armazém 01", "Desc");
                when(repository.buscarPorId(id)).thenReturn(localExistente);

                String nomeGigante = "A".repeat(101);

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.atualizar(id, nomeGigante, "Nova Desc");
                });
                verify(repository, never()).atualizar(any(Local.class));
        }

        @Test
        public void testAtualizarLocalComDescricaoMaiorQue255Caracteres() {
                // Arrange
                int id = 1;
                Local localExistente = new Local(id, "Armazém 01", "Desc");
                when(repository.buscarPorId(id)).thenReturn(localExistente);

                String descricaoGigante = "A".repeat(256);

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.atualizar(id, "Nome Válido", descricaoGigante);
                });
                verify(repository, never()).atualizar(any(Local.class));
        }

        // =========================================================================
        // TESTES PARAMETRIZADOS PARA ATUALIZAR
        // =========================================================================

        @ParameterizedTest(name = "Falha ao atualizar local com nome inválido: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "   " })
        public void testAtualizarLocalComNomeInvalido(String nomeAtualizado) {
                // Arrange
                int id = 1;
                String descricaoAtualizada = "Local de produtos eletrônicos atualizado";
                Local localExistente = new Local(id, "Armazém 01", "Local de produtos eletrônicos");

                // Ensina o Mock: simule que esse local existe no banco
                when(repository.buscarPorId(id)).thenReturn(localExistente);

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.atualizar(id, nomeAtualizado, descricaoAtualizada);
                });

                // Verifica se o método atualizar do repositório nunca foi chamado
                verify(repository, never()).atualizar(any(Local.class));
        }

        @ParameterizedTest(name = "Falha ao atualizar local com descrição inválida: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = { " ", "   " })
        public void testAtualizarLocalComDescricaoInvalida(String descricaoAtualizada) {
                // Arrange
                int id = 1;
                String nomeAtualizado = "Armazém 01 Atualizado";
                Local localExistente = new Local(id, "Armazém 01", "Local de produtos eletrônicos");

                // Ensina o Mock: simule que esse local existe no banco
                when(repository.buscarPorId(id)).thenReturn(localExistente);

                // Act & Assert
                assertThrows(ValidacaoException.class, () -> {
                        service.atualizar(id, nomeAtualizado, descricaoAtualizada);
                });

                // Verifica se o método atualizar do repositório nunca foi chamado
                verify(repository, never()).atualizar(any(Local.class));
        }

        // =========================================================================
        // TESTES DE LISTAGEM
        // =========================================================================

        @Test
        public void testListarTodosLocais() {
                // Arrange
                Local local1 = new Local(1, "Armazém 01", "Local de produtos eletrônicos");
                Local local2 = new Local(2, "Armazém 02", "Local de peças de reposição");

                // Ensina o Mock: simule que existem dois locais no banco
                when(repository.listarTodos()).thenReturn(java.util.Arrays.asList(local1, local2));

                // Act
                java.util.List<Local> locais = service.listarTodos();

                // Assert
                assertNotNull(locais);
                assertEquals(2, locais.size());
        }

        @Test
        public void testListarTodosLocaisVazio() {
                // Arrange
                // Ensina o Mock: simule que não existem locais no banco
                when(repository.listarTodos()).thenReturn(java.util.Collections.emptyList());

                // Act
                java.util.List<Local> locais = service.listarTodos();

                // Assert
                assertNotNull(locais);
                assertEquals(0, locais.size());
        }

        @Test
        public void testListarTodosLocaisNulo() {
                // Arrange
                // Ensina o Mock: simule que o repositório retorna null
                when(repository.listarTodos()).thenReturn(null);

                // Act
                java.util.List<Local> locais = service.listarTodos();

                // Assert
                assertNotNull(locais);
                assertEquals(0, locais.size());
        }
}
