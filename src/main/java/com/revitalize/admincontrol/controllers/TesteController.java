package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
@ResponseBody
public class TesteController {
    private AdmUsuarioRepository aur;


    @GetMapping("/teste")
    public ModelAndView listaUsuario(){
        ModelAndView mv = new ModelAndView("teste");
        Iterable<AdmUsuarioModel> listaUsuario = aur.findAll();
        mv.addObject("usuario", listaUsuario);
        return mv;
    }
}
