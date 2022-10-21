package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmProdutoModel;
import com.revitalize.admincontrol.repository.AdmProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class ListaProdutoController {

    @Autowired
    private AdmProdutoRepository admProdutoRepository;

    @RequestMapping("/product")
    public ModelAndView produto() {
        ModelAndView mvProduto = new ModelAndView("product.html");
        Iterable<AdmProdutoModel> produto = admProdutoRepository.findAll();
        mvProduto.addObject("produto", produto);
        return mvProduto;
    }

    @GetMapping("/product/{id}/delete")
    public String deleteProduct(@PathVariable UUID id) {
        admProdutoRepository.deleteById(id);
        return "redirect:/product";
    }

    @GetMapping("/product/{id}/update")
    public ModelAndView editProduct(@PathVariable UUID id) {
        ModelAndView mvEditProduct = new ModelAndView("product.html");
        mvEditProduct.setViewName("/product");
        return mvEditProduct;


    }
}