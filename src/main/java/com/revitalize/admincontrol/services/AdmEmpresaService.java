package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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
        return admEmpresaRepository.findAll();
    }

    public Optional<AdmEmpresaModel> findById(UUID id) {
        return admEmpresaRepository.findById(id);
    }

    @Transactional
    public void deleteById(UUID id) {
        admEmpresaRepository.deleteById(id);
    }

    public Optional<AdmEmpresaModel> findByCnpj(String cnpj) {
        return admEmpresaRepository.findByCnpj(cnpj);
    }

    public List<AdmEmpresaModel> findLatest(int limit) {
        int size = Math.min(Math.max(limit, 1), 50);
        return admEmpresaRepository.findAll(PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "dtCadastro"))).getContent();
    }
}
