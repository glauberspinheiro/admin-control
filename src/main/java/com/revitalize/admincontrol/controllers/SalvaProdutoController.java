package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmProdutoModel;
import com.revitalize.admincontrol.repository.AdmProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Controller
public class SalvaProdutoController {

    @Autowired
    private AdmProdutoRepository admProdutoRepository;


    @RequestMapping(value="/addproduct", method= RequestMethod.GET)
    public String formProduct(){
        return "registerproduct";
    }

    @RequestMapping(value="/addproduct", method=RequestMethod.POST)
    public String formProduct(AdmProdutoModel admProdutoModel) {
        admProdutoModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admProdutoModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admProdutoRepository.save(admProdutoModel);
        return "redirect:/product";
    }
}