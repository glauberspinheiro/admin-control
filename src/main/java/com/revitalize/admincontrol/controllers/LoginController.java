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
            //Buscando campos de credenciais para o login, nesse caso e-mail e senha.
            AdmUsuarioModel admobsuserlogin = this.admUsuarioRepository.Login(admUsuarioModel.getEmail(), admUsuarioModel.getSenha());
            //Validando se os dados do usuário estão corretos.
            if(admobsuserlogin != null){
            // se sim direcione para a pagina inicial.
              return "redirect:/index";
            }
            // se não informe que as credenciais estão invalidas e mantem na pagina da login.
            model.addAttribute("erro", "Usuario ou Senha inválido");
            return "/login";
        }
}