package com.erp.patrimonio.service;

import java.util.List;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.PatrimonioRepository;

public class PatrimonioService {

    private static final String ERRO_NUMERO_SERIE_DUPLICADO
            = "Número de série já cadastrado.";

    private static final String ERRO_PATRIMONIO_NAO_ENCONTRADO
            = "Patrimônio não encontrado.";

    private static final String ERRO_FALHA_ATUALIZACAO
            = "Falha ao atualizar patrimônio. O registro pode ter sido alterado ou removido.";

    private static final String ERRO_PATRIMONIO_DUPLICADO
            = "Já existe um patrimônio com esse nome.";

    private final PatrimonioRepository repository;

    public PatrimonioService(PatrimonioRepository repository) {
        this.repository = repository;
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
                unidadeMedida
        );
        repository.salvar(patrimonio);
        return patrimonio;
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
            Local local,
            String numeroSerie,
            double valor,
            UnidadeMedida unidadeMedida) {

        buscarPorId(id);

        Patrimonio existenteNome = repository.buscarPorNome(nome);

        if (existenteNome != null && existenteNome.getId() != id) {
            throw new DuplicidadeException(ERRO_PATRIMONIO_DUPLICADO);
        }

        Patrimonio existenteNumeroSerie = repository.buscarPorNumeroSerie(numeroSerie);

        if (existenteNumeroSerie != null && existenteNumeroSerie.getId() != id) {
            throw new DuplicidadeException(ERRO_NUMERO_SERIE_DUPLICADO);
        }

        Patrimonio atualizado = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor,
                unidadeMedida
        );

        boolean atualizadoComSucesso = repository.atualizar(atualizado);

        if (!atualizadoComSucesso) {
            throw new EstadoInvalidoException(ERRO_FALHA_ATUALIZACAO);
        }
        return atualizado;
    }
}
