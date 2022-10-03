package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.repository.AdmTipoProdutoRepository;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdmTiipoProdutoService {

    final AdmTipoProdutoRepository admTipoProdutoRepository;

    public AdmTiipoProdutoService(AdmTipoProdutoRepository admTipoProdutoRepository){
        this.admTipoProdutoRepository = admTipoProdutoRepository;
    }

    @Transactional
    public AdmTipoProdutoModel saveTipoProduto(AdmTipoProdutoModel admTipoProdutoModel){
        return admTipoProdutoRepository.save(admTipoProdutoModel);
    }

    public List<AdmTipoProdutoModel>findAll(){

        return (List<AdmTipoProdutoModel>)admTipoProdutoRepository.findAll();
    }

}
