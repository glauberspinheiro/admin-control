package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

    @Controller
    public class LoginController {

        @Autowired
        private AdmUsuarioRepository admUsuarioRepository;

        @PostMapping("/")
        public String logar(Model model, AdmUsuarioModel admUsuarioModel, String lembrar) {
            AdmUsuarioModel admobsuserlogin = this.admUsuarioRepository.Login(admUsuarioModel.getEmail(), admUsuarioModel.getSenha());
            if(admobsuserlogin != null){
              return "redirect:/index";
            }
            model.addAttribute("erro", "ATENÇÃO: Usuario ou Senha inválido");
            return "/login";
        }
    }

