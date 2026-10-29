package com.finanfe_api.service;

import com.finanfe_api.dto.TransacaoDTO;
import com.finanfe_api.exception.RecursoNaoEncontradoException;
import com.finanfe_api.model.Categoria;
import com.finanfe_api.model.Transacao;
import com.finanfe_api.model.TipoTransacao;
import com.finanfe_api.repository.CategoriaRepository;
import com.finanfe_api.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<TransacaoDTO> listarTodas() {
        return transacaoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> listarPorTipo(TipoTransacao tipo) {
        return transacaoRepository.findByTipo(tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return transacaoRepository.findByDataBetweenOrderByDataDesc(inicio, fim)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransacaoDTO> listarPorCategoria(Long categoriaId) {
        return transacaoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransacaoDTO buscarPorId(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não encontrada com id: " + id));
        return toDTO(transacao);
    }

    public TransacaoDTO criar(TransacaoDTO dto) {
        Transacao transacao = toEntity(dto);
        return toDTO(transacaoRepository.save(transacao));
    }

    public TransacaoDTO atualizar(Long id, TransacaoDTO dto) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não encontrada com id: " + id));

        transacao.setDescricao(dto.getDescricao());
        transacao.setValor(dto.getValor());
        transacao.setTipo(dto.getTipo());
        transacao.setData(dto.getData());
        transacao.setObservacao(dto.getObservacao());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com id: " + dto.getCategoriaId()));
            transacao.setCategoria(categoria);
        } else {
            transacao.setCategoria(null);
        }

        return toDTO(transacaoRepository.save(transacao));
    }

    public void deletar(Long id) {
        if (!transacaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Transação não encontrada com id: " + id);
        }
        transacaoRepository.deleteById(id);
    }

    // Mappers
    private TransacaoDTO toDTO(Transacao t) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setId(t.getId());
        dto.setDescricao(t.getDescricao());
        dto.setValor(t.getValor());
        dto.setTipo(t.getTipo());
        dto.setData(t.getData());
        dto.setObservacao(t.getObservacao());
        dto.setCriadoEm(t.getCriadoEm());
        dto.setAtualizadoEm(t.getAtualizadoEm());
        if (t.getCategoria() != null) {
            dto.setCategoriaId(t.getCategoria().getId());
            dto.setCategoriaNome(t.getCategoria().getNome());
            dto.setCategoriaIcone(t.getCategoria().getIcone());
        }
        return dto;
    }

    private Transacao toEntity(TransacaoDTO dto) {
        Transacao transacao = new Transacao();
        transacao.setDescricao(dto.getDescricao());
        transacao.setValor(dto.getValor());
        transacao.setTipo(dto.getTipo());
        transacao.setData(dto.getData());
        transacao.setObservacao(dto.getObservacao());
        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com id: " + dto.getCategoriaId()));
            transacao.setCategoria(categoria);
        }
        return transacao;
    }
}

