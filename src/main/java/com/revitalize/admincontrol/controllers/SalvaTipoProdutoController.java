package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import com.revitalize.admincontrol.repository.AdmTipoProdutoRepository;
import com.revitalize.admincontrol.services.AdmTipoProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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


    @Autowired
    private AdmTipoProdutoService admTipoProdutoService;

    public ModelAndView tProductList(){

        ModelAndView ListaTipoProduto = new ModelAndView("registerproduct.html");
        List<AdmTipoProdutoModel> tipoProdutoLista = this.admTipoProdutoService.getListaTipoProduto();
        ListaTipoProduto.addObject("tipoproduto", tipoProdutoLista);
        return ListaTipoProduto;

    }

}