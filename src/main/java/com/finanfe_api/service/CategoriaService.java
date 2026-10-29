package com.finanfe_api.service;

import com.finanfe_api.dto.CategoriaDTO;
import com.finanfe_api.exception.RecursoNaoEncontradoException;
import com.finanfe_api.model.Categoria;
import com.finanfe_api.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com id: " + id));
        return toDTO(categoria);
    }

    public CategoriaDTO criar(CategoriaDTO dto) {
        if (categoriaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + dto.getNome());
        }
        Categoria categoria = toEntity(dto);
        return toDTO(categoriaRepository.save(categoria));
    }

    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com id: " + id));

        // Verificar duplicidade de nome (exceto o próprio registro)
        categoriaRepository.findByNomeIgnoreCase(dto.getNome())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new IllegalArgumentException("Já existe outra categoria com o nome: " + dto.getNome()); });

        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setIcone(dto.getIcone());
        return toDTO(categoriaRepository.save(categoria));
    }

    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Categoria não encontrada com id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    // Mappers
    private CategoriaDTO toDTO(Categoria c) {
        return new CategoriaDTO(c.getId(), c.getNome(), c.getDescricao(), c.getIcone());
    }

    private Categoria toEntity(CategoriaDTO dto) {
        return new Categoria(dto.getNome(), dto.getDescricao(), dto.getIcone());
    }
}

