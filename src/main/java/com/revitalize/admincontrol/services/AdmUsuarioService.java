package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdmUsuarioService {

    final AdmUsuarioRepository admUsuarioRepository;

    public AdmUsuarioService(AdmUsuarioRepository admUsuarioRepository){
        this.admUsuarioRepository = admUsuarioRepository;
    }

    @Transactional
    public AdmUsuarioModel saveUsuario(AdmUsuarioModel admUsuarioModel){
        return admUsuarioRepository.save(admUsuarioModel);
    }

    public List<AdmUsuarioModel>findAll(){

        return (List<AdmUsuarioModel>)admUsuarioRepository.findAll();
    }
}