package com.erp.patrimonio.service;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.HistoricoMov;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.CategoriaRepository;
import com.erp.patrimonio.repository.CategoriaRepositoryJdbc;
import com.erp.patrimonio.repository.HistoricoMovRepository;
import com.erp.patrimonio.repository.HistoricoMovRepositoryJdbc;
import com.erp.patrimonio.repository.LocalRepository;
import com.erp.patrimonio.repository.LocalRepositoryJdbc;
import com.erp.patrimonio.repository.PatrimonioRepository;
import com.erp.patrimonio.repository.PatrimonioRepositoryJdbc;

class PatrimonioServiceTest {

        private LocalRepository localRepository;
        private PatrimonioRepository patrimonioRepository;
        private PatrimonioService patrimonioService;
        private Categoria categoria;
        private Local local;

        @BeforeEach
        void setUp() {
                // Configura a conexão com o banco de dados e o repositório
                ConnectionFactory connectionFactory = new ConnectionFactory();
                TransactionManager transactionManager = new TransactionManager(connectionFactory);

                PatrimonioRepository patrimonioRepository = new PatrimonioRepositoryJdbc(connectionFactory,
                                transactionManager);
                CategoriaRepository categoriaRepository = new CategoriaRepositoryJdbc(connectionFactory,
                                transactionManager);
                this.localRepository = new LocalRepositoryJdbc(connectionFactory, transactionManager);
                // Instancia o repositório de histórico para satisfazer o novo construtor do
                // Service
                HistoricoMovRepository historicoMovRepository = new HistoricoMovRepositoryJdbc(connectionFactory,
                                transactionManager);

                // Limpa o banco antes de cada teste
                try (Connection conn = connectionFactory.recuperarConexao();
                                Statement stmt = conn.createStatement()) {
                        
                        // Limpa as tabelas na ordem correta para evitar problemas de Foreign Key
                        stmt.executeUpdate("DELETE FROM historico_movimentacao");
                        stmt.executeUpdate("DELETE FROM patrimonios");
                        stmt.executeUpdate("DELETE FROM locais");
                        stmt.executeUpdate("DELETE FROM categorias");
                } catch (Exception e) {
                        throw new RuntimeException("Erro ao limpar banco para os testes de serviço: " + e.getMessage());
                }

                // Salva previamente Categoria e Local para satisfazer as Foreign Keys do banco
                this.categoria = new Categoria(
                                1,
                                "Eletrônicos",
                                "Equipamentos de informática");
                categoriaRepository.salvar(this.categoria);

                this.local = new Local(
                                1,
                                "Sala 101",
                                "Primeiro andar");
                localRepository.salvar(this.local);

                // Passa o repositório de HistoricoMov para o construtor do Service
                patrimonioService = new PatrimonioService(patrimonioRepository, historicoMovRepository);
        }

        private Patrimonio cadastrarPatrimonio() {

                return patrimonioService.cadastrar(
                                "Notebook",
                                "Notebook Dell",
                                categoria,
                                local,
                                "SN123456",
                                5000.00,
                                UnidadeMedida.UNIDADE);
        }

        @Test
        void deveCadastrarPatrimonioComDadosValidos() {
                Patrimonio patrimonio = cadastrarPatrimonio();

                assertNotNull(patrimonio);
                assertTrue(patrimonio.getId() > 0, "O ID deve ser gerado pelo banco");
                assertEquals("Notebook", patrimonio.getNome());
                assertEquals("Notebook Dell", patrimonio.getDescricao());
                assertEquals("SN123456", patrimonio.getNumeroSerie());
                assertEquals(5000.00, patrimonio.getValor());
                assertEquals(UnidadeMedida.UNIDADE, patrimonio.getUnidadeMedida());
        }

        @Test
        void deveGerarIdAutomaticamenteAoCadastrarPatrimonios() {
                Patrimonio patrimonio1 = patrimonioService.cadastrar(
                                "Notebook", "Notebook Dell", categoria, local, "SN123456", 5000.00,
                                UnidadeMedida.UNIDADE);

                Patrimonio patrimonio2 = patrimonioService.cadastrar(
                                "Computador", "Computador Dell", categoria, local, "SN654321", 4000.00,
                                UnidadeMedida.UNIDADE);

                assertTrue(patrimonio1.getId() > 0);
                assertTrue(patrimonio2.getId() > patrimonio1.getId(),
                                "O ID do segundo patrimônio deve ser incremental");
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
                                                UnidadeMedida.UNIDADE));
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
                                                UnidadeMedida.UNIDADE));
        }

        @Test
        void deveBuscarPatrimonioPorIdExistente() {
                Patrimonio cadastrado = cadastrarPatrimonio();

                Patrimonio encontrado = patrimonioService.buscarPorId(cadastrado.getId());

                assertNotNull(encontrado);
                assertEquals(cadastrado.getId(), encontrado.getId());
                assertEquals(cadastrado.getNome(), encontrado.getNome());
                assertEquals(cadastrado.getNumeroSerie(), encontrado.getNumeroSerie());
        }

        @Test
        void deveLancarExcecaoQuandoBuscarPatrimonioInexistente() {

                assertThrows(
                                EntidadeNaoEncontradaException.class,
                                () -> patrimonioService.buscarPorId(99));
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
                                UnidadeMedida.UNIDADE);

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
                                () -> patrimonioService.buscarPorId(patrimonio.getId()));
        }

        @Test
        void deveLancarExcecaoAoRemoverPatrimonioInexistente() {

                assertThrows(
                                EntidadeNaoEncontradaException.class,
                                () -> patrimonioService.remover(99));
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
                                UnidadeMedida.CAIXA,
                                "" // Passa uma string vazia como motivo para o teste compilar com a nova regra
                );

                assertEquals(patrimonio.getId(), atualizado.getId());
                assertEquals("Notebook Atualizado", atualizado.getNome());
                assertEquals(
                                "Notebook Dell Atualizado",
                                atualizado.getDescricao());
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
                                UnidadeMedida.UNIDADE,
                                "" // Passa uma string vazia como motivo para o teste compilar com a nova regra
                );

                assertEquals("Notebook", atualizado.getNome());
                assertEquals(
                                "Descrição atualizada",
                                atualizado.getDescricao());
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
                                UnidadeMedida.CAIXA,
                                "" // Passa uma string vazia como motivo para o teste compilar com a nova regra
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
                                                UnidadeMedida.UNIDADE,
                                                "" // Passa uma string vazia como motivo para o teste compilar com a
                                                   // nova regra
                                ));
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
                                UnidadeMedida.UNIDADE);

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
                                                UnidadeMedida.UNIDADE,
                                                "" // Passa uma string vazia como motivo para o teste compilar com a
                                                   // nova regra
                                ));
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
                                UnidadeMedida.UNIDADE);

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
                                                UnidadeMedida.UNIDADE,
                                                "" // Passa uma string vazia como motivo para o teste compilar com a
                                                   // nova regra
                                ));
        }

        @Test
        void deveRegistrarHistoricoQuandoLocalForAlterado() {
                // Cadastra um patrimônio inicial no setup do teste já num local específico
                Patrimonio patrimonio = cadastrarPatrimonio();

                // Cria e salva um novo local de destino no banco para simular a movimentação
                Local novoLocal = new Local(2, "Sala do RH", "Segundo Andar");
                localRepository.salvar(novoLocal);

                String motivo = "Transferência de departamento";

                // Chama o atualizar trocando apenas o local e passando o motivo
                patrimonioService.atualizar(
                                patrimonio.getId(),
                                patrimonio.getNome(),
                                patrimonio.getDescricao(),
                                patrimonio.getCategoria(),
                                novoLocal, // Aqui é onde o local é alterado
                                patrimonio.getNumeroSerie(),
                                patrimonio.getValor(),
                                patrimonio.getUnidadeMedida(),
                                motivo);

                // Puxa o histórico e usa os asserts para verificar se a movimentação foi
                // registrada corretamente
                List<HistoricoMov> historico = patrimonioService.listarHistoricoMovimentacoes(patrimonio.getId());

                assertEquals(1, historico.size(), "Deve haver exatamente 1 registro de movimentação");

                HistoricoMov mov = historico.get(0);
                assertEquals(local.getId(), mov.getLocalOrigem().getId(), "O local de origem deve ser a Sala 101");
                assertEquals(novoLocal.getId(), mov.getLocalDestino().getId(), "O local de destino deve ser o RH");
                assertEquals(motivo, mov.getMotivo(), "O motivo deve bater com o que digitamos");
        }
}
