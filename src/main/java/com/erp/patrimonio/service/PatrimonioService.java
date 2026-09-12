package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.HistoricoMov;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.HistoricoMovRepository;
import com.erp.patrimonio.repository.PatrimonioRepository;

public class PatrimonioService {

    private static final String ERRO_NUMERO_SERIE_DUPLICADO = "Número de série já cadastrado.";

    private static final String ERRO_PATRIMONIO_NAO_ENCONTRADO = "Patrimônio não encontrado.";

    private static final String ERRO_FALHA_ATUALIZACAO = "Falha ao atualizar patrimônio. O registro pode ter sido alterado ou removido.";

    private static final String ERRO_PATRIMONIO_DUPLICADO = "Já existe um patrimônio com esse nome.";

    private final PatrimonioRepository repository;

    private final HistoricoMovRepository historicoMovRepository;

    //  Construtor para injeção de dependência do repositório de HistoricoMov
    public PatrimonioService(PatrimonioRepository repository, HistoricoMovRepository historicoMovRepository) {
        this.repository = repository;
        this.historicoMovRepository = historicoMovRepository;
    }

    public Patrimonio cadastrar(
            String nome,
            String descricao,
            Categoria categoria,
            Local local,
            String numeroSerie,
            double valor,
            UnidadeMedida unidadeMedida) {

        Patrimonio existenteNome = repository.buscarPorNome(nome);

        if (existenteNome != null) {
            throw new DuplicidadeException(ERRO_PATRIMONIO_DUPLICADO);
        }

        Patrimonio existenteNumeroSerie = repository.buscarPorNumeroSerie(numeroSerie);

        if (existenteNumeroSerie != null) {
            throw new DuplicidadeException(ERRO_NUMERO_SERIE_DUPLICADO);
        }

        int id = 0; // O ID será gerado pelo banco de dados ou pelo repositório

        Patrimonio patrimonio = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor,
                unidadeMedida);

        repository.salvar(patrimonio); // Executa o void com id gerado internamente no repositório
        return patrimonio; // Retorna o objeto já com o id gerado
    }

    public void remover(int id) {
        buscarPorId(id);
        repository.remover(id);
    }

    public List<Patrimonio> listarTodos() {
        return repository.listarTodos();
    }

    public Patrimonio buscarPorId(int id) {
        Patrimonio patrimonio = repository.buscarPorId(id);
        if (patrimonio == null) {
            throw new EntidadeNaoEncontradaException(ERRO_PATRIMONIO_NAO_ENCONTRADO);
        }
        return patrimonio;
    }

    public Patrimonio atualizar(
            int id,
            String nome,
            String descricao,
            Categoria categoria,
            Local novoLocal, // novo parâmetro para o local
            String numeroSerie,
            double valor,
            UnidadeMedida unidadeMedida,
            String motivoMovimentacao) { // novo parâmetro para registro

        // Pega o estado atual do patrimônio antes de qualquer alteração
        Patrimonio patrimonioAtual = buscarPorId(id);

        Patrimonio existenteNome = repository.buscarPorNome(nome);
        if (existenteNome != null && existenteNome.getId() != id) {
            throw new DuplicidadeException(ERRO_PATRIMONIO_DUPLICADO);
        }

        Patrimonio existenteNumeroSerie = repository.buscarPorNumeroSerie(numeroSerie);
        if (existenteNumeroSerie != null && existenteNumeroSerie.getId() != id) {
            throw new DuplicidadeException(ERRO_NUMERO_SERIE_DUPLICADO);
        }

        // Monta o objeto com os novos valores, mantendo o ID original
        Patrimonio atualizado = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                novoLocal,
                numeroSerie,
                valor,
                unidadeMedida);

        // Verifica se houve mudança de Local
        boolean localFoiAlterado = patrimonioAtual.getLocal().getId() != novoLocal.getId();

        // Executa a atualização
        boolean atualizadoComSucesso = repository.atualizar(atualizado);

        if (!atualizadoComSucesso) {
            throw new EstadoInvalidoException(ERRO_FALHA_ATUALIZACAO);
        }

        // Se o local mudou, registra o histórico de movimentação
        if (localFoiAlterado) {
            HistoricoMov historico = new HistoricoMov(
                    atualizado, // O patrimônio que foi movido
                    patrimonioAtual.getLocal(), // O local de origem
                    novoLocal, // O local de destino
                    motivoMovimentacao);
            historicoMovRepository.salvar(historico);
        }

        return atualizado;
    }
}
