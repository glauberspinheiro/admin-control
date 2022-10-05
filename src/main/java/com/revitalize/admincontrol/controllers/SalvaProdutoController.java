package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmProdutoModel;
import com.revitalize.admincontrol.repository.AdmProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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



    @PostMapping("/addproduct")
    public String saveProduct(@Valid AdmProdutoModel admProdutoModel, BindingResult result, RedirectAttributes redirect) {
        admProdutoModel.setDt_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        admProdutoModel.setDt_alteracao_cadastro(LocalDateTime.now(ZoneId.of("-03:00")));
        if(result.hasErrors()){
            redirect.addFlashAttribute("mensagemsalvarproduto", "Favor preencher todos os campos");
            return "redirect:/addproduct";
        }
        admProdutoRepository.save(admProdutoModel);
        return "redirect:/product";
    }
}