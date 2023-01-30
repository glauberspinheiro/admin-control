package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Controller
public class SalvaEmpresaController {
    @Autowired
    private AdmEmpresaRepository admEmpresaRepository;

    @RequestMapping(value="/addcompany", method= RequestMethod.GET)
    public String formCompany(){

        return "registercompany";
    }


    @RequestMapping(value="/addcompany", method=RequestMethod.POST)
    public String formCompany(@Valid AdmEmpresaModel admEmpresaModel, BindingResult result, RedirectAttributes redirect) {
        admEmpresaModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admEmpresaModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        if(result.hasErrors()){
            redirect.addFlashAttribute("mensagemsalvarempresa", "Favor preencher todos os campos");
            return "redirect:/addcompany";
        }
        this.admEmpresaRepository.save(admEmpresaModel);
        return "redirect:/company";
    }
}
