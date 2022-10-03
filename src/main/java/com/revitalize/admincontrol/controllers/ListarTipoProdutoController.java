package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.UUID;

@Controller
public class ListarCadastroController {

    @Autowired
    private AdmUsuarioRepository admUsuarioRepository;

        @RequestMapping("/users")
        public ModelAndView listaUsuario() {
            ModelAndView mvListaUser = new ModelAndView("users.html");
            Iterable<AdmUsuarioModel> listaUsuario = admUsuarioRepository.findAll();
            mvListaUser.addObject("usuarios", listaUsuario);
        return mvListaUser;
        }

        @GetMapping("/users/{id}/delete")
        public String deleteUser(@PathVariable UUID id) {
            admUsuarioRepository.deleteById(id);
        return "redirect:/users";
        }
}