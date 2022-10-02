package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Controller
public class SalvaCadastroController {

    @Autowired
    private AdmUsuarioRepository admUsuarioRepository;

    @RequestMapping(value="/adduser", method= RequestMethod.GET)
    public String formUser(){
            return "register";
    }

    @RequestMapping(value="/adduser", method=RequestMethod.POST)
    public String formUser(AdmUsuarioModel admUsuarioModel) {
        admUsuarioModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admUsuarioModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admUsuarioRepository.save(admUsuarioModel);
        return "redirect:/users";
    }
}