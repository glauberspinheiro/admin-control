package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ListarCadastroController {

    @Autowired
    private AdmUsuarioRepository admUsuarioRepository;

        @RequestMapping("/users")
        public ModelAndView listaUsuario() {
            ModelAndView mvListaUser = new ModelAndView("users.html");
            Iterable<AdmUsuarioModel> listarUsuario = admUsuarioRepository.findAll();
            mvListaUser.addObject("usuarios", listarUsuario);
        return mvListaUser;
        }

        @GetMapping("/users/{id}/delete")
        public String deleteUser(@PathVariable UUID id) {
            admUsuarioRepository.deleteById(id);
        return "redirect:/users";
        }

    @GetMapping("/users/{id}")
    public String findUser(@PathVariable UUID id, Model model) {
        Optional<AdmUsuarioModel> findUsuario = admUsuarioRepository.findById(id);
        try{
            model.addAttribute("findUser", findUsuario.get());
        }
        catch (Exception err){
            return "redirect:/users";
        }
        return "/register-change";
    }

    @PostMapping("/users/{id}/alterar")
    public String alterarUsers(@PathVariable UUID id, AdmUsuarioModel admUsuarioModel){
            if (admUsuarioRepository.findAllById(id));
    }
}