package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Controller
public class SalvaEmpresaController {
    @Autowired
    private AdmEmpresaRepository admEmpresaRepository;

    @RequestMapping(value="/addcompany", method= RequestMethod.GET)
    public String formCompany(){

        return "registercompany";
    }


    @RequestMapping(value="/addcompany", method=RequestMethod.POST)
    public String formCompany(AdmEmpresaModel admEmpresaModel) {
        admEmpresaModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admEmpresaModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admEmpresaRepository.save(admEmpresaModel);
        return "redirect:/company";
    }
}
