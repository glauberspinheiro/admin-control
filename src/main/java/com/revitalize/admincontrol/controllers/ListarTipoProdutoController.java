package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.repository.AdmTipoProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import java.util.UUID;

@Controller
public class ListarTipoProdutoController {

    @Autowired
    private AdmTipoProdutoRepository admTipoProdutoRepository;

    @GetMapping("/typeproduct")
    public ModelAndView formtypeproductList() {
        ModelAndView mvListaTipoProduto = new ModelAndView("registertypeproduct.html");
        Iterable<AdmTipoProdutoModel> TipoProduto = admTipoProdutoRepository.findAll();
        mvListaTipoProduto.addObject("tpproduto", TipoProduto);
        return mvListaTipoProduto;
    }

    @GetMapping("/typeproduct/{id}/delete")
    public String deleteTypeProduct(@PathVariable UUID id) {
        admTipoProdutoRepository.deleteById(id);
        return "redirect:/typeproduct";
    }

}