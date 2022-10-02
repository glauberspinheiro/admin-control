package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import javax.transaction.Transactional;
import java.util.List;

public class AdmEmpresaService {
    final AdmEmpresaRepository admEmpresaRepository;

    public AdmEmpresaService(AdmEmpresaRepository admEmpresaRepository) {
        this.admEmpresaRepository = admEmpresaRepository;
    }
    @Transactional
    public AdmEmpresaModel saveEmpresa(AdmEmpresaModel admEmpresaModel){
        return admEmpresaRepository.save(admEmpresaModel);
    }

    public List<AdmEmpresaModel> findAll(){
        return (List<AdmEmpresaModel>)admEmpresaRepository.findAll();
    }
}
