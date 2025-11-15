package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.repository.AdmTipoProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdmTipoProdutoService {

    final AdmTipoProdutoRepository admTipoProdutoRepository;

    public AdmTipoProdutoService(AdmTipoProdutoRepository admTipoProdutoRepository){
        this.admTipoProdutoRepository = admTipoProdutoRepository;
    }

    @Transactional
    public AdmTipoProdutoModel saveTipoProduto(AdmTipoProdutoModel admTipoProdutoModel){
        return admTipoProdutoRepository.save(admTipoProdutoModel);
    }

    public List<AdmTipoProdutoModel> findAll() {
        return (List<AdmTipoProdutoModel>) admTipoProdutoRepository.findAll();
    }

    public Optional<AdmTipoProdutoModel> findById(UUID id) {
        return admTipoProdutoRepository.findById(id);
    }

    @Transactional
    public void deleteById(UUID id) {
        admTipoProdutoRepository.deleteById(id);
    }
}
