package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.UUID;

@Controller
public class ListaEmpresaController {

    @Autowired
    private AdmEmpresaRepository admEmpresaRepository;

    @RequestMapping("/company")
    public ModelAndView listaEmpresa() {
        ModelAndView mvListaCompany = new ModelAndView("company.html");
        Iterable<AdmEmpresaModel> listaEmpresa = admEmpresaRepository.findAll();
        mvListaCompany.addObject("empresas", listaEmpresa);
        return mvListaCompany;
    }

    @GetMapping("/company/{id}/delete")
    public String deleteCompany(@PathVariable UUID id) {
        admEmpresaRepository.deleteById(id);
        return "redirect:/company";
    }

    @GetMapping("/company/{id}/update")
    public ModelAndView editCompany(@PathVariable UUID id) {
        ModelAndView mvEditCompany = new ModelAndView("company.html");
        mvEditCompany.setViewName("/addcompany");
        return mvEditCompany;


    }
}