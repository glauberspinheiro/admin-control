package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmProdutoModel;
import com.revitalize.admincontrol.repository.AdmProdutoRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
}