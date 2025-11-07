package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<AdmUsuarioModel> findById(UUID id) {
        return admUsuarioRepository.findById(id);
    }

    public Optional<AdmUsuarioModel> findByEmail(String email) {
        return admUsuarioRepository.findByEmail(email);
    }

    public boolean emailAlreadyInUse(String email) {
        return admUsuarioRepository.existsByEmail(email);
    }

    public boolean emailInUseByOther(String email, UUID userId) {
        return admUsuarioRepository.existsByEmailAndIdNot(email, userId);
    }

    @Transactional
    public void deleteById(UUID id) {
        admUsuarioRepository.deleteById(id);
    }
}
