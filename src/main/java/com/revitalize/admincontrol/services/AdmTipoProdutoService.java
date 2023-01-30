package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.repository.AdmTipoProdutoRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

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
}
