package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmProdutoModel;
import com.revitalize.admincontrol.repository.AdmProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdmProdutoService {

    final AdmProdutoRepository admProdutoRepository;

    public AdmProdutoService(AdmProdutoRepository admProdutoRepository){
        this.admProdutoRepository = admProdutoRepository;
    }

    @Transactional
    public AdmProdutoModel saveProduto(AdmProdutoModel admProdutoModel){
        return admProdutoRepository.save(admProdutoModel);
    }

    public List<AdmProdutoModel>findAll(){
        return (List<AdmProdutoModel>)admProdutoRepository.findAll();
    }

    public Optional<AdmProdutoModel> findById(UUID id) {
        return admProdutoRepository.findById(id);
    }

    @Transactional
    public void deleteById(UUID id) {
        admProdutoRepository.deleteById(id);
    }
}
