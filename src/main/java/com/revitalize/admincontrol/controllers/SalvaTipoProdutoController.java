package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.repository.AdmEmpresaRepository;
import com.revitalize.admincontrol.repository.AdmTipoProdutoRepository;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Controller
public class SalvaTipoProdutoController {
    @Autowired
    private AdmTipoProdutoRepository admTipoProdutoRepository;


    @RequestMapping(value="/typeproduct", method=RequestMethod.POST)
    public String formtypeproduct(AdmTipoProdutoModel admTipoProdutoModel) {
        admTipoProdutoModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admTipoProdutoModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admTipoProdutoRepository.save(admTipoProdutoModel);
        return "redirect:/typeproduct";
    }

}
